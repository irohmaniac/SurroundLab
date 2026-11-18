package de.surroundlab.app.audio

import android.content.Context
import de.surroundlab.app.domain.model.FrequencyMeasurementPoint
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.LevelCheckVerdict
import de.surroundlab.app.domain.model.MeasurementRunProgress
import de.surroundlab.app.domain.model.MeasurementSpeed
import de.surroundlab.app.domain.model.RoomGraphUtils
import de.surroundlab.app.domain.model.RoomLevelCheckState
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.SweepPhase
import de.surroundlab.app.domain.model.SweepStepMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Orchestrates live microphone level checks and automated stepped sine sweeps
 * against either the remote TV host (via WebSocket state sync) or local phone audio.
 */
class RoomMeasurementCoordinator(
    context: Context,
    private val stateProvider: () -> SurroundAppState,
    private val isConnectedToTv: () -> Boolean,
    private val sendMeasurementTone: suspend (
        preset: GeneratorPreset,
        customChannels: Set<SpeakerChannel>,
        frequencyHz: Int,
        playing: Boolean
    ) -> Unit
) {
    private val analyzer = RoomAcousticAnalyzer(context.applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _levelCheckState = MutableStateFlow(RoomLevelCheckState())
    val levelCheckState: StateFlow<RoomLevelCheckState> = _levelCheckState.asStateFlow()

    private val _runProgress = MutableStateFlow(MeasurementRunProgress())
    val runProgress: StateFlow<MeasurementRunProgress> = _runProgress.asStateFlow()

    private var levelJob: Job? = null
    private var sweepJob: Job? = null

    fun startLevelCheck(
        preset: GeneratorPreset,
        customChannels: Set<SpeakerChannel>,
        testHz: Int,
        withTone: Boolean
    ) {
        stopAllMeasurementJobs()
        val safeHz = testHz.coerceIn(20, 20000)
        _levelCheckState.value = RoomLevelCheckState(
            running = true,
            playingTestTone = false,
            testFrequencyHz = safeHz,
            verdict = LevelCheckVerdict.CALIBRATING_SILENCE
        )

        levelJob = scope.launch {
            val record = analyzer.openAudioRecord()
            if (record == null) {
                _levelCheckState.value = RoomLevelCheckState(
                    running = false,
                    verdict = LevelCheckVerdict.IDLE
                )
                return@launch
            }

            try {
                sendMeasurementTone(preset, customChannels, safeHz, false)
                record.startRecording()
                delay(120L)
                analyzer.drainBufferedSamples(record, 60L)

                val silenceSamples = analyzer.captureSamples(record, 550L) { isActive }
                val ambientAtFreq = analyzer.computeGoertzelDbFs(silenceSamples, safeHz)

                if (withTone) {
                    sendMeasurementTone(preset, customChannels, safeHz, true)
                    waitForToneSync(safeHz, playing = true)
                    delay(180L)
                    analyzer.drainBufferedSamples(record, 50L)
                }

                while (isActive) {
                    val chunk = analyzer.captureSamples(record, 240L) { isActive }
                    if (!isActive) break
                    val snap = analyzer.analyzeSnapshot(chunk, safeHz)
                    val snr = snap.targetFreqDbFs - ambientAtFreq
                    val verdict = when {
                        snap.clipping -> LevelCheckVerdict.CLIPPING
                        !withTone -> LevelCheckVerdict.CALIBRATING_SILENCE
                        snr < 10f -> LevelCheckVerdict.TOO_QUIET
                        else -> LevelCheckVerdict.OPTIMAL
                    }
                    _levelCheckState.value = RoomLevelCheckState(
                        running = true,
                        playingTestTone = withTone,
                        testFrequencyHz = safeHz,
                        ambientNoiseDbFs = ambientAtFreq,
                        liveToneDbFs = snap.targetFreqDbFs,
                        snrDb = snr,
                        clippingDetected = snap.clipping,
                        verdict = verdict
                    )
                }
            } finally {
                runCatching { record.stop() }
                runCatching { record.release() }
                sendMeasurementTone(preset, customChannels, safeHz, false)
                _levelCheckState.value = _levelCheckState.value.copy(
                    running = false,
                    playingTestTone = false
                )
            }
        }
    }

    fun stopLevelCheck() {
        levelJob?.cancel()
        levelJob = null
        _levelCheckState.value = _levelCheckState.value.copy(
            running = false,
            playingTestTone = false,
            verdict = LevelCheckVerdict.IDLE
        )
    }

    fun startSweep(
        preset: GeneratorPreset,
        customChannels: Set<SpeakerChannel>,
        startHz: Int,
        endHz: Int,
        stepMode: SweepStepMode,
        stepValue: Int,
        speed: MeasurementSpeed,
        micErrorLabel: String,
        onCompleted: suspend (List<FrequencyMeasurementPoint>) -> Unit
    ) {
        stopAllMeasurementJobs()
        val frequencies = RoomGraphUtils.buildSweepFrequencies(startHz, endHz, stepMode, stepValue)
        if (frequencies.isEmpty()) return

        sweepJob = scope.launch {
            var record: android.media.AudioRecord? = null
            try {
                // 3-second countdown so the user can hold the phone steady and stay quiet
                for (sec in 3 downTo 1) {
                    if (!isActive) return@launch
                    _runProgress.value = MeasurementRunProgress(
                        phase = SweepPhase.COUNTDOWN,
                        countdownSeconds = sec,
                        totalSteps = frequencies.size
                    )
                    delay(1000L)
                }

                record = analyzer.openAudioRecord()
                if (record == null) {
                    _runProgress.value = MeasurementRunProgress(
                        phase = SweepPhase.ERROR,
                        statusMessage = micErrorLabel
                    )
                    return@launch
                }

                _runProgress.value = MeasurementRunProgress(
                    phase = SweepPhase.MEASURING_SILENCE,
                    totalSteps = frequencies.size
                )

                sendMeasurementTone(preset, customChannels, frequencies.first(), false)
                record.startRecording()
                delay(140L)
                analyzer.drainBufferedSamples(record, 80L)
                val silenceBuffer = analyzer.captureSamples(record, 850L) { isActive }
                if (!isActive) return@launch

                val collected = ArrayList<FrequencyMeasurementPoint>(frequencies.size)

                for ((index, hz) in frequencies.withIndex()) {
                    if (!isActive) break
                    _runProgress.value = MeasurementRunProgress(
                        phase = SweepPhase.SWEEPING,
                        currentStepIndex = index + 1,
                        totalSteps = frequencies.size,
                        currentFrequencyHz = hz,
                        livePoints = collected.toList()
                    )

                    sendMeasurementTone(preset, customChannels, hz, true)
                    waitForToneSync(hz, playing = true)
                    delay(speed.settleMs)
                    if (!isActive) break

                    analyzer.drainBufferedSamples(record, 60L)
                    val toneSamples = analyzer.captureSamples(record, speed.measureMs) { isActive }
                    if (!isActive) break

                    sendMeasurementTone(preset, customChannels, hz, false)

                    val point = analyzer.buildPointFromSamples(hz, toneSamples, silenceBuffer)
                    collected.add(point)

                    _runProgress.value = MeasurementRunProgress(
                        phase = SweepPhase.SWEEPING,
                        currentStepIndex = index + 1,
                        totalSteps = frequencies.size,
                        currentFrequencyHz = hz,
                        livePoints = collected.toList()
                    )

                    if (index < frequencies.lastIndex) {
                        delay(speed.pauseMs)
                    }
                }

                if (isActive && collected.isNotEmpty()) {
                    _runProgress.value = MeasurementRunProgress(
                        phase = SweepPhase.COMPLETED,
                        currentStepIndex = frequencies.size,
                        totalSteps = frequencies.size,
                        currentFrequencyHz = frequencies.last(),
                        livePoints = collected.toList()
                    )
                    onCompleted(collected)
                }
            } finally {
                runCatching { record?.stop() }
                runCatching { record?.release() }
                runCatching {
                    sendMeasurementTone(preset, customChannels, frequencies.first(), false)
                }
            }
        }
    }

    fun cancelSweep() {
        val wasRunning = sweepJob?.isActive == true
        sweepJob?.cancel()
        sweepJob = null
        if (wasRunning) {
            _runProgress.value = _runProgress.value.copy(phase = SweepPhase.CANCELLED)
        }
    }

    fun resetProgress() {
        _runProgress.value = MeasurementRunProgress()
    }

    fun stopAllMeasurementJobs() {
        stopLevelCheck()
        cancelSweep()
    }

    private suspend fun waitForToneSync(targetHz: Int, playing: Boolean) {
        val timeoutMs = if (isConnectedToTv()) 350L else 60L
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            val tone = stateProvider().measurementTone
            if (tone.playing == playing && (!playing || tone.frequencyHz == targetHz)) {
                return
            }
            delay(15L)
        }
    }
}

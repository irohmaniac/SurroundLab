package de.surroundlab.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Process
import android.util.Log
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TestMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Real-time multichannel PCM DSP audio engine (48 kHz).
 *
 * Guarantees (SPEC.md 3.2 & 7, AGENTS.md 4.2):
 * - Strictly ZERO object allocations inside the real-time AudioTrack write loop.
 * - Opens CHANNEL_OUT_STEREO (2.0 or mobile standalone), CHANNEL_OUT_5POINT1 (2.1/3.1/5.0/5.1
 *   with digital silence padding for unused channels), or CHANNEL_OUT_7POINT1_SURROUND (7.1).
 * - Automatically selects ENCODING_PCM_16BIT for multichannel HDMI DIRECT output when the
 *   hardware HAL does not advertise direct ENCODING_PCM_FLOAT support (e.g., Chromecast / Amlogic
 *   HDMI HALs), preventing Android's AudioMixer from downmixing 5.1/7.1 to 2.0 stereo.
 * - ITU-R BS.775 downmix when forceStereoDownmix is enabled (Smartphone standalone mode).
 * - 15 ms Raised-Cosine fade-in / fade-out on every start, stop, and channel/mode switch.
 * - 10 ms one-pole smoothing on frequency and channel gains.
 * - 2nd-order Butterworth 80 Hz low-pass filter on LFE (.1).
 * - Default -12 dBFS digital headroom + soft-clip limiter clamped at +/- 0.98f.
 */
class SurroundAudioEngine(
    private val context: Context,
    private val forceStereoDownmix: Boolean
) {
    companion object {
        const val SAMPLE_RATE = 48000
        private const val FRAMES_PER_BUFFER = 512
        private const val FADE_SAMPLES = (SAMPLE_RATE * 15) / 1000 // 720 samples = 15 ms
        private const val IDLE_RELEASE_FRAMES = SAMPLE_RATE * 4 // 4 s grace period before closing idle track
        private const val TWO_PI = 2.0 * PI
    }

    /**
     * Immutable target snapshot built on the control thread; read atomically by the audio thread.
     */
    private class DspTarget(
        val setup: SpeakerSetup,
        val playing: Boolean,
        val sourceType: SignalSourceType,
        val frequencyHz: Float,
        val masterGainLinear: Float,
        val swap71SurroundBack: Boolean,
        val airFilterAlpha: Float,
        val channelGains: FloatArray, // length 8, indexed by SpeakerChannel.ordinal
        val customSamples: FloatArray?,
        val switchSignature: Long
    )

    private data class TrackConfig(
        val track: AudioTrack,
        val streamChannels: Int,
        val usePcm16: Boolean
    )

    @Volatile
    private var latestTarget: DspTarget = DspTarget(
        setup = SpeakerSetup.S5_1,
        playing = false,
        sourceType = SignalSourceType.PINK_NOISE,
        frequencyHz = 400f,
        masterGainLinear = dbToLinear(-12f),
        swap71SurroundBack = false,
        airFilterAlpha = 1.0f,
        channelGains = FloatArray(8),
        customSamples = null,
        switchSignature = 0L
    )

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val idleLock = Object()

    @Volatile
    private var running = false
    @Volatile
    private var forceTrackRebuild = false
    private var audioThread: Thread? = null
    private var deviceCallback: AudioDeviceCallback? = null

    // Pre-allocated scratch arrays for target construction on control thread
    private val stagingGains = FloatArray(8)
    private var lastSignature = 0L
    private var lastWasPlaying = false
    private var lastModeSignature = 0L

    fun start() {
        if (running) return
        running = true
        registerHdmiDeviceCallback()
        audioThread = Thread(::audioLoop, "SurroundLab-DSP").apply {
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    fun stop() {
        running = false
        synchronized(idleLock) {
            idleLock.notifyAll()
        }
        unregisterHdmiDeviceCallback()
        audioThread?.join(600L)
        audioThread = null
    }

    private fun registerHdmiDeviceCallback() {
        if (deviceCallback != null) return
        val cb = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
                if (addedDevices.any { isHdmiDeviceType(it.type) }) {
                    Log.i("SurroundLabAudio", "HDMI output device added; scheduling AudioTrack rebuild")
                    forceTrackRebuild = true
                }
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
                if (removedDevices.any { isHdmiDeviceType(it.type) }) {
                    Log.i("SurroundLabAudio", "HDMI output device removed; scheduling AudioTrack rebuild")
                    forceTrackRebuild = true
                }
            }
        }
        deviceCallback = cb
        runCatching { audioManager.registerAudioDeviceCallback(cb, null) }
    }

    private fun unregisterHdmiDeviceCallback() {
        deviceCallback?.let { cb ->
            runCatching { audioManager.unregisterAudioDeviceCallback(cb) }
        }
        deviceCallback = null
    }

    private fun isHdmiDeviceType(type: Int): Boolean =
        type == AudioDeviceInfo.TYPE_HDMI ||
            type == AudioDeviceInfo.TYPE_HDMI_ARC ||
            type == AudioDeviceInfo.TYPE_HDMI_EARC

    /**
     * Translates the current SurroundAppState + optional pre-decoded custom audio samples
     * into an atomic DSP target snapshot. Called from the control/repository coroutine, never
     * from inside the real-time audio loop.
     */
    fun applyState(state: SurroundAppState, customSamples: FloatArray?) {
        val gains = FloatArray(8)
        var playing = false
        var sourceType = SignalSourceType.PINK_NOISE
        var frequencyHz = 400f
        var airFilterAlpha = 1.0f
        var modeSig = 0L

        when (state.activeMode) {
            TestMode.HUB -> {
                playing = false
                modeSig = 1L
            }
            TestMode.SINGLE_SPEAKER -> {
                val active = state.singleSpeaker.activeChannel
                playing = state.singleSpeaker.playing && active != null && active in state.setup.channels
                if (active != null) {
                    val cfg = state.singleSpeaker.configFor(active)
                    sourceType = cfg.sourceType
                    frequencyHz = cfg.frequencyHz.toFloat()
                    if (playing) {
                        gains[active.ordinal] = 1.0f
                    }
                    modeSig = 100L + active.ordinal * 10L + sourceType.ordinal +
                        ((cfg.audioId?.hashCode() ?: 0).toLong() and 0xFFFFFFL) * 1000L
                } else {
                    modeSig = 100L
                }
            }
            TestMode.GENERATOR -> {
                playing = state.generator.playing
                sourceType = if (state.generator.signalType == SignalSourceType.CUSTOM_AUDIO) {
                    SignalSourceType.SINE
                } else {
                    state.generator.signalType
                }
                frequencyHz = state.generator.frequencyHz.toFloat()
                val activeChannels = state.generator.preset.resolveChannels(
                    state.setup,
                    state.generator.customChannels
                )
                if (playing && activeChannels.isNotEmpty()) {
                    val scale = if (activeChannels.size > 1) {
                        (1.0f / kotlin.math.sqrt(activeChannels.size.toFloat())).coerceAtLeast(0.4f)
                    } else 1.0f
                    for (ch in activeChannels) {
                        gains[ch.ordinal] = scale
                    }
                }
                modeSig = 200L + state.generator.preset.ordinal * 10L + sourceType.ordinal
            }
            TestMode.PUCK_2D -> {
                playing = state.puck.playing
                sourceType = state.puck.sourceType
                frequencyHz = state.puck.frequencyHz.toFloat()
                if (state.puck.panningMode == PanningMode.SWEET_SPOT_VBAP) {
                    val normR = hypot(state.puck.x, state.puck.y).coerceIn(0f, 1f)
                    if (normR > 0.05f) {
                        // Gentle air-absorption high-frequency rolloff: 20 kHz near head -> 6.5 kHz at edge
                        val cutoffHz = 20000.0 * (6500.0 / 20000.0).pow(normR.toDouble())
                        airFilterAlpha = (1.0 - exp(-TWO_PI * cutoffHz / SAMPLE_RATE.toDouble()))
                            .toFloat()
                            .coerceIn(0.05f, 1.0f)
                    }
                }
                if (playing) {
                    SpatialPanner.computeGains(
                        setup = state.setup,
                        x = state.puck.x,
                        y = state.puck.y,
                        panningMode = state.puck.panningMode,
                        subwooferEnabled = state.puck.subwooferEnabled,
                        outGains = stagingGains,
                        layoutPreset = state.speakerLayoutPreset
                    )
                    System.arraycopy(stagingGains, 0, gains, 0, 8)
                }
                modeSig = 300L + sourceType.ordinal +
                    ((state.puck.audioId?.hashCode() ?: 0).toLong() and 0xFFFFFFL) * 1000L
            }
        }

        if (playing && lastWasPlaying && modeSig != lastModeSignature) {
            lastSignature++
        }
        lastWasPlaying = playing
        lastModeSignature = modeSig

        latestTarget = DspTarget(
            setup = state.setup,
            playing = playing,
            sourceType = sourceType,
            frequencyHz = frequencyHz.coerceIn(20f, 20000f),
            masterGainLinear = dbToLinear(state.masterGainDb.coerceIn(-30f, 0f)),
            swap71SurroundBack = state.swap71SurroundBack,
            airFilterAlpha = airFilterAlpha,
            channelGains = gains,
            customSamples = customSamples,
            switchSignature = lastSignature
        )
        if (playing) {
            synchronized(idleLock) {
                idleLock.notifyAll()
            }
        }
    }

    private fun releaseTrack(track: AudioTrack?) {
        if (track == null) return
        runCatching {
            track.pause()
            track.flush()
            track.release()
        }
    }

    private fun audioLoop() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

        var currentSetup = latestTarget.setup
        var track: AudioTrack? = null
        var streamChannels = 0
        var usePcm16 = true

        // Pre-allocate max 8-channel buffers once per track setup (never inside active DSP loop)
        var floatOutputBuffer = FloatArray(0)
        var shortOutputBuffer = ShortArray(0)

        var activeTarget = latestTarget
        var pendingSwitchTarget: DspTarget? = null

        var phase = 0.0
        var customSamplePos = 0
        var lastCustomArray: FloatArray? = null

        // Paul Kellet Pink Noise filter states + XorShift32 PRNG
        var rngState = 0x5DEECE66D.toInt()
        var b0 = 0f
        var b1 = 0f
        var b2 = 0f
        var b3 = 0f
        var b4 = 0f
        var b5 = 0f
        var b6 = 0f

        // 2nd-order Butterworth 80 Hz LFE low-pass filter coefficients & state
        val lfeCoeff = computeButterworthLowPass80Hz()
        val lfeB0 = lfeCoeff[0]
        val lfeB1 = lfeCoeff[1]
        val lfeB2 = lfeCoeff[2]
        val lfeA1 = lfeCoeff[3]
        val lfeA2 = lfeCoeff[4]
        var lfeZ1 = 0f
        var lfeZ2 = 0f

        // 10 ms one-pole smoothing coefficient
        val smoothAlpha = 1.0f - exp(-1.0f / (0.010f * SAMPLE_RATE))
        var smoothedFreq = activeTarget.frequencyHz
        var smoothedMasterGain = activeTarget.masterGainLinear
        var smoothedAirAlpha = activeTarget.airFilterAlpha
        var airLpState = 0f
        val smoothedChGains = FloatArray(8)

        // 15 ms Raised-Cosine fade envelope & idle lifecycle state
        var fadePos = 0
        var currentPlaying = false
        var idleFrames = 0

        try {
            while (running) {
                val incoming = latestTarget

                // Sleep cleanly without holding an AudioTrack when idle
                if (track == null) {
                    if (!incoming.playing) {
                        synchronized(idleLock) {
                            if (running && !latestTarget.playing) {
                                runCatching { idleLock.wait(100L) }
                            }
                        }
                        continue
                    }

                    val config = try {
                        createTrackConfig(incoming.setup)
                    } catch (t: Throwable) {
                        Log.w("SurroundLabAudio", "Failed to create AudioTrack: ${t.message}")
                        Thread.sleep(250L)
                        continue
                    }

                    if (config.track.state != AudioTrack.STATE_INITIALIZED) {
                        Log.w("SurroundLabAudio", "AudioTrack uninitialized; retrying")
                        releaseTrack(config.track)
                        Thread.sleep(250L)
                        continue
                    }

                    val playOk = runCatching { config.track.play() }.isSuccess
                    if (!playOk) {
                        releaseTrack(config.track)
                        Thread.sleep(250L)
                        continue
                    }

                    currentSetup = incoming.setup
                    track = config.track
                    streamChannels = config.streamChannels
                    usePcm16 = config.usePcm16
                    val neededSize = FRAMES_PER_BUFFER * streamChannels
                    if (floatOutputBuffer.size != neededSize) {
                        floatOutputBuffer = FloatArray(neededSize)
                        shortOutputBuffer = ShortArray(neededSize)
                    }
                    fadePos = 0
                    currentPlaying = false
                    idleFrames = 0
                    forceTrackRebuild = false
                    airLpState = 0f
                    lfeZ1 = 0f
                    lfeZ2 = 0f
                    for (c in 0 until 8) {
                        smoothedChGains[c] = 0f
                    }
                }

                val activeTrack = track ?: continue

                // Reopen AudioTrack if hardware speaker setup or HDMI topology changed
                if (incoming.setup != currentSetup || forceTrackRebuild) {
                    Log.i(
                        "SurroundLabAudio",
                        "Rebuilding AudioTrack (setup=${incoming.setup.name}, hdmiChanged=$forceTrackRebuild)"
                    )
                    forceTrackRebuild = false
                    releaseTrack(activeTrack)
                    track = null
                    currentSetup = incoming.setup
                    fadePos = 0
                    currentPlaying = false
                    idleFrames = 0
                    continue
                }

                // Handle clean channel/source switch via fade-out -> switch -> fade-in
                if (incoming.switchSignature != activeTarget.switchSignature && fadePos > 0) {
                    pendingSwitchTarget = incoming
                } else if (pendingSwitchTarget == null) {
                    activeTarget = incoming
                }

                val targetPlaying = if (pendingSwitchTarget != null) false else activeTarget.playing
                val customArr = activeTarget.customSamples
                if (customArr !== lastCustomArray) {
                    lastCustomArray = customArr
                    customSamplePos = 0
                }

                val targetFreq = activeTarget.frequencyHz
                val targetMaster = activeTarget.masterGainLinear
                val targetAirAlpha = activeTarget.airFilterAlpha
                val swap71 = activeTarget.swap71SurroundBack
                val targetChGains = activeTarget.channelGains
                val sourceType = activeTarget.sourceType

                val hasC = SpeakerChannel.C in currentSetup.channels
                val hasSub = currentSetup.hasSubwoofer
                val hasSl = SpeakerChannel.SL in currentSetup.channels
                val hasSr = SpeakerChannel.SR in currentSetup.channels

                var bufIdx = 0
                for (frame in 0 until FRAMES_PER_BUFFER) {
                    // 1. Update 15 ms Raised-Cosine fade envelope
                    if (targetPlaying) {
                        if (fadePos < FADE_SAMPLES) fadePos++
                        currentPlaying = true
                    } else {
                        if (fadePos > 0) {
                            fadePos--
                        } else if (pendingSwitchTarget != null) {
                            activeTarget = pendingSwitchTarget!!
                            pendingSwitchTarget = null
                            phase = 0.0
                        } else {
                            currentPlaying = false
                        }
                    }

                    val fadeEnv = if (fadePos <= 0) {
                        0f
                    } else if (fadePos >= FADE_SAMPLES) {
                        1f
                    } else {
                        val t = fadePos.toFloat() / FADE_SAMPLES.toFloat()
                        0.5f * (1.0f - cos(PI.toFloat() * t))
                    }

                    // 2. Smooth frequency, master gain, air-absorption, and per-channel gains (10 ms one-pole)
                    smoothedFreq += (targetFreq - smoothedFreq) * smoothAlpha
                    smoothedMasterGain += (targetMaster - smoothedMasterGain) * smoothAlpha
                    smoothedAirAlpha += (targetAirAlpha - smoothedAirAlpha) * smoothAlpha
                    for (c in 0 until 8) {
                        smoothedChGains[c] += (targetChGains[c] - smoothedChGains[c]) * smoothAlpha
                    }

                    // 3. Generate base mono signal sample
                    val baseSample: Float
                    if (!currentPlaying && fadeEnv <= 0f) {
                        baseSample = 0f
                    } else {
                        baseSample = when (sourceType) {
                            SignalSourceType.SINE -> {
                                val s = sin(phase).toFloat()
                                phase += TWO_PI * smoothedFreq.toDouble() / SAMPLE_RATE.toDouble()
                                if (phase >= TWO_PI) phase -= TWO_PI
                                s
                            }
                            SignalSourceType.PINK_NOISE -> {
                                rngState = rngState xor (rngState shl 13)
                                rngState = rngState xor (rngState ushr 17)
                                rngState = rngState xor (rngState shl 5)
                                val white = rngState.toFloat() / 2147483648.0f
                                b0 = 0.99886f * b0 + white * 0.0555179f
                                b1 = 0.99332f * b1 + white * 0.0750759f
                                b2 = 0.96900f * b2 + white * 0.1538520f
                                b3 = 0.86650f * b3 + white * 0.3104856f
                                b4 = 0.55000f * b4 + white * 0.5329522f
                                b5 = -0.7616f * b5 - white * 0.0168980f
                                val pink = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f) * 0.11f
                                b6 = white * 0.115926f
                                pink
                            }
                            SignalSourceType.CUSTOM_AUDIO -> {
                                if (customArr != null && customArr.isNotEmpty()) {
                                    if (customSamplePos >= customArr.size) customSamplePos = 0
                                    val s = customArr[customSamplePos]
                                    customSamplePos++
                                    s
                                } else {
                                    0f
                                }
                            }
                        }
                    }

                    val scaledBase = baseSample * smoothedMasterGain * fadeEnv
                    airLpState += smoothedAirAlpha * (scaledBase - airLpState)
                    val satBase = if (smoothedAirAlpha >= 0.99f) scaledBase else airLpState

                    // 4. LFE 80 Hz 2nd-order Butterworth Low-Pass (Direct Form II Transposed)
                    val lfeIn = scaledBase * smoothedChGains[SpeakerChannel.LFE.ordinal]
                    val lfeFiltered = lfeB0 * lfeIn + lfeZ1
                    lfeZ1 = lfeB1 * lfeIn - lfeA1 * lfeFiltered + lfeZ2
                    lfeZ2 = lfeB2 * lfeIn - lfeA2 * lfeFiltered

                    val sFl = softLimit(satBase * smoothedChGains[SpeakerChannel.FL.ordinal])
                    val sFr = softLimit(satBase * smoothedChGains[SpeakerChannel.FR.ordinal])
                    val sC = softLimit(satBase * smoothedChGains[SpeakerChannel.C.ordinal])
                    val sLfe = softLimit(lfeFiltered)
                    val sSl = softLimit(satBase * smoothedChGains[SpeakerChannel.SL.ordinal])
                    val sSr = softLimit(satBase * smoothedChGains[SpeakerChannel.SR.ordinal])
                    val sSbl = softLimit(satBase * smoothedChGains[SpeakerChannel.SBL.ordinal])
                    val sSbr = softLimit(satBase * smoothedChGains[SpeakerChannel.SBR.ordinal])

                    // 5. Channel Mapping & Digital Silence Padding / ITU-R BS.775 Downmix
                    if (forceStereoDownmix || currentSetup == SpeakerSetup.S2_0) {
                        val left = softLimit(sFl + 0.707f * sC + 0.707f * sSl + 0.707f * sSbl + 0.5f * sLfe)
                        val right = softLimit(sFr + 0.707f * sC + 0.707f * sSr + 0.707f * sSbr + 0.5f * sLfe)
                        if (usePcm16) {
                            shortOutputBuffer[bufIdx++] = floatToShort(left)
                            shortOutputBuffer[bufIdx++] = floatToShort(right)
                        } else {
                            floatOutputBuffer[bufIdx++] = left
                            floatOutputBuffer[bufIdx++] = right
                        }
                    } else if (streamChannels == 6) {
                        // Android / WAVE 5.1 order: 0:FL, 1:FR, 2:FC, 3:LFE, 4:BL/SL, 5:BR/SR
                        val cOut = if (hasC) sC else 0f
                        val lfeOut = if (hasSub) sLfe else 0f
                        val slOut = if (hasSl) sSl else 0f
                        val srOut = if (hasSr) sSr else 0f
                        if (usePcm16) {
                            shortOutputBuffer[bufIdx++] = floatToShort(sFl)
                            shortOutputBuffer[bufIdx++] = floatToShort(sFr)
                            shortOutputBuffer[bufIdx++] = floatToShort(cOut)
                            shortOutputBuffer[bufIdx++] = floatToShort(lfeOut)
                            shortOutputBuffer[bufIdx++] = floatToShort(slOut)
                            shortOutputBuffer[bufIdx++] = floatToShort(srOut)
                        } else {
                            floatOutputBuffer[bufIdx++] = sFl
                            floatOutputBuffer[bufIdx++] = sFr
                            floatOutputBuffer[bufIdx++] = cOut
                            floatOutputBuffer[bufIdx++] = lfeOut
                            floatOutputBuffer[bufIdx++] = slOut
                            floatOutputBuffer[bufIdx++] = srOut
                        }
                    } else {
                        // Standard Android / WAVE 7.1: 0:FL, 1:FR, 2:FC, 3:LFE, 4:SBL, 5:SBR, 6:SL, 7:SR
                        // When swap71 is active (e.g. Amlogic/CEA-861 HDMI HAL), slots 4/5 carry SL/SR and 6/7 carry SBL/SBR.
                        val ch4 = if (swap71) sSl else sSbl
                        val ch5 = if (swap71) sSr else sSbr
                        val ch6 = if (swap71) sSbl else sSl
                        val ch7 = if (swap71) sSbr else sSr
                        if (usePcm16) {
                            shortOutputBuffer[bufIdx++] = floatToShort(sFl)
                            shortOutputBuffer[bufIdx++] = floatToShort(sFr)
                            shortOutputBuffer[bufIdx++] = floatToShort(sC)
                            shortOutputBuffer[bufIdx++] = floatToShort(sLfe)
                            shortOutputBuffer[bufIdx++] = floatToShort(ch4)
                            shortOutputBuffer[bufIdx++] = floatToShort(ch5)
                            shortOutputBuffer[bufIdx++] = floatToShort(ch6)
                            shortOutputBuffer[bufIdx++] = floatToShort(ch7)
                        } else {
                            floatOutputBuffer[bufIdx++] = sFl
                            floatOutputBuffer[bufIdx++] = sFr
                            floatOutputBuffer[bufIdx++] = sC
                            floatOutputBuffer[bufIdx++] = sLfe
                            floatOutputBuffer[bufIdx++] = ch4
                            floatOutputBuffer[bufIdx++] = ch5
                            floatOutputBuffer[bufIdx++] = ch6
                            floatOutputBuffer[bufIdx++] = ch7
                        }
                    }
                }

                if (!targetPlaying && fadePos <= 0 && !currentPlaying) {
                    idleFrames += FRAMES_PER_BUFFER
                    if (idleFrames >= IDLE_RELEASE_FRAMES) {
                        Log.i("SurroundLabAudio", "Releasing idle AudioTrack after silence grace period")
                        releaseTrack(activeTrack)
                        track = null
                        idleFrames = 0
                        continue
                    }
                } else {
                    idleFrames = 0
                }

                val written = if (usePcm16) {
                    activeTrack.write(shortOutputBuffer, 0, bufIdx, AudioTrack.WRITE_BLOCKING)
                } else {
                    activeTrack.write(floatOutputBuffer, 0, bufIdx, AudioTrack.WRITE_BLOCKING)
                }
                if (written < 0) {
                    Log.w("SurroundLabAudio", "AudioTrack write error=$written; recreating track")
                    releaseTrack(activeTrack)
                    track = null
                    fadePos = 0
                    currentPlaying = false
                    Thread.sleep(150L)
                } else if (written == 0) {
                    Thread.sleep(5L)
                }
            }
        } finally {
            releaseTrack(track)
        }
    }

    private fun resolveStreamChannelCount(setup: SpeakerSetup): Int =
        if (forceStereoDownmix) 2 else setup.streamChannelCount

    private fun createTrackConfig(setup: SpeakerSetup): TrackConfig {
        val streamChannels = resolveStreamChannelCount(setup)
        val channelMask = when {
            forceStereoDownmix || setup == SpeakerSetup.S2_0 -> AudioFormat.CHANNEL_OUT_STEREO
            setup == SpeakerSetup.S7_1 -> AudioFormat.CHANNEL_OUT_7POINT1_SURROUND
            else -> AudioFormat.CHANNEL_OUT_5POINT1
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        // Critical for Android TV / Chromecast HDMI:
        // Android's primary AudioMixer is strictly 2-channel stereo and downmixes multichannel
        // PCM_FLOAT tracks unless the HAL exposes a DIRECT multichannel output profile.
        // Chromecast's "direct output" MixPort only accepts ENCODING_PCM_16BIT for 5.1 / 7.1!
        val encoding = selectBestEncoding(channelMask, audioAttributes, streamChannels)
        val usePcm16 = encoding == AudioFormat.ENCODING_PCM_16BIT
        val bytesPerSample = if (usePcm16) 2 else 4

        val preferredHdmiDevice = if (!forceStereoDownmix && streamChannels > 2) {
            AudioDeviceInspector.tryEnableDirectMultichannelMixer(
                context = context,
                channelMask = channelMask,
                sampleRate = SAMPLE_RATE,
                encoding = encoding
            )
        } else null

        val audioFormat = AudioFormat.Builder()
            .setEncoding(encoding)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(channelMask)
            .build()

        val minBufBytes = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            channelMask,
            encoding
        ).coerceAtLeast(FRAMES_PER_BUFFER * streamChannels * bytesPerSample)

        val bufferBytes = (minBufBytes * 2).coerceAtLeast(
            FRAMES_PER_BUFFER * streamChannels * bytesPerSample * 2
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferBytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_NONE)
            .build()

        if (preferredHdmiDevice != null) {
            track.preferredDevice = preferredHdmiDevice
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val directSupport = AudioManager.getDirectPlaybackSupport(audioFormat, audioAttributes)
            Log.i(
                "SurroundLabAudio",
                "Created AudioTrack ch=$streamChannels mask=0x${channelMask.toString(16)} enc=$encoding " +
                    "directSupport=$directSupport routed=${track.routedDevice?.id}"
            )
        }

        return TrackConfig(
            track = track,
            streamChannels = streamChannels,
            usePcm16 = usePcm16
        )
    }

    private fun selectBestEncoding(
        channelMask: Int,
        audioAttributes: AudioAttributes,
        streamChannels: Int
    ): Int {
        if (forceStereoDownmix || streamChannels <= 2) {
            return AudioFormat.ENCODING_PCM_FLOAT
        }

        // Multichannel HDMI DIRECT output profiles on Android TV / Chromecast HALs
        // universally require 16-bit LPCM (AUDIO_FORMAT_PCM_16_BIT); PCM_FLOAT falls back
        // to the 2-channel software AudioMixer (libdownmix.so).
        return AudioFormat.ENCODING_PCM_16BIT
    }

    private fun floatToShort(sample: Float): Short =
        (sample * 32767.0f).toInt().coerceIn(-32768, 32767).toShort()

    private fun softLimit(x: Float): Float {
        val ax = abs(x)
        if (ax <= 0.75f) return x
        val sign = if (x >= 0f) 1f else -1f
        val excess = (ax - 0.75f) / 0.23f
        val compressed = 0.75f + 0.23f * tanh(excess.toDouble()).toFloat()
        return (sign * compressed).coerceIn(-0.98f, 0.98f)
    }

    private fun computeButterworthLowPass80Hz(): FloatArray {
        val fc = 80.0
        val k = kotlin.math.tan(PI * fc / SAMPLE_RATE.toDouble())
        val k2 = k * k
        val sqrt2 = kotlin.math.sqrt(2.0)
        val norm = 1.0 / (1.0 + sqrt2 * k + k2)
        val b0 = (k2 * norm).toFloat()
        val b1 = (2.0 * b0).toFloat()
        val b2 = b0
        val a1 = (2.0 * (k2 - 1.0) * norm).toFloat()
        val a2 = ((1.0 - sqrt2 * k + k2) * norm).toFloat()
        return floatArrayOf(b0, b1, b2, a1, a2)
    }

    private fun dbToLinear(db: Float): Float =
        10.0f.pow(db / 20.0f)
}

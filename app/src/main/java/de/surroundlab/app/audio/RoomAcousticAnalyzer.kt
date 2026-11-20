package de.surroundlab.app.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import de.surroundlab.app.domain.model.ExternalMicOption
import de.surroundlab.app.domain.model.FrequencyMeasurementPoint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Low-level smartphone microphone capture & Goertzel single-frequency analyzer.
 *
 * Uses MediaRecorder.AudioSource.UNPROCESSED when supported by the device HAL (falling back
 * to VOICE_RECOGNITION, then MIC) so phone AGC/noise-suppression does not flatten room modes.
 */
class RoomAcousticAnalyzer(private val context: Context) {

    companion object {
        const val SAMPLE_RATE = 48000
        private const val MIN_DBFS = -96f
    }

    data class CaptureSnapshot(
        val broadbandRmsDbFs: Float,
        val peakAmplitude: Float,
        val clipping: Boolean,
        val targetFreqDbFs: Float
    )

    private fun selectAudioSource(): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val supportsUnprocessed = am?.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) == "true"
        return if (supportsUnprocessed) {
            MediaRecorder.AudioSource.UNPROCESSED
        } else {
            MediaRecorder.AudioSource.VOICE_RECOGNITION
        }
    }

    fun listExternalMicrophones(): List<ExternalMicOption> {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return emptyList()
        val inputs = am.getDevices(AudioManager.GET_DEVICES_INPUTS)
        return inputs.mapNotNull { dev ->
            val tag = when (dev.type) {
                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_USB_HEADSET,
                AudioDeviceInfo.TYPE_USB_ACCESSORY -> "USB"
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "BT"
                AudioDeviceInfo.TYPE_WIRED_HEADSET,
                AudioDeviceInfo.TYPE_LINE_ANALOG,
                AudioDeviceInfo.TYPE_AUX_LINE -> "AUX"
                else -> if (Build.VERSION.SDK_INT >= 31 && dev.type == AudioDeviceInfo.TYPE_BLE_HEADSET) "BT" else null
            } ?: return@mapNotNull null

            val label = dev.productName?.toString()?.trim()?.takeIf { it.isNotBlank() } ?: "External Mic"
            ExternalMicOption(id = dev.id, name = label, transportTag = tag)
        }.distinctBy { it.id }
    }

    @SuppressLint("MissingPermission")
    fun openAudioRecord(preferredDeviceId: Int? = null): AudioRecord? {
        val minBytes = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)
        val bufferBytes = max(minBytes * 3, SAMPLE_RATE * 2) // >= 1s ring buffer

        val preferredDevice = if (preferredDeviceId != null) {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            am?.getDevices(AudioManager.GET_DEVICES_INPUTS)?.firstOrNull { it.id == preferredDeviceId }
        } else {
            null
        }

        val candidates = listOf(
            selectAudioSource(),
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            MediaRecorder.AudioSource.MIC
        ).distinct()

        for (source in candidates) {
            val rec = runCatching {
                AudioRecord(
                    source,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferBytes
                )
            }.getOrNull()
            if (rec != null && rec.state == AudioRecord.STATE_INITIALIZED) {
                if (preferredDevice != null) {
                    rec.setPreferredDevice(preferredDevice)
                }
                return rec
            }
            runCatching { rec?.release() }
        }
        return null
    }

    /**
     * Drains buffered stale audio samples so the next capture window only sees fresh room audio.
     */
    fun drainBufferedSamples(record: AudioRecord, maxDrainMs: Long = 80L) {
        val chunk = ShortArray(1024)
        val deadline = System.currentTimeMillis() + maxDrainMs
        while (System.currentTimeMillis() < deadline) {
            val read = record.read(chunk, 0, chunk.size, AudioRecord.READ_NON_BLOCKING)
            if (read <= 0) break
        }
    }

    /**
     * Captures exact [durationMs] of mono float samples [-1f, 1f].
     */
    fun captureSamples(
        record: AudioRecord,
        durationMs: Long,
        shouldContinue: () -> Boolean
    ): FloatArray {
        val totalNeeded = ((SAMPLE_RATE * durationMs) / 1000L).toInt().coerceAtLeast(1024)
        val out = FloatArray(totalNeeded)
        val shortChunk = ShortArray(1024)
        var offset = 0
        while (offset < totalNeeded && shouldContinue()) {
            val toRead = minOf(shortChunk.size, totalNeeded - offset)
            val read = record.read(shortChunk, 0, toRead)
            if (read > 0) {
                for (i in 0 until read) {
                    out[offset + i] = shortChunk[i] / 32768.0f
                }
                offset += read
            } else if (read < 0) {
                break
            }
        }
        return if (offset == totalNeeded) out else out.copyOf(offset)
    }

    fun analyzeSnapshot(samples: FloatArray, targetFrequencyHz: Int): CaptureSnapshot {
        if (samples.isEmpty()) {
            return CaptureSnapshot(MIN_DBFS, 0f, false, MIN_DBFS)
        }
        var sumSq = 0.0
        var peak = 0f
        for (s in samples) {
            val a = abs(s)
            if (a > peak) peak = a
            sumSq += s.toDouble() * s.toDouble()
        }
        val rms = sqrt(sumSq / samples.size.toDouble()).toFloat()
        val broadbandDb = amplitudeToDbFs(rms)
        val targetDb = computeGoertzelDbFs(samples, targetFrequencyHz)
        return CaptureSnapshot(
            broadbandRmsDbFs = broadbandDb,
            peakAmplitude = peak,
            clipping = peak >= 0.96f,
            targetFreqDbFs = targetDb
        )
    }

    /**
     * Hann-windowed Goertzel RMS level in dBFS for exact [frequencyHz].
     * Strongly rejects out-of-band room noise (fans, speech, traffic at other frequencies).
     */
    fun computeGoertzelDbFs(samples: FloatArray, frequencyHz: Int): Float {
        val n = samples.size
        if (n < 64) return MIN_DBFS

        val omega = (2.0 * PI * frequencyHz.toDouble()) / SAMPLE_RATE.toDouble()
        val coeff = 2.0 * cos(omega)
        val twoPiOverNm1 = (2.0 * PI) / (n - 1).toDouble()

        var s1 = 0.0
        var s2 = 0.0
        for (i in 0 until n) {
            val hann = 0.5 * (1.0 - cos(twoPiOverNm1 * i))
            val xw = samples[i].toDouble() * hann
            val s0 = xw + coeff * s1 - s2
            s2 = s1
            s1 = s0
        }
        val power = (s1 * s1 + s2 * s2 - coeff * s1 * s2).coerceAtLeast(0.0)
        // With Hann window (coherent sum N/2), sine RMS amplitude is 2*sqrt(2)*sqrt(power)/N
        val rms = (2.0 * sqrt(2.0) * sqrt(power)) / n.toDouble()
        return amplitudeToDbFs(rms.toFloat())
    }

    fun buildPointFromSamples(
        frequencyHz: Int,
        toneSamples: FloatArray,
        silenceBuffer: FloatArray
    ): FrequencyMeasurementPoint {
        val signalDb = computeGoertzelDbFs(toneSamples, frequencyHz)
        val noiseWindow = if (silenceBuffer.size > toneSamples.size) {
            silenceBuffer.copyOf(toneSamples.size)
        } else {
            silenceBuffer
        }
        val noiseDb = if (noiseWindow.isNotEmpty()) {
            computeGoertzelDbFs(noiseWindow, frequencyHz)
        } else {
            MIN_DBFS
        }
        return FrequencyMeasurementPoint(
            frequencyHz = frequencyHz,
            rawDbFs = signalDb,
            noiseFloorDbFs = noiseDb
        )
    }

    private fun amplitudeToDbFs(rms: Float): Float {
        if (rms <= 1e-5f) return MIN_DBFS
        return (20.0 * log10(rms.toDouble())).toFloat().coerceIn(MIN_DBFS, 0f)
    }
}

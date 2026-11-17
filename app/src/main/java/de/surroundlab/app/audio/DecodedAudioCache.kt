package de.surroundlab.app.audio

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.LruCache
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteOrder
import kotlin.math.min

/**
 * Decodes uploaded audio files (.wav, .mp3, .flac, .ogg, etc.) to 48 kHz mono FloatArray
 * using Android MediaExtractor + MediaCodec.
 *
 * Enforces a strict 64 MB LRU RAM budget (AGENTS.md 4.2) for Chromecast / Android TV stability.
 */
class DecodedAudioCache {

    companion object {
        const val TARGET_SAMPLE_RATE = 48000
        // 64 MB budget tracked in KiB (64 * 1024 KiB)
        private const val MAX_CACHE_KIB = 64 * 1024
        // Cap single track decoded length at ~48 MB (12,000,000 mono floats = 250 s @ 48 kHz)
        private const val MAX_SINGLE_TRACK_SAMPLES = 12_000_000
    }

    private val decodeMutex = Mutex()

    private val cache = object : LruCache<String, FloatArray>(MAX_CACHE_KIB) {
        override fun sizeOf(key: String, value: FloatArray): Int {
            return ((value.size.toLong() * 4L) / 1024L).toInt().coerceAtLeast(1)
        }
    }

    fun getIfCached(trackId: String): FloatArray? = cache.get(trackId)

    fun evict(trackId: String) {
        cache.remove(trackId)
    }

    fun clear() {
        cache.evictAll()
    }

    suspend fun decodeAndCache(trackId: String, file: File): Result<FloatArray> =
        withContext(Dispatchers.Default) {
            cache.get(trackId)?.let { return@withContext Result.success(it) }
            val coroutineCtx = currentCoroutineContext()
            decodeMutex.withLock {
                cache.get(trackId)?.let { return@withLock Result.success(it) }
                runCatching {
                    val decoded = decodeFileTo48kMono(file) { !coroutineCtx.isActive }
                    cache.put(trackId, decoded)
                    decoded
                }
            }
        }

    private fun decodeFileTo48kMono(file: File, isCancelled: () -> Boolean): FloatArray {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null
        try {
            extractor.setDataSource(file.absolutePath)
            var audioTrackIndex = -1
            var inputFormat: MediaFormat? = null
            for (i in 0 until extractor.trackCount) {
                val fmt = extractor.getTrackFormat(i)
                val mime = fmt.getString(MediaFormat.KEY_MIME) ?: continue
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i
                    inputFormat = fmt
                    break
                }
            }
            require(audioTrackIndex >= 0 && inputFormat != null) {
                "Keine Audiospur gefunden."
            }

            extractor.selectTrack(audioTrackIndex)
            val mime = inputFormat.getString(MediaFormat.KEY_MIME)!!
            var srcSampleRate = if (inputFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            } else TARGET_SAMPLE_RATE
            var srcChannels = if (inputFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT).coerceAtLeast(1)
            } else 1

            codec = MediaCodec.createDecoderByType(mime)
            codec.configure(inputFormat, null, null, 0)
            codec.start()

            val bufferInfo = MediaCodec.BufferInfo()
            var inputEos = false
            var outputEos = false
            var encodingPcmFloat = false

            val maxSourceFrames = if (srcSampleRate > 0 && srcSampleRate != TARGET_SAMPLE_RATE) {
                min(
                    18_000_000,
                    ((MAX_SINGLE_TRACK_SAMPLES.toDouble() * srcSampleRate) / TARGET_SAMPLE_RATE).toInt()
                        .coerceAtLeast(MAX_SINGLE_TRACK_SAMPLES)
                )
            } else MAX_SINGLE_TRACK_SAMPLES

            val chunks = ArrayList<FloatArray>()
            var totalSrcFrames = 0

            while (!outputEos && totalSrcFrames < maxSourceFrames) {
                if (isCancelled()) throw CancellationException("Decode cancelled")

                var didWork = false
                if (!inputEos) {
                    // Drain up to several input buffers without stalling
                    while (true) {
                        val inIdx = codec.dequeueInputBuffer(0L)
                        if (inIdx < 0) break
                        didWork = true
                        val inBuf = codec.getInputBuffer(inIdx)!!
                        val sampleSize = extractor.readSampleData(inBuf, 0)
                        if (sampleSize < 0) {
                            codec.queueInputBuffer(
                                inIdx,
                                0,
                                0,
                                0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            inputEos = true
                            break
                        } else {
                            codec.queueInputBuffer(inIdx, 0, sampleSize, extractor.sampleTime, 0)
                            extractor.advance()
                        }
                    }
                }

                // Drain all immediately available output buffers per pass
                val outTimeout = if (didWork) 0L else 2_000L
                var outIdx = codec.dequeueOutputBuffer(bufferInfo, outTimeout)
                while (outIdx >= 0 || outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val outFmt = codec.outputFormat
                        if (outFmt.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                            srcSampleRate = outFmt.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                        }
                        if (outFmt.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                            srcChannels = outFmt.getInteger(MediaFormat.KEY_CHANNEL_COUNT).coerceAtLeast(1)
                        }
                        encodingPcmFloat = if (outFmt.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                            outFmt.getInteger(MediaFormat.KEY_PCM_ENCODING) ==
                                android.media.AudioFormat.ENCODING_PCM_FLOAT
                        } else false
                    } else if (outIdx >= 0) {
                        val outBuf = codec.getOutputBuffer(outIdx)
                        if (outBuf != null && bufferInfo.size > 0) {
                            outBuf.position(bufferInfo.offset)
                            outBuf.limit(bufferInfo.offset + bufferInfo.size)
                            outBuf.order(ByteOrder.LITTLE_ENDIAN)

                            val monoChunk = if (encodingPcmFloat) {
                                val floatBuf = outBuf.asFloatBuffer()
                                val floatCount = floatBuf.remaining()
                                val frames = floatCount / srcChannels
                                val mono = FloatArray(frames)
                                val invCh = 1.0f / srcChannels
                                for (f in 0 until frames) {
                                    var sum = 0f
                                    for (c in 0 until srcChannels) {
                                        sum += floatBuf.get()
                                    }
                                    mono[f] = (sum * invCh).coerceIn(-1f, 1f)
                                }
                                mono
                            } else {
                                val shortBuf = outBuf.asShortBuffer()
                                val shortCount = shortBuf.remaining()
                                val frames = shortCount / srcChannels
                                val mono = FloatArray(frames)
                                val scale = 1.0f / (32768.0f * srcChannels)
                                for (f in 0 until frames) {
                                    var sum = 0f
                                    for (c in 0 until srcChannels) {
                                        sum += shortBuf.get().toFloat()
                                    }
                                    mono[f] = (sum * scale).coerceIn(-1f, 1f)
                                }
                                mono
                            }
                            if (monoChunk.isNotEmpty()) {
                                chunks.add(monoChunk)
                                totalSrcFrames += monoChunk.size
                            }
                        }
                        codec.releaseOutputBuffer(outIdx, false)
                        if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            outputEos = true
                            break
                        }
                        if (totalSrcFrames >= maxSourceFrames) break
                    }
                    outIdx = codec.dequeueOutputBuffer(bufferInfo, 0L)
                }
            }

            if (totalSrcFrames == 0) {
                return FloatArray(TARGET_SAMPLE_RATE) // 1s silence fallback if empty
            }

            return if (srcSampleRate == TARGET_SAMPLE_RATE) {
                val outLen = min(MAX_SINGLE_TRACK_SAMPLES, totalSrcFrames)
                val out = FloatArray(outLen)
                var offset = 0
                for (i in chunks.indices) {
                    val chunk = chunks[i]
                    val toCopy = min(chunk.size, outLen - offset)
                    if (toCopy <= 0) break
                    System.arraycopy(chunk, 0, out, offset, toCopy)
                    offset += toCopy
                    chunks[i] = FloatArray(0)
                }
                out
            } else {
                resampleLinearFromChunks(chunks, totalSrcFrames, srcSampleRate, TARGET_SAMPLE_RATE)
            }
        } finally {
            runCatching { codec?.stop() }
            runCatching { codec?.release() }
            runCatching { extractor.release() }
        }
    }

    private fun resampleLinearFromChunks(
        chunks: MutableList<FloatArray>,
        totalSrcFrames: Int,
        srcRate: Int,
        dstRate: Int
    ): FloatArray {
        if (totalSrcFrames <= 1 || srcRate <= 0) return chunks.firstOrNull() ?: FloatArray(0)
        val ratio = srcRate.toDouble() / dstRate.toDouble()
        val outLen = min(
            MAX_SINGLE_TRACK_SAMPLES,
            (totalSrcFrames.toDouble() / ratio).toInt().coerceAtLeast(1)
        )
        val out = FloatArray(outLen)
        val chunkOffsets = IntArray(chunks.size + 1)
        for (i in chunks.indices) {
            chunkOffsets[i + 1] = chunkOffsets[i] + chunks[i].size
        }

        val lastIdx = totalSrcFrames - 1
        var cIdx = 0
        for (i in 0 until outLen) {
            val pos = i * ratio
            val idx0 = pos.toInt().coerceIn(0, lastIdx)
            while (cIdx < chunks.size - 1 && idx0 >= chunkOffsets[cIdx + 1]) {
                cIdx++
            }
            val local0 = idx0 - chunkOffsets[cIdx]
            val curChunk = chunks[cIdx]
            val v0 = curChunk[local0]
            val v1 = when {
                local0 + 1 < curChunk.size -> curChunk[local0 + 1]
                cIdx + 1 < chunks.size && chunks[cIdx + 1].isNotEmpty() -> chunks[cIdx + 1][0]
                else -> v0
            }
            val frac = (pos - idx0).toFloat()
            out[i] = v0 + (v1 - v0) * frac
        }
        chunks.clear()
        return out
    }
}

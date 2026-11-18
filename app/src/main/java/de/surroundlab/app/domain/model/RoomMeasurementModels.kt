package de.surroundlab.app.domain.model

import kotlinx.serialization.Serializable
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Serializable
enum class SweepStepMode {
    LINEAR_HZ,
    OCTAVE_FRACTION
}

@Serializable
enum class DbScaleMode {
    LINEAR_FLAT,
    DBA
}

@Serializable
enum class MeasurementSpeed(
    val settleMs: Long,
    val measureMs: Long,
    val pauseMs: Long
) {
    FAST(settleMs = 220L, measureMs = 280L, pauseMs = 120L),
    STANDARD(settleMs = 280L, measureMs = 420L, pauseMs = 180L),
    PRECISE(settleMs = 360L, measureMs = 600L, pauseMs = 240L)
}

enum class RoomPresetType(
    val startHz: Int,
    val endHz: Int,
    val stepMode: SweepStepMode,
    val stepValue: Int
) {
    ROOM_MODES(startHz = 20, endHz = 200, stepMode = SweepStepMode.LINEAR_HZ, stepValue = 5),
    SUBWOOFER(startHz = 20, endHz = 120, stepMode = SweepStepMode.LINEAR_HZ, stepValue = 5),
    FULL_RANGE(startHz = 20, endHz = 20000, stepMode = SweepStepMode.OCTAVE_FRACTION, stepValue = 6),
    CUSTOM(startHz = 20, endHz = 200, stepMode = SweepStepMode.LINEAR_HZ, stepValue = 10)
}

@Serializable
data class FrequencyMeasurementPoint(
    val frequencyHz: Int,
    val rawDbFs: Float,
    val noiseFloorDbFs: Float
)

@Serializable
data class ListeningPositionTrace(
    val id: String,
    val name: String,
    val colorArgb: Long,
    val points: List<FrequencyMeasurementPoint>,
    val createdAtMs: Long
)

@Serializable
data class RoomMeasurementSession(
    val id: String,
    val name: String,
    val note: String = "",
    val createdAtMs: Long,
    val setupCode: String,
    val speakerPreset: GeneratorPreset,
    val channels: List<String>,
    val startHz: Int,
    val endHz: Int,
    val stepMode: SweepStepMode,
    val stepValue: Int,
    val isRemoteTv: Boolean,
    val positions: List<ListeningPositionTrace>
)

enum class LevelCheckVerdict {
    IDLE,
    CALIBRATING_SILENCE,
    TOO_QUIET,
    OPTIMAL,
    CLIPPING
}

data class RoomLevelCheckState(
    val running: Boolean = false,
    val playingTestTone: Boolean = false,
    val testFrequencyHz: Int = 100,
    val ambientNoiseDbFs: Float = -70f,
    val liveToneDbFs: Float = -70f,
    val snrDb: Float = 0f,
    val clippingDetected: Boolean = false,
    val verdict: LevelCheckVerdict = LevelCheckVerdict.IDLE
)

enum class SweepPhase {
    IDLE,
    COUNTDOWN,
    MEASURING_SILENCE,
    SWEEPING,
    COMPLETED,
    CANCELLED,
    ERROR
}

data class MeasurementRunProgress(
    val phase: SweepPhase = SweepPhase.IDLE,
    val countdownSeconds: Int = 0,
    val currentStepIndex: Int = 0,
    val totalSteps: Int = 0,
    val currentFrequencyHz: Int = 0,
    val livePoints: List<FrequencyMeasurementPoint> = emptyList(),
    val statusMessage: String? = null
)

object RoomGraphUtils {
    val TRACE_COLORS: List<Long> = listOf(
        0xFF4DDAD5L, // Teal / Primary
        0xFFFFB77BL, // Tangerine / Secondary
        0xFFADC6FFL, // Periwinkle / Tertiary
        0xFF7ADB94L, // Mint / Success
        0xFFF28B82L, // Coral
        0xFFE8C547L, // Warm Gold
        0xFFD0BCFFL, // Lavender
        0xFFFF9EC7L  // Rose
    )

    val PDF_TRACE_COLORS: List<Int> = listOf(
        0xFF00796B.toInt(), // Deep Teal
        0xFFD84315.toInt(), // Burnt Orange
        0xFF1565C0.toInt(), // Cobalt Blue
        0xFF2E7D32.toInt(), // Forest Green
        0xFFC62828.toInt(), // Crimson
        0xFF8E24AA.toInt(), // Purple
        0xFFF9A825.toInt(), // Amber
        0xFF37474F.toInt()  // Slate
    )

    fun colorForIndex(index: Int): Long = TRACE_COLORS[index % TRACE_COLORS.size]

    fun pdfColorForIndex(index: Int): Int = PDF_TRACE_COLORS[index % PDF_TRACE_COLORS.size]

    /**
     * Generates sorted unique measurement frequencies between [startHz] and [endHz].
     */
    fun buildSweepFrequencies(
        startHz: Int,
        endHz: Int,
        stepMode: SweepStepMode,
        stepValue: Int
    ): List<Int> {
        val lo = startHz.coerceIn(20, 20000)
        val hi = endHz.coerceIn(lo, 20000)
        if (lo == hi) return listOf(lo)

        val out = LinkedHashSet<Int>()
        when (stepMode) {
            SweepStepMode.LINEAR_HZ -> {
                val step = stepValue.coerceIn(1, 5000)
                var f = lo
                while (f < hi && out.size < 450) {
                    out.add(f)
                    f += step
                }
                out.add(hi)
            }
            SweepStepMode.OCTAVE_FRACTION -> {
                val fraction = stepValue.coerceIn(1, 24)
                val ratio = 2.0.pow(1.0 / fraction.toDouble())
                var f = lo.toDouble()
                while (f < hi.toDouble() && out.size < 450) {
                    val rounded = f.roundToInt().coerceIn(lo, hi)
                    out.add(rounded)
                    val next = f * ratio
                    f = if (next.roundToInt() <= rounded) (rounded + 1).toDouble() else next
                }
                out.add(hi)
            }
        }
        return out.toList().sorted()
    }

    /**
     * Standard IEC 61672 A-weighting curve offset in dB at [freqHz].
     */
    fun aWeightingOffsetDb(freqHz: Int): Float {
        val f = freqHz.coerceAtLeast(10).toDouble()
        val f2 = f * f
        val c1 = 12194.0 * 12194.0
        val c2 = 20.6 * 20.6
        val c3 = 107.7 * 107.7
        val c4 = 737.9 * 737.9
        val num = c1 * f2 * f2
        val den = (f2 + c2) * sqrt((f2 + c3) * (f2 + c4)) * (f2 + c1)
        val ra = (num / den).coerceAtLeast(1e-9)
        return (20.0 * log10(ra) + 2.0).toFloat()
    }

    fun valueForScale(point: FrequencyMeasurementPoint, scaleMode: DbScaleMode): Float =
        when (scaleMode) {
            DbScaleMode.LINEAR_FLAT -> point.rawDbFs
            DbScaleMode.DBA -> point.rawDbFs + aWeightingOffsetDb(point.frequencyHz)
        }

    fun shouldUseLogXAxis(startHz: Int, endHz: Int, stepMode: SweepStepMode): Boolean =
        stepMode == SweepStepMode.OCTAVE_FRACTION || endHz > 1000 || (endHz.toFloat() / startHz.coerceAtLeast(1)) > 15f

    fun normalizeX(freqHz: Int, minHz: Int, maxHz: Int, useLogX: Boolean): Float {
        val lo = minHz.coerceAtLeast(1).toFloat()
        val hi = maxHz.coerceAtLeast(minHz + 1).toFloat()
        val f = freqHz.toFloat().coerceIn(lo, hi)
        return if (useLogX) {
            (ln(f / lo) / ln(hi / lo)).coerceIn(0f, 1f)
        } else {
            ((f - lo) / (hi - lo)).coerceIn(0f, 1f)
        }
    }

    /**
     * Computes the arithmetic dB average curve across multiple listening positions.
     */
    fun computeMeanCurve(
        positions: List<ListeningPositionTrace>,
        scaleMode: DbScaleMode
    ): List<Pair<Int, Float>> {
        if (positions.size < 2) return emptyList()
        val byFreq = sortedMapOf<Int, MutableList<Float>>()
        for (pos in positions) {
            for (pt in pos.points) {
                byFreq.getOrPut(pt.frequencyHz) { mutableListOf() }
                    .add(valueForScale(pt, scaleMode))
            }
        }
        return byFreq.entries.map { (hz, vals) ->
            hz to (vals.sum() / vals.size.toFloat())
        }
    }

    /**
     * Gentle 3-point weighted smoothing (0.25, 0.50, 0.25) preserving endpoints.
     */
    fun smoothCurve(points: List<Pair<Int, Float>>): List<Pair<Int, Float>> {
        if (points.size < 3) return points
        return points.mapIndexed { idx, (hz, v) ->
            when (idx) {
                0, points.lastIndex -> hz to v
                else -> {
                    val smoothed = 0.25f * points[idx - 1].second + 0.50f * v + 0.25f * points[idx + 1].second
                    hz to smoothed
                }
            }
        }
    }

    fun formatFreqShort(hz: Int): String = when {
        hz >= 1000 && hz % 1000 == 0 -> "${hz / 1000}k"
        hz >= 1000 -> String.format(java.util.Locale.US, "%.1fk", hz / 1000f)
        else -> "$hz"
    }
}

package de.surroundlab.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.DbScaleMode
import de.surroundlab.app.domain.model.RoomGraphUtils
import de.surroundlab.app.domain.model.SweepStepMode
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

data class GraphSeries(
    val id: String,
    val label: String,
    val color: Color,
    val points: List<Pair<Int, Float>>,
    val dashed: Boolean = false
)

@Composable
fun RoomFrequencyGraph(
    series: List<GraphSeries>,
    startHz: Int,
    endHz: Int,
    stepMode: SweepStepMode,
    scaleMode: DbScaleMode,
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    interactive: Boolean = true
) {
    val textMeasurer = rememberTextMeasurer()
    val useLogX = remember(startHz, endHz, stepMode) {
        RoomGraphUtils.shouldUseLogXAxis(startHz, endHz, stepMode)
    }

    val allDbValues = remember(series) { series.flatMap { s -> s.points.map { it.second } } }
    val yMin = remember(allDbValues) {
        val rawMin = allDbValues.minOrNull() ?: -65f
        (floor((rawMin - 5f) / 10f) * 10f).coerceAtLeast(-95f)
    }
    val yMax = remember(allDbValues, yMin) {
        val rawMax = allDbValues.maxOrNull() ?: -15f
        (ceil((rawMax + 5f) / 10f) * 10f).coerceAtLeast(yMin + 20f).coerceAtMost(10f)
    }
    val ySpan = (yMax - yMin).coerceAtLeast(10f)

    var touchNormX by remember(series, startHz, endHz) { mutableStateOf<Float?>(null) }

    val inspectedFreq = remember(touchNormX, series, startHz, endHz, useLogX) {
        val nx = touchNormX ?: return@remember null
        val allFreqs = series.flatMap { s -> s.points.map { it.first } }.distinct().sorted()
        if (allFreqs.isEmpty()) return@remember null
        allFreqs.minByOrNull { f ->
            abs(RoomGraphUtils.normalizeX(f, startHz, endHz, useLogX) - nx)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurroundColors.SurfaceContainer)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (inspectedFreq != null) {
            val hits = series.mapNotNull { s ->
                val pt = s.points.minByOrNull { abs(it.first - inspectedFreq) }
                if (pt != null && abs(pt.first - inspectedFreq) <= maxOf(5, inspectedFreq / 10)) {
                    Triple(s.label, s.color, pt.second)
                } else null
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurroundColors.SurfaceContainerHigh)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$inspectedFreq Hz (${if (scaleMode == DbScaleMode.DBA) "dBA" else "dB"})",
                    fontFamily = MonoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.Primary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    hits.take(3).forEach { (_, col, db) ->
                        Text(
                            text = "${db.roundToInt()} dB",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = col
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val pointerModifier = if (interactive) {
                Modifier.pointerInput(startHz, endHz, useLogX) {
                    detectTapGestures { offset ->
                        val gLeft = 36.dp.toPx()
                        val gRight = size.width - 8.dp.toPx()
                        val nx = ((offset.x - gLeft) / (gRight - gLeft).coerceAtLeast(1f)).coerceIn(0f, 1f)
                        touchNormX = if (touchNormX != null && abs(touchNormX!! - nx) < 0.03f) null else nx
                    }
                }.pointerInput(startHz, endHz, useLogX) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            change.consume()
                            val gLeft = 36.dp.toPx()
                            val gRight = size.width - 8.dp.toPx()
                            touchNormX = ((change.position.x - gLeft) / (gRight - gLeft).coerceAtLeast(1f)).coerceIn(0f, 1f)
                        }
                    )
                }
            } else Modifier

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .then(pointerModifier)
            ) {
                val gLeft = 36.dp.toPx()
                val gRight = size.width - 8.dp.toPx()
                val gTop = 8.dp.toPx()
                val gBottom = size.height - 22.dp.toPx()
                val gWidth = (gRight - gLeft).coerceAtLeast(10f)
                val gHeight = (gBottom - gTop).coerceAtLeast(10f)

                val gridColor = SurroundColors.Outline.copy(alpha = 0.35f)
                val labelStyle = TextStyle(
                    color = SurroundColors.OnSurfaceVariant,
                    fontSize = 9.sp,
                    fontFamily = MonoFontFamily
                )

                // Horizontal dB grid
                val stepDb = if (ySpan <= 40f) 5 else 10
                var tickDb = yMin.toInt()
                while (tickDb <= yMax.toInt()) {
                    val ny = (tickDb - yMin) / ySpan
                    val py = gBottom - ny * gHeight
                    drawLine(
                        color = gridColor,
                        start = Offset(gLeft, py),
                        end = Offset(gRight, py),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawAxisText(
                        measurer = textMeasurer,
                        text = "$tickDb",
                        style = labelStyle,
                        x = gLeft - 4.dp.toPx(),
                        y = py,
                        alignRight = true,
                        centerY = true
                    )
                    tickDb += stepDb
                }

                // Vertical frequency grid
                val xTicks = buildCanvasFreqTicks(startHz, endHz, useLogX)
                for (hz in xTicks) {
                    val nx = RoomGraphUtils.normalizeX(hz, startHz, endHz, useLogX)
                    val px = gLeft + nx * gWidth
                    drawLine(
                        color = gridColor,
                        start = Offset(px, gTop),
                        end = Offset(px, gBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawAxisText(
                        measurer = textMeasurer,
                        text = RoomGraphUtils.formatFreqShort(hz),
                        style = labelStyle,
                        x = px,
                        y = gBottom + 4.dp.toPx(),
                        alignCenterX = true
                    )
                }

                // Curves
                for (s in series) {
                    if (s.points.isEmpty()) continue
                    if (s.points.size == 1) {
                        val (hz, db) = s.points.first()
                        val nx = RoomGraphUtils.normalizeX(hz, startHz, endHz, useLogX)
                        val ny = ((db - yMin) / ySpan).coerceIn(0f, 1f)
                        drawCircle(
                            color = s.color,
                            radius = 4.dp.toPx(),
                            center = Offset(gLeft + nx * gWidth, gBottom - ny * gHeight)
                        )
                        continue
                    }
                    val path = Path()
                    s.points.forEachIndexed { idx, (hz, db) ->
                        val nx = RoomGraphUtils.normalizeX(hz, startHz, endHz, useLogX)
                        val ny = ((db - yMin) / ySpan).coerceIn(0f, 1f)
                        val px = gLeft + nx * gWidth
                        val py = gBottom - ny * gHeight
                        if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(
                        path = path,
                        color = s.color,
                        style = Stroke(
                            width = if (s.dashed) 2.4.dp.toPx() else 2.2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = if (s.dashed) {
                                PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            } else null
                        )
                    )
                }

                // Touch crosshair
                if (inspectedFreq != null) {
                    val nx = RoomGraphUtils.normalizeX(inspectedFreq, startHz, endHz, useLogX)
                    val px = gLeft + nx * gWidth
                    drawLine(
                        color = SurroundColors.Primary.copy(alpha = 0.7f),
                        start = Offset(px, gTop),
                        end = Offset(px, gBottom),
                        strokeWidth = 1.4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAxisText(
    measurer: TextMeasurer,
    text: String,
    style: TextStyle,
    x: Float,
    y: Float,
    alignRight: Boolean = false,
    alignCenterX: Boolean = false,
    centerY: Boolean = false
) {
    val result = measurer.measure(text, style)
    val drawX = when {
        alignRight -> x - result.size.width
        alignCenterX -> (x - result.size.width / 2f).coerceIn(0f, size.width - result.size.width)
        else -> x
    }
    val drawY = if (centerY) y - result.size.height / 2f else y
    drawText(
        textLayoutResult = result,
        topLeft = Offset(drawX, drawY)
    )
}

private fun buildCanvasFreqTicks(startHz: Int, endHz: Int, useLog: Boolean): List<Int> {
    if (useLog) {
        val candidates = listOf(20, 40, 80, 150, 300, 1000, 3000, 10000, 20000)
        val inside = candidates.filter { it in startHz..endHz }.toMutableList()
        if (inside.isEmpty() || inside.first() != startHz) inside.add(0, startHz)
        if (inside.last() != endHz) inside.add(endHz)
        return inside.distinct()
    }
    val span = (endHz - startHz).coerceAtLeast(10)
    val step = when {
        span <= 100 -> 20
        span <= 250 -> 40
        span <= 500 -> 100
        else -> 200
    }
    val ticks = mutableListOf(startHz)
    var t = ((startHz / step) + 1) * step
    while (t < endHz - step / 3) {
        ticks.add(t)
        t += step
    }
    ticks.add(endHz)
    return ticks.distinct()
}

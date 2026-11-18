package de.surroundlab.app.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import de.surroundlab.app.domain.model.DbScaleMode
import de.surroundlab.app.domain.model.RoomGraphUtils
import de.surroundlab.app.domain.model.RoomMeasurementSession
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Exports one or more [RoomMeasurementSession] records into a clean DIN A4 PDF report
 * (3 compact full-width frequency response charts per page) via Android's PdfDocument + FileProvider.
 */
object RoomPdfExporter {

    private const val A4_SHORT = 595   // DIN A4 at 72 pt/in
    private const val A4_LONG = 842
    private const val MARGIN_X = 32f
    private const val TOP_HEADER_H = 54f
    private const val FOOTER_H = 26f
    private const val SESSIONS_PER_PAGE_PORTRAIT = 3

    data class ExportLabels(
        val reportTitle: String,
        val generatedPrefix: String,
        val pageLabel: (page: Int, total: Int) -> String,
        val scaleLabel: String,
        val channelsPrefix: String,
        val meanCurveLabel: String
    )

    fun exportToPdfUri(
        context: Context,
        sessions: List<RoomMeasurementSession>,
        scaleMode: DbScaleMode,
        showMeanCurve: Boolean,
        smoothCurves: Boolean,
        landscapeSinglePerPage: Boolean = false,
        labels: ExportLabels
    ): Result<Uri> = runCatching {
        require(sessions.isNotEmpty()) { "No sessions selected for export." }

        val pageWidth = if (landscapeSinglePerPage) A4_LONG else A4_SHORT
        val pageHeight = if (landscapeSinglePerPage) A4_SHORT else A4_LONG
        val perPage = if (landscapeSinglePerPage) 1 else SESSIONS_PER_PAGE_PORTRAIT

        val pdf = PdfDocument()
        val totalPages = ceil(sessions.size.toDouble() / perPage).toInt().coerceAtLeast(1)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val nowStr = dateFormat.format(Date())

        val bodyHeight = pageHeight - TOP_HEADER_H - FOOTER_H - 12f
        val cardGap = 12f
        val cardHeight = (bodyHeight - cardGap * (perPage - 1)) / perPage

        try {
            for (pageIndex in 0 until totalPages) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
                val page = pdf.startPage(pageInfo)
                val canvas = page.canvas

                canvas.drawColor(Color.WHITE)
                drawPageHeader(canvas, pageWidth, nowStr, labels)

                val from = pageIndex * perPage
                val to = minOf(sessions.size, from + perPage)
                for (i in from until to) {
                    val slot = i - from
                    val topY = TOP_HEADER_H + slot * (cardHeight + cardGap)
                    drawSessionCard(
                        canvas = canvas,
                        session = sessions[i],
                        left = MARGIN_X,
                        top = topY,
                        width = pageWidth - 2 * MARGIN_X,
                        height = cardHeight,
                        scaleMode = scaleMode,
                        showMeanCurve = showMeanCurve,
                        smoothCurves = smoothCurves,
                        dateFormat = dateFormat,
                        labels = labels
                    )
                }

                drawPageFooter(canvas, pageWidth, pageHeight, pageIndex + 1, totalPages, labels)
                pdf.finishPage(page)
            }

            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            // Keep cache tidy by deleting old temporary PDFs
            exportDir.listFiles()?.forEach { old ->
                if (System.currentTimeMillis() - old.lastModified() > 24 * 60 * 60 * 1000L) {
                    old.delete()
                }
            }
            val fileStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val pdfFile = File(exportDir, "SurroundLab_RoomReport_$fileStamp.pdf")
            FileOutputStream(pdfFile).use { fos ->
                pdf.writeTo(fos)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        } finally {
            pdf.close()
        }
    }

    private fun drawPageHeader(canvas: Canvas, pageWidth: Int, nowStr: String, labels: ExportLabels) {
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF004F4F.toInt()
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4A5554.toInt()
            textSize = 9.5f
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFD0D8D7.toInt()
            strokeWidth = 1f
        }
        canvas.drawText(labels.reportTitle, MARGIN_X, 26f, titlePaint)
        canvas.drawText("${labels.generatedPrefix}: $nowStr  •  ${labels.scaleLabel}", MARGIN_X, 40f, subPaint)
        canvas.drawLine(MARGIN_X, 47f, pageWidth - MARGIN_X, 47f, linePaint)
    }

    private fun drawPageFooter(
        canvas: Canvas,
        pageWidth: Int,
        pageHeight: Int,
        pageNum: Int,
        totalPages: Int,
        labels: ExportLabels
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF60706F.toInt()
            textSize = 8.5f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(
            labels.pageLabel(pageNum, totalPages),
            pageWidth - MARGIN_X,
            pageHeight - 12f,
            paint
        )
    }

    private fun drawSessionCard(
        canvas: Canvas,
        session: RoomMeasurementSession,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        scaleMode: DbScaleMode,
        showMeanCurve: Boolean,
        smoothCurves: Boolean,
        dateFormat: SimpleDateFormat,
        labels: ExportLabels
    ) {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFD5DFDE.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFAFDFC.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(left, top, left + width, top + height, 8f, 8f, bgPaint)
        canvas.drawRoundRect(left, top, left + width, top + height, 8f, 8f, borderPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF111717.toInt()
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4A5554.toInt()
            textSize = 8.5f
        }
        val dateStr = dateFormat.format(Date(session.createdAtMs))
        val stepText = when (session.stepMode) {
            de.surroundlab.app.domain.model.SweepStepMode.LINEAR_HZ -> "${session.stepValue} Hz"
            de.surroundlab.app.domain.model.SweepStepMode.OCTAVE_FRACTION -> "1/${session.stepValue} Oct"
        }
        val channelSummary = session.channels.joinToString("+")
        val headerRight = "$dateStr  •  ${session.setupCode} ($channelSummary)  •  ${session.startHz}–${session.endHz} Hz ($stepText)"

        canvas.drawText(session.name.take(46), left + 10f, top + 16f, titlePaint)
        val rightPaint = Paint(metaPaint).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText(headerRight, left + width - 10f, top + 16f, rightPaint)

        var graphTopOffset = 24f
        if (session.note.isNotBlank()) {
            canvas.drawText(session.note.take(90), left + 10f, top + 27f, metaPaint)
            graphTopOffset = 34f
        }

        val legendHeight = 22f
        val graphLeft = left + 36f
        val graphRight = left + width - 12f
        val graphTop = top + graphTopOffset
        val graphBottom = top + height - legendHeight - 14f

        drawFrequencyGraph(
            canvas = canvas,
            session = session,
            gLeft = graphLeft,
            gTop = graphTop,
            gRight = graphRight,
            gBottom = graphBottom,
            scaleMode = scaleMode,
            showMeanCurve = showMeanCurve,
            smoothCurves = smoothCurves
        )

        // Legend row at bottom of card
        val legendY = top + height - 9f
        var cursorX = left + 10f
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val legendTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF1E2625.toInt()
            textSize = 8f
        }
        session.positions.forEachIndexed { idx, pos ->
            val col = RoomGraphUtils.pdfColorForIndex(idx)
            dotPaint.color = col
            canvas.drawCircle(cursorX + 3.5f, legendY - 2.5f, 3.5f, dotPaint)
            val label = pos.name.take(20)
            canvas.drawText(label, cursorX + 10f, legendY, legendTextPaint)
            cursorX += 14f + legendTextPaint.measureText(label) + 10f
            if (cursorX > left + width - 110f) return@forEachIndexed
        }
        if (showMeanCurve && session.positions.size >= 2 && cursorX < left + width - 70f) {
            val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFF111717.toInt()
                strokeWidth = 1.6f
                pathEffect = DashPathEffect(floatArrayOf(4f, 3f), 0f)
            }
            canvas.drawLine(cursorX, legendY - 2.5f, cursorX + 12f, legendY - 2.5f, dashPaint)
            canvas.drawText(labels.meanCurveLabel, cursorX + 15f, legendY, legendTextPaint)
        }
    }

    private fun drawFrequencyGraph(
        canvas: Canvas,
        session: RoomMeasurementSession,
        gLeft: Float,
        gTop: Float,
        gRight: Float,
        gBottom: Float,
        scaleMode: DbScaleMode,
        showMeanCurve: Boolean,
        smoothCurves: Boolean
    ) {
        val gWidth = (gRight - gLeft).coerceAtLeast(10f)
        val gHeight = (gBottom - gTop).coerceAtLeast(10f)

        val allSeries = session.positions.map { pos ->
            val raw = pos.points.map { it.frequencyHz to RoomGraphUtils.valueForScale(it, scaleMode) }
            if (smoothCurves) RoomGraphUtils.smoothCurve(raw) else raw
        }
        val allVals = allSeries.flatten().map { it.second }
        val minVal = allVals.minOrNull() ?: -60f
        val maxVal = allVals.maxOrNull() ?: -10f
        val yMin = (floor((minVal - 4f) / 10f) * 10f).coerceAtLeast(-90f)
        val yMax = (ceil((maxVal + 4f) / 10f) * 10f).coerceAtLeast(yMin + 20f).coerceAtMost(10f)
        val ySpan = (yMax - yMin).coerceAtLeast(10f)

        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE3EAEA.toInt()
            strokeWidth = 0.7f
        }
        val axisLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF5A6665.toInt()
            textSize = 7.5f
        }

        // Horizontal dB grid lines
        val stepDb = if (ySpan <= 40f) 5 else 10
        var dbTick = yMin.toInt()
        val yLabelPaint = Paint(axisLabelPaint).apply { textAlign = Paint.Align.RIGHT }
        while (dbTick <= yMax.toInt()) {
            val normY = (dbTick - yMin) / ySpan
            val py = gBottom - normY * gHeight
            canvas.drawLine(gLeft, py, gRight, py, gridPaint)
            canvas.drawText("${dbTick}dB", gLeft - 4f, py + 2.5f, yLabelPaint)
            dbTick += stepDb
        }

        // Vertical frequency grid lines
        val useLog = RoomGraphUtils.shouldUseLogXAxis(session.startHz, session.endHz, session.stepMode)
        val xTicks = buildPdfFreqTicks(session.startHz, session.endHz, useLog)
        val xLabelPaint = Paint(axisLabelPaint).apply { textAlign = Paint.Align.CENTER }
        for (hz in xTicks) {
            val nx = RoomGraphUtils.normalizeX(hz, session.startHz, session.endHz, useLog)
            val px = gLeft + nx * gWidth
            canvas.drawLine(px, gTop, px, gBottom, gridPaint)
            canvas.drawText(RoomGraphUtils.formatFreqShort(hz), px, gBottom + 9f, xLabelPaint)
        }

        // Position polylines
        allSeries.forEachIndexed { idx, pts ->
            if (pts.isEmpty()) return@forEachIndexed
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = RoomGraphUtils.pdfColorForIndex(idx)
                style = Paint.Style.STROKE
                strokeWidth = 1.6f
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            val path = Path()
            pts.forEachIndexed { pIdx, (hz, db) ->
                val nx = RoomGraphUtils.normalizeX(hz, session.startHz, session.endHz, useLog)
                val ny = ((db - yMin) / ySpan).coerceIn(0f, 1f)
                val px = gLeft + nx * gWidth
                val py = gBottom - ny * gHeight
                if (pIdx == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            canvas.drawPath(path, linePaint)
        }

        // Spatial average dashed overlay
        if (showMeanCurve && session.positions.size >= 2) {
            val rawMean = RoomGraphUtils.computeMeanCurve(session.positions, scaleMode)
            val meanPts = if (smoothCurves) RoomGraphUtils.smoothCurve(rawMean) else rawMean
            if (meanPts.isNotEmpty()) {
                val meanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF111717.toInt()
                    style = Paint.Style.STROKE
                    strokeWidth = 1.9f
                    pathEffect = DashPathEffect(floatArrayOf(5f, 3f), 0f)
                }
                val path = Path()
                meanPts.forEachIndexed { pIdx, (hz, db) ->
                    val nx = RoomGraphUtils.normalizeX(hz, session.startHz, session.endHz, useLog)
                    val ny = ((db - yMin) / ySpan).coerceIn(0f, 1f)
                    val px = gLeft + nx * gWidth
                    val py = gBottom - ny * gHeight
                    if (pIdx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                canvas.drawPath(path, meanPaint)
            }
        }
    }

    private fun buildPdfFreqTicks(startHz: Int, endHz: Int, useLog: Boolean): List<Int> {
        if (useLog) {
            val candidates = listOf(20, 30, 50, 80, 100, 200, 500, 1000, 2000, 5000, 10000, 20000)
            val inside = candidates.filter { it in startHz..endHz }.toMutableList()
            if (inside.isEmpty() || inside.first() != startHz) inside.add(0, startHz)
            if (inside.last() != endHz) inside.add(endHz)
            return inside.distinct()
        }
        val span = (endHz - startHz).coerceAtLeast(10)
        val step = when {
            span <= 100 -> 20
            span <= 250 -> 30
            span <= 500 -> 50
            else -> 100
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
}

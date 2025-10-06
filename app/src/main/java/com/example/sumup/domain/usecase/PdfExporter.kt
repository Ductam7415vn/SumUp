package com.example.sumup.domain.usecase

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import com.example.sumup.domain.model.Summary
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Exports summary to PDF format using Android's PdfDocument API
 * Implements AC-006.4: PDF Export
 */
@Singleton
class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Page dimensions (A4 size in points: 1 inch = 72 points)
        private const val PAGE_WIDTH = 595  // 8.27 inches * 72
        private const val PAGE_HEIGHT = 842 // 11.69 inches * 72

        // Margins
        private const val MARGIN_LEFT = 50f
        private const val MARGIN_RIGHT = 50f
        private const val MARGIN_TOP = 50f
        private const val MARGIN_BOTTOM = 50f

        // Text sizes
        private const val TITLE_SIZE = 24f
        private const val HEADING_SIZE = 18f
        private const val BODY_SIZE = 12f
        private const val CAPTION_SIZE = 10f

        // Line spacing
        private const val LINE_SPACING = 1.5f
    }

    /**
     * Export summary to PDF file
     * AC-006.4: PDF generation completes < 3s
     * AC-006.4: File saved to Downloads
     * AC-006.5: PDF < 1MB
     */
    fun export(summary: Summary, file: File) {
        val startTime = System.currentTimeMillis()

        val document = PdfDocument()
        var currentPage = 1
        var yPosition = MARGIN_TOP
        val timeSaved = summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime

        try {
            // Create first page
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPage).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            // Title
            yPosition = drawTitle(canvas, yPosition, "Summary Report")
            yPosition += 20f

            // Metadata section
            yPosition = drawHeading(canvas, yPosition, "Metadata")
            yPosition += 10f

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date(summary.createdAt))

            yPosition = drawBody(canvas, yPosition, "Generated: $timestamp")
            yPosition = drawBody(canvas, yPosition, "Persona: ${summary.persona.displayName}")
            yPosition += 20f

            // Metrics section
            yPosition = drawHeading(canvas, yPosition, "Metrics")
            yPosition += 10f
            yPosition = drawBody(canvas, yPosition, "Original Words: ${summary.metrics.originalWordCount}")
            yPosition = drawBody(canvas, yPosition, "Summary Words: ${summary.metrics.summaryWordCount}")
            yPosition = drawBody(canvas, yPosition, "Reduction: ${summary.metrics.reductionPercentage}%")
            yPosition = drawBody(canvas, yPosition, "Time Saved: $timeSaved minutes")
            yPosition += 20f

            // Check if we need a new page
            if (yPosition > PAGE_HEIGHT - MARGIN_BOTTOM - 100) {
                document.finishPage(page)
                currentPage++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPage).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                yPosition = MARGIN_TOP
            }

            // Summary section
            yPosition = drawHeading(canvas, yPosition, "Summary")
            yPosition += 10f

            // Draw summary text with word wrapping
            val summaryLines = wrapText(summary.summaryText, PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, BODY_SIZE)
            for (line in summaryLines) {
                // Check if we need a new page
                if (yPosition > PAGE_HEIGHT - MARGIN_BOTTOM - 50) {
                    document.finishPage(page)
                    currentPage++
                    val newPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPage).create()
                    page = document.startPage(newPageInfo)
                    canvas = page.canvas
                    yPosition = MARGIN_TOP
                }

                yPosition = drawBody(canvas, yPosition, line)
            }

            // Footer
            yPosition = max(yPosition, PAGE_HEIGHT - MARGIN_BOTTOM - 20)
            drawFooter(canvas, PAGE_HEIGHT - 30f)

            // Finish the last page
            document.finishPage(page)

            // Write to file
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }

            val duration = System.currentTimeMillis() - startTime
            android.util.Log.d("PdfExporter", "PDF generated in ${duration}ms, size: ${file.length()} bytes")

            // AC-006.4: Verify PDF < 1MB
            if (file.length() > 1024 * 1024) {
                android.util.Log.w("PdfExporter", "PDF size exceeds 1MB: ${file.length()} bytes")
            }

        } finally {
            document.close()
        }
    }

    private fun drawTitle(canvas: android.graphics.Canvas, y: Float, text: String): Float {
        val paint = TextPaint().apply {
            textSize = TITLE_SIZE
            color = android.graphics.Color.BLACK
            isFakeBoldText = true
        }
        canvas.drawText(text, MARGIN_LEFT, y + TITLE_SIZE, paint)
        return y + TITLE_SIZE + 10f
    }

    private fun drawHeading(canvas: android.graphics.Canvas, y: Float, text: String): Float {
        val paint = TextPaint().apply {
            textSize = HEADING_SIZE
            color = android.graphics.Color.BLACK
            isFakeBoldText = true
        }
        canvas.drawText(text, MARGIN_LEFT, y + HEADING_SIZE, paint)
        return y + HEADING_SIZE + 5f
    }

    private fun drawBody(canvas: android.graphics.Canvas, y: Float, text: String): Float {
        val paint = TextPaint().apply {
            textSize = BODY_SIZE
            color = android.graphics.Color.DKGRAY
        }
        canvas.drawText(text, MARGIN_LEFT, y + BODY_SIZE, paint)
        return y + BODY_SIZE * LINE_SPACING
    }

    private fun drawFooter(canvas: android.graphics.Canvas, y: Float) {
        val paint = TextPaint().apply {
            textSize = CAPTION_SIZE
            color = android.graphics.Color.GRAY
            textAlign = Paint.Align.CENTER
        }
        val footerText = "Generated with SumUp - AI Text Summarization"
        canvas.drawText(footerText, PAGE_WIDTH / 2f, y, paint)
    }

    /**
     * Wrap text to fit within specified width
     */
    private fun wrapText(text: String, maxWidth: Float, textSize: Float): List<String> {
        val paint = TextPaint().apply {
            this.textSize = textSize
        }

        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) {
                word
            } else {
                "$currentLine $word"
            }

            val width = paint.measureText(testLine)
            if (width > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                if (currentLine.isNotEmpty()) {
                    currentLine.append(" ")
                }
                currentLine.append(word)
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }

        return lines
    }
}

package com.example.sumup.presentation.screens.result.export

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service to export summaries to various formats
 */
class SummaryExportService(
    private val context: Context
) {
    companion object {
        private const val PDF_PAGE_WIDTH = 595 // A4 width in points
        private const val PDF_PAGE_HEIGHT = 842 // A4 height in points
        private const val MARGIN = 50f
        private const val LINE_SPACING = 1.5f
        private const val IMAGE_WIDTH = 1080
        private const val IMAGE_PADDING = 60
    }
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    /**
     * Export summary to PDF format
     */
    suspend fun exportToPdf(
        summary: Summary,
        persona: SummaryPersona
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(
                PDF_PAGE_WIDTH,
                PDF_PAGE_HEIGHT,
                1
            ).create()
            
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            // Draw content
            drawPdfContent(canvas, summary, persona)
            
            pdfDocument.finishPage(page)
            
            // Save to file
            val fileName = "summary_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            
            pdfDocument.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export summary to image format
     */
    suspend fun exportToImage(
        summary: Summary,
        persona: SummaryPersona
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Calculate required height
            val textHeight = calculateTextHeight(summary)
            val imageHeight = textHeight + (IMAGE_PADDING * 2)
            
            // Create bitmap
            val bitmap = Bitmap.createBitmap(
                IMAGE_WIDTH,
                imageHeight,
                Bitmap.Config.ARGB_8888
            )
            
            val canvas = Canvas(bitmap)
            
            // Draw content
            drawImageContent(canvas, summary, persona)
            
            // Save to file
            val fileName = "summary_${System.currentTimeMillis()}.png"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
            
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            
            bitmap.recycle()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun drawPdfContent(
        canvas: Canvas,
        summary: Summary,
        persona: SummaryPersona
    ) {
        var yPosition = MARGIN
        
        // Title paint
        val titlePaint = Paint().apply {
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }
        
        // Header paint
        val headerPaint = Paint().apply {
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }
        
        // Body paint
        val bodyPaint = Paint().apply {
            textSize = 12f
            color = Color.BLACK
        }
        
        // Meta paint
        val metaPaint = Paint().apply {
            textSize = 10f
            color = Color.GRAY
        }
        
        // Draw header
        canvas.drawText("AI Summary Report", MARGIN, yPosition, titlePaint)
        yPosition += 40f
        
        // Draw metadata
        canvas.drawText(
            "Generated: ${dateFormatter.format(Date(summary.createdAt))}",
            MARGIN,
            yPosition,
            metaPaint
        )
        yPosition += 20f
        
        canvas.drawText(
            "Persona: ${persona.displayName}",
            MARGIN,
            yPosition,
            metaPaint
        )
        yPosition += 30f
        
        // Draw metrics
        canvas.drawText("Summary Metrics", MARGIN, yPosition, headerPaint)
        yPosition += 25f
        
        val metrics = listOf(
            "Original: ${summary.metrics.originalWordCount} words",
            "Summary: ${summary.metrics.summaryWordCount} words",
            "Reduction: ${summary.metrics.reductionPercentage}%",
            "Time saved: ${summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime} minutes"
        )
        
        metrics.forEach { metric ->
            canvas.drawText(metric, MARGIN + 20f, yPosition, bodyPaint)
            yPosition += 20f
        }
        yPosition += 20f
        
        // Draw summary
        canvas.drawText("Summary", MARGIN, yPosition, headerPaint)
        yPosition += 25f
        
        // Word wrap summary text
        val summaryLines = wrapText(summary.summary, bodyPaint, PDF_PAGE_WIDTH - (MARGIN * 2))
        summaryLines.forEach { line ->
            canvas.drawText(line, MARGIN, yPosition, bodyPaint)
            yPosition += 20f * LINE_SPACING
        }
        yPosition += 20f
        
        // Draw key points
        canvas.drawText("Key Points", MARGIN, yPosition, headerPaint)
        yPosition += 25f
        
        summary.bulletPoints.forEachIndexed { index, point ->
            val bulletLines = wrapText("• $point", bodyPaint, PDF_PAGE_WIDTH - (MARGIN * 2) - 20f)
            bulletLines.forEach { line ->
                canvas.drawText(line, MARGIN + 20f, yPosition, bodyPaint)
                yPosition += 20f * LINE_SPACING
            }
            yPosition += 10f
        }
        
        // Draw footer
        val footerPaint = Paint().apply {
            textSize = 10f
            color = Color.GRAY
            textAlign = Paint.Align.CENTER
        }
        
        canvas.drawText(
            "Generated by SumUp - AI-Powered Text Summarization",
            PDF_PAGE_WIDTH / 2f,
            PDF_PAGE_HEIGHT - 30f,
            footerPaint
        )
    }
    
    private fun drawImageContent(
        canvas: Canvas,
        summary: Summary,
        persona: SummaryPersona
    ) {
        // Background
        canvas.drawColor(Color.WHITE)
        
        // Create gradient background
        val gradientPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f,
                IMAGE_WIDTH.toFloat(), 300f,
                intArrayOf(
                    androidx.compose.ui.graphics.Color(0xFF2196F3).toArgb(),
                    androidx.compose.ui.graphics.Color(0xFF64B5F6).toArgb()
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        
        canvas.drawRect(0f, 0f, IMAGE_WIDTH.toFloat(), 300f, gradientPaint)
        
        var yPosition = IMAGE_PADDING.toFloat()
        
        // Title
        val titlePaint = Paint().apply {
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }
        
        canvas.drawText(
            "AI Summary",
            IMAGE_WIDTH / 2f,
            yPosition + 50f,
            titlePaint
        )
        
        // Card background
        val cardPaint = Paint().apply {
            color = Color.WHITE
            setShadowLayer(10f, 0f, 5f, Color.argb(50, 0, 0, 0))
        }
        
        val cardLeft = 40f
        val cardRight = IMAGE_WIDTH - 40f
        val cardTop = 250f
        
        // Draw card with rounded corners
        val cardPath = Path().apply {
            addRoundRect(
                cardLeft, cardTop,
                cardRight, calculateTextHeight(summary).toFloat() + 100f,
                20f, 20f,
                Path.Direction.CW
            )
        }
        
        canvas.drawPath(cardPath, cardPaint)
        
        // Content inside card
        yPosition = cardTop + IMAGE_PADDING
        
        val bodyPaint = Paint().apply {
            textSize = 24f
            color = Color.BLACK
        }
        
        val headerPaint = Paint().apply {
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }
        
        // Summary section
        canvas.drawText("Summary", cardLeft + 40f, yPosition, headerPaint)
        yPosition += 50f
        
        val summaryLines = wrapText(
            summary.summary,
            bodyPaint,
            cardRight - cardLeft - 80f
        )
        
        summaryLines.forEach { line ->
            canvas.drawText(line, cardLeft + 40f, yPosition, bodyPaint)
            yPosition += 35f
        }
        yPosition += 40f
        
        // Key points section
        canvas.drawText("Key Points", cardLeft + 40f, yPosition, headerPaint)
        yPosition += 50f
        
        summary.bulletPoints.forEach { point ->
            val bulletLines = wrapText(
                "• $point",
                bodyPaint,
                cardRight - cardLeft - 100f
            )
            bulletLines.forEach { line ->
                canvas.drawText(line, cardLeft + 60f, yPosition, bodyPaint)
                yPosition += 35f
            }
            yPosition += 20f
        }
        
        // Footer
        val footerPaint = Paint().apply {
            textSize = 20f
            color = Color.GRAY
            textAlign = Paint.Align.CENTER
        }
        
        canvas.drawText(
            "SumUp • ${dateFormatter.format(Date())}",
            IMAGE_WIDTH / 2f,
            yPosition + 40f,
            footerPaint
        )
    }
    
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            
            if (width > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        
        return lines
    }
    
    private fun calculateTextHeight(summary: Summary): Int {
        val bodyPaint = Paint().apply { textSize = 24f }
        var height = 600 // Base height for headers and spacing
        
        // Summary text height
        val summaryLines = wrapText(summary.summary, bodyPaint, IMAGE_WIDTH - 160f)
        height += summaryLines.size * 35
        
        // Bullet points height
        summary.bulletPoints.forEach { point ->
            val bulletLines = wrapText("• $point", bodyPaint, IMAGE_WIDTH - 180f)
            height += bulletLines.size * 35 + 20
        }
        
        return height + 200 // Extra padding
    }
    
    /**
     * Export summary to plain text format
     */
    suspend fun exportToText(
        summary: Summary,
        persona: SummaryPersona
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val content = buildString {
                appendLine("=== AI SUMMARY REPORT ===")
                appendLine()
                appendLine("Generated: ${dateFormatter.format(Date(summary.createdAt))}")
                appendLine("Persona: ${persona.displayName}")
                appendLine()
                appendLine("SUMMARY METRICS:")
                appendLine("- Original: ${summary.metrics.originalWordCount} words")
                appendLine("- Summary: ${summary.metrics.summaryWordCount} words")
                appendLine("- Reduction: ${summary.metrics.reductionPercentage}%")
                appendLine("- Time saved: ${summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime} minutes")
                appendLine()
                appendLine("SUMMARY:")
                appendLine(summary.summary)
                appendLine()
                
                if (!summary.briefOverview.isNullOrEmpty()) {
                    appendLine("BRIEF OVERVIEW:")
                    appendLine(summary.briefOverview)
                    appendLine()
                }
                
                if (!summary.detailedSummary.isNullOrEmpty()) {
                    appendLine("DETAILED SUMMARY:")
                    appendLine(summary.detailedSummary)
                    appendLine()
                }
                
                appendLine("KEY POINTS:")
                summary.bulletPoints.forEach { point ->
                    appendLine("• $point")
                }
                
                summary.keyInsights?.let { insights ->
                    if (insights.isNotEmpty()) {
                        appendLine()
                        appendLine("KEY INSIGHTS:")
                        insights.forEach { insight ->
                            appendLine("• $insight")
                        }
                    }
                }
                
                summary.actionItems?.let { actions ->
                    if (actions.isNotEmpty()) {
                        appendLine()
                        appendLine("ACTION ITEMS:")
                        actions.forEach { action ->
                            appendLine("□ $action")
                        }
                    }
                }
                
                summary.keywords?.let { keywords ->
                    if (keywords.isNotEmpty()) {
                        appendLine()
                        appendLine("KEYWORDS:")
                        appendLine(keywords.joinToString(", "))
                    }
                }
                
                appendLine()
                appendLine("---")
                appendLine("Generated by SumUp - AI-Powered Text Summarization")
                appendLine("https://sumup.app")
            }
            
            val fileName = "summary_${System.currentTimeMillis()}.txt"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            file.writeText(content)
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export summary to markdown format (can be converted to DOCX by external apps)
     */
    suspend fun exportToMarkdown(
        summary: Summary,
        persona: SummaryPersona
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val content = buildString {
                appendLine("# AI Summary Report")
                appendLine()
                appendLine("> Generated: ${dateFormatter.format(Date(summary.createdAt))}")
                appendLine("> Persona: ${persona.displayName}")
                appendLine()
                appendLine("## Summary Metrics")
                appendLine()
                appendLine("| Metric | Value |")
                appendLine("|--------|-------|")
                appendLine("| Original Words | ${summary.metrics.originalWordCount} |")
                appendLine("| Summary Words | ${summary.metrics.summaryWordCount} |")
                appendLine("| Reduction | ${summary.metrics.reductionPercentage}% |")
                appendLine("| Time Saved | ${summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime} minutes |")
                appendLine()
                appendLine("## Summary")
                appendLine()
                appendLine(summary.summary)
                appendLine()
                
                if (!summary.briefOverview.isNullOrEmpty()) {
                    appendLine("### Brief Overview")
                    appendLine()
                    appendLine(summary.briefOverview)
                    appendLine()
                }
                
                if (!summary.detailedSummary.isNullOrEmpty()) {
                    appendLine("### Detailed Summary")
                    appendLine()
                    appendLine(summary.detailedSummary)
                    appendLine()
                }
                
                appendLine("## Key Points")
                appendLine()
                summary.bulletPoints.forEach { point ->
                    appendLine("- $point")
                }
                
                summary.keyInsights?.let { insights ->
                    if (insights.isNotEmpty()) {
                        appendLine()
                        appendLine("## Key Insights")
                        appendLine()
                        insights.forEach { insight ->
                            appendLine("- $insight")
                        }
                    }
                }
                
                summary.actionItems?.let { actions ->
                    if (actions.isNotEmpty()) {
                        appendLine()
                        appendLine("## Action Items")
                        appendLine()
                        actions.forEach { action ->
                            appendLine("- [ ] $action")
                        }
                    }
                }
                
                summary.keywords?.let { keywords ->
                    if (keywords.isNotEmpty()) {
                        appendLine()
                        appendLine("## Keywords")
                        appendLine()
                        appendLine(keywords.joinToString(", "))
                    }
                }
                
                appendLine()
                appendLine("---")
                appendLine()
                appendLine("*Generated by [SumUp](https://sumup.app) - AI-Powered Text Summarization*")
            }
            
            val fileName = "summary_${System.currentTimeMillis()}.md"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            file.writeText(content)
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export summary to JSON format
     */
    suspend fun exportToJson(
        summary: Summary,
        persona: SummaryPersona
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val json = org.json.JSONObject().apply {
                put("generated_at", dateFormatter.format(Date(summary.createdAt)))
                put("persona", persona.displayName)
                put("summary", summary.summary)
                put("brief_overview", summary.briefOverview ?: "")
                put("detailed_summary", summary.detailedSummary ?: "")
                put("bullet_points", org.json.JSONArray(summary.bulletPoints))
                put("key_insights", org.json.JSONArray(summary.keyInsights ?: emptyList<String>()))
                put("action_items", org.json.JSONArray(summary.actionItems ?: emptyList<String>()))
                put("keywords", org.json.JSONArray(summary.keywords ?: emptyList<String>()))
                put("metrics", org.json.JSONObject().apply {
                    put("original_word_count", summary.metrics.originalWordCount)
                    put("summary_word_count", summary.metrics.summaryWordCount)
                    put("reduction_percentage", summary.metrics.reductionPercentage)
                    put("time_saved_minutes", summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime)
                })
            }
            
            val fileName = "summary_${System.currentTimeMillis()}.json"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            file.writeText(json.toString(2))
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get URI for sharing file
     */
    fun getFileUri(file: File): android.net.Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
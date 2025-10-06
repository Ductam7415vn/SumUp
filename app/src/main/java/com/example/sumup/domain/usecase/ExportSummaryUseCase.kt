package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.ExportFormat
import com.example.sumup.domain.model.AppError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use Case for exporting summaries to different formats.
 * Implements UC-006: Export Tóm tắt
 *
 * Supports:
 * - Plain Text (.txt)
 * - Markdown (.md)
 * - PDF (.pdf)
 */
@Singleton
class ExportSummaryUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfExporter: PdfExporter,
    private val markdownExporter: MarkdownExporter,
    private val textExporter: TextExporter
) {

    /**
     * Export summary to specified format
     *
     * AC-006.1: Support 3 formats: Plain Text, Markdown, PDF
     * AC-006.4: PDF generation completes < 3s
     */
    suspend operator fun invoke(
        summary: Summary,
        format: ExportFormat
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ExportSummaryUseCase", "Exporting summary ${summary.id} to $format")

            val fileName = generateFileName(summary, format)
            val file = createExportFile(fileName)

            when (format) {
                ExportFormat.PLAIN_TEXT -> {
                    textExporter.export(summary, file)
                }
                ExportFormat.MARKDOWN -> {
                    markdownExporter.export(summary, file)
                }
                ExportFormat.PDF -> {
                    pdfExporter.export(summary, file)
                }
            }

            android.util.Log.d("ExportSummaryUseCase", "Export successful: ${file.absolutePath}")
            Result.success(Uri.fromFile(file))

        } catch (e: Exception) {
            android.util.Log.e("ExportSummaryUseCase", "Export failed", e)
            Result.failure(Exception(e.message ?: "Export failed"))
        }
    }

    /**
     * Export to clipboard (for Plain Text)
     * AC-006.2: Copy to clipboard works
     */
    suspend fun exportToClipboard(summary: Summary): Result<String> = withContext(Dispatchers.IO) {
        try {
            val content = textExporter.generateContent(summary)
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Clipboard export failed"))
        }
    }

    /**
     * Generate filename following convention: Summary_YYYY-MM-DD_HH-MM.ext
     * AC-006.1: File naming convention
     */
    private fun generateFileName(summary: Summary, format: ExportFormat): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val timestamp = dateFormat.format(Date(summary.createdAt))
        val extension = when (format) {
            ExportFormat.PLAIN_TEXT -> "txt"
            ExportFormat.MARKDOWN -> "md"
            ExportFormat.PDF -> "pdf"
        }
        return "Summary_${timestamp}.${extension}"
    }

    /**
     * Create export file in Downloads directory
     * AC-006.4: File saved to Downloads
     */
    private fun createExportFile(fileName: String): File {
        // For Android 10+ (Scoped Storage)
        val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
            ?: context.filesDir

        return File(downloadsDir, fileName).apply {
            if (!exists()) {
                parentFile?.mkdirs()
                createNewFile()
            }
        }
    }
}

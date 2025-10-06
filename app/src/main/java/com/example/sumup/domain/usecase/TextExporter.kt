package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.Summary
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exports summary to plain text format
 * Implements AC-006.2: Plain Text Export
 */
@Singleton
class TextExporter @Inject constructor() {

    /**
     * Export summary to plain text file
     * AC-006.2: Formatted with line breaks preserved
     */
    fun export(summary: Summary, file: File) {
        val content = generateContent(summary)
        file.writeText(content)
    }

    /**
     * Generate plain text content from summary
     * AC-006.1: Includes metadata
     */
    fun generateContent(summary: Summary): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date(summary.createdAt))
        val timeSaved = summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime

        return buildString {
            // Header
            appendLine("=" .repeat(60))
            appendLine("SUMMARY REPORT")
            appendLine("=" .repeat(60))
            appendLine()

            // Metadata
            appendLine("Generated: $timestamp")
            appendLine("Persona: ${summary.persona.displayName}")
            appendLine()

            // Metrics
            appendLine("-" .repeat(60))
            appendLine("METRICS")
            appendLine("-" .repeat(60))
            appendLine("Original Words: ${summary.metrics.originalWordCount}")
            appendLine("Summary Words: ${summary.metrics.summaryWordCount}")
            appendLine("Reduction: ${summary.metrics.reductionPercentage}%")
            appendLine("Reading Time Saved: $timeSaved minutes")
            appendLine()

            // Original Text (optional, only if not too long)
            if (summary.originalText.length <= 1000) {
                appendLine("-" .repeat(60))
                appendLine("ORIGINAL TEXT")
                appendLine("-" .repeat(60))
                appendLine(summary.originalText)
                appendLine()
            }

            // Summary
            appendLine("-" .repeat(60))
            appendLine("SUMMARY")
            appendLine("-" .repeat(60))
            appendLine(summary.summaryText)
            appendLine()

            // Footer
            appendLine("-" .repeat(60))
            appendLine("Generated with SumUp - AI Text Summarization")
            appendLine("https://github.com/Ductam7415vn/SumUp")
            appendLine("=" .repeat(60))
        }
    }

    /**
     * Generate clipboard-friendly content (shorter format)
     * AC-006.2: Copy to clipboard works
     */
    fun generateClipboardContent(summary: Summary): String {
        return buildString {
            appendLine("📝 SUMMARY (${summary.persona.displayName})")
            appendLine()
            appendLine(summary.summaryText)
            appendLine()
            appendLine("📊 Metrics: ${summary.metrics.originalWordCount} → ${summary.metrics.summaryWordCount} words (${summary.metrics.reductionPercentage}% reduction)")
        }
    }
}

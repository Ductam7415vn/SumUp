package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.Summary
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exports summary to Markdown format
 * Implements AC-006.3: Markdown Export
 */
@Singleton
class MarkdownExporter @Inject constructor() {

    /**
     * Export summary to Markdown file
     * AC-006.3: Valid markdown syntax
     */
    fun export(summary: Summary, file: File) {
        val content = generateContent(summary)
        file.writeText(content)
    }

    /**
     * Generate Markdown content from summary
     * AC-006.3: Includes heading levels (H1, H2)
     * AC-006.3: Metrics in bullet list format
     */
    fun generateContent(summary: Summary): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date(summary.createdAt))
        val timeSaved = summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime

        return buildString {
            // Main Title
            appendLine("# Summary Report")
            appendLine()

            // Metadata section
            appendLine("## Metadata")
            appendLine()
            appendLine("- **Generated**: $timestamp")
            appendLine("- **Persona**: ${summary.persona.displayName}")
            appendLine()

            // Metrics section
            appendLine("## Metrics")
            appendLine()
            appendLine("| Metric | Value |")
            appendLine("|--------|-------|")
            appendLine("| Original Words | ${summary.metrics.originalWordCount} |")
            appendLine("| Summary Words | ${summary.metrics.summaryWordCount} |")
            appendLine("| Reduction | ${summary.metrics.reductionPercentage}% |")
            appendLine("| Reading Time Saved | $timeSaved min |")
            appendLine()

            // Key Insights (if available)
            if (!summary.keyInsights.isNullOrEmpty()) {
                appendLine("## Key Insights")
                appendLine()
                summary.keyInsights.forEachIndexed { index, insight ->
                    appendLine("${index + 1}. $insight")
                }
                appendLine()
            }

            // Summary section
            appendLine("## Summary")
            appendLine()
            appendLine("> **${summary.persona.displayName} Perspective**")
            appendLine()
            appendLine(summary.summaryText)
            appendLine()

            // Original Text (optional, in code block)
            if (summary.originalText.length <= 2000) {
                appendLine("## Original Text")
                appendLine()
                appendLine("```")
                appendLine(summary.originalText)
                appendLine("```")
                appendLine()
            }

            // Action Items (if available)
            if (!summary.actionItems.isNullOrEmpty()) {
                appendLine("## Action Items")
                appendLine()
                summary.actionItems.forEach { item ->
                    appendLine("- [ ] $item")
                }
                appendLine()
            }

            // Footer
            appendLine("---")
            appendLine()
            appendLine("*Generated with [SumUp](https://github.com/Ductam7415vn/SumUp) - AI Text Summarization*")
        }
    }

    /**
     * Generate GitHub-flavored Markdown with enhanced formatting
     */
    fun generateGitHubFlavoredMarkdown(summary: Summary): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date(summary.createdAt))
        val timeSaved = summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime

        return buildString {
            // Title with badge
            appendLine("# 📝 Summary Report")
            appendLine()
            appendLine("![Persona](https://img.shields.io/badge/Persona-${summary.persona.name}-blue)")
            appendLine("![Reduction](https://img.shields.io/badge/Reduction-${summary.metrics.reductionPercentage}%25-success)")
            appendLine()

            // Summary in callout
            appendLine("> **💡 ${summary.persona.displayName} Summary**")
            appendLine(">")
            summary.summaryText.lines().forEach { line ->
                appendLine("> $line")
            }
            appendLine()

            // Metrics table
            appendLine("## 📊 Statistics")
            appendLine()
            appendLine("| Metric | Value |")
            appendLine("|--------|-------|")
            appendLine("| 📖 Original Words | ${summary.metrics.originalWordCount} |")
            appendLine("| ✂️ Summary Words | ${summary.metrics.summaryWordCount} |")
            appendLine("| 📉 Reduction | ${summary.metrics.reductionPercentage}% |")
            appendLine("| ⏱️ Time Saved | $timeSaved min |")
            appendLine("| 📅 Generated | $timestamp |")
            appendLine()

            // Footer
            appendLine("---")
            appendLine()
            appendLine("<sub>Generated with [SumUp](https://github.com/Ductam7415vn/SumUp) 🚀</sub>")
        }
    }
}

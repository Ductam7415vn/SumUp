package com.example.sumup.domain.model

/**
 * Supported export formats for summaries
 * Implements UC-006: Export Tóm tắt
 */
enum class ExportFormat(
    val displayName: String,
    val extension: String,
    val mimeType: String
) {
    PLAIN_TEXT(
        displayName = "Plain Text",
        extension = "txt",
        mimeType = "text/plain"
    ),
    MARKDOWN(
        displayName = "Markdown",
        extension = "md",
        mimeType = "text/markdown"
    ),
    PDF(
        displayName = "PDF Document",
        extension = "pdf",
        mimeType = "application/pdf"
    );

    companion object {
        fun fromExtension(ext: String): ExportFormat? {
            return values().find { it.extension.equals(ext, ignoreCase = true) }
        }
    }
}

package com.example.sumup.domain.model

/**
 * Represents a generic document that can be processed
 */
data class Document(
    val uri: String,
    val fileName: String,
    val sizeBytes: Long,
    val type: DocumentType,
    val pageCount: Int? = null,
    val isPasswordProtected: Boolean = false,
    val extractedText: String? = null,
    val processingState: DocumentProcessingState = DocumentProcessingState.Idle
)

enum class DocumentType {
    PDF,
    DOC,
    DOCX,
    TXT,
    RTF
}

sealed class DocumentProcessingState {
    object Idle : DocumentProcessingState()
    data class Processing(val progress: Float) : DocumentProcessingState()
    object Completed : DocumentProcessingState()
    data class Error(val message: String) : DocumentProcessingState()
}

/**
 * Result of document text extraction
 */
data class DocumentExtractionResult(
    val extractedText: String,
    val pageCount: Int?,
    val success: Boolean,
    val errorMessage: String? = null,
    val confidence: Float = 0.0f,
    val hasTableStructure: Boolean = false,
    val metadata: Map<String, String> = emptyMap()
)
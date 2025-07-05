package com.example.sumup.domain.model

/**
 * Represents the state of file upload and processing
 */
sealed class FileUploadState {
    object Idle : FileUploadState()
    object SelectingFile : FileUploadState()
    data class Uploading(val progress: Float) : FileUploadState()
    data class Processing(val stage: ProcessingStage, val progress: Float) : FileUploadState()
    data class Success(val extractedText: String) : FileUploadState()
    data class Error(val error: FileUploadError) : FileUploadState()
    data class LargePdfDetected(
        val pageCount: Int,
        val estimatedProcessingTime: Long
    ) : FileUploadState()
}

enum class ProcessingStage(val displayName: String) {
    READING_FILE("Reading file..."),
    EXTRACTING_TEXT("Extracting text..."),
    CLEANING_TEXT("Cleaning text..."),
    PREPARING_SUMMARY("Preparing for summary...")
}

sealed class FileUploadError(val message: String) {
    object FileTooLarge : FileUploadError("File too large. Maximum 10MB allowed.")
    object UnsupportedFormat : FileUploadError("Unsupported file format. Only PDF files allowed.")
    object PasswordProtected : FileUploadError("Password-protected PDFs not supported.")
    object CorruptedFile : FileUploadError("File appears to be corrupted.")
    object NoTextFound : FileUploadError("No readable text found in this PDF.")
    object ExtractionFailed : FileUploadError("Failed to extract text from PDF.")
    data class ProcessingFailed(val details: String) : FileUploadError("Processing failed: $details")
    data class NetworkError(val details: String) : FileUploadError("Network error: $details")
    data class UnknownError(val details: String) : FileUploadError("Unknown error: $details")
}

package com.example.sumup.domain.model

/**
 * Represents a summarized section of a document
 */
data class SectionSummary(
    val id: String,
    val title: String,
    val content: String,
    val summary: String,
    val bulletPoints: List<String> = emptyList(),
    val startIndex: Int,
    val endIndex: Int,
    val status: ProcessingStatus = ProcessingStatus.PENDING,
    val processingTime: Long? = null,
    val error: String? = null
)

/**
 * Processing status for sections and summaries
 */
enum class ProcessingStatus {
    PENDING,      // Not yet started
    PROCESSING,   // Currently being processed
    COMPLETED,    // Successfully completed
    FAILED,       // Failed with error
    CANCELLED     // Cancelled by user
}

/**
 * Event for streaming updates
 */
sealed class StreamingEvent {
    data class SectionStarted(val sectionId: String, val sectionIndex: Int) : StreamingEvent()
    data class SectionCompleted(val section: SectionSummary) : StreamingEvent()
    data class SectionFailed(val sectionId: String, val error: String) : StreamingEvent()
    data class ProgressUpdate(val current: Int, val total: Int, val percentage: Float) : StreamingEvent()
    data class OverallSummaryReady(val summary: Summary) : StreamingEvent()
    object ProcessingComplete : StreamingEvent()
    data class ProcessingError(val error: String) : StreamingEvent()
}
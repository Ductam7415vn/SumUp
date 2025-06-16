package com.example.sumup.domain.model

/**
 * Core domain model representing a summary
 */
data class Summary(
    val id: String,
    val originalText: String,
    val summary: String = "",
    val bulletPoints: List<String>,
    val persona: SummaryPersona,
    val createdAt: Long,
    val isFavorite: Boolean = false,
    val metrics: SummaryMetrics,
    val confidence: Float = 0.0f
) {
    /**
     * Get the summary as a single text string
     */
    val summaryText: String
        get() = bulletPoints.joinToString("\n") { "• $it" }
    
    /**
     * Get a preview of the summary (first bullet point)
     */
    val preview: String
        get() = bulletPoints.firstOrNull() ?: ""
}

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
    val confidence: Float = 0.0f,
    // Multi-tier content (Phase 2)
    val briefOverview: String? = null,
    val detailedSummary: String? = null,
    val keyInsights: List<String>? = null,
    val actionItems: List<String>? = null,
    val keywords: List<String>? = null,
    // AI Quality Metrics
    val aiQualityMetrics: AiQualityMetrics? = null,
    // Streaming and partial results support
    val isPartial: Boolean = false,
    val processedSections: Int = 0,
    val totalSections: Int = 0,
    val sections: List<SectionSummary> = emptyList(),
    val processingStatus: ProcessingStatus = ProcessingStatus.COMPLETED
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

    companion object
}
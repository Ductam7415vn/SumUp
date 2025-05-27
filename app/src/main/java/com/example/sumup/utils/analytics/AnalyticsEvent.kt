package com.example.sumup.utils.analytics

sealed class AnalyticsEvent(val name: String, val params: Map<String, Any> = emptyMap()) {
    object SummarizeClicked : AnalyticsEvent("summarize_clicked")
    object ClearClicked : AnalyticsEvent("clear_clicked")
    object CopyClicked : AnalyticsEvent("copy_clicked")
    
    data class SummarizeSuccess(
        val textLength: Int,
        val summaryLength: Int
    ) : AnalyticsEvent(
        "summarize_success", 
        mapOf(
            "text_length" to textLength,
            "summary_length" to summaryLength
        )
    )
    
    data class SummarizeError(
        val error: String
    ) : AnalyticsEvent(
        "summarize_error", 
        mapOf("error" to error)
    )
    
    data class PasteTruncated(
        val originalLength: Int,
        val truncatedLength: Int
    ) : AnalyticsEvent(
        "paste_truncated", 
        mapOf(
            "original_length" to originalLength,
            "truncated_length" to truncatedLength
        )
    )
}
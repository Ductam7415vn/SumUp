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
    
    // Advanced analytics events
    data class SessionStart(
        val sessionId: String,
        val userId: String?
    ) : AnalyticsEvent(
        "session_start",
        mapOf(
            "session_id" to sessionId,
            "user_id" to (userId ?: "anonymous")
        )
    )
    
    data class FeatureUsed(
        val featureName: String,
        val context: Map<String, Any>
    ) : AnalyticsEvent(
        "feature_used",
        mapOf("feature" to featureName) + context
    )
    
    data class PerformanceMetric(
        val operation: String,
        val duration: Long,
        val success: Boolean,
        val additionalMetrics: Map<String, Any>
    ) : AnalyticsEvent(
        "performance_metric",
        mapOf(
            "operation" to operation,
            "duration" to duration,
            "success" to success
        ) + additionalMetrics
    )
    
    data class AlgorithmExecution(
        val algorithmName: String,
        val inputSize: Int,
        val processingTime: Long,
        val accuracy: Double
    ) : AnalyticsEvent(
        "algorithm_execution",
        mapOf(
            "algorithm" to algorithmName,
            "input_size" to inputSize,
            "processing_time" to processingTime,
            "accuracy" to accuracy
        )
    )
    
    data class UserJourneyStep(
        val stepName: String,
        val stepDuration: Long,
        val metadata: Map<String, Any>
    ) : AnalyticsEvent(
        "user_journey_step",
        mapOf(
            "step" to stepName,
            "duration" to stepDuration
        ) + metadata
    )
    
    data class ErrorOccurred(
        val errorType: String,
        val errorMessage: String,
        val context: Map<String, Any>
    ) : AnalyticsEvent(
        "error_occurred",
        mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage
        ) + context
    )
}
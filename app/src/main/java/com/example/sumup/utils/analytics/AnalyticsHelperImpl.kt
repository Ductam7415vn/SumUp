package com.example.sumup.utils.analytics

import android.util.Log
import com.example.sumup.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics implementation that only logs events in debug mode.
 * For production, replace with Firebase Analytics or other analytics service.
 */
@Singleton
class AnalyticsHelperImpl @Inject constructor() : AnalyticsHelper {
    
    override fun logEvent(event: AnalyticsEvent) {
        // Only log in debug builds to avoid exposing user data
        if (BuildConfig.DEBUG) {
            Log.d(TAG, formatEvent(event))
        }
    }
    
    override fun logError(throwable: Throwable, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Error: $message", throwable)
        }
        // In production, this would send to Crashlytics or similar service
    }
    
    private fun formatEvent(event: AnalyticsEvent): String {
        return when (event) {
            AnalyticsEvent.SummarizeClicked -> 
                "SUMMARIZE_CLICKED"
            AnalyticsEvent.ClearClicked -> 
                "CLEAR_CLICKED"
            AnalyticsEvent.CopyClicked -> 
                "COPY_CLICKED"
            is AnalyticsEvent.PasteTruncated -> 
                "PASTE_TRUNCATED: ${event.originalLength} -> ${event.truncatedLength}"
            is AnalyticsEvent.SummarizeSuccess -> 
                "SUMMARIZE_SUCCESS: ${event.textLength} -> ${event.summaryLength} chars"
            is AnalyticsEvent.SummarizeError -> 
                "SUMMARIZE_ERROR: ${event.error}"
            is AnalyticsEvent.SessionStart -> 
                "SESSION_START: ${event.sessionId}"
            is AnalyticsEvent.FeatureUsed -> 
                "FEATURE_USED: ${event.featureName}"
            is AnalyticsEvent.PerformanceMetric -> 
                "PERFORMANCE: ${event.operation} = ${event.duration}ms"
            is AnalyticsEvent.AlgorithmExecution -> 
                "ALGORITHM: ${event.algorithmName}"
            is AnalyticsEvent.UserJourneyStep -> 
                "JOURNEY_STEP: ${event.stepName}"
            is AnalyticsEvent.ErrorOccurred -> 
                "ERROR: ${event.errorType} - ${event.errorMessage}"
        }
    }
    
    companion object {
        private const val TAG = "Analytics"
    }
}
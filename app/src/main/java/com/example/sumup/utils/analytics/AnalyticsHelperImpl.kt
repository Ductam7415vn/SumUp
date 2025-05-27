package com.example.sumup.utils.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelperImpl @Inject constructor() : AnalyticsHelper {
    
    override fun logEvent(event: AnalyticsEvent) {
        // TODO: Replace with actual Firebase Analytics implementation
        Log.d("Analytics", "Event: ${event.name}, Params: ${event.params}")
        
        // Mock implementation for now
        when (event) {
            is AnalyticsEvent.SummarizeClicked -> {
                Log.d("Analytics", "User clicked summarize button")
            }
            is AnalyticsEvent.ClearClicked -> {
                Log.d("Analytics", "User clicked clear button")
            }
            is AnalyticsEvent.CopyClicked -> {
                Log.d("Analytics", "User clicked copy button")
            }
            is AnalyticsEvent.PasteTruncated -> {
                Log.d("Analytics", "Text was truncated from ${event.originalLength} to ${event.truncatedLength}")
            }
            is AnalyticsEvent.SummarizeSuccess -> {
                Log.d("Analytics", "Summary generated successfully - Text: ${event.textLength}, Summary: ${event.summaryLength}")
            }
            is AnalyticsEvent.SummarizeError -> {
                Log.e("Analytics", "Summarization failed: ${event.error}")
            }
        }
    }
    
    override fun logError(throwable: Throwable, message: String?) {
        Log.e("Analytics", "Error: $message", throwable)
        // TODO: Log to Crashlytics/Firebase
    }
}
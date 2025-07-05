package com.example.sumup.utils.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.example.sumup.utils.analytics.AnalyticsEvent.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsHelper {
    
    override fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        
        analytics.logEvent(event.name, bundle)
    }
    
    fun logScreenView(screenName: String, screenClass: String?) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let {
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, it)
            }
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
    
    fun setUserId(userId: String?) {
        analytics.setUserId(userId)
        Firebase.crashlytics.setUserId(userId ?: "")
    }
    
    fun setUserProperty(key: String, value: String?) {
        analytics.setUserProperty(key, value)
        Firebase.crashlytics.setCustomKey(key, value ?: "")
    }
    
    override fun logError(throwable: Throwable, message: String?) {
        // Log to Firebase Crashlytics
        Firebase.crashlytics.recordException(throwable)
        message?.let {
            Firebase.crashlytics.log(it)
        }
        
        // Also log as analytics event
        logEvent(
            ErrorOccurred(
                errorType = throwable.javaClass.simpleName,
                errorMessage = message ?: throwable.message ?: "Unknown error",
                context = mapOf(
                    "stack_trace" to throwable.stackTraceToString().take(500)
                )
            )
        )
    }
    
    // Predefined events for SumUp
    fun logSummarizeEvent(
        inputType: String,
        textLength: Int,
        persona: String,
        success: Boolean
    ) {
        logEvent(
            FeatureUsed(
                featureName = "summarize_text",
                context = mapOf(
                    "input_type" to inputType,
                    "text_length" to textLength,
                    "persona" to persona,
                    "success" to success
                )
            )
        )
    }
    
    fun logPdfProcessEvent(
        pageCount: Int,
        fileSize: Long,
        success: Boolean,
        errorMessage: String? = null
    ) {
        logEvent(
            FeatureUsed(
                featureName = "process_pdf",
                context = buildMap {
                    put("page_count", pageCount)
                    put("file_size_kb", fileSize / 1024)
                    put("success", success)
                    errorMessage?.let { put("error", it) }
                }
            )
        )
    }
    
    fun logOcrEvent(
        confidence: Float,
        textLength: Int,
        captureMode: String,
        success: Boolean
    ) {
        logEvent(
            FeatureUsed(
                featureName = "ocr_scan",
                context = mapOf(
                    "confidence" to confidence,
                    "text_length" to textLength,
                    "capture_mode" to captureMode,
                    "success" to success
                )
            )
        )
    }
    
    fun logApiError(
        endpoint: String,
        errorCode: Int,
        errorMessage: String
    ) {
        logEvent(
            ErrorOccurred(
                errorType = "api_error",
                errorMessage = errorMessage,
                context = mapOf(
                    "endpoint" to endpoint,
                    "error_code" to errorCode
                )
            )
        )
        
        // Also log to Crashlytics for debugging
        Firebase.crashlytics.recordException(
            Exception("API Error: $endpoint - $errorCode - $errorMessage")
        )
    }
    
    fun logPerformanceIssue(
        screen: String,
        action: String,
        duration: Long
    ) {
        if (duration > 3000) { // Log if action takes more than 3 seconds
            logEvent(
                PerformanceMetric(
                    operation = "$screen.$action",
                    duration = duration,
                    success = false,
                    additionalMetrics = mapOf(
                        "screen" to screen,
                        "action" to action,
                        "threshold_exceeded" to true
                    )
                )
            )
        }
    }
}
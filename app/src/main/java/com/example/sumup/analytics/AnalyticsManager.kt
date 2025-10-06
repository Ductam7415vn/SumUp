package com.example.sumup.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics Manager for tracking user events and behavior
 * Implements NFR-6: Analytics & Monitoring
 */
@Singleton
class AnalyticsManager @Inject constructor() {

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    companion object {
        // Screen names
        const val SCREEN_MAIN = "main_screen"
        const val SCREEN_RESULT = "result_screen"
        const val SCREEN_HISTORY = "history_screen"
        const val SCREEN_SETTINGS = "settings_screen"
        const val SCREEN_OCR = "ocr_screen"
        const val SCREEN_PROCESSING = "processing_screen"

        // Event names
        const val EVENT_SUMMARY_CREATED = "summary_created"
        const val EVENT_SUMMARY_DELETED = "summary_deleted"
        const val EVENT_SUMMARY_SHARED = "summary_shared"
        const val EVENT_SUMMARY_EXPORTED = "summary_exported"
        const val EVENT_PDF_PROCESSED = "pdf_processed"
        const val EVENT_OCR_CAPTURED = "ocr_captured"
        const val EVENT_API_KEY_ADDED = "api_key_added"
        const val EVENT_PERSONA_CHANGED = "persona_changed"
        const val EVENT_THEME_CHANGED = "theme_changed"
        const val EVENT_LANGUAGE_CHANGED = "language_changed"
        const val EVENT_ERROR_OCCURRED = "error_occurred"

        // Parameter names
        const val PARAM_PERSONA = "persona"
        const val PARAM_WORD_COUNT = "word_count"
        const val PARAM_REDUCTION_PERCENTAGE = "reduction_percentage"
        const val PARAM_EXPORT_FORMAT = "export_format"
        const val PARAM_ERROR_TYPE = "error_type"
        const val PARAM_ERROR_MESSAGE = "error_message"
        const val PARAM_PROCESSING_TIME_MS = "processing_time_ms"
        const val PARAM_SUCCESS = "success"
        const val PARAM_SOURCE = "source"
    }

    /**
     * Log screen view
     * AC-NFR6.1: Track user navigation
     */
    fun logScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    /**
     * Log summary creation
     * AC-NFR6.2: Track summarization events
     */
    fun logSummaryCreated(
        persona: String,
        wordCount: Int,
        reductionPercentage: Int,
        processingTimeMs: Long,
        source: String = "text"
    ) {
        analytics.logEvent(EVENT_SUMMARY_CREATED) {
            param(PARAM_PERSONA, persona)
            param(PARAM_WORD_COUNT, wordCount.toLong())
            param(PARAM_REDUCTION_PERCENTAGE, reductionPercentage.toLong())
            param(PARAM_PROCESSING_TIME_MS, processingTimeMs)
            param(PARAM_SOURCE, source)
        }
    }

    /**
     * Log export action
     * AC-NFR6.3: Track export usage
     */
    fun logExport(format: String, success: Boolean, wordCount: Int) {
        analytics.logEvent(EVENT_SUMMARY_EXPORTED) {
            param(PARAM_EXPORT_FORMAT, format)
            param(PARAM_SUCCESS, if (success) 1L else 0L)
            param(PARAM_WORD_COUNT, wordCount.toLong())
        }
    }

    /**
     * Log PDF processing
     * AC-NFR6.2: Track PDF processing
     */
    fun logPdfProcessed(pageCount: Int, success: Boolean, processingTimeMs: Long) {
        analytics.logEvent(EVENT_PDF_PROCESSED) {
            param("page_count", pageCount.toLong())
            param(PARAM_SUCCESS, if (success) 1L else 0L)
            param(PARAM_PROCESSING_TIME_MS, processingTimeMs)
        }
    }

    /**
     * Log OCR capture
     * AC-NFR6.2: Track OCR usage
     */
    fun logOcrCaptured(success: Boolean, wordCount: Int? = null) {
        analytics.logEvent(EVENT_OCR_CAPTURED) {
            param(PARAM_SUCCESS, if (success) 1L else 0L)
            wordCount?.let { param(PARAM_WORD_COUNT, it.toLong()) }
        }
    }

    /**
     * Log error events
     * AC-NFR6.4: Track errors for improvement
     */
    fun logError(errorType: String, errorMessage: String, context: String = "") {
        analytics.logEvent(EVENT_ERROR_OCCURRED) {
            param(PARAM_ERROR_TYPE, errorType)
            param(PARAM_ERROR_MESSAGE, errorMessage.take(100)) // Limit message length
            if (context.isNotEmpty()) {
                param("context", context)
            }
        }
    }

    /**
     * Log sharing action
     */
    fun logShare(method: String, contentType: String = "summary") {
        analytics.logEvent(FirebaseAnalytics.Event.SHARE) {
            param(FirebaseAnalytics.Param.METHOD, method)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }
    }

    /**
     * Log settings change
     */
    fun logSettingsChange(setting: String, value: String) {
        analytics.logEvent("settings_changed") {
            param("setting", setting)
            param("value", value)
        }
    }

    /**
     * Log API key management
     */
    fun logApiKeyAdded(provider: String = "gemini") {
        analytics.logEvent(EVENT_API_KEY_ADDED) {
            param("provider", provider)
        }
    }

    /**
     * Set user properties
     * AC-NFR6.5: User segmentation
     */
    fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    /**
     * Set default persona preference
     */
    fun setDefaultPersona(persona: String) {
        setUserProperty("preferred_persona", persona)
    }

    /**
     * Set theme preference
     */
    fun setThemePreference(theme: String) {
        setUserProperty("theme", theme)
    }

    /**
     * Set language preference
     */
    fun setLanguagePreference(language: String) {
        setUserProperty("language", language)
    }

    /**
     * Custom event with bundle
     */
    fun logCustomEvent(eventName: String, params: Bundle) {
        analytics.logEvent(eventName, params)
    }

    /**
     * Enable/disable analytics collection
     */
    fun setAnalyticsEnabled(enabled: Boolean) {
        analytics.setAnalyticsCollectionEnabled(enabled)
    }
}

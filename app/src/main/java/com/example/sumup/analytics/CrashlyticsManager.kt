package com.example.sumup.analytics

import android.util.Log
import com.example.sumup.domain.model.AppError
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Crashlytics Manager for error tracking and crash reporting
 * Implements NFR-6: Crashlytics Integration
 */
@Singleton
class CrashlyticsManager @Inject constructor() {

    private val crashlytics: FirebaseCrashlytics by lazy {
        Firebase.crashlytics
    }

    companion object {
        private const val TAG = "CrashlyticsManager"

        // Custom keys
        const val KEY_USER_ID = "user_id"
        const val KEY_SCREEN = "current_screen"
        const val KEY_PERSONA = "selected_persona"
        const val KEY_API_KEY_SET = "has_api_key"
        const val KEY_APP_VERSION = "app_version"
        const val KEY_SUMMARY_COUNT = "summary_count"
        const val KEY_LAST_ACTION = "last_action"
    }

    /**
     * Log non-fatal exception
     * AC-NFR6.6: Track non-fatal errors
     */
    fun logException(throwable: Throwable, context: String = "") {
        if (context.isNotEmpty()) {
            crashlytics.setCustomKey("error_context", context)
        }
        crashlytics.recordException(throwable)
        Log.e(TAG, "Exception logged to Crashlytics: ${throwable.message}", throwable)
    }

    /**
     * Log AppError to Crashlytics
     * AC-NFR6.7: Track custom app errors
     */
    fun logAppError(error: AppError, context: String = "") {
        val exception = when (error) {
            is AppError.NetworkError -> NetworkException(error.message)
            is AppError.ServerError -> ServerException(error.message)
            is AppError.RateLimitError -> RateLimitException("Reset at: ${error.resetTime}")
            is AppError.TextTooShortError -> ValidationException(error.message)
            is AppError.InvalidInputError -> ValidationException(error.message)
            is AppError.OCRFailedError -> OCRException(error.message)
            is AppError.ModelLoadingError -> ModelException(error.message)
            is AppError.StorageFullError -> StorageException(error.message)
            is AppError.ApiKeyError -> ApiKeyException(error.message)
            is AppError.InvalidApiKeyError -> ApiKeyException(error.message)
            is AppError.ExportError -> ExportException(error.originalMessage)
            AppError.StoragePermissionError -> PermissionException(error.message)
            AppError.DiskFullError -> StorageException(error.message)
            is AppError.UnknownError -> UnknownAppException(error.originalMessage)
        }

        logException(exception, context)
    }

    /**
     * Set custom key-value pair
     * AC-NFR6.8: Add contextual data to crashes
     */
    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Long) {
        crashlytics.setCustomKey(key, value)
    }

    /**
     * Set user identifier
     * AC-NFR6.9: Track user-specific issues
     */
    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    /**
     * Set current screen
     */
    fun setCurrentScreen(screenName: String) {
        setCustomKey(KEY_SCREEN, screenName)
    }

    /**
     * Set selected persona
     */
    fun setSelectedPersona(persona: String) {
        setCustomKey(KEY_PERSONA, persona)
    }

    /**
     * Set API key status
     */
    fun setApiKeyStatus(hasKey: Boolean) {
        setCustomKey(KEY_API_KEY_SET, hasKey)
    }

    /**
     * Log breadcrumb for debugging
     * AC-NFR6.10: Track user journey before crash
     */
    fun log(message: String) {
        crashlytics.log(message)
        Log.d(TAG, "Breadcrumb: $message")
    }

    /**
     * Log action breadcrumb
     */
    fun logAction(action: String, details: String = "") {
        val message = if (details.isNotEmpty()) {
            "$action: $details"
        } else {
            action
        }
        log(message)
        setCustomKey(KEY_LAST_ACTION, action)
    }

    /**
     * Enable/disable crash reporting
     * AC-NFR6.11: User privacy control
     */
    fun setCrashlyticsEnabled(enabled: Boolean) {
        crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    /**
     * Send unsent crash reports
     */
    fun sendUnsentReports() {
        crashlytics.sendUnsentReports()
    }

    /**
     * Check for unsent reports
     */
    fun checkForUnsentReports(callback: (Boolean) -> Unit) {
        crashlytics.checkForUnsentReports().addOnCompleteListener { task ->
            callback(task.result ?: false)
        }
    }

    /**
     * Delete unsent reports
     */
    fun deleteUnsentReports() {
        crashlytics.deleteUnsentReports()
    }
}

// Custom exception classes for better categorization in Firebase Console

class NetworkException(message: String) : Exception("Network Error: $message")
class ServerException(message: String) : Exception("Server Error: $message")
class RateLimitException(message: String) : Exception("Rate Limit: $message")
class ValidationException(message: String) : Exception("Validation Error: $message")
class OCRException(message: String) : Exception("OCR Error: $message")
class ModelException(message: String) : Exception("Model Error: $message")
class StorageException(message: String) : Exception("Storage Error: $message")
class ApiKeyException(message: String) : Exception("API Key Error: $message")
class ExportException(message: String) : Exception("Export Error: $message")
class PermissionException(message: String) : Exception("Permission Error: $message")
class UnknownAppException(message: String) : Exception("Unknown Error: $message")

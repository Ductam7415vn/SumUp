package com.example.sumup.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.sumup.domain.model.AppError
import com.example.sumup.presentation.components.ErrorDisplayMode

data class ErrorInfo(val title: String, val message: String, val icon: String)

fun getErrorInfo(error: AppError): ErrorInfo = when (error) {
    is AppError.NetworkError -> ErrorInfo("No Internet Connection", "Check your connection and try again.", "📵")
    is AppError.RateLimitError -> ErrorInfo("Daily Limit Reached", "You've made 50 summaries today! Resets in 4 hours.", "⏰")
    is AppError.TextTooShortError -> ErrorInfo("Need More Text", "Add at least 23 more words for a meaningful summary.", "✏️")
    is AppError.OCRFailedError -> ErrorInfo("Couldn't Read Text", "Try better lighting or hold your device steady.", "👀")
    is AppError.ServerError -> ErrorInfo("Something Went Wrong", "Our servers are having issues. Please try again.", "🔧")
    is AppError.ModelLoadingError -> ErrorInfo("AI Warming Up", "First-time setup. This takes about 10 seconds.", "🤖")
    is AppError.StorageFullError -> ErrorInfo("Storage Full", "You've reached the 100 MB limit. Delete old summaries.", "💾")
    is AppError.InvalidInputError -> ErrorInfo("Can't Process This", "Text contains too many special characters.", "⚠️")
    is AppError.ApiKeyError -> ErrorInfo("API Key Required", "Add your Gemini API key in settings.", "🔑")
    is AppError.InvalidApiKeyError -> ErrorInfo("Invalid API Key", "Check your API key and try again.", "❌")
    is AppError.UnknownError -> ErrorInfo("Something Went Wrong", error.originalMessage, "😕")
}

/**
 * Error handling configuration for different contexts
 */
data class ErrorHandlingConfig(
    val showRetryForNetworkErrors: Boolean = true,
    val autoDismissMinorErrors: Boolean = true,
    val useHapticFeedback: Boolean = true,
    val preferredDisplayMode: ErrorDisplayMode? = null
)

/**
 * Smart error categorization based on context and error type
 */
@Composable
fun rememberErrorHandlingConfig(
    isFormContext: Boolean = false,
    isFullScreenContext: Boolean = false,
    isCriticalOperation: Boolean = false
): ErrorHandlingConfig {
    return remember(isFormContext, isFullScreenContext, isCriticalOperation) {
        when {
            isFormContext -> ErrorHandlingConfig(
                showRetryForNetworkErrors = false,
                autoDismissMinorErrors = true,
                preferredDisplayMode = ErrorDisplayMode.INLINE
            )
            isFullScreenContext -> ErrorHandlingConfig(
                showRetryForNetworkErrors = true,
                autoDismissMinorErrors = false,
                preferredDisplayMode = ErrorDisplayMode.DIALOG
            )
            isCriticalOperation -> ErrorHandlingConfig(
                showRetryForNetworkErrors = true,
                autoDismissMinorErrors = false,
                preferredDisplayMode = ErrorDisplayMode.DIALOG
            )
            else -> ErrorHandlingConfig()
        }
    }
}

/**
 * Get user-friendly error message
 */
fun AppError.getUserFriendlyMessage(): String {
    return when (this) {
        is AppError.NetworkError -> "No internet connection. Please check your network."
        is AppError.ServerError -> "Server is having issues. Please try again later."
        is AppError.RateLimitError -> "You've reached your daily limit. Try again tomorrow!"
        is AppError.TextTooShortError -> "Please add more text (minimum 50 characters)."
        is AppError.InvalidInputError -> "Invalid input. Please check and try again."
        is AppError.OCRFailedError -> "Could not scan text. Try better lighting."
        is AppError.ModelLoadingError -> "AI is loading. Please wait a moment."
        is AppError.StorageFullError -> "Storage full. Please delete old summaries."
        is AppError.ApiKeyError -> "API key required. Add it in settings."
        is AppError.InvalidApiKeyError -> "Invalid API key. Please check and try again."
        is AppError.UnknownError -> originalMessage.ifEmpty { "Something went wrong." }
    }
}

/**
 * Get appropriate action text for error
 */
fun AppError.getActionText(): String {
    return when (this) {
        is AppError.NetworkError -> "Retry"
        is AppError.ServerError -> "Try Again"
        is AppError.RateLimitError -> "See Options"
        is AppError.TextTooShortError -> "Add More Text"
        is AppError.InvalidInputError -> "Fix Input"
        is AppError.OCRFailedError -> "Scan Again"
        is AppError.ModelLoadingError -> "Wait"
        is AppError.StorageFullError -> "Manage Storage"
        is AppError.ApiKeyError -> "Add Key"
        is AppError.InvalidApiKeyError -> "Fix Key"
        is AppError.UnknownError -> "Retry"
    }
}

/**
 * Check if error should allow retry
 */
fun AppError.canRetry(): Boolean {
    return when (this) {
        is AppError.NetworkError -> true
        is AppError.ServerError -> true
        is AppError.OCRFailedError -> true
        is AppError.UnknownError -> true
        else -> false
    }
}

/**
 * Check if error is critical and needs immediate attention
 */
fun AppError.isCritical(): Boolean {
    return when (this) {
        is AppError.StorageFullError -> true
        is AppError.ServerError -> true
        is AppError.RateLimitError -> true
        else -> false
    }
}

/**
 * Get suggested wait time for rate limit errors
 */
fun AppError.getSuggestedWaitTime(): Long? {
    return when (this) {
        is AppError.RateLimitError -> {
            // Calculate time until midnight
            val now = System.currentTimeMillis()
            val midnight = (now / 86400000 + 1) * 86400000
            midnight - now
        }
        is AppError.ModelLoadingError -> 10000L // 10 seconds
        else -> null
    }
}
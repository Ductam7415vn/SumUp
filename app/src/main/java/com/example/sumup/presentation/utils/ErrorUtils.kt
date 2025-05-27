package com.example.sumup.presentation.utils

import com.example.sumup.domain.model.AppError

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
    is AppError.UnknownError -> ErrorInfo("Something Went Wrong", error.originalMessage, "😕")
}
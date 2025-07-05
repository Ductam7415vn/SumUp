package com.example.sumup.domain.model

sealed class AppError(val message: String) {
    object NetworkError : AppError("No internet connection")
    object ServerError : AppError("Server error occurred")
    data class RateLimitError(val resetTime: Long) : AppError("Daily limit reached")
    object TextTooShortError : AppError("Text too short for summary")
    object OCRFailedError : AppError("Couldn't read text from image")
    object InvalidInputError : AppError("Invalid text format")
    object StorageFullError : AppError("Storage limit reached")
    object ModelLoadingError : AppError("AI model loading")
    object ApiKeyError : AppError("API key required")
    object InvalidApiKeyError : AppError("Invalid API key")
    data class UnknownError(val originalMessage: String) : AppError(originalMessage)
}
package com.example.sumup.data.remote.api

import com.example.sumup.domain.model.AppError
import retrofit2.HttpException
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

object GeminiErrorHandler {
    
    fun handleApiError(throwable: Throwable): AppError {
        return when (throwable) {
            is HttpException -> handleHttpError(throwable)
            is UnknownHostException -> AppError.NetworkError
            is SocketTimeoutException -> AppError.NetworkError
            is SSLException -> AppError.NetworkError
            is GeminiApiException -> throwable.toAppError()
            else -> AppError.UnknownError(
                throwable.message ?: "An unexpected error occurred"
            )
        }
    }
    
    private fun handleHttpError(exception: HttpException): AppError {
        return when (exception.code()) {
            400 -> AppError.InvalidInputError
            401 -> AppError.ServerError
            403 -> AppError.ServerError
            429 -> AppError.RateLimitError(
                resetTime = System.currentTimeMillis() + 60000 // Reset in 1 minute
            )
            500, 502, 503 -> AppError.ServerError
            else -> AppError.ServerError
        }
    }
}

sealed class GeminiApiException(message: String) : Exception(message) {
    
    class InvalidApiKeyException : GeminiApiException(
        "Invalid or missing Gemini API key. Please check your configuration."
    )
    
    class QuotaExceededException : GeminiApiException(
        "API quota exceeded. Please upgrade your plan or wait until tomorrow."
    )
    
    class ContentFilteredException(val reason: String) : GeminiApiException(
        "Content was filtered due to: $reason"
    )
    
    class ModelOverloadedException : GeminiApiException(
        "The AI model is currently overloaded. Please try again in a few moments."
    )
    
    fun toAppError(): AppError = when (this) {
        is InvalidApiKeyException -> AppError.ServerError
        is QuotaExceededException -> AppError.RateLimitError(
            resetTime = System.currentTimeMillis() + 86400000 // Reset in 24 hours
        )
        is ContentFilteredException -> AppError.InvalidInputError
        is ModelOverloadedException -> AppError.ServerError
    }
}
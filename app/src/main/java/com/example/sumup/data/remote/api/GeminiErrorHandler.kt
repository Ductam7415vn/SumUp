package com.example.sumup.data.remote.api

import com.example.sumup.domain.model.AppError
import retrofit2.HttpException
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

object GeminiErrorHandler {
    
    fun handleApiError(throwable: Throwable): AppError {
        android.util.Log.e("GeminiErrorHandler", "=== ERROR HANDLING ===")
        android.util.Log.e("GeminiErrorHandler", "Error type: ${throwable.javaClass.simpleName}")
        android.util.Log.e("GeminiErrorHandler", "Error message: ${throwable.message}")
        
        return when (throwable) {
            is HttpException -> {
                android.util.Log.e("GeminiErrorHandler", "HTTP Error code: ${throwable.code()}")
                handleHttpError(throwable)
            }
            is UnknownHostException -> {
                android.util.Log.e("GeminiErrorHandler", "Network error: UnknownHostException")
                AppError.NetworkError
            }
            is SocketTimeoutException -> {
                android.util.Log.e("GeminiErrorHandler", "Network error: SocketTimeoutException")
                AppError.NetworkError
            }
            is SSLException -> {
                android.util.Log.e("GeminiErrorHandler", "Network error: SSLException")
                AppError.NetworkError
            }
            is GeminiApiException -> {
                android.util.Log.e("GeminiErrorHandler", "Gemini API Exception: ${throwable.javaClass.simpleName}")
                throwable.toAppError()
            }
            else -> {
                android.util.Log.e("GeminiErrorHandler", "Unknown error type")
                android.util.Log.e("GeminiErrorHandler", "Stack trace:", throwable)
                AppError.UnknownError(
                    throwable.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    private fun handleHttpError(exception: HttpException): AppError {
        val errorBody = try {
            exception.response()?.errorBody()?.string()
        } catch (e: Exception) {
            "Unable to read error body"
        }
        
        android.util.Log.e("GeminiErrorHandler", "HTTP ${exception.code()}: $errorBody")
        
        return when (exception.code()) {
            400 -> {
                android.util.Log.e("GeminiErrorHandler", "Bad Request (400) - Invalid input")
                AppError.InvalidInputError
            }
            401 -> {
                android.util.Log.e("GeminiErrorHandler", "Unauthorized (401) - Invalid API key")
                AppError.ServerError
            }
            403 -> {
                android.util.Log.e("GeminiErrorHandler", "Forbidden (403) - Access denied")
                AppError.ServerError
            }
            429 -> {
                android.util.Log.e("GeminiErrorHandler", "Rate Limited (429) - Too many requests")
                AppError.RateLimitError(
                    resetTime = System.currentTimeMillis() + 60000 // Reset in 1 minute
                )
            }
            500, 502, 503 -> {
                android.util.Log.e("GeminiErrorHandler", "Server Error (${exception.code()}) - Backend issue")
                AppError.ServerError
            }
            else -> {
                android.util.Log.e("GeminiErrorHandler", "Unexpected HTTP error: ${exception.code()}")
                AppError.ServerError
            }
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
package com.example.sumup.utils

import com.example.sumup.data.remote.dto.GeminiContent
import com.example.sumup.data.remote.dto.GeminiGenerationConfig
import com.example.sumup.data.remote.dto.GeminiPart
import com.example.sumup.data.remote.dto.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates Gemini API keys by making test requests
 */
@Singleton
class ApiKeyValidator @Inject constructor(
    private val retrofit: Retrofit
) {
    
    // Simple API interface just for validation
    private interface ValidationApi {
        @POST("v1beta/models/gemini-1.5-flash:generateContent")
        suspend fun testApiKey(
            @Query("key") apiKey: String,
            @Body request: GeminiRequest
        ): com.example.sumup.data.remote.dto.GeminiResponse
    }
    
    private val api = retrofit.create(ValidationApi::class.java)
    
    /**
     * Validates the API key by making a simple test request
     * @return ApiKeyValidationResult with status and optional error message
     */
    suspend fun validateApiKey(apiKey: String): ApiKeyValidationResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "your_gemini_api_key_here") {
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "Please enter a valid API key"
            )
        }
        
        try {
            // Create a minimal test request
            val testRequest = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = "Hello")
                        )
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.1f,
                    maxOutputTokens = 5
                )
            )
            
            // Make the API call
            val response = api.testApiKey(apiKey, testRequest)
            
            // Check if we got a valid response
            if (response.candidates.isNotEmpty()) {
                return@withContext ApiKeyValidationResult(
                    isValid = true,
                    errorMessage = null
                )
            } else {
                return@withContext ApiKeyValidationResult(
                    isValid = false,
                    errorMessage = "Invalid response from API"
                )
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Invalid API key format"
                401, 403 -> "Invalid or unauthorized API key"
                429 -> "Rate limit exceeded. Please try again later"
                500, 502, 503 -> "Server error. Please try again later"
                else -> "HTTP error: ${e.code()}"
            }
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = errorMessage
            )
        } catch (e: java.net.SocketTimeoutException) {
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "Connection timeout. Please check your internet connection"
            )
        } catch (e: java.net.UnknownHostException) {
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "No internet connection"
            )
        } catch (e: Exception) {
            android.util.Log.e("ApiKeyValidator", "API key validation failed", e)
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "Validation failed: ${e.message}"
            )
        }
    }
}

/**
 * Result of API key validation
 */
data class ApiKeyValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)
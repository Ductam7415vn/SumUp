package com.example.sumup.utils

import com.example.sumup.data.remote.dto.GeminiContent
import com.example.sumup.data.remote.dto.GeminiGenerationConfig
import com.example.sumup.data.remote.dto.GeminiPart
import com.example.sumup.data.remote.dto.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
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
        // Using Gemini 2.0 Flash - Google's latest fast model
        @POST("v1/models/gemini-2.0-flash:generateContent")
        suspend fun testApiKey(
            @Query("key") apiKey: String,
            @Body request: GeminiRequest
        ): com.example.sumup.data.remote.dto.GeminiResponse
    }

    // Create a separate OkHttpClient with shorter timeout for validation
    private val validationClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)  // Short timeout for validation
        .readTimeout(10, TimeUnit.SECONDS)     // Short timeout for validation
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    // Create a separate Retrofit instance for validation with shorter timeouts
    private val validationRetrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(validationClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = validationRetrofit.create(ValidationApi::class.java)
    
    /**
     * Validates the API key by making a simple test request
     * @return ApiKeyValidationResult with status and optional error message
     */
    suspend fun validateApiKey(apiKey: String): ApiKeyValidationResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        android.util.Log.d("ApiKeyValidator", "=== STARTING API KEY VALIDATION ===")
        android.util.Log.d("ApiKeyValidator", "Key format: ${apiKey.take(10)}...${apiKey.takeLast(4)}")

        // Quick format validation first (no network call)
        if (apiKey.isBlank() || apiKey == "your_gemini_api_key_here") {
            android.util.Log.d("ApiKeyValidator", "Validation failed: blank or placeholder key")
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "Please enter a valid API key"
            )
        }

        // Basic format check: Gemini API keys typically start with "AIza" and are 39 characters
        if (!apiKey.startsWith("AIza")) {
            android.util.Log.d("ApiKeyValidator", "Validation failed: incorrect prefix (expected 'AIza')")
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "Invalid API key format. Gemini API keys should start with 'AIza'"
            )
        }

        if (apiKey.length < 39) {
            android.util.Log.d("ApiKeyValidator", "Validation failed: key too short (${apiKey.length} chars)")
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "API key too short. Please check your key"
            )
        }

        android.util.Log.d("ApiKeyValidator", "Format check passed, making API call...")
        try {
            // Add an overall timeout of 15 seconds for the entire validation
            val result = withTimeout(15_000) {
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
                android.util.Log.d("ApiKeyValidator", "Sending test request to Gemini API...")
                val apiCallStart = System.currentTimeMillis()
                val response = api.testApiKey(apiKey, testRequest)
                val apiCallDuration = System.currentTimeMillis() - apiCallStart
                android.util.Log.d("ApiKeyValidator", "API call completed in ${apiCallDuration}ms")

                // Check if we got a valid response
                if (response.candidates.isNotEmpty()) {
                    val totalDuration = System.currentTimeMillis() - startTime
                    android.util.Log.d("ApiKeyValidator", "Validation SUCCESSFUL (total: ${totalDuration}ms)")
                    ApiKeyValidationResult(
                        isValid = true,
                        errorMessage = null
                    )
                } else {
                    android.util.Log.d("ApiKeyValidator", "Validation failed: empty candidates")
                    ApiKeyValidationResult(
                        isValid = false,
                        errorMessage = "Invalid response from API"
                    )
                }
            }
            return@withContext result
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            val totalDuration = System.currentTimeMillis() - startTime
            android.util.Log.e("ApiKeyValidator", "Validation timeout after ${totalDuration}ms", e)
            return@withContext ApiKeyValidationResult(
                isValid = false,
                errorMessage = "Validation timeout (15s). Please check your internet connection"
            )
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
package com.example.sumup.data.remote.api

import com.example.sumup.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
    
    // High-level method for text summarization
    suspend fun summarizeText(request: SummarizeRequest): SummarizeResponse
}

// Real implementation using Gemini API
class RealGeminiApiService(
    private val retrofit: retrofit2.Retrofit,
    private val apiKey: String
) : GeminiApiService {
    
    private val geminiApi = retrofit.create(GeminiApiService::class.java)
    
    override suspend fun generateContent(apiKey: String, request: GeminiRequest): GeminiResponse {
        return geminiApi.generateContent(apiKey, request)
    }
    
    override suspend fun summarizeText(request: SummarizeRequest): SummarizeResponse {
        val startTime = System.currentTimeMillis()
        
        try {
            val prompt = buildPrompt(request)
            val geminiRequest = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    maxOutputTokens = 1024
                )
            )
            
            val response = generateContent(apiKey, geminiRequest)
            val processingTime = System.currentTimeMillis() - startTime
            
            return parseGeminiResponse(response, processingTime)
            
        } catch (e: Exception) {
            // Log error for debugging
            android.util.Log.e("GeminiAPI", "API call failed: ${e.message}", e)
            
            // Use error handler to convert to AppError
            val appError = GeminiErrorHandler.handleApiError(e)
            
            // For development, fallback to mock response
            if (appError is com.example.sumup.domain.model.AppError.ServerError && 
                appError.message.contains("API key")) {
                android.util.Log.w("GeminiAPI", "Using fallback response due to API key issue")
                return createFallbackResponse(request, System.currentTimeMillis() - startTime)
            }
            
            // Re-throw the error for proper handling
            throw e
        }
    }
    
    private fun buildPrompt(request: SummarizeRequest): String {
        return GeminiPromptBuilder.buildAdvancedPrompt(request)
    }
    
    private fun parseGeminiResponse(response: GeminiResponse, processingTime: Long): SummarizeResponse {
        val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw GeminiApiException.ModelOverloadedException()
        
        return try {
            // Try to parse JSON response
            val gson = com.google.gson.Gson()
            val jsonResponse = gson.fromJson(responseText, AdvancedGeminiResponse::class.java)
            
            // Calculate confidence based on response quality
            val confidence = calculateConfidence(jsonResponse)
            
            SummarizeResponse(
                summary = jsonResponse.summary ?: "Summary not available",
                bullets = jsonResponse.bullets ?: listOf("Content processing completed"),
                confidence = confidence,
                processingTime = processingTime
            )
        } catch (e: Exception) {
            android.util.Log.w("GeminiAPI", "Failed to parse JSON response, using fallback", e)
            // Fallback parsing if JSON fails
            parseFallbackResponse(responseText, processingTime)
        }
    }
    
    private fun calculateConfidence(response: AdvancedGeminiResponse): Float {
        var confidence = 0.5f
        
        // Check summary quality
        if (!response.summary.isNullOrBlank() && response.summary.length > 50) {
            confidence += 0.2f
        }
        
        // Check bullets quality
        if (!response.bullets.isNullOrEmpty() && response.bullets.size >= 3) {
            confidence += 0.2f
        }
        
        // Check if keywords were extracted
        if (!response.keywords.isNullOrEmpty()) {
            confidence += 0.1f
        }
        
        return confidence.coerceIn(0.5f, 0.95f)
    }
    
    private fun parseFallbackResponse(text: String, processingTime: Long): SummarizeResponse {
        // Extract first paragraph as summary
        val paragraphs = text.split("\n\n").filter { it.trim().isNotEmpty() }
        val summary = paragraphs.firstOrNull()?.take(200) ?: "Unable to generate summary"
        
        // Extract bullet points (lines starting with -, *, or numbers)
        val bulletRegex = """^[\-\*•]\s*(.+)$|^\d+\.\s*(.+)$""".toRegex(RegexOption.MULTILINE)
        val bullets = bulletRegex.findAll(text)
            .map { it.groupValues[1].ifEmpty { it.groupValues[2] } }
            .filter { it.isNotEmpty() }
            .take(5)
            .toList()
            .ifEmpty { 
                // Create bullets from paragraphs if no formatted bullets found
                paragraphs.drop(1).take(3).map { it.take(100) }
            }
        
        return SummarizeResponse(
            summary = summary,
            bullets = bullets,
            confidence = 0.65f,
            processingTime = processingTime
        )
    }
    
    // Data class for parsing advanced Gemini response
    private data class AdvancedGeminiResponse(
        val summary: String?,
        val bullets: List<String>?,
        val keywords: List<String>?,
        val readingTime: String?,
        val complexity: String?
    )
    
    private fun createFallbackResponse(request: SummarizeRequest, processingTime: Long): SummarizeResponse {
        val words = request.text.split(" ")
        val bullets = when (request.style) {
            "educational" -> listOf(
                "Core concept: ${words.take(3).joinToString(" ")} represents the fundamental principle",
                "Learning objective: Understanding the relationship between key components",
                "Practical application: How this knowledge applies to real-world scenarios"
            )
            "actionable" -> listOf(
                "Action item: Review and analyze the main findings from this content",
                "Next step: Implement the suggested strategies within the next timeframe",
                "Decision point: Determine the best approach based on available resources"
            )
            "precise" -> listOf(
                "Primary finding: ${words.take(4).joinToString(" ")} with specific details",
                "Technical specification: Exact parameters and requirements outlined",
                "Compliance note: Adherence to established standards and regulations"
            )
            else -> listOf(
                "Key insight from the text that captures the main theme and central message",
                "Supporting evidence or secondary point that reinforces the primary argument",
                "Final conclusion that ties together the important concepts discussed"
            )
        }
        
        return SummarizeResponse(
            summary = "This is a ${request.style} summary of the provided text (fallback mode).",
            bullets = bullets,
            confidence = 0.70f,
            processingTime = processingTime
        )
    }
}

// Mock implementation for development
class MockGeminiApiService : GeminiApiService {
    override suspend fun generateContent(apiKey: String, request: GeminiRequest): GeminiResponse {
        kotlinx.coroutines.delay(1000)
        return GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(GeminiPart(text = "Mock response"))
                    ),
                    finishReason = "STOP",
                    safetyRatings = emptyList()
                )
            )
        )
    }
    
    override suspend fun summarizeText(request: SummarizeRequest): SummarizeResponse {
        kotlinx.coroutines.delay(2000)
        
        val words = request.text.split(" ")
        val bullets = when (request.style) {
            "educational" -> generateEducationalBullets(words)
            "actionable" -> generateActionableBullets(words)
            "precise" -> generatePreciseBullets(words)
            else -> generateGeneralBullets(words)
        }
        
        return SummarizeResponse(
            summary = "This is a ${request.style} summary of the provided text.",
            bullets = bullets,
            confidence = 0.87f,
            processingTime = 1856L
        )
    }    
    
    private fun generateGeneralBullets(words: List<String>): List<String> {
        return listOf(
            "Key insight from the text that captures the main theme and central message",
            "Supporting evidence or secondary point that reinforces the primary argument",
            "Final conclusion that ties together the important concepts discussed"
        )
    }
    
    private fun generateEducationalBullets(words: List<String>): List<String> {
        return listOf(
            "Core concept: ${words.take(3).joinToString(" ")} represents the fundamental principle",
            "Learning objective: Understanding the relationship between key components",
            "Practical application: How this knowledge applies to real-world scenarios"
        )
    }
    
    private fun generateActionableBullets(words: List<String>): List<String> {
        return listOf(
            "Action item: Review and analyze the main findings from this content",
            "Next step: Implement the suggested strategies within the next timeframe",
            "Decision point: Determine the best approach based on available resources"
        )
    }
    
    private fun generatePreciseBullets(words: List<String>): List<String> {
        return listOf(
            "Primary finding: ${words.take(4).joinToString(" ")} with specific details",
            "Technical specification: Exact parameters and requirements outlined",
            "Compliance note: Adherence to established standards and regulations"
        )
    }
}
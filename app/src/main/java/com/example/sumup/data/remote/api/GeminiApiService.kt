package com.example.sumup.data.remote.api

import com.example.sumup.data.remote.dto.SummarizeRequest
import com.example.sumup.data.remote.dto.SummarizeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApiService {
    @POST("v1/summarize")
    suspend fun summarizeText(
        @Body request: SummarizeRequest
    ): SummarizeResponse
}

// Mock implementation for development
class MockGeminiApiService : GeminiApiService {
    override suspend fun summarizeText(request: SummarizeRequest): SummarizeResponse {
        // Simulate network delay
        kotlinx.coroutines.delay(2000)
        
        // Generate mock bullets based on input text
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
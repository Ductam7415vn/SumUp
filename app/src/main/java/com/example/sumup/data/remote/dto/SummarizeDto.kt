package com.example.sumup.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request DTOs for Gemini API
data class GeminiRequest(
    @SerializedName("contents")
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig")
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    @SerializedName("parts")
    val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text")
    val text: String
)

data class GeminiGenerationConfig(
    @SerializedName("temperature")
    val temperature: Float = 0.7f,
    @SerializedName("topK")
    val topK: Int = 40,
    @SerializedName("topP")
    val topP: Float = 0.95f,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 1024
)

// Response DTOs from Gemini API
data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    @SerializedName("content")
    val content: GeminiContent,
    @SerializedName("finishReason")
    val finishReason: String?,
    @SerializedName("safetyRatings")
    val safetyRatings: List<GeminiSafetyRating>?
)

data class GeminiSafetyRating(
    @SerializedName("category")
    val category: String,
    @SerializedName("probability")
    val probability: String
)

// App-specific DTOs (for internal use)
data class SummarizeRequest(
    val text: String,
    val style: String,
    val maxLength: Int,
    val language: String = "auto"
)

data class SummarizeResponse(
    val summary: String,
    val bullets: List<String>,
    val confidence: Float,
    val processingTime: Long,
    // Multi-tier content fields
    val briefOverview: String? = null,
    val detailedSummary: String? = null,
    val keyInsights: List<String>? = null,
    val actionItems: List<String>? = null,
    val keywords: List<String>? = null
)
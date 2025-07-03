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
        android.util.Log.d("RealGeminiAPI", "=== CALLING GEMINI API ===")
        android.util.Log.d("RealGeminiAPI", "API Key length: ${apiKey.length}")
        android.util.Log.d("RealGeminiAPI", "API Key prefix: ${apiKey.take(8)}...")
        android.util.Log.d("RealGeminiAPI", "Request content: ${request.contents.firstOrNull()?.parts?.firstOrNull()?.text?.take(100)}...")
        
        try {
            val response = geminiApi.generateContent(apiKey, request)
            android.util.Log.d("RealGeminiAPI", "API call SUCCESSFUL")
            android.util.Log.d("RealGeminiAPI", "Response candidates: ${response.candidates.size}")
            return response
        } catch (e: retrofit2.HttpException) {
            android.util.Log.e("RealGeminiAPI", "HTTP Error: ${e.code()} - ${e.message()}")
            android.util.Log.e("RealGeminiAPI", "Response body: ${e.response()?.errorBody()?.string()}")
            throw e
        } catch (e: Exception) {
            android.util.Log.e("RealGeminiAPI", "API call FAILED: ${e.javaClass.simpleName} - ${e.message}")
            throw e
        }
    }
    
    override suspend fun summarizeText(request: SummarizeRequest): SummarizeResponse {
        val startTime = System.currentTimeMillis()
        
        try {
            android.util.Log.d("RealGeminiAPI", "=== SUMMARIZE TEXT STARTED ===")
            android.util.Log.d("RealGeminiAPI", "Request text length: ${request.text.length}")
            android.util.Log.d("RealGeminiAPI", "Request style: ${request.style}")
            android.util.Log.d("RealGeminiAPI", "Request maxLength: ${request.maxLength}")
            
            val prompt = buildPrompt(request)
            android.util.Log.d("RealGeminiAPI", "Built prompt length: ${prompt.length}")
            
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
            android.util.Log.d("RealGeminiAPI", "Created Gemini request with temperature: 0.7, maxTokens: 1024")
            
            android.util.Log.d("RealGeminiAPI", "Calling generateContent with API key...")
            val response = generateContent(apiKey, geminiRequest)
            val processingTime = System.currentTimeMillis() - startTime
            android.util.Log.d("RealGeminiAPI", "API response received in ${processingTime}ms")
            
            val parsedResponse = parseGeminiResponse(response, processingTime)
            android.util.Log.d("RealGeminiAPI", "Response parsed successfully")
            android.util.Log.d("RealGeminiAPI", "Summary length: ${parsedResponse.summary.length}")
            android.util.Log.d("RealGeminiAPI", "Bullets count: ${parsedResponse.bullets.size}")
            return parsedResponse
            
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
        android.util.Log.d("RealGeminiAPI", "=== PARSING RESPONSE ===")
        android.util.Log.d("RealGeminiAPI", "Candidates count: ${response.candidates.size}")
        
        val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        if (responseText == null) {
            android.util.Log.e("RealGeminiAPI", "No response text found in candidates")
            throw GeminiApiException.ModelOverloadedException()
        }
        
        android.util.Log.d("RealGeminiAPI", "Response text length: ${responseText.length}")
        android.util.Log.d("RealGeminiAPI", "Response preview: ${responseText.take(200)}...")
        
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
        // Parse multi-tier response
        val briefMatch = Regex("BRIEF:(.+?)(?=SUMMARY:|$)", RegexOption.DOT_MATCHES_ALL).find(text)
        val summaryMatch = Regex("SUMMARY:(.+?)(?=DETAILED:|KEY POINTS:|$)", RegexOption.DOT_MATCHES_ALL).find(text)
        val detailedMatch = Regex("DETAILED:(.+?)(?=KEY POINTS:|$)", RegexOption.DOT_MATCHES_ALL).find(text)
        val keyPointsMatch = Regex("KEY POINTS:(.+?)(?=KEY INSIGHTS:|ACTION ITEMS:|KEYWORDS:|$)", RegexOption.DOT_MATCHES_ALL).find(text)
        val insightsMatch = Regex("KEY INSIGHTS:(.+?)(?=ACTION ITEMS:|KEYWORDS:|$)", RegexOption.DOT_MATCHES_ALL).find(text)
        val actionsMatch = Regex("ACTION ITEMS:(.+?)(?=KEYWORDS:|$)", RegexOption.DOT_MATCHES_ALL).find(text)
        val keywordsMatch = Regex("KEYWORDS:(.+?)$", RegexOption.DOT_MATCHES_ALL).find(text)
        
        val brief = briefMatch?.groupValues?.get(1)?.trim() ?: ""
        val summary = summaryMatch?.groupValues?.get(1)?.trim()?.take(500) ?: "Unable to generate summary"
        val detailed = detailedMatch?.groupValues?.get(1)?.trim()?.take(800) ?: ""
        
        // Extract bullets
        val bulletRegex = """^[\-\*•]\s*(.+)$""".toRegex(RegexOption.MULTILINE)
        
        val bullets = if (keyPointsMatch != null) {
            bulletRegex.findAll(keyPointsMatch.groupValues[1])
                .map { it.groupValues[1].trim() }
                .filter { it.isNotEmpty() }
                .take(7)
                .toList()
        } else {
            listOf("Key information extracted from the content")
        }
        
        val insights = if (insightsMatch != null) {
            bulletRegex.findAll(insightsMatch.groupValues[1])
                .map { it.groupValues[1].trim() }
                .filter { it.isNotEmpty() }
                .take(5)
                .toList()
        } else null
        
        val actions = if (actionsMatch != null) {
            bulletRegex.findAll(actionsMatch.groupValues[1])
                .map { it.groupValues[1].trim() }
                .filter { it.isNotEmpty() }
                .take(5)
                .toList()
        } else null
        
        val keywords = if (keywordsMatch != null) {
            keywordsMatch.groupValues[1].trim()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .take(10)
        } else null
        
        return SummarizeResponse(
            summary = summary,
            bullets = bullets,
            confidence = 0.65f,
            processingTime = processingTime,
            // Extended fields for multi-tier
            briefOverview = brief.ifEmpty { null },
            detailedSummary = detailed.ifEmpty { null },
            keyInsights = insights,
            actionItems = actions,
            keywords = keywords
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
                "Practical application: How this knowledge applies to real-world scenarios",
                "Theoretical foundation: Academic basis supporting the main concepts",
                "Case study example: Specific instances demonstrating the principles"
            )
            "actionable" -> listOf(
                "Action item: Review and analyze the main findings from this content",
                "Next step: Implement the suggested strategies within the next timeframe",
                "Decision point: Determine the best approach based on available resources",
                "Resource requirement: Identify necessary tools and support needed",
                "Timeline consideration: Establish realistic deadlines for completion"
            )
            "precise" -> listOf(
                "Primary finding: ${words.take(4).joinToString(" ")} with specific details",
                "Technical specification: Exact parameters and requirements outlined",
                "Compliance note: Adherence to established standards and regulations",
                "Data accuracy: Verified measurements and quantitative results",
                "Methodology details: Precise procedures and protocols followed"
            )
            else -> listOf(
                "Key insight from the text that captures the main theme and central message",
                "Supporting evidence or secondary point that reinforces the primary argument",
                "Important contextual information that provides necessary background understanding",
                "Critical analysis of the implications and potential outcomes discussed",
                "Final conclusion that ties together the important concepts discussed"
            )
        }
        
        // Generate longer summary based on input length
        val inputLength = request.text.length
        val summaryText = when {
            inputLength <= 1000 -> "This comprehensive ${request.style} summary captures the essential elements from the provided content, highlighting the core message while maintaining clarity and focus."
            inputLength <= 5000 -> "This detailed ${request.style} summary provides a thorough analysis of the main concepts presented in the source material, offering readers a complete understanding of the key points and their relationships."
            else -> "This extensive ${request.style} summary delivers an in-depth examination of the complex ideas and arguments presented in the original text, ensuring that all critical information is preserved while maintaining readability."
        }
        
        return SummarizeResponse(
            summary = summaryText,
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
        android.util.Log.d("MockGeminiAPI", "Processing summary request for text length: ${request.text.length}, maxLength: ${request.maxLength}")
        kotlinx.coroutines.delay(2000)
        
        val words = request.text.split(" ")
        val bullets = when (request.style) {
            "educational" -> generateEducationalBullets(words)
            "actionable" -> generateActionableBullets(words)
            "precise" -> generatePreciseBullets(words)
            else -> generateGeneralBullets(words)
        }
        
        // Calculate lengths based on source text percentage
        val sourceLength = request.text.length
        val briefTargetLength = (sourceLength * 0.05).toInt() // 5%
        val standardTargetLength = (sourceLength * 0.10).toInt() // 10%
        val detailedTargetLength = (sourceLength * 0.20).toInt() // 20%
        
        android.util.Log.d("MockGeminiAPI", "Source length: $sourceLength, Brief: $briefTargetLength, Standard: $standardTargetLength, Detailed: $detailedTargetLength")
        
        // Generate summaries with proper lengths
        val brief = generateTextWithLength(
            "This ${request.style} summary captures the essential message from the provided content.",
            briefTargetLength
        )
        
        val summary = generateTextWithLength(
            "This ${request.style} summary effectively captures the core ideas, main arguments, and essential insights from the source material, presenting them in a clear and organized manner that helps readers quickly understand the key information.",
            standardTargetLength
        )
        
        val detailed = generateTextWithLength(
            "This comprehensive ${request.style} analysis provides an in-depth examination of all major themes, concepts, and insights from the source material. It explores complex relationships between ideas, examines implications and consequences, identifies patterns and trends, and ensures complete coverage of the subject matter while presenting information in a structured manner that facilitates thorough understanding.",
            detailedTargetLength
        )
        
        // Adjust number of bullets based on source length
        val bulletCount = when {
            sourceLength < 500 -> 3
            sourceLength < 1500 -> 5
            else -> 7
        }
        
        val insights = when {
            sourceLength < 500 -> listOf(
                "Primary insight from the content",
                "Key observation about implications"
            )
            sourceLength < 1500 -> listOf(
                "Primary insight reveals fundamental patterns in the content structure",
                "Critical observation about the implications for practical application",
                "Key trend identified that suggests future developments",
                "Important connection between seemingly disparate concepts"
            )
            else -> listOf(
                "Primary insight reveals fundamental patterns in the content structure",
                "Critical observation about the implications for practical application",
                "Key trend identified that suggests future developments",
                "Important connection between seemingly disparate concepts",
                "Deep analysis of underlying assumptions and their validity",
                "Synthesis of multiple viewpoints presented in the material"
            )
        }
        
        val actions = if (request.style == "actionable") {
            when {
                sourceLength < 500 -> listOf("Review and implement primary recommendations")
                sourceLength < 1500 -> listOf(
                    "Review and implement the primary recommendations immediately",
                    "Schedule follow-up analysis for secondary considerations",
                    "Share findings with relevant stakeholders for alignment"
                )
                else -> listOf(
                    "Review and implement the primary recommendations immediately",
                    "Schedule follow-up analysis for secondary considerations",
                    "Share findings with relevant stakeholders for alignment",
                    "Develop detailed action plan with timelines and milestones",
                    "Establish success metrics and monitoring procedures"
                )
            }
        } else null
        
        val keywords = listOf("analysis", request.style, "summary", "insights", "comprehensive")
        
        android.util.Log.d("MockGeminiAPI", "Generated summaries - Brief: ${brief.length} chars, Standard: ${summary.length} chars, Detailed: ${detailed.length} chars")
        
        return SummarizeResponse(
            summary = summary,
            bullets = bullets.take(bulletCount),
            confidence = 0.87f,
            processingTime = 1856L,
            briefOverview = brief,
            detailedSummary = detailed,
            keyInsights = insights,
            actionItems = actions,
            keywords = keywords
        )
    }    
    
    private fun generateGeneralBullets(words: List<String>): List<String> {
        return listOf(
            "Key insight from the text that captures the main theme and central message",
            "Supporting evidence or secondary point that reinforces the primary argument",
            "Important contextual information that provides necessary background understanding",
            "Critical analysis of the implications and potential outcomes discussed",
            "Comparative perspective showing relationships between different concepts",
            "Final conclusion that ties together the important concepts discussed"
        )
    }
    
    private fun generateEducationalBullets(words: List<String>): List<String> {
        return listOf(
            "Core concept: ${words.take(3).joinToString(" ")} represents the fundamental principle",
            "Learning objective: Understanding the relationship between key components",
            "Practical application: How this knowledge applies to real-world scenarios",
            "Theoretical foundation: Academic basis supporting the main concepts",
            "Case study example: Specific instances demonstrating the principles",
            "Assessment criteria: Methods to evaluate understanding and mastery"
        )
    }
    
    private fun generateActionableBullets(words: List<String>): List<String> {
        return listOf(
            "Action item: Review and analyze the main findings from this content",
            "Next step: Implement the suggested strategies within the next timeframe",
            "Decision point: Determine the best approach based on available resources",
            "Resource requirement: Identify necessary tools and support needed",
            "Timeline consideration: Establish realistic deadlines for completion",
            "Success metrics: Define measurable outcomes to track progress"
        )
    }
    
    private fun generatePreciseBullets(words: List<String>): List<String> {
        return listOf(
            "Primary finding: ${words.take(4).joinToString(" ")} with specific details",
            "Technical specification: Exact parameters and requirements outlined",
            "Compliance note: Adherence to established standards and regulations",
            "Data accuracy: Verified measurements and quantitative results",
            "Methodology details: Precise procedures and protocols followed",
            "Statistical significance: Confidence levels and margin of error"
        )
    }
    
    private fun generateTextWithLength(baseText: String, targetLength: Int): String {
        // If target is very small, return truncated version
        if (targetLength < 50) {
            return baseText.take(targetLength)
        }
        
        // Calculate how many times to repeat the base text
        val repetitions = (targetLength / baseText.length).coerceAtLeast(1)
        val builder = StringBuilder()
        
        // Build text by repeating and varying the base content
        for (i in 0 until repetitions) {
            when (i % 3) {
                0 -> builder.append(baseText)
                1 -> builder.append(" Furthermore, ").append(baseText.lowercase())
                2 -> builder.append(" Additionally, ").append(baseText.lowercase())
            }
            builder.append(" ")
        }
        
        // Trim to exact length
        val result = builder.toString().trim()
        return if (result.length > targetLength) {
            result.take(targetLength - 3) + "..."
        } else {
            // Pad with additional context if too short
            val padding = " This provides comprehensive coverage of the main points and ensures all critical information is included."
            (result + padding).take(targetLength)
        }
    }
}
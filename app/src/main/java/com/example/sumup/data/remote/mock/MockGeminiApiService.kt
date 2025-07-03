package com.example.sumup.data.remote.mock

import com.example.sumup.data.remote.api.GeminiApiService
import com.example.sumup.data.remote.dto.*
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

/**
 * Mock implementation of GeminiApiService for demo mode
 * Provides realistic responses without requiring an API key
 */
class MockGeminiApiService @Inject constructor() : GeminiApiService {
    
    override suspend fun generateContent(apiKey: String, request: GeminiRequest): GeminiResponse {
        // Simulate network delay
        delay(Random.nextLong(1500, 3000))
        
        // Extract text from request
        val inputText = request.contents.firstOrNull()?.parts?.firstOrNull()?.text ?: ""
        
        // Generate mock summary based on input length
        val mockSummary = when {
            inputText.length < 100 -> generateShortMockSummary(inputText)
            inputText.length < 500 -> generateMediumMockSummary(inputText)
            else -> generateLongMockSummary(inputText)
        }
        
        return GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(
                            GeminiPart(text = mockSummary)
                        )
                    ),
                    finishReason = "STOP",
                    safetyRatings = listOf(
                        GeminiSafetyRating(
                            category = "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                            probability = "NEGLIGIBLE"
                        ),
                        GeminiSafetyRating(
                            category = "HARM_CATEGORY_HATE_SPEECH",
                            probability = "NEGLIGIBLE"
                        ),
                        GeminiSafetyRating(
                            category = "HARM_CATEGORY_HARASSMENT",
                            probability = "NEGLIGIBLE"
                        ),
                        GeminiSafetyRating(
                            category = "HARM_CATEGORY_DANGEROUS_CONTENT",
                            probability = "NEGLIGIBLE"
                        )
                    )
                )
            )
        )
    }
    
    override suspend fun summarizeText(request: SummarizeRequest): SummarizeResponse {
        // Simulate using generateContent under the hood
        val geminiRequest = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = request.text))
                )
            )
        )
        
        val response = generateContent("mock-key", geminiRequest)
        val summary = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        
        return SummarizeResponse(
            summary = summary,
            bullets = listOf("Mock key point 1", "Mock key point 2", "Mock key point 3"),
            confidence = 0.95f,
            processingTime = 2000L,
            briefOverview = "Brief overview of the content",
            detailedSummary = summary,
            keyInsights = listOf("Key insight 1", "Key insight 2"),
            actionItems = listOf("Action item 1", "Action item 2"),
            keywords = listOf("keyword1", "keyword2", "keyword3")
        )
    }
    
    private fun generateShortMockSummary(input: String): String {
        val wordCount = input.split("\\s+".toRegex()).size
        return """
            ## Summary
            This is a demo summary generated in mock mode. The original text contains approximately $wordCount words.
            
            ## Key Points
            • Main idea extracted from the beginning of your text
            • Supporting details identified in the content
            • Conclusion or final thoughts from the text
            
            ## Brief Overview
            The text discusses various topics that are summarized here in a concise manner. This mock summary demonstrates how the app will format and present summaries when connected to the real Gemini AI API.
            
            *Note: This is a demo summary. Add your Gemini API key in Settings for real AI-powered summaries.*
        """.trimIndent()
    }
    
    private fun generateMediumMockSummary(input: String): String {
        val wordCount = input.split("\\s+".toRegex()).size
        val firstSentence = input.split(".").firstOrNull()?.trim() ?: "Your text"
        
        return """
            ## Executive Summary
            This comprehensive analysis covers the main themes presented in your ${wordCount}-word text. $firstSentence...
            
            ## Detailed Key Points
            • **Primary Theme**: The central focus appears to be on key concepts introduced early in the text
            • **Supporting Arguments**: Several supporting points reinforce the main thesis
            • **Evidence Presented**: The text includes various examples and data points
            • **Implications**: The content suggests important implications for readers
            • **Conclusions**: Final thoughts tie together the main arguments effectively
            
            ## Analysis
            The document presents a well-structured argument with clear progression from introduction to conclusion. The writing style is appropriate for the intended audience, and the key messages are communicated effectively.
            
            ## Recommendations
            Based on this summary, readers should:
            1. Focus on the main themes identified above
            2. Review supporting evidence for deeper understanding
            3. Consider the practical implications discussed
            
            *Note: This is a demo summary. Add your Gemini API key in Settings for real AI-powered summaries.*
        """.trimIndent()
    }
    
    private fun generateLongMockSummary(input: String): String {
        val wordCount = input.split("\\s+".toRegex()).size
        val sentences = input.split(".").take(3).joinToString(". ")
        
        return """
            ## Comprehensive Summary Analysis
            
            ### Document Overview
            This extensive document contains approximately $wordCount words covering multiple interconnected topics. The analysis below breaks down the content into digestible sections for easier comprehension.
            
            ### Introduction Summary
            $sentences...
            
            ### Main Themes Identified
            
            #### Theme 1: Primary Focus Area
            The document's primary focus centers on key concepts that are introduced early and developed throughout. These concepts form the foundation for subsequent arguments and discussions.
            
            #### Theme 2: Supporting Elements
            Secondary themes provide context and support for the main arguments. These include:
            • Historical background and context
            • Current state analysis
            • Future implications and predictions
            • Practical applications
            
            #### Theme 3: Conclusions and Recommendations
            The document concludes with actionable insights and recommendations based on the analysis presented.
            
            ### Detailed Key Points
            
            1. **Opening Arguments**
               - Establishes the importance of the topic
               - Provides necessary background information
               - Sets up the framework for discussion
            
            2. **Central Analysis**
               - Examines multiple perspectives
               - Presents evidence and data
               - Draws connections between concepts
            
            3. **Supporting Evidence**
               - Statistical data and research findings
               - Case studies and real-world examples
               - Expert opinions and citations
            
            4. **Implications**
               - Short-term impacts
               - Long-term consequences
               - Stakeholder considerations
            
            5. **Recommendations**
               - Immediate action items
               - Strategic long-term planning
               - Risk mitigation strategies
            
            ### Critical Insights
            
            The analysis reveals several critical insights that warrant special attention:
            
            • **Insight 1**: The interconnected nature of the topics suggests a holistic approach is necessary
            • **Insight 2**: Implementation challenges require careful consideration and planning
            • **Insight 3**: Stakeholder buy-in is crucial for successful outcomes
            • **Insight 4**: Continuous monitoring and adaptation will be essential
            
            ### Action Items
            
            Based on this comprehensive analysis, the following actions are recommended:
            
            1. Review and internalize the key themes identified
            2. Develop an implementation strategy based on the recommendations
            3. Establish metrics for measuring success
            4. Create feedback loops for continuous improvement
            5. Engage stakeholders early and often
            
            ### Conclusion
            
            This summary captures the essence of the ${wordCount}-word document, distilling complex ideas into actionable insights. The multi-layered analysis ensures that readers at all levels can extract value from the content.
            
            *Note: This is a demo summary generated in mock mode. For actual AI-powered summaries that analyze your specific content, please add your Gemini API key in the Settings menu.*
        """.trimIndent()
    }
}
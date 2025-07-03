package com.example.sumup.data.remote.api

import com.example.sumup.data.remote.dto.SummarizeRequest

object GeminiPromptBuilder {

    fun buildAdvancedPrompt(request: SummarizeRequest): String {
        val stylePrompt = getStylePrompt(request.style)
        val sourceLength = request.text.length
        val lengthConstraint = getPercentageBasedLengthConstraint(sourceLength)
        val outputFormat = getOutputFormat()
        val languageHint = getLanguageHint(request.language)

        return """
            You are an expert summarization AI assistant specialized in creating high-quality, ${request.style} summaries.
            
            ${languageHint}
            
            ${stylePrompt}
            
            ${lengthConstraint}
            
            ${outputFormat}
            
            CRITICAL REQUIREMENTS:
            1. ACCURACY: Preserve the exact meaning and nuance of the original text
            2. CLARITY: Use simple, clear language appropriate for the target audience
            3. STRUCTURE: Organize information logically with smooth transitions
            4. COMPLETENESS: Cover all key points without redundancy
            5. ACTIONABILITY: Make bullet points specific and actionable
            6. NO HALLUCINATION: Never add information not present in the source text
            7. FORMAT: You MUST respond in valid JSON format as specified above
            
            QUALITY GUIDELINES:
            - Start the summary with the most important insight
            - Each bullet point should be self-contained and meaningful
            - Use active voice and strong verbs
            - Avoid jargon unless present in the original text
            - Maintain consistent tone throughout
            
            SOURCE TEXT TO SUMMARIZE:
            ---
            ${request.text}
            ---
            
            Generate your response now in the exact JSON format specified above.
        """.trimIndent()
    }

    private fun getLanguageHint(language: String): String = when (language) {
        "auto" -> "LANGUAGE: Detect the source language and respond in the same language."
        "en" -> "LANGUAGE: Respond in English."
        "vi" -> "LANGUAGE: Respond in Vietnamese (Tiếng Việt)."
        else -> "LANGUAGE: Respond in $language."
    }

    private fun getStylePrompt(style: String): String = when (style) {
        "educational" -> """
            EDUCATIONAL STYLE REQUIREMENTS:
            - PRIMARY GOAL: Transform content into teachable moments
            - LEARNING FOCUS: Identify the core lesson or principle being taught
            - SIMPLIFICATION: Break complex concepts into digestible parts
            - EXAMPLES: Extract or infer practical applications
            - STRUCTURE: Use "What, Why, How" framework when applicable
            - DEFINITIONS: Clarify technical terms in plain language
            - TAKEAWAY: End with clear learning outcomes
            
            TONE: Patient, encouraging, explanatory (like a skilled teacher)
        """.trimIndent()

        "actionable" -> """
            ACTIONABLE STYLE REQUIREMENTS:
            - PRIMARY GOAL: Convert information into executable tasks
            - ACTION FOCUS: Extract specific "to-do" items from the content
            - PRIORITIZATION: Order by impact and urgency (if determinable)
            - SPECIFICITY: Include WHO, WHAT, WHEN details where available
            - DECISION POINTS: Highlight choices that need to be made
            - OUTCOMES: Link actions to expected results
            - NEXT STEPS: Always end with immediate next action
            
            TONE: Direct, motivating, results-oriented (like a project manager)
        """.trimIndent()

        "precise" -> """
            PRECISE STYLE REQUIREMENTS:
            - PRIMARY GOAL: Capture exact details without interpretation
            - ACCURACY FOCUS: Preserve numbers, dates, specifications exactly
            - TERMINOLOGY: Use the exact technical terms from source
            - DATA POINTS: Include all metrics, measurements, percentages
            - FACTUAL ONLY: No inference or estimation allowed
            - STRUCTURE: Organize by categories or importance of facts
            - VERIFICATION: Double-check accuracy of extracted information
            
            TONE: Clinical, objective, fact-based (like a technical auditor)
        """.trimIndent()

        else -> """
            GENERAL STYLE REQUIREMENTS:
            - PRIMARY GOAL: Create a well-rounded summary for general audience
            - BALANCE: Cover main points with appropriate weight
            - INSIGHTS: Extract both primary message and supporting points
            - CLARITY: Use accessible language for broad understanding
            - COMPLETENESS: Don't miss any critical information
            - FLOW: Ensure logical progression of ideas
            - USEFULNESS: Focus on what readers need to know
            
            TONE: Professional, informative, neutral (like a news reporter)
        """.trimIndent()
    }

    private fun getPercentageBasedLengthConstraint(sourceLength: Int): String {
        // Calculate target lengths based on source text length
        val briefLength = (sourceLength * 0.05).toInt().coerceAtLeast(50) // 5% of source, min 50 chars
        val standardLength = (sourceLength * 0.10).toInt().coerceAtLeast(100) // 10% of source, min 100 chars
        val detailedLength = (sourceLength * 0.20).toInt().coerceAtLeast(200) // 20% of source, min 200 chars
        
        return """
        LENGTH REQUIREMENTS:
        - BRIEF: Exactly ${briefLength} characters (5% of source text)
        - SUMMARY: Exactly ${standardLength} characters (10% of source text)
        - DETAILED: Exactly ${detailedLength} characters (20% of source text)
        - Each bullet point: 50-100 characters
        - Provide 5-7 bullet points based on content richness
        - CRITICAL: You MUST meet these exact character counts for each section
        - If source is short, still maintain the percentage ratios
        - Focus on extracting TRUE CONTENT - no filler or repetition
    """.trimIndent()
    }

    private fun getOutputFormat(): String = """
        OUTPUT FORMAT:
        Please provide your response in PLAIN TEXT with the following structure:
        
        BRIEF:
        [One concise sentence that is exactly 5% of the source text length]
        
        SUMMARY:
        [Write a comprehensive paragraph that is exactly 10% of the source text length]
        
        DETAILED:
        [Expanded analysis with full context that is exactly 20% of the source text length]
        
        KEY POINTS:
        • [First key point]
        • [Second key point]
        • [Third key point]
        • [Fourth key point]
        • [Fifth key point]
        • [Additional points as needed, 5-7 total]
        
        KEY INSIGHTS:
        • [Deep insight about implications]
        • [Pattern or trend identified]
        • [Critical observation]
        • [3-5 insights total]
        
        ACTION ITEMS:
        • [Specific action if applicable]
        • [Next steps if relevant]
        • [Include only if actionable content exists]
        
        KEYWORDS:
        [keyword1, keyword2, keyword3, keyword4, keyword5]
        
        FORMATTING RULES:
        - Each section must start with its label in CAPS followed by colon
        - Write brief as one concise sentence
        - Summary should be one detailed paragraph
        - Detailed should expand with more context
        - Use bullet symbol (•) for all bullet points
        - Each bullet point should be complete and standalone
        - Include ACTION ITEMS only if the content suggests actions
        - Keywords should be comma-separated, lowercase
    """.trimIndent()

    fun buildMultiModalPrompt(
        text: String,
        style: String,
        additionalContext: Map<String, Any>
    ): String {
        val basePrompt = buildAdvancedPrompt(SummarizeRequest(text, style, 500))
        val contextInfo = additionalContext.entries.joinToString("\n") {
            "${it.key}: ${it.value}"
        }

        return """
            $basePrompt
            
            ADDITIONAL CONTEXT:
            $contextInfo
            
            Consider this context when creating the summary.
        """.trimIndent()
    }

    /**
     * Calculate dynamic summary length based on input text length
     * Returns a target summary length that scales with input size
     */
    private fun calculateDynamicLength(text: String, baseMaxLength: Int): Int {
        // Just use the requested length - don't override it
        // The baseMaxLength already contains the multiplier applied by the UI
        return baseMaxLength
    }
}
package com.example.sumup.data.remote.api

import com.example.sumup.data.remote.dto.SummarizeRequest

object GeminiPromptBuilder {
    
    fun buildAdvancedPrompt(request: SummarizeRequest): String {
        val stylePrompt = getStylePrompt(request.style)
        val lengthConstraint = getLengthConstraint(request.maxLength)
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
    
    private fun getLengthConstraint(maxLength: Int): String = """
        LENGTH REQUIREMENTS:
        - Summary paragraph: ${(maxLength * 0.3).toInt()} characters maximum
        - Each bullet point: ${(maxLength * 0.15).toInt()} characters maximum
        - Total response: Must fit within $maxLength characters
        - Provide 3-5 bullet points based on content importance
    """.trimIndent()
    
    private fun getOutputFormat(): String = """
        OUTPUT FORMAT:
        Please provide your response in PLAIN TEXT with the following structure:
        
        SUMMARY:
        [Write a comprehensive paragraph here]
        
        KEY POINTS:
        • [First key point]
        • [Second key point]
        • [Third key point]
        • [Additional points as needed, 3-5 total]
        
        FORMATTING RULES:
        - Start with "SUMMARY:" on its own line
        - Write the summary as one paragraph
        - Start bullet points section with "KEY POINTS:" on its own line
        - Use bullet symbol (•) for each point
        - Each bullet point should be complete and standalone
        - Keep each bullet point concise (50-150 characters)
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
}
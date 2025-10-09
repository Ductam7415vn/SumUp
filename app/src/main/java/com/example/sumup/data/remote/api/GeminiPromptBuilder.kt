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
            - PRIMARY GOAL: Transform content into effective study materials
            - LEARNING FOCUS: Extract key concepts, definitions, and principles
            - STRUCTURE: Organize information in a logical learning sequence
            - MEMORY AIDS: Create memorable bullet points and summaries
            - EXAMPLES: Include concrete examples from the text
            - REVIEW POINTS: Highlight what to remember for exams/tests
            - STUDY TIPS: Suggest how to apply or remember the information
            
            TONE: Clear, instructive, encouraging (like a helpful tutor)
        """.trimIndent()

        "actionable" -> """
            PROFESSIONAL STYLE REQUIREMENTS:
            - PRIMARY GOAL: Create executive-ready summaries for business use
            - BUSINESS FOCUS: Extract actionable insights and recommendations
            - STRUCTURE: Lead with key findings, follow with supporting details
            - METRICS: Highlight any numbers, percentages, or KPIs
            - DECISIONS: Identify decision points and recommendations
            - NEXT STEPS: Clear action items with ownership when possible
            - TIME SENSITIVITY: Note any deadlines or urgent matters
            
            TONE: Professional, concise, action-oriented (like a business consultant)
        """.trimIndent()

        "precise" -> """
            ACADEMIC STYLE REQUIREMENTS:
            - PRIMARY GOAL: Maintain scholarly rigor and accuracy
            - CITATION FOCUS: Preserve all references, sources, and attributions
            - TERMINOLOGY: Keep all technical and academic terms intact
            - METHODOLOGY: Note research methods or approaches mentioned
            - FINDINGS: Clearly separate findings from interpretations
            - CRITICAL ANALYSIS: Include any critiques or limitations mentioned
            - CONTEXT: Maintain theoretical framework and academic context
            
            TONE: Formal, objective, scholarly (like an academic reviewer)
        """.trimIndent()

        "simplified" -> """
            SIMPLE LANGUAGE STYLE REQUIREMENTS:
            - PRIMARY GOAL: Make complex content accessible to everyone
            - SIMPLIFICATION: Use everyday words instead of jargon
            - EXPLANATIONS: Define any unavoidable technical terms
            - SHORT SENTENCES: Keep sentences under 20 words when possible
            - EXAMPLES: Use relatable analogies and comparisons
            - STRUCTURE: One main idea per paragraph or bullet point
            - CLARITY: If grandma can't understand it, simplify more
            
            TONE: Friendly, clear, conversational (like explaining to a friend)
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
        LENGTH REQUIREMENTS - EXTREMELY IMPORTANT:
        Source text length: ${sourceLength} characters

        BRIEF (5%): Write EXACTLY ${briefLength} characters - ONE SHORT SENTENCE only
        - Must be 1-2 sentences maximum
        - Focus on the single most important point
        - MUST BE SHORTER than SUMMARY

        SUMMARY (10%): Write EXACTLY ${standardLength} characters - ONE PARAGRAPH
        - Must be 3-5 sentences
        - Cover main points comprehensively
        - MUST BE LONGER than BRIEF but SHORTER than DETAILED

        DETAILED (20%): Write EXACTLY ${detailedLength} characters - MULTIPLE PARAGRAPHS
        - Must be 6-10 sentences
        - Include full context, examples, and implications
        - MUST BE THE LONGEST section - at least TWICE as long as SUMMARY

        CRITICAL: These are THREE DIFFERENT summaries with DIFFERENT LENGTHS!
        - BRIEF < SUMMARY < DETAILED (each progressively longer)
        - Each bullet point: 50-100 characters
        - Provide 5-7 bullet points based on content richness
    """.trimIndent()
    }

    private fun getOutputFormat(): String = """
        OUTPUT FORMAT - STRICT PLAIN TEXT (NOT JSON OR MARKDOWN):
        You MUST respond in PLAIN TEXT format. DO NOT use JSON, Markdown headers (##, ###), or any formatting symbols.
        Follow this EXACT structure with NOTHING ELSE:

        BRIEF:
        [Write ONE SHORT SENTENCE here - must be significantly shorter than SUMMARY]

        SUMMARY:
        [Write ONE FULL PARAGRAPH here - must be longer than BRIEF but shorter than DETAILED]

        DETAILED:
        [Write MULTIPLE PARAGRAPHS here - must be the LONGEST section, at least twice as long as SUMMARY]

        KEY POINTS:
        • [First key point - complete sentence]
        • [Second key point - complete sentence]
        • [Third key point - complete sentence]
        • [Fourth key point - complete sentence]
        • [Fifth key point - complete sentence]
        • [Additional points, 5-7 total]

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

        CORRECT EXAMPLE (for 1000 character source):

        BRIEF:
        The article explains investment strategies that work in any market.

        SUMMARY:
        The article explains investment strategies that allow profit in any market condition through diversification. It covers three main approaches: long positions, short positions, and hedging techniques. These strategies help investors protect capital while seeking returns regardless of market direction.

        DETAILED:
        The article provides a comprehensive explanation of modern investment strategies that enable investors to generate profits regardless of market direction. It discusses the historical development of these approaches, starting from traditional buy-and-hold methods to more sophisticated hedging techniques. The content explores three primary strategies: establishing long positions in undervalued assets, taking short positions when markets are overpriced, and using derivatives to hedge against downside risk. Additionally, it explains how portfolio diversification across different asset classes and geographic regions can reduce overall risk while maintaining growth potential. The practical applications include specific examples of when to apply each strategy based on market conditions and investor risk tolerance.

        KEY POINTS:
        • Investment strategies can generate profits in both rising and falling markets
        • Three main approaches: long positions, short positions, and hedging techniques
        • Portfolio diversification reduces risk while maintaining growth potential
        • Different strategies suit different market conditions and risk tolerances
        • Understanding market cycles is crucial for effective strategy implementation

        KEY INSIGHTS:
        • Modern investors need flexibility to adapt to changing market conditions
        • Risk management is as important as profit generation
        • No single strategy works in all market environments

        KEYWORDS:
        investment, diversification, hedging, risk management, portfolio

        ABSOLUTELY FORBIDDEN:
        ❌ DO NOT use Markdown headers like ## or ###
        ❌ DO NOT use JSON format with quotes and braces
        ❌ DO NOT add extra introductory text before BRIEF:
        ❌ DO NOT add extra explanatory text after KEYWORDS:
        ❌ DO NOT use "BRIEF":" with quotes - use BRIEF: only

        CRITICAL FORMATTING RULES:
        ✓ Use PLAIN TEXT only - NO Markdown, NO JSON
        ✓ Start IMMEDIATELY with "BRIEF:" on line 1
        ✓ Each section label must be in CAPS followed by colon (BRIEF:, not "BRIEF":)
        ✓ BRIEF must be 1-2 sentences (shortest)
        ✓ SUMMARY must be 3-5 sentences (medium)
        ✓ DETAILED must be 6-10 sentences (longest - TWICE as long as SUMMARY)
        ✓ Put content on NEW LINE after each label
        ✓ Use bullet symbol (•) for all bullet points
        ✓ Keywords should be comma-separated, lowercase
        ✓ No extra commentary outside the specified sections
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
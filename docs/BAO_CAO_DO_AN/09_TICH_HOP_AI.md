# CHƯƠNG 9: TÍCH HỢP TRÍ TUỆ NHÂN TẠO

## 9.1. Tổng quan về AI trong SumUp

### 9.1.1. Vai trò của AI
Trí tuệ nhân tạo là trái tim của ứng dụng SumUp, đảm nhận các nhiệm vụ:
- **Text Summarization**: Tóm tắt văn bản thông minh
- **Context Understanding**: Hiểu ngữ cảnh và ý nghĩa
- **Language Processing**: Xử lý đa ngôn ngữ (Việt/Anh)
- **Personalization**: Tùy chỉnh output theo persona

### 9.1.2. Lựa chọn AI Provider
**Google Gemini** được chọn vì:
- **Performance**: Gemini 1.5 Flash cực kỳ nhanh
- **Quality**: Output chất lượng cao, đặc biệt với tiếng Việt
- **Cost**: Free tier hào phóng (60 RPM, 1M tokens/tháng)
- **Integration**: Native Android SDK support
- **Reliability**: Google infrastructure đảm bảo uptime

## 9.2. Google Gemini API

### 9.2.1. Gemini Models Comparison

| Model | Speed | Quality | Context Window | Best For |
|-------|-------|---------|----------------|----------|
| Gemini 1.5 Flash | ⚡⚡⚡⚡⚡ | ⭐⭐⭐⭐ | 1M tokens | SumUp primary |
| Gemini 1.5 Pro | ⚡⚡⚡ | ⭐⭐⭐⭐⭐ | 2M tokens | Complex docs |
| Gemini 1.0 Pro | ⚡⚡⚡⚡ | ⭐⭐⭐ | 32K tokens | Legacy support |

### 9.2.2. API Configuration

```kotlin
// API Key Management
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideGeminiApiKey(
        @ApplicationContext context: Context,
        apiKeyManager: ApiKeyManager
    ): String {
        return apiKeyManager.getApiKey() ?: BuildConfig.GEMINI_API_KEY
    }
    
    @Provides
    @Singleton
    fun provideGeminiService(
        apiKey: String,
        okHttpClient: OkHttpClient
    ): GeminiApiService {
        return if (apiKey.isValidApiKey()) {
            RealGeminiApiService(apiKey, okHttpClient)
        } else {
            MockGeminiApiService() // Fallback to mock
        }
    }
}
```

### 9.2.3. API Request Structure

```kotlin
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

data class Content(
    val parts: List<Part>,
    val role: String = "user"
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,      // Creativity level
    val topK: Int = 40,                 // Token selection
    val topP: Float = 0.95f,            // Nucleus sampling
    val maxOutputTokens: Int = 1024,    // Output length
    val stopSequences: List<String>? = null
)
```

## 9.3. Prompt Engineering

### 9.3.1. Prompt Design Principles

1. **Clear Instructions**: Rõ ràng về output mong muốn
2. **Context Setting**: Cung cấp context phù hợp
3. **Examples**: Few-shot learning khi cần
4. **Constraints**: Giới hạn rõ ràng (độ dài, format)
5. **Language**: Native language instructions

### 9.3.2. Persona-based Prompts

```kotlin
object PromptTemplates {
    fun getDefaultPrompt(lang: Language): String = when (lang) {
        Language.VIETNAMESE -> """
            Bạn là một chuyên gia tóm tắt văn bản. Nhiệm vụ của bạn là:
            
            1. Đọc kỹ văn bản được cung cấp
            2. Xác định các ý chính và thông tin quan trọng
            3. Tạo bản tóm tắt ngắn gọn nhưng đầy đủ
            4. Giữ nguyên các con số, tên riêng quan trọng
            5. Độ dài tóm tắt khoảng 20-30% văn bản gốc
            
            Văn bản cần tóm tắt:
        """.trimIndent()
        
        Language.ENGLISH -> """
            You are an expert text summarizer. Your task is to:
            
            1. Carefully read the provided text
            2. Identify main ideas and key information
            3. Create a concise yet comprehensive summary
            4. Preserve important numbers and proper names
            5. Summary length should be 20-30% of original
            
            Text to summarize:
        """.trimIndent()
    }
    
    fun getStudentPrompt(lang: Language): String = when (lang) {
        Language.VIETNAMESE -> """
            Bạn là một gia sư đang giúp sinh viên học tập. 
            Hãy tóm tắt văn bản này theo cách dễ hiểu cho sinh viên:
            
            - Sử dụng ngôn ngữ đơn giản, tránh từ ngữ phức tạp
            - Làm nổi bật các khái niệm và định nghĩa quan trọng
            - Thêm ví dụ minh họa nếu cần thiết
            - Sắp xếp thông tin theo dạng bullet points
            - Nhấn mạnh những điểm cần ghi nhớ cho kỳ thi
            
            Định dạng output:
            📌 KHÁI NIỆM CHÍNH:
            • [Khái niệm 1]: [Giải thích]
            • [Khái niệm 2]: [Giải thích]
            
            📝 ĐIỂM QUAN TRỌNG:
            • [Điểm 1]
            • [Điểm 2]
            
            💡 GHI NHỚ:
            • [Tip 1]
            • [Tip 2]
            
            Văn bản cần tóm tắt:
        """.trimIndent()
        // English version...
    }
    
    fun getProfessionalPrompt(lang: Language): String = when (lang) {
        Language.VIETNAMESE -> """
            Bạn là một business analyst chuyên nghiệp.
            Tạo executive summary theo format sau:
            
            TÓM TẮT ĐIỀU HÀNH
            ==================
            
            1. TÓM LƯỢC (2-3 câu overview)
            
            2. ĐIỂM CHÍNH
               • [Điểm 1 với số liệu cụ thể]
               • [Điểm 2 với số liệu cụ thể]
               • [Điểm 3 với số liệu cụ thể]
            
            3. PHÂN TÍCH
               • [Insight 1]
               • [Insight 2]
            
            4. KHUYẾN NGHỊ
               • [Action item 1]
               • [Action item 2]
            
            5. KẾT LUẬN
               [1-2 câu tổng kết]
            
            Giữ giọng điệu formal, chuyên nghiệp.
            Focus vào data và actionable insights.
            
            Văn bản cần phân tích:
        """.trimIndent()
        // English version...
    }
    
    fun getAcademicPrompt(lang: Language): String = when (lang) {
        Language.VIETNAMESE -> """
            Bạn là một nhà nghiên cứu học thuật.
            Tóm tắt văn bản theo cấu trúc academic paper:
            
            ABSTRACT
            --------
            [Tóm tắt ngắn gọn 100-150 từ về toàn bộ nội dung]
            
            1. GIỚI THIỆU
               - Bối cảnh và vấn đề nghiên cứu
               - Mục tiêu
            
            2. PHƯƠNG PHÁP (nếu có)
               - Methodology được sử dụng
               - Cách tiếp cận
            
            3. KẾT QUẢ CHÍNH
               - Finding 1: [Chi tiết]
               - Finding 2: [Chi tiết]
               - Finding 3: [Chi tiết]
            
            4. THẢO LUẬN
               - Ý nghĩa của kết quả
               - So sánh với nghiên cứu khác (nếu có)
            
            5. HẠN CHẾ (nếu có)
               - Những limitations
            
            6. KẾT LUẬN
               - Tóm tắt findings
               - Future directions
            
            Giữ nguyên tất cả thuật ngữ chuyên môn.
            Citations và số liệu phải chính xác tuyệt đối.
            
            Văn bản cần tóm tắt:
        """.trimIndent()
        // English version...
    }
    
    fun getCreativePrompt(lang: Language): String = when (lang) {
        Language.VIETNAMESE -> """
            Bạn là một storyteller tài năng.
            Hãy tóm tắt văn bản theo cách sáng tạo và thu hút:
            
            🎯 MỞ ĐẦU HẤP DẪN
            [Hook reader với câu mở đầu ấn tượng]
            
            🌟 CÂU CHUYỆN CHÍNH
            [Kể lại nội dung như một câu chuyện với:
             - Nhân vật (nếu có)
             - Conflict/Challenge
             - Resolution/Outcome]
            
            💎 NHỮNG VIÊN NGỌC TRI THỨC
            • [Insight thú vị 1]
            • [Insight thú vị 2]
            • [Fun fact hoặc surprising element]
            
            🎭 TWIST HOẶC GÓC NHÌN MỚI
            [Một cách nhìn độc đáo về vấn đề]
            
            ✨ KẾT THÚC ĐÁNG NHỚ
            [Câu kết tạo ấn tượng sâu sắc hoặc call-to-action]
            
            Sử dụng ngôn ngữ sinh động, metaphors, và imagery.
            Làm cho dry content trở nên interesting.
            
            Văn bản cần biến hóa:
        """.trimIndent()
        // English version...
    }
}
```

### 9.3.3. Dynamic Prompt Optimization

```kotlin
class PromptOptimizer @Inject constructor() {
    
    fun optimizePrompt(
        basePrompt: String,
        textLength: Int,
        textComplexity: Float,
        userPreferences: UserPreferences
    ): String {
        var optimizedPrompt = basePrompt
        
        // Adjust for text length
        when {
            textLength < 500 -> {
                optimizedPrompt += "\n\nLưu ý: Văn bản ngắn, hãy giữ tóm tắt cực kỳ súc tích (3-5 câu)."
            }
            textLength > 10000 -> {
                optimizedPrompt += "\n\nLưu ý: Văn bản dài, hãy chia tóm tắt thành sections rõ ràng."
            }
        }
        
        // Adjust for complexity
        if (textComplexity > 0.8f) {
            optimizedPrompt += "\n\nVăn bản phức tạp, hãy giải thích các thuật ngữ khó."
        }
        
        // User preferences
        if (userPreferences.preferBulletPoints) {
            optimizedPrompt += "\n\nSử dụng bullet points cho mọi điểm chính."
        }
        
        return optimizedPrompt
    }
}
```

## 9.4. AI Processing Pipeline

### 9.4.1. Complete Processing Flow

```mermaid
graph TD
    A[User Input] --> B{Input Type?}
    B -->|Text| C[Text Preprocessing]
    B -->|PDF| D[PDF Extraction]
    B -->|Image| E[OCR Processing]
    
    C --> F[Text Validation]
    D --> F
    E --> F
    
    F --> G{Valid?}
    G -->|No| H[Error Handling]
    G -->|Yes| I[Chunk if Needed]
    
    I --> J[Build Prompt]
    J --> K[Call Gemini API]
    K --> L{Success?}
    
    L -->|No| M[Retry Logic]
    M --> K
    L -->|Yes| N[Parse Response]
    
    N --> O[Post-processing]
    O --> P[Quality Check]
    P --> Q{Quality OK?}
    
    Q -->|No| R[Regenerate]
    R --> J
    Q -->|Yes| S[Save & Display]
```

### 9.4.2. Text Preprocessing

```kotlin
class TextPreprocessor @Inject constructor() {
    
    fun preprocess(text: String): ProcessedText {
        var processed = text
            .trim()
            .normalizeWhitespace()
            .removeInvisibleChars()
            .fixCommonOCRErrors()
        
        // Detect language
        val language = detectLanguage(processed)
        
        // Fix encoding issues
        if (language == Language.VIETNAMESE) {
            processed = fixVietnameseEncoding(processed)
        }
        
        // Calculate metrics
        val wordCount = countWords(processed, language)
        val complexity = calculateComplexity(processed)
        val readingTime = estimateReadingTime(wordCount)
        
        return ProcessedText(
            text = processed,
            language = language,
            wordCount = wordCount,
            complexity = complexity,
            readingTimeSeconds = readingTime
        )
    }
    
    private fun normalizeWhitespace(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")     // Multiple spaces to single
            .replace(Regex("\\n{3,}"), "\n\n") // Multiple newlines to double
            .trim()
    }
    
    private fun fixVietnameseEncoding(text: String): String {
        return text
            .replace("Ä'", "đ")
            .replace("Ä\u0090", "Đ")
            .replace("Æ¡", "ơ")
            .replace("Æ ", "Ơ")
            .replace("Æ°", "ư")
            .replace("Æ¯", "Ư")
        // More replacements...
    }
    
    private fun calculateComplexity(text: String): Float {
        val avgWordLength = text.split(" ").map { it.length }.average()
        val avgSentenceLength = text.split("[.!?]".toRegex()).size
        val uniqueWordRatio = text.split(" ").distinct().size.toFloat() / 
                             text.split(" ").size
        
        return (avgWordLength * 0.3f + avgSentenceLength * 0.3f + 
                uniqueWordRatio * 0.4f).toFloat()
    }
}
```

### 9.4.3. Response Processing

```kotlin
class ResponseProcessor @Inject constructor() {
    
    fun processGeminiResponse(
        response: GenerateContentResponse,
        originalText: String,
        persona: Persona
    ): Summary {
        // Extract text from response
        val summaryText = response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw ApiException("Empty response from Gemini")
        
        // Clean and format
        val cleanedSummary = cleanSummaryText(summaryText, persona)
        
        // Validate quality
        validateSummaryQuality(
            original = originalText,
            summary = cleanedSummary
        )
        
        // Calculate metrics
        val metrics = calculateMetrics(originalText, cleanedSummary)
        
        return Summary(
            id = UUID.randomUUID().toString(),
            originalText = originalText,
            summarizedText = cleanedSummary,
            persona = persona,
            timestamp = System.currentTimeMillis(),
            wordCountOriginal = metrics.originalWords,
            wordCountSummary = metrics.summaryWords,
            compressionRatio = metrics.compressionRatio,
            processingTimeMs = metrics.processingTime
        )
    }
    
    private fun cleanSummaryText(text: String, persona: Persona): String {
        var cleaned = text.trim()
        
        // Remove any prompt leakage
        cleaned = cleaned
            .removePrefix("Here's the summary:")
            .removePrefix("Tóm tắt:")
            .trim()
        
        // Format based on persona
        when (persona) {
            Persona.STUDENT -> {
                // Ensure bullet points are properly formatted
                cleaned = cleaned.replace("•", "\n•")
                    .replace("- ", "\n• ")
                    .replace(Regex("\n{3,}"), "\n\n")
            }
            Persona.PROFESSIONAL -> {
                // Ensure numbered lists are clean
                cleaned = cleaned.replace(Regex("(\\d+\\.)"), "\n$1")
                    .trim()
            }
            // Other personas...
        }
        
        return cleaned
    }
    
    private fun validateSummaryQuality(original: String, summary: String) {
        // Check length ratio
        val ratio = summary.length.toFloat() / original.length
        if (ratio > 0.8f) {
            throw QualityException("Summary too long (${(ratio * 100).toInt()}% of original)")
        }
        if (ratio < 0.05f) {
            throw QualityException("Summary too short (${(ratio * 100).toInt()}% of original)")
        }
        
        // Check for common issues
        if (summary.contains("I cannot") || summary.contains("I'm unable")) {
            throw QualityException("AI refused to summarize")
        }
        
        // Check language consistency
        val originalLang = detectLanguage(original)
        val summaryLang = detectLanguage(summary)
        if (originalLang != summaryLang) {
            throw QualityException("Language mismatch")
        }
    }
}
```

## 9.5. Advanced AI Features

### 9.5.1. Chunk Processing cho văn bản dài

```kotlin
class ChunkProcessor @Inject constructor(
    private val geminiService: GeminiApiService
) {
    companion object {
        const val MAX_CHUNK_SIZE = 10000 // Characters
        const val CHUNK_OVERLAP = 500    // Overlap between chunks
    }
    
    suspend fun processLongText(
        text: String,
        persona: Persona
    ): String {
        if (text.length <= MAX_CHUNK_SIZE) {
            return processNormally(text, persona)
        }
        
        // Split into chunks with overlap
        val chunks = createChunks(text)
        
        // Process each chunk
        val chunkSummaries = chunks.mapIndexed { index, chunk ->
            val chunkPrompt = buildChunkPrompt(
                chunk = chunk,
                chunkNumber = index + 1,
                totalChunks = chunks.size,
                persona = persona
            )
            
            geminiService.generateContent(chunkPrompt, chunk)
        }
        
        // Combine chunk summaries
        val combinedText = chunkSummaries.joinToString("\n\n")
        
        // Final summarization pass
        val finalPrompt = buildFinalSummaryPrompt(persona)
        return geminiService.generateContent(finalPrompt, combinedText)
    }
    
    private fun createChunks(text: String): List<String> {
        val chunks = mutableListOf<String>()
        var startIndex = 0
        
        while (startIndex < text.length) {
            var endIndex = minOf(startIndex + MAX_CHUNK_SIZE, text.length)
            
            // Try to break at sentence boundary
            if (endIndex < text.length) {
                val lastPeriod = text.lastIndexOf('.', endIndex)
                if (lastPeriod > startIndex + MAX_CHUNK_SIZE / 2) {
                    endIndex = lastPeriod + 1
                }
            }
            
            chunks.add(text.substring(startIndex, endIndex))
            startIndex = endIndex - CHUNK_OVERLAP
        }
        
        return chunks
    }
}
```

### 9.5.2. Context-Aware Summarization

```kotlin
class ContextAwareSummarizer @Inject constructor(
    private val geminiService: GeminiApiService,
    private val contextAnalyzer: ContextAnalyzer
) {
    
    suspend fun summarizeWithContext(
        text: String,
        previousSummaries: List<Summary>,
        userProfile: UserProfile
    ): Summary {
        // Analyze context
        val context = contextAnalyzer.analyze(
            currentText = text,
            history = previousSummaries,
            profile = userProfile
        )
        
        // Build context-aware prompt
        val prompt = buildContextAwarePrompt(
            basePrompt = getPromptForPersona(context.suggestedPersona),
            context = context
        )
        
        // Add relevant history
        val enhancedPrompt = if (context.relatedTopics.isNotEmpty()) {
            prompt + "\n\nChủ đề liên quan đã tóm tắt trước đây:\n" +
            context.relatedTopics.joinToString("\n") { "- $it" }
        } else {
            prompt
        }
        
        return geminiService.generateContent(enhancedPrompt, text)
    }
}

data class AnalysisContext(
    val documentType: DocumentType,      // Email, Article, Report, etc.
    val suggestedPersona: Persona,       // Best persona for this content
    val relatedTopics: List<String>,     // From history
    val userPreferences: Map<String, Any> // User's style preferences
)
```

### 9.5.3. Multi-modal Processing (OCR + AI)

```kotlin
class MultiModalProcessor @Inject constructor(
    private val textRecognizer: TextRecognizer,
    private val geminiService: GeminiApiService,
    private val imageEnhancer: ImageEnhancer
) {
    
    suspend fun processImage(imageUri: Uri): Flow<ProcessingState> = flow {
        emit(ProcessingState.EnhancingImage)
        
        // Enhance image for better OCR
        val enhancedBitmap = imageEnhancer.enhance(imageUri)
        
        emit(ProcessingState.ExtractingText)
        
        // Perform OCR
        val inputImage = InputImage.fromBitmap(enhancedBitmap, 0)
        val visionText = textRecognizer.process(inputImage).await()
        
        // Build structured text with confidence
        val structuredText = buildStructuredText(visionText)
        
        emit(ProcessingState.AnalyzingContent)
        
        // Use Gemini to clean and structure OCR output
        val cleanedText = cleanOCRText(structuredText)
        
        emit(ProcessingState.GeneratingSummary)
        
        // Summarize the cleaned text
        val summary = geminiService.generateContent(
            prompt = getOCRSummaryPrompt(),
            text = cleanedText
        )
        
        emit(ProcessingState.Complete(summary))
    }
    
    private suspend fun cleanOCRText(ocrResult: StructuredOCRText): String {
        val cleaningPrompt = """
            The following text was extracted from an image using OCR.
            It may contain errors, formatting issues, or incomplete sentences.
            
            Please clean and correct this text:
            1. Fix obvious OCR errors
            2. Restore proper formatting
            3. Complete incomplete sentences if context allows
            4. Maintain the original meaning
            
            OCR confidence: ${ocrResult.averageConfidence}
            Low confidence areas: ${ocrResult.lowConfidenceBlocks}
            
            Text to clean:
            ${ocrResult.text}
        """.trimIndent()
        
        return geminiService.generateContent(cleaningPrompt, "")
    }
}
```

## 9.6. AI Safety và Ethics

### 9.6.1. Content Filtering

```kotlin
class ContentSafetyFilter @Inject constructor() {
    
    fun buildSafetySettings(): List<SafetySetting> {
        return listOf(
            SafetySetting(
                category = HarmCategory.HARM_CATEGORY_HARASSMENT,
                threshold = SafetyThreshold.BLOCK_MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                category = HarmCategory.HARM_CATEGORY_HATE_SPEECH,
                threshold = SafetyThreshold.BLOCK_MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                category = HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT,
                threshold = SafetyThreshold.BLOCK_LOW_AND_ABOVE
            ),
            SafetySetting(
                category = HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT,
                threshold = SafetyThreshold.BLOCK_MEDIUM_AND_ABOVE
            )
        )
    }
    
    fun validateContent(text: String): ContentValidation {
        // Check for PII
        val hasPII = detectPII(text)
        
        // Check for inappropriate content
        val appropriateness = checkAppropriateness(text)
        
        return ContentValidation(
            isValid = !hasPII && appropriateness.isAppropriate,
            warnings = buildList {
                if (hasPII) add("Text contains personal information")
                if (!appropriateness.isAppropriate) {
                    add("Content may be inappropriate: ${appropriateness.reason}")
                }
            }
        )
    }
    
    private fun detectPII(text: String): Boolean {
        val patterns = listOf(
            Regex("\\b\\d{9,12}\\b"),           // Phone numbers
            Regex("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", RegexOption.IGNORE_CASE), // Email
            Regex("\\b\\d{9,10}\\b"),           // ID numbers
            Regex("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\b") // Credit cards
        )
        
        return patterns.any { it.containsMatchIn(text) }
    }
}
```

### 9.6.2. Bias Mitigation

```kotlin
class BiasMitigation @Inject constructor() {
    
    fun addBiasInstructions(basePrompt: String): String {
        return basePrompt + """
            
            IMPORTANT GUIDELINES:
            - Be objective and neutral in tone
            - Avoid stereotypes or assumptions
            - Present multiple perspectives when relevant
            - Use inclusive language
            - Do not make judgments about people or groups
            - Focus on facts rather than opinions
            - If the source contains bias, note it objectively
        """.trimIndent()
    }
    
    fun checkOutputBias(summary: String): BiasCheck {
        val biasIndicators = listOf(
            "all", "every", "never", "always", "must", "should"
        ).map { it.lowercase() }
        
        val absolutistLanguage = biasIndicators.count { 
            summary.lowercase().contains(it) 
        }
        
        return BiasCheck(
            score = 1.0f - (absolutistLanguage / 10f),
            suggestions = if (absolutistLanguage > 3) {
                listOf("Consider using more balanced language")
            } else emptyList()
        )
    }
}
```

## 9.7. Performance Optimization

### 9.7.1. Caching Strategy

```kotlin
@Singleton
class AICacheManager @Inject constructor(
    private val cacheDir: File,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val memoryCache = LruCache<String, CachedSummary>(50)
    
    suspend fun getCachedSummary(
        text: String,
        persona: Persona
    ): Summary? = withContext(ioDispatcher) {
        val key = generateCacheKey(text, persona)
        
        // Check memory cache first
        memoryCache.get(key)?.let { 
            if (!it.isExpired()) return@withContext it.summary
        }
        
        // Check disk cache
        val file = File(cacheDir, "$key.json")
        if (file.exists()) {
            try {
                val cached = Json.decodeFromString<CachedSummary>(
                    file.readText()
                )
                if (!cached.isExpired()) {
                    memoryCache.put(key, cached)
                    return@withContext cached.summary
                }
            } catch (e: Exception) {
                file.delete()
            }
        }
        
        null
    }
    
    suspend fun cacheSummary(
        text: String,
        persona: Persona,
        summary: Summary
    ) = withContext(ioDispatcher) {
        val key = generateCacheKey(text, persona)
        val cached = CachedSummary(
            summary = summary,
            timestamp = System.currentTimeMillis(),
            ttlHours = 24
        )
        
        // Memory cache
        memoryCache.put(key, cached)
        
        // Disk cache
        val file = File(cacheDir, "$key.json")
        file.writeText(Json.encodeToString(cached))
    }
    
    private fun generateCacheKey(text: String, persona: Persona): String {
        val content = "$text:${persona.name}"
        return MessageDigest.getInstance("SHA-256")
            .digest(content.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
            .take(16)
    }
}
```

### 9.7.2. Request Batching

```kotlin
class AIRequestBatcher @Inject constructor(
    private val geminiService: GeminiApiService,
    private val scope: CoroutineScope
) {
    private val pendingRequests = mutableListOf<PendingRequest>()
    private var batchJob: Job? = null
    
    fun queueRequest(
        text: String,
        persona: Persona,
        callback: (Result<Summary>) -> Unit
    ) {
        pendingRequests.add(
            PendingRequest(text, persona, callback)
        )
        
        // Start batch processing if not running
        if (batchJob?.isActive != true) {
            batchJob = scope.launch {
                delay(100) // Wait for more requests
                processBatch()
            }
        }
    }
    
    private suspend fun processBatch() {
        val batch = pendingRequests.toList()
        pendingRequests.clear()
        
        // Group by persona for efficiency
        batch.groupBy { it.persona }.forEach { (persona, requests) ->
            try {
                // Process all texts with same persona in one request
                val combinedPrompt = buildBatchPrompt(persona, requests.size)
                val combinedText = requests.mapIndexed { index, req ->
                    "=== Text ${index + 1} ===\n${req.text}"
                }.joinToString("\n\n")
                
                val response = geminiService.generateContent(
                    combinedPrompt,
                    combinedText
                )
                
                // Parse and distribute results
                val summaries = parseBatchResponse(response)
                requests.forEachIndexed { index, req ->
                    req.callback(Result.success(summaries[index]))
                }
                
            } catch (e: Exception) {
                requests.forEach { it.callback(Result.failure(e)) }
            }
        }
    }
}
```

## 9.8. Monitoring và Analytics

### 9.8.1. AI Usage Metrics

```kotlin
@Singleton
class AIMetricsCollector @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {
    fun trackSummarizationRequest(
        persona: Persona,
        textLength: Int,
        processingTime: Long,
        success: Boolean,
        error: String? = null
    ) {
        analyticsManager.logEvent("ai_summarization", mapOf(
            "persona" to persona.name,
            "text_length" to textLength,
            "processing_time_ms" to processingTime,
            "success" to success,
            "error_type" to (error ?: "none"),
            "compression_ratio" to calculateCompressionRatio(textLength)
        ))
    }
    
    fun trackTokenUsage(
        inputTokens: Int,
        outputTokens: Int,
        model: String
    ) {
        analyticsManager.logEvent("ai_token_usage", mapOf(
            "input_tokens" to inputTokens,
            "output_tokens" to outputTokens,
            "total_tokens" to (inputTokens + outputTokens),
            "model" to model,
            "estimated_cost" to estimateCost(inputTokens + outputTokens, model)
        ))
    }
    
    fun trackQualityMetrics(
        summary: Summary,
        userFeedback: UserFeedback? = null
    ) {
        val metrics = mapOf(
            "compression_ratio" to summary.compressionRatio,
            "processing_time" to summary.processingTimeMs,
            "word_count_original" to summary.wordCountOriginal,
            "word_count_summary" to summary.wordCountSummary,
            "persona" to summary.persona.name,
            "user_rating" to (userFeedback?.rating ?: -1),
            "readability_score" to calculateReadability(summary.summarizedText)
        )
        
        analyticsManager.logEvent("ai_quality_metrics", metrics)
    }
}
```

## 9.9. Future AI Enhancements

### 9.9.1. Planned Features
1. **Multi-language Support**: Expand beyond Vietnamese/English
2. **Voice Input/Output**: Speech-to-text và text-to-speech
3. **Document Intelligence**: Understanding document structure
4. **Sentiment Analysis**: Analyze tone và emotion
5. **Custom Model Fine-tuning**: Train on user's specific domain

### 9.9.2. AI Model Abstraction

```kotlin
// Prepare for multiple AI providers
interface AIProvider {
    suspend fun summarize(
        text: String,
        options: SummarizationOptions
    ): Result<Summary>
    
    fun getCapabilities(): AICapabilities
    fun getModelInfo(): ModelInfo
}

class GeminiProvider : AIProvider { /* Implementation */ }
class OpenAIProvider : AIProvider { /* Implementation */ }
class LocalLLMProvider : AIProvider { /* Implementation */ }

// Factory pattern for provider selection
class AIProviderFactory @Inject constructor(
    private val providers: Map<String, @JvmSuppressWildcards AIProvider>
) {
    fun getProvider(type: AIProviderType): AIProvider {
        return providers[type.name] 
            ?: throw IllegalArgumentException("Unknown provider: $type")
    }
}
```

## 9.10. Tóm tắt chương

Chương này đã trình bày chi tiết về tích hợp AI trong SumUp:

1. **Google Gemini Integration**: API setup, configuration, models
2. **Prompt Engineering**: Persona-based prompts với Vietnamese focus
3. **Processing Pipeline**: Complete flow từ input đến output
4. **Advanced Features**: Chunking, context-aware, multi-modal
5. **Safety & Ethics**: Content filtering, bias mitigation
6. **Performance**: Caching, batching, optimization
7. **Monitoring**: Metrics, analytics, quality tracking
8. **Future-ready**: Abstraction cho multiple AI providers

Key achievements:
- **Smart Summarization**: High-quality summaries với multiple styles
- **Vietnamese Excellence**: Optimized cho tiếng Việt
- **Scalable Architecture**: Ready cho growth và new features
- **Ethical AI**: Safe, unbiased, và responsible
- **Performance**: Fast response với smart caching

AI là trái tim của SumUp, biến nó từ simple text processor thành intelligent assistant cho mọi nhu cầu tóm tắt văn bản.
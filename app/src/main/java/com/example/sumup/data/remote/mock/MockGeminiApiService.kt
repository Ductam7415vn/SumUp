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

        // Detect language from prompt
        val language = detectLanguageFromPrompt(inputText)

        // Generate mock summary based on input length and language
        val mockSummary = when {
            inputText.length < 100 -> generateShortMockSummary(inputText, language)
            inputText.length < 500 -> generateMediumMockSummary(inputText, language)
            else -> generateLongMockSummary(inputText, language)
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
        val language = detectLanguageFromPrompt(request.text)

        return SummarizeResponse(
            summary = summary,
            bullets = if (language == "vi") {
                listOf("Điểm chính 1", "Điểm chính 2", "Điểm chính 3")
            } else {
                listOf("Mock key point 1", "Mock key point 2", "Mock key point 3")
            },
            confidence = 0.95f,
            processingTime = 2000L,
            briefOverview = if (language == "vi") "Tóm tắt ngắn gọn" else "Brief overview of the content",
            detailedSummary = summary,
            keyInsights = if (language == "vi") {
                listOf("Nhận định quan trọng 1", "Nhận định quan trọng 2")
            } else {
                listOf("Key insight 1", "Key insight 2")
            },
            actionItems = if (language == "vi") {
                listOf("Hành động cần thực hiện 1", "Hành động cần thực hiện 2")
            } else {
                listOf("Action item 1", "Action item 2")
            },
            keywords = if (language == "vi") {
                listOf("từkhóa1", "từkhóa2", "từkhóa3")
            } else {
                listOf("keyword1", "keyword2", "keyword3")
            }
        )
    }

    private fun detectLanguageFromPrompt(text: String): String {
        // Check for language instructions in the prompt
        return when {
            text.contains("Vietnamese (Tiếng Việt)") || text.contains("Respond in Vietnamese") -> "vi"
            text.contains("Respond in English") -> "en"
            text.contains("Detect the source language and respond in the same language") -> {
                // Simple heuristic: check for Vietnamese characters or common words
                if (isVietnamese(text)) "vi" else "en"
            }

            else -> "en" // Default to English
        }
    }

    private fun isVietnamese(text: String): Boolean {
        // Check for Vietnamese-specific characters or common words
        val vietnameseChars = listOf(
            'ă', 'â', 'đ', 'ê', 'ô', 'ơ', 'ư', 'ạ', 'ả', 'ã', 'à', 'á',
            'ậ', 'ầ', 'ấ', 'ẩ', 'ẫ', 'ằ', 'ắ', 'ẳ', 'ẵ', 'ặ', 'ẹ', 'ẻ', 'ẽ', 'è', 'é',
            'ệ', 'ề', 'ế', 'ể', 'ễ', 'ị', 'ỉ', 'ĩ', 'ì', 'í', 'ọ', 'ỏ', 'õ', 'ò', 'ó',
            'ộ', 'ồ', 'ố', 'ổ', 'ỗ', 'ợ', 'ờ', 'ớ', 'ở', 'ỡ', 'ụ', 'ủ', 'ũ', 'ù', 'ú',
            'ự', 'ừ', 'ứ', 'ử', 'ữ', 'ỵ', 'ỷ', 'ỹ', 'ỳ', 'ý'
        )

        val vietnameseWords =
            listOf("và", "của", "là", "có", "trong", "được", "với", "cho", "về", "này")

        val lowercaseText = text.lowercase()

        // Check for Vietnamese characters
        if (vietnameseChars.any { lowercaseText.contains(it) }) {
            return true
        }

        // Check for common Vietnamese words
        return vietnameseWords.count { lowercaseText.contains(" $it ") } >= 2
    }

    private fun generateShortMockSummary(input: String, language: String = "en"): String {
        val wordCount = input.split("\\s+".toRegex()).size

        return if (language == "vi") {
            """
            ## Tóm tắt
            Đây là bản tóm tắt demo được tạo trong chế độ mock. Văn bản gốc chứa khoảng $wordCount từ.
            
            ## Các điểm chính
            • Ý chính được trích xuất từ phần đầu văn bản của bạn
            • Các chi tiết hỗ trợ được xác định trong nội dung
            • Kết luận hoặc suy nghĩ cuối cùng từ văn bản
            
            ## Tổng quan ngắn gọn
            Văn bản thảo luận về các chủ đề khác nhau được tóm tắt ở đây một cách ngắn gọn. Bản tóm tắt mock này minh họa cách ứng dụng sẽ định dạng và trình bày tóm tắt khi kết nối với API Gemini AI thực.
            
            *Lưu ý: Đây là bản tóm tắt demo. Thêm khóa API Gemini của bạn trong Cài đặt để có tóm tắt AI thực sự.*
            """.trimIndent()
        } else {
            """
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
    }

    private fun generateMediumMockSummary(input: String, language: String = "en"): String {
        val wordCount = input.split("\\s+".toRegex()).size
        val firstSentence = input.split(".").firstOrNull()?.trim()
            ?: if (language == "vi") "Văn bản của bạn" else "Your text"

        return if (language == "vi") {
            """
            ## Tóm tắt tổng quát
            Phân tích toàn diện này bao gồm các chủ đề chính được trình bày trong văn bản ${wordCount} từ của bạn. $firstSentence...
            
            ## Các điểm chính chi tiết
            • **Chủ đề chính**: Trọng tâm dường như là các khái niệm quan trọng được giới thiệu sớm trong văn bản
            • **Luận điểm hỗ trợ**: Một số điểm hỗ trợ củng cố luận điểm chính
            • **Bằng chứng được trình bày**: Văn bản bao gồm nhiều ví dụ và dữ liệu khác nhau
            • **Ý nghĩa**: Nội dung gợi ý những ý nghĩa quan trọng cho người đọc
            • **Kết luận**: Suy nghĩ cuối cùng gắn kết các luận điểm chính một cách hiệu quả
            
            ## Phân tích
            Tài liệu trình bày một lập luận có cấu trúc tốt với sự tiến triển rõ ràng từ phần giới thiệu đến kết luận. Phong cách viết phù hợp với đối tượng mục tiêu và các thông điệp chính được truyền đạt hiệu quả.
            
            ## Khuyến nghị
            Dựa trên bản tóm tắt này, người đọc nên:
            1. Tập trung vào các chủ đề chính được xác định ở trên
            2. Xem xét bằng chứng hỗ trợ để hiểu sâu hơn
            3. Cân nhắc các ý nghĩa thực tiễn được thảo luận
            
            *Lưu ý: Đây là bản tóm tắt demo. Thêm khóa API Gemini của bạn trong Cài đặt để có tóm tắt AI thực sự.*
            """.trimIndent()
        } else {
            """
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
    }

    private fun generateLongMockSummary(input: String, language: String = "en"): String {
        val wordCount = input.split("\\s+".toRegex()).size
        val sentences = input.split(".").take(3).joinToString(". ")

        return if (language == "vi") {
            """
            ## Phân tích Tóm tắt Toàn diện
            
            ### Tổng quan Tài liệu
            Tài liệu mở rộng này chứa khoảng $wordCount từ bao gồm nhiều chủ đề liên quan. Phân tích dưới đây chia nhỏ nội dung thành các phần dễ hiểu hơn.
            
            ### Tóm tắt Giới thiệu
            $sentences...
            
            ### Các Chủ đề Chính Được Xác định
            
            #### Chủ đề 1: Lĩnh vực Trọng tâm Chính
            Trọng tâm chính của tài liệu tập trung vào các khái niệm quan trọng được giới thiệu sớm và phát triển xuyên suốt. Những khái niệm này tạo nền tảng cho các lập luận và thảo luận tiếp theo.
            
            #### Chủ đề 2: Các Yếu tố Hỗ trợ
            Các chủ đề phụ cung cấp bối cảnh và hỗ trợ cho các lập luận chính. Bao gồm:
            • Bối cảnh và lịch sử
            • Phân tích tình trạng hiện tại
            • Ý nghĩa và dự đoán tương lai
            • Ứng dụng thực tiễn
            
            #### Chủ đề 3: Kết luận và Khuyến nghị
            Tài liệu kết thúc với những hiểu biết sâu sắc và khuyến nghị có thể thực hiện dựa trên phân tích được trình bày.
            
            ### Các Điểm Chính Chi tiết
            
            1. **Lập luận Mở đầu**
               - Thiết lập tầm quan trọng của chủ đề
               - Cung cấp thông tin nền cần thiết
               - Thiết lập khung cho thảo luận
            
            2. **Phân tích Trung tâm**
               - Xem xét nhiều quan điểm
               - Trình bày bằng chứng và dữ liệu
               - Vẽ các kết nối giữa các khái niệm
            
            3. **Bằng chứng Hỗ trợ**
               - Dữ liệu thống kê và kết quả nghiên cứu
               - Nghiên cứu tình huống và ví dụ thực tế
               - Ý kiến chuyên gia và trích dẫn
            
            ### Hành động Cần thực hiện
            
            Dựa trên phân tích toàn diện này, các hành động sau được khuyến nghị:
            
            1. Xem xét và nội hóa các chủ đề chính đã xác định
            2. Phát triển chiến lược thực hiện dựa trên các khuyến nghị
            3. Thiết lập các chỉ số để đo lường thành công
            4. Tạo vòng phản hồi để cải thiện liên tục
            5. Tham gia các bên liên quan sớm và thường xuyên
            
            ### Kết luận
            
            Bản tóm tắt này nắm bắt bản chất của tài liệu ${wordCount} từ, chắt lọc các ý tưởng phức tạp thành những hiểu biết sâu sắc có thể hành động.
            
            *Lưu ý: Đây là bản tóm tắt demo được tạo trong chế độ mock. Để có tóm tắt AI thực sự phân tích nội dung cụ thể của bạn, vui lòng thêm khóa API Gemini trong menu Cài đặt.*
            """.trimIndent()
        } else {
            """
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
}
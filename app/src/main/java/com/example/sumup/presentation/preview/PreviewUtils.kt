package com.example.sumup.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.domain.model.*
import com.example.sumup.presentation.screens.main.MainUiState
import com.example.sumup.domain.model.ServiceInfo
import com.example.sumup.presentation.screens.main.FeatureTip
import com.example.sumup.presentation.screens.result.ResultUiState
import com.example.sumup.presentation.screens.history.HistoryUiState
import com.example.sumup.presentation.screens.settings.SettingsUiState
import com.example.sumup.ui.theme.SumUpTheme
import java.util.Date

// Preview wrapper that applies theme
@Composable
fun PreviewWrapper(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    SumUpTheme(darkTheme = darkTheme) {
        content()
    }
}

// Sample data for previews
object PreviewData {
    val sampleSummary = Summary(
        id = "1",
        originalText = "This is a sample original text that is quite long and contains multiple sentences. It discusses various topics and provides detailed information about different subjects. The text is meant to be comprehensive and cover all aspects of the topic at hand. It includes examples, explanations, and detailed analysis of the subject matter.",
        summary = "This is a comprehensive summary that captures the main points of the original text, providing a concise overview of the key topics discussed.",
        bulletPoints = listOf(
            "Main topic discussed with comprehensive coverage",
            "Multiple examples and detailed explanations provided",
            "In-depth analysis of various aspects",
            "Key insights and important takeaways",
            "Practical applications and real-world relevance"
        ),
        createdAt = Date().time,
        isFavorite = false,
        metrics = SummaryMetrics(
            originalWordCount = 150,
            summaryWordCount = 30,
            reductionPercentage = 80,
            originalReadingTime = 1,
            summaryReadingTime = 0
        ),
        briefOverview = "A concise overview capturing the essence of the content.",
        detailedSummary = "This detailed summary provides an extensive analysis of the original text, covering all major points and subtopics. It includes comprehensive explanations and maintains the logical flow of ideas presented in the source material.",
        keyInsights = listOf(
            "Critical insight about the main theme",
            "Important pattern identified in the content",
            "Key relationship between concepts",
            "Significant implication for practical use"
        ),
        actionItems = listOf(
            "Review and implement the suggested strategies",
            "Analyze the provided examples for better understanding",
            "Apply the concepts to real-world scenarios"
        ),
        keywords = listOf("analysis", "comprehensive", "insights", "practical", "implementation"),
        persona = SummaryPersona.GENERAL
    )

    val mainUiStateDefault = MainUiState()
    
    val mainUiStateWithText = MainUiState(
        inputText = "This is some sample text that the user has entered for summarization.",
        serviceInfo = ServiceInfo(
            type = ServiceType.REAL_API
        ),
        todayCount = 5,
        weekCount = 23,
        totalCount = 150
    )

    val mainUiStateWithPdf = MainUiState(
        inputType = MainUiState.InputType.PDF,
        selectedPdfUri = "/path/to/document.pdf",
        selectedPdfName = "document.pdf",
        pdfPageCount = 10,
        serviceInfo = ServiceInfo(
            type = ServiceType.MOCK_API
        )
    )

    val mainUiStateLoading = MainUiState(
        inputText = "Loading state example",
        isLoading = true
    )

    val mainUiStateWithError = MainUiState(
        inputText = "Error state example",
        error = AppError.NetworkError
    )

    val resultUiStateDefault = ResultUiState(
        summary = sampleSummary,
        isLoading = false,
        selectedPersona = SummaryPersona.GENERAL
    )

    val resultUiStateLoading = ResultUiState(
        isLoading = true
    )

    val historyUiStateWithItems = HistoryUiState(
        groupedSummaries = mapOf(
            "Today" to listOf(
            sampleSummary,
            sampleSummary.copy(
                id = "2",
                summary = "Another summary with different content",
                createdAt = System.currentTimeMillis() - 86400000, // Yesterday
                isFavorite = true
            ),
            sampleSummary.copy(
                id = "3",
                summary = "A third summary to show multiple items",
                createdAt = System.currentTimeMillis() - 172800000 // 2 days ago
            )
            )
        ),
        isLoading = false
    )

    val historyUiStateEmpty = HistoryUiState(
        groupedSummaries = emptyMap(),
        isLoading = false
    )

    val historyUiStateLoading = HistoryUiState(
        isLoading = true
    )

    val settingsUiState = SettingsUiState()

    val onboardingPages = OnboardingData.pages

    val featureTip = FeatureTip(
        id = "pdf_upload",
        title = "PDF Support",
        description = "You can now upload PDF files for summarization!",
        targetElement = "pdf_button"
    )
}

// Preview annotations
annotation class LightDarkPreview

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class ThemePreview

@Preview(name = "Phone", device = "spec:width=411dp,height=891dp", showBackground = true)
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
annotation class DevicePreview

@Preview(name = "Light Phone", device = "spec:width=411dp,height=891dp", showBackground = true)
@Preview(name = "Dark Phone", device = "spec:width=411dp,height=891dp", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
annotation class AllDevicePreview
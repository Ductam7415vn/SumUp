package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.DocumentSection
import com.example.sumup.domain.model.SectionedSummary
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryMetrics
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import kotlin.math.ceil

/**
 * Use case for smart sectioning of long documents
 * Breaks documents >10,000 chars into logical sections and summarizes each
 */
class SmartSectioningUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val summarizeTextUseCase: SummarizeTextUseCase
) {
    companion object {
        const val SECTION_THRESHOLD = 10_000 // Characters
        const val MAX_SECTION_SIZE = 5_000 // Characters per section
        const val MIN_SECTION_SIZE = 1_000 // Minimum characters per section
        const val OVERLAP_SIZE = 200 // Characters overlap between sections
    }

    sealed class SectioningResult {
        data class Progress(val currentSection: Int, val totalSections: Int) : SectioningResult()
        data class Success(val sectionedSummary: SectionedSummary) : SectioningResult()
        data class Error(val message: String) : SectioningResult()
    }

    suspend operator fun invoke(
        text: String,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        language: String = "auto"
    ): Flow<SectioningResult> = channelFlow {
        try {
            // Check if sectioning is needed
            if (text.length < SECTION_THRESHOLD) {
                // Document is short enough, use regular summarization
                val summaryResult = summarizeTextUseCase(text, persona)
                summaryResult.fold(
                    onSuccess = { summary ->
                        send(SectioningResult.Success(
                            SectionedSummary(
                                sections = listOf(
                                    DocumentSection(
                                        title = "Full Document",
                                        content = text,
                                        summary = summary,
                                        startIndex = 0,
                                        endIndex = text.length
                                    )
                                ),
                                overallSummary = summary,
                                totalCharacters = text.length,
                                sectionCount = 1
                            )
                        ))
                    },
                    onFailure = { error ->
                        send(SectioningResult.Error(error.message ?: "Failed to summarize"))
                    }
                )
                return@channelFlow
            }

            // Smart sectioning needed
            val sections = createSmartSections(text)
            send(SectioningResult.Progress(0, sections.size))

            // Process sections sequentially to avoid concurrent emissions
            val sectionSummaries = sections.mapIndexed { index, section ->
                send(SectioningResult.Progress(index + 1, sections.size))
                val summaryResult = summarizeTextUseCase(
                    section.content,
                    persona
                )
                summaryResult.getOrNull()?.let { summary ->
                    section.copy(summary = summary)
                } ?: section
            }

            // Generate overall summary from section summaries
            val combinedSectionSummaries = sectionSummaries.joinToString("\n\n") { section ->
                """
                    Section: ${section.title}
                    ${section.summary.summary}
                    Key Points: ${section.summary.bulletPoints.joinToString("; ")}
                """.trimIndent()
            }

            val overallSummaryResult = summarizeTextUseCase(
                combinedSectionSummaries,
                persona
            )

            overallSummaryResult.fold(
                onSuccess = { overallSummary ->
                    send(SectioningResult.Success(
                        SectionedSummary(
                            sections = sectionSummaries,
                            overallSummary = overallSummary,
                            totalCharacters = text.length,
                            sectionCount = sections.size
                        )
                    ))
                },
                onFailure = { error ->
                    send(SectioningResult.Error(error.message ?: "Failed to generate overall summary"))
                }
            )

        } catch (e: Exception) {
            send(SectioningResult.Error(e.message ?: "Unknown error during sectioning"))
        }
    }

    /**
     * Creates smart sections based on natural breaks in the text
     */
    private fun createSmartSections(text: String): List<DocumentSection> {
        val sections = mutableListOf<DocumentSection>()
        
        // Try to detect natural sections first (headers, chapters, etc.)
        val naturalSections = detectNaturalSections(text)
        
        if (naturalSections.isNotEmpty() && naturalSections.all { it.content.length <= MAX_SECTION_SIZE }) {
            return naturalSections
        }

        // Fall back to paragraph-based sectioning
        val paragraphs = text.split("\n\n", "\n").filter { it.isNotBlank() }
        var currentSection = StringBuilder()
        var currentStart = 0
        var sectionNumber = 1

        paragraphs.forEach { paragraph ->
            val paragraphStart = text.indexOf(paragraph, currentStart)
            
            if (currentSection.length + paragraph.length > MAX_SECTION_SIZE && 
                currentSection.length >= MIN_SECTION_SIZE) {
                // Create section
                sections.add(
                    DocumentSection(
                        title = "Section $sectionNumber",
                        content = currentSection.toString().trim(),
                        summary = Summary.empty(), // Will be filled later
                        startIndex = currentStart,
                        endIndex = paragraphStart - 1
                    )
                )
                
                // Start new section with overlap
                currentSection = StringBuilder()
                if (sections.isNotEmpty() && paragraph.length > OVERLAP_SIZE) {
                    currentSection.append(paragraph.takeLast(OVERLAP_SIZE))
                    currentSection.append("\n\n")
                }
                currentStart = paragraphStart
                sectionNumber++
            }
            
            currentSection.append(paragraph)
            currentSection.append("\n\n")
        }

        // Add final section
        if (currentSection.isNotBlank()) {
            sections.add(
                DocumentSection(
                    title = "Section $sectionNumber",
                    content = currentSection.toString().trim(),
                    summary = Summary.empty(),
                    startIndex = currentStart,
                    endIndex = text.length
                )
            )
        }

        return sections
    }

    /**
     * Attempts to detect natural sections in the text (headers, chapters, etc.)
     */
    private fun detectNaturalSections(text: String): List<DocumentSection> {
        val sections = mutableListOf<DocumentSection>()
        
        // Common header patterns
        val headerPatterns = listOf(
            Regex("^#{1,6}\\s+(.+)$", RegexOption.MULTILINE), // Markdown headers
            Regex("^(Chapter|Section|Part)\\s+\\d+[:\\s]+(.+)$", RegexOption.MULTILINE), // Chapter headers
            Regex("^\\d+\\.\\s+(.+)$", RegexOption.MULTILINE), // Numbered sections
            Regex("^[A-Z][A-Z\\s]+$", RegexOption.MULTILINE) // All caps headers
        )

        val headerMatches = mutableListOf<Pair<Int, String>>()
        
        headerPatterns.forEach { pattern ->
            pattern.findAll(text).forEach { match ->
                headerMatches.add(match.range.first to match.value)
            }
        }

        if (headerMatches.isEmpty()) return emptyList()

        // Sort by position
        headerMatches.sortBy { it.first }

        // Create sections based on headers
        for (i in headerMatches.indices) {
            val (headerStart, headerText) = headerMatches[i]
            val contentStart = headerStart + headerText.length
            val contentEnd = if (i < headerMatches.size - 1) {
                headerMatches[i + 1].first - 1
            } else {
                text.length
            }

            val content = text.substring(contentStart, contentEnd).trim()
            if (content.length >= MIN_SECTION_SIZE) {
                sections.add(
                    DocumentSection(
                        title = headerText.trim(),
                        content = content,
                        summary = Summary.empty(),
                        startIndex = headerStart,
                        endIndex = contentEnd
                    )
                )
            }
        }

        return sections
    }
}

// Extension function for creating empty summary
private fun Summary.Companion.empty(): Summary = Summary(
    id = "",
    originalText = "",
    summary = "",
    bulletPoints = emptyList(),
    persona = SummaryPersona.GENERAL,
    createdAt = System.currentTimeMillis(),
    metrics = SummaryMetrics(
        originalWordCount = 0,
        summaryWordCount = 0,
        reductionPercentage = 0,
        originalReadingTime = 0,
        summaryReadingTime = 0
    )
)
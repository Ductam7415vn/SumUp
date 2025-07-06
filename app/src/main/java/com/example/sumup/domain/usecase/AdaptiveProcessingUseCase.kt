package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.*
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min

/**
 * Adaptive processing use case that intelligently chooses processing strategy
 * based on document size and user preference
 */
class AdaptiveProcessingUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    companion object {
        // Thresholds for automatic strategy selection
        const val SINGLE_STRATEGY_THRESHOLD = 30_000 // < 30K chars = 1 request
        const val DUAL_STRATEGY_THRESHOLD = 100_000  // 30K-100K chars = 2-3 requests
        
        // Max section sizes for different strategies
        const val SINGLE_MAX_SIZE = 30_000
        const val DUAL_MAX_SIZE = 50_000
        const val MULTI_MAX_SIZE = 10_000
        
        // Processing constants
        const val MIN_SECTION_SIZE = 5_000
        const val SECTION_OVERLAP = 300 // Character overlap between sections
    }
    
    /**
     * Get processing options based on text length
     */
    fun getProcessingOptions(textLength: Int): List<ProcessingOption> {
        val options = mutableListOf<ProcessingOption>()
        
        // Always offer Quick Summary
        options.add(
            ProcessingOption(
                strategy = ProcessingStrategy.SINGLE,
                title = "Quick Summary",
                description = "Fast processing with a single API request. Best for getting the main idea quickly.",
                estimatedRequests = 1,
                benefits = listOf(
                    "Fastest processing",
                    "Lowest API cost",
                    "Immediate results"
                ),
                drawbacks = if (textLength > SINGLE_STRATEGY_THRESHOLD) {
                    listOf("May miss some details")
                } else emptyList(),
                isRecommended = textLength < SINGLE_STRATEGY_THRESHOLD
            )
        )
        
        // Offer Balanced for medium-large texts
        if (textLength > 20_000) {
            val dualRequests = calculateRequestCount(textLength, ProcessingStrategy.DUAL)
            options.add(
                ProcessingOption(
                    strategy = ProcessingStrategy.DUAL,
                    title = "Balanced Analysis",
                    description = "Splits text into larger sections for better coverage while keeping costs reasonable.",
                    estimatedRequests = dualRequests,
                    benefits = listOf(
                        "Better detail coverage",
                        "Reasonable cost",
                        "Good for most documents"
                    ),
                    drawbacks = listOf(
                        "$dualRequests API requests",
                        "Slightly longer processing"
                    ),
                    isRecommended = textLength in SINGLE_STRATEGY_THRESHOLD..DUAL_STRATEGY_THRESHOLD
                )
            )
        }
        
        // Offer Deep Analysis for all texts, but especially large ones
        if (textLength > 10_000) {
            val multiRequests = calculateRequestCount(textLength, ProcessingStrategy.MULTI)
            options.add(
                ProcessingOption(
                    strategy = ProcessingStrategy.MULTI,
                    title = "Deep Analysis",
                    description = "Comprehensive processing with multiple sections for maximum detail extraction.",
                    estimatedRequests = multiRequests,
                    benefits = listOf(
                        "Maximum detail extraction",
                        "Best for research/analysis",
                        "Section-by-section insights"
                    ),
                    drawbacks = listOf(
                        "$multiRequests API requests",
                        "Longer processing time",
                        "Higher API cost"
                    ),
                    isRecommended = textLength > DUAL_STRATEGY_THRESHOLD
                )
            )
        }
        
        return options
    }
    
    /**
     * Calculate number of API requests for a given strategy
     */
    private fun calculateRequestCount(textLength: Int, strategy: ProcessingStrategy): Int {
        val sectionSize = when (strategy) {
            ProcessingStrategy.SINGLE -> textLength // One section
            ProcessingStrategy.DUAL -> DUAL_MAX_SIZE
            ProcessingStrategy.MULTI -> MULTI_MAX_SIZE
        }
        
        val sectionCount = ceil(textLength.toDouble() / sectionSize).toInt()
        
        // For multi-section strategies, add 1 for final aggregation
        return if (sectionCount > 1) sectionCount + 1 else 1
    }
    
    /**
     * Process text with selected strategy
     */
    suspend operator fun invoke(
        text: String,
        strategy: ProcessingStrategy,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        targetLengthRatio: Float = 0.10f, // 10% by default
        language: String = "auto"
    ): Flow<ProcessingResult> = flow {
        emit(ProcessingResult.Starting(strategy))
        
        try {
            when (strategy) {
                ProcessingStrategy.SINGLE -> {
                    emit(ProcessingResult.Progress(0.2f, "Analyzing text..."))
                    
                    // Direct summarization - preserve target ratio
                    val summary = summaryRepository.summarizeText(
                        text = text,
                        persona = persona,
                        lengthMultiplier = targetLengthRatio
                    )
                    
                    emit(ProcessingResult.Progress(1.0f, "Complete!"))
                    emit(ProcessingResult.Success(
                        summary = summary,
                        totalRequests = 1,
                        sectionsProcessed = 1
                    ))
                }
                
                ProcessingStrategy.DUAL -> {
                    processDualStrategy(text, persona, targetLengthRatio, language)
                        .collect { emit(it) }
                }
                
                ProcessingStrategy.MULTI -> {
                    processMultiStrategy(text, persona, targetLengthRatio, language)
                        .collect { emit(it) }
                }
            }
        } catch (e: Exception) {
            emit(ProcessingResult.Error(e.message ?: "Processing failed"))
        }
    }
    
    /**
     * Process with dual strategy (2-3 large sections)
     */
    private suspend fun processDualStrategy(
        text: String,
        persona: SummaryPersona,
        targetLengthRatio: Float,
        language: String
    ): Flow<ProcessingResult> = flow {
        val sections = createAdaptiveSections(text, DUAL_MAX_SIZE)
        val totalSections = sections.size
        
        emit(ProcessingResult.Progress(0.1f, "Processing $totalSections sections..."))
        
        // Process sections
        val sectionSummaries = mutableListOf<String>()
        
        sections.forEachIndexed { index, section ->
            val progress = 0.1f + (0.7f * (index + 1) / totalSections)
            emit(ProcessingResult.Progress(
                progress, 
                "Processing section ${index + 1} of $totalSections..."
            ))
            
            // Each section summary should be proportional
            val sectionSummary = summaryRepository.summarizeText(
                text = section,
                persona = persona,
                lengthMultiplier = targetLengthRatio * 2 // Slightly larger for intermediate
            )
            
            sectionSummaries.add(sectionSummary.summary)
        }
        
        // Final aggregation
        emit(ProcessingResult.Progress(0.85f, "Combining results..."))
        
        val combinedText = sectionSummaries.joinToString("\n\n")
        val finalTargetLength = (text.length * targetLengthRatio).toInt()
        
        // Ensure final summary respects original target ratio
        val finalSummary = summaryRepository.summarizeText(
            text = combinedText,
            persona = persona,
            lengthMultiplier = finalTargetLength.toFloat() / combinedText.length
        )
        
        emit(ProcessingResult.Progress(1.0f, "Complete!"))
        emit(ProcessingResult.Success(
            summary = finalSummary,
            totalRequests = totalSections + 1,
            sectionsProcessed = totalSections
        ))
    }
    
    /**
     * Process with multi strategy (many smaller sections)
     */
    private suspend fun processMultiStrategy(
        text: String,
        persona: SummaryPersona,
        targetLengthRatio: Float,
        language: String
    ): Flow<ProcessingResult> = flow {
        val sections = createAdaptiveSections(text, MULTI_MAX_SIZE)
        val totalSections = sections.size
        
        emit(ProcessingResult.Progress(0.1f, "Processing $totalSections sections..."))
        
        // Process sections in parallel batches
        val batchSize = 3
        val sectionSummaries = mutableListOf<InternalSectionSummary>()
        
        sections.chunked(batchSize).forEachIndexed { batchIndex, batch ->
            val batchProgress = 0.1f + (0.7f * (batchIndex + 1) * batchSize / totalSections)
            
            coroutineScope {
                val deferredSummaries = batch.mapIndexed { indexInBatch, section ->
                    val sectionIndex = batchIndex * batchSize + indexInBatch
                    
                    async {
                        emit(ProcessingResult.Progress(
                            batchProgress,
                            "Processing sections ${sectionIndex + 1}-${min(sectionIndex + batchSize, totalSections)} of $totalSections..."
                        ))
                        
                        val summary = summaryRepository.summarizeText(
                            text = section,
                            persona = persona,
                            lengthMultiplier = targetLengthRatio * 3 // Larger for intermediate
                        )
                        
                        InternalSectionSummary(
                            index = sectionIndex,
                            originalText = section,
                            summary = summary.summary,
                            bulletPoints = summary.bulletPoints
                        )
                    }
                }
                
                sectionSummaries.addAll(deferredSummaries.awaitAll())
            }
        }
        
        // Create structured summary from sections
        emit(ProcessingResult.Progress(0.85f, "Creating final summary..."))
        
        val structuredContent = createStructuredSummary(sectionSummaries)
        val finalTargetLength = (text.length * targetLengthRatio).toInt()
        
        // Final aggregation preserving target ratio
        val finalSummary = summaryRepository.summarizeText(
            text = structuredContent,
            persona = persona,
            lengthMultiplier = finalTargetLength.toFloat() / structuredContent.length
        )
        
        // Enhance with section insights
        val enhancedSummary = finalSummary.copy(
            sections = sectionSummaries.map { section ->
                SectionSummary(
                    id = java.util.UUID.randomUUID().toString(),
                    title = "Section ${section.index + 1}",
                    content = section.originalText,
                    summary = section.summary,
                    bulletPoints = section.bulletPoints,
                    startIndex = 0, // We don't track exact positions in our sectioning
                    endIndex = section.originalText.length,
                    status = ProcessingStatus.COMPLETED
                )
            }
        )
        
        emit(ProcessingResult.Progress(1.0f, "Complete!"))
        emit(ProcessingResult.Success(
            summary = enhancedSummary,
            totalRequests = totalSections + 1,
            sectionsProcessed = totalSections
        ))
    }
    
    /**
     * Create adaptive sections with overlap
     */
    private fun createAdaptiveSections(text: String, maxSectionSize: Int): List<String> {
        if (text.length <= maxSectionSize) {
            return listOf(text)
        }
        
        val sections = mutableListOf<String>()
        var startIndex = 0
        
        while (startIndex < text.length) {
            var endIndex = min(startIndex + maxSectionSize, text.length)
            
            // If not at the end, try to find a good break point
            if (endIndex < text.length) {
                // Look for paragraph break
                val paragraphBreak = text.lastIndexOf("\n\n", endIndex)
                if (paragraphBreak > startIndex + MIN_SECTION_SIZE) {
                    endIndex = paragraphBreak
                } else {
                    // Look for sentence break
                    val sentenceBreak = text.lastIndexOf(". ", endIndex)
                    if (sentenceBreak > startIndex + MIN_SECTION_SIZE) {
                        endIndex = sentenceBreak + 1
                    }
                }
            }
            
            sections.add(text.substring(startIndex, endIndex))
            
            // Move to next section with overlap
            startIndex = if (endIndex < text.length) {
                endIndex - SECTION_OVERLAP
            } else {
                endIndex
            }
        }
        
        return sections
    }
    
    /**
     * Create structured summary from section summaries
     */
    private fun createStructuredSummary(sections: List<InternalSectionSummary>): String {
        val builder = StringBuilder()
        
        builder.appendLine("DOCUMENT SECTIONS ANALYSIS:")
        builder.appendLine()
        
        sections.forEach { section ->
            builder.appendLine("Section ${section.index + 1}:")
            builder.appendLine(section.summary)
            if (section.bulletPoints.isNotEmpty()) {
                builder.appendLine("Key points:")
                section.bulletPoints.forEach { bullet ->
                    builder.appendLine("- $bullet")
                }
            }
            builder.appendLine()
        }
        
        builder.appendLine("OVERALL SYNTHESIS:")
        builder.appendLine("Combine the above section summaries into a cohesive overall summary.")
        
        return builder.toString()
    }
    
    /**
     * Data class for internal section summaries during processing
     */
    private data class InternalSectionSummary(
        val index: Int,
        val originalText: String,
        val summary: String,
        val bulletPoints: List<String>
    )
    
    /**
     * Sealed class for processing results
     */
    sealed class ProcessingResult {
        data class Starting(val strategy: ProcessingStrategy) : ProcessingResult()
        data class Progress(val progress: Float, val message: String) : ProcessingResult()
        data class Success(
            val summary: Summary,
            val totalRequests: Int,
            val sectionsProcessed: Int
        ) : ProcessingResult()
        data class Error(val message: String) : ProcessingResult()
    }
}
package com.example.sumup.data.repository

import com.example.sumup.data.local.dao.SummaryDao
import com.example.sumup.data.local.entity.SummaryEntity
import com.example.sumup.data.mapper.toEntity
import com.example.sumup.data.mapper.toSummary
import com.example.sumup.domain.model.*
import com.example.sumup.domain.repository.StreamingSummaryRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of StreamingSummaryRepository
 */
@Singleton
class StreamingSummaryRepositoryImpl @Inject constructor(
    private val summaryDao: SummaryDao
) : StreamingSummaryRepository {
    
    // In-memory cache for sections and events
    private val sectionsCache = mutableMapOf<String, MutableList<SectionSummary>>()
    private val eventFlows = mutableMapOf<String, MutableSharedFlow<StreamingEvent>>()
    
    override suspend fun saveSectionResult(
        summaryId: String,
        sectionIndex: Int,
        sectionSummary: SectionSummary
    ) {
        // Update cache
        val sections = sectionsCache.getOrPut(summaryId) { mutableListOf() }
        
        // Add or update section
        val existingIndex = sections.indexOfFirst { it.id == sectionSummary.id }
        if (existingIndex >= 0) {
            sections[existingIndex] = sectionSummary
        } else {
            sections.add(sectionSummary)
        }
        
        // Sort by start index
        sections.sortBy { it.startIndex }
        
        // Update database
        updatePartialSummaryInDb(summaryId, sections)
        
        // Emit event
        getOrCreateEventFlow(summaryId).emit(
            StreamingEvent.SectionCompleted(sectionSummary)
        )
    }
    
    override fun observeSummaryProgress(summaryId: String): Flow<Summary> {
        return summaryDao.observeSummaryById(summaryId)
            .filterNotNull()
            .map { entity ->
                val summary = entity.toSummary()
                
                // Enrich with cached sections if available
                val cachedSections = sectionsCache[summaryId]
                if (cachedSections != null) {
                    summary.copy(
                        sections = cachedSections,
                        processedSections = cachedSections.count { 
                            it.status == ProcessingStatus.COMPLETED 
                        }
                    )
                } else {
                    summary
                }
            }
    }
    
    override fun observeStreamingEvents(summaryId: String): Flow<StreamingEvent> {
        return getOrCreateEventFlow(summaryId).asSharedFlow()
    }
    
    override suspend fun getSections(summaryId: String): List<SectionSummary> {
        return sectionsCache[summaryId] ?: emptyList()
    }
    
    override suspend fun updateSectionStatus(
        summaryId: String,
        sectionId: String,
        status: ProcessingStatus,
        error: String?
    ) {
        val sections = sectionsCache[summaryId] ?: return
        val section = sections.find { it.id == sectionId } ?: return
        
        val updatedSection = section.copy(
            status = status,
            error = error
        )
        
        saveSectionResult(summaryId, sections.indexOf(section), updatedSection)
        
        // Emit appropriate event
        val event = when (status) {
            ProcessingStatus.PROCESSING -> StreamingEvent.SectionStarted(sectionId, sections.indexOf(section))
            ProcessingStatus.FAILED -> StreamingEvent.SectionFailed(sectionId, error ?: "Unknown error")
            else -> return
        }
        
        getOrCreateEventFlow(summaryId).emit(event)
    }
    
    override suspend fun markSummaryComplete(
        summaryId: String,
        overallSummary: String,
        overallBulletPoints: List<String>
    ) {
        val entity = summaryDao.getSummaryById(summaryId) ?: return
        val sections = sectionsCache[summaryId] ?: emptyList()
        
        val updatedEntity = entity.copy(
            summaryText = overallSummary,
            bullets = overallBulletPoints,
            isPartial = false,
            processingStatus = ProcessingStatus.COMPLETED.name,
            processedSections = sections.size,
            totalSections = sections.size
        )
        
        summaryDao.updateSummary(updatedEntity)
        
        // Emit completion event
        getOrCreateEventFlow(summaryId).emit(StreamingEvent.ProcessingComplete)
        
        // Clean up cache after a delay
        kotlinx.coroutines.delay(5000)
        sectionsCache.remove(summaryId)
        eventFlows.remove(summaryId)
    }
    
    override suspend fun createPartialSummary(
        summaryId: String,
        originalText: String,
        totalSections: Int,
        persona: SummaryPersona
    ): Summary {
        val summary = Summary(
            id = summaryId,
            originalText = originalText,
            summary = "Processing...",
            bulletPoints = emptyList(),
            persona = persona,
            createdAt = System.currentTimeMillis(),
            metrics = SummaryMetrics(
                originalWordCount = originalText.split("\\s+".toRegex()).size,
                summaryWordCount = 0,
                reductionPercentage = 0,
                originalReadingTime = calculateReadingTime(originalText),
                summaryReadingTime = 0
            ),
            isPartial = true,
            totalSections = totalSections,
            processingStatus = ProcessingStatus.PROCESSING
        )
        
        summaryDao.insertSummary(summary.toEntity())
        
        // Initialize cache
        sectionsCache[summaryId] = mutableListOf()
        
        return summary
    }
    
    override suspend fun getPartialSummary(summaryId: String): Summary? {
        return summaryDao.getSummaryById(summaryId)?.toSummary()?.let { summary ->
            // Enrich with cached sections
            val cachedSections = sectionsCache[summaryId]
            if (cachedSections != null) {
                summary.copy(
                    sections = cachedSections,
                    processedSections = cachedSections.count { 
                        it.status == ProcessingStatus.COMPLETED 
                    }
                )
            } else {
                summary
            }
        }
    }
    
    override suspend fun deleteSummary(summaryId: String) {
        summaryDao.deleteSummaryById(summaryId)
        sectionsCache.remove(summaryId)
        eventFlows.remove(summaryId)
    }
    
    private suspend fun updatePartialSummaryInDb(
        summaryId: String,
        sections: List<SectionSummary>
    ) {
        val entity = summaryDao.getSummaryById(summaryId) ?: return
        
        val completedSections = sections.filter { it.status == ProcessingStatus.COMPLETED }
        val combinedSummary = if (completedSections.isNotEmpty()) {
            completedSections.joinToString("\n\n") { section ->
                "${section.title}\n${section.summary}"
            }
        } else {
            "Processing..."
        }
        
        val combinedBulletPoints = completedSections.flatMap { it.bulletPoints }
        
        val updatedEntity = entity.copy(
            summaryText = combinedSummary,
            bullets = combinedBulletPoints,
            processedSections = completedSections.size,
            // Serializing sections would require updating the entity model
            // For now, keep them in memory cache only
        )
        
        summaryDao.updateSummary(updatedEntity)
        
        // Emit progress event
        val progress = if (entity.totalSections > 0) {
            (completedSections.size.toFloat() / entity.totalSections) * 100
        } else {
            0f
        }
        
        getOrCreateEventFlow(summaryId).emit(
            StreamingEvent.ProgressUpdate(
                current = completedSections.size,
                total = entity.totalSections,
                percentage = progress
            )
        )
    }
    
    private fun getOrCreateEventFlow(summaryId: String): MutableSharedFlow<StreamingEvent> {
        return eventFlows.getOrPut(summaryId) {
            MutableSharedFlow(replay = 1, extraBufferCapacity = 10)
        }
    }
    
    private fun calculateReadingTime(text: String): Int {
        val wordsPerMinute = 200
        val wordCount = text.split("\\s+".toRegex()).size
        return (wordCount / wordsPerMinute).coerceAtLeast(1)
    }
}

// Extension functions for entity mapping
private fun Summary.toEntity(): SummaryEntity {
    return SummaryEntity(
        id = id,
        originalText = originalText,
        summaryText = summary,
        bullets = bulletPoints,
        persona = persona.name,
        originalWordCount = metrics.originalWordCount,
        summaryWordCount = metrics.summaryWordCount,
        originalReadingTime = metrics.originalReadingTime,
        summaryReadingTime = metrics.summaryReadingTime,
        reductionPercent = metrics.reductionPercentage.toInt(),
        confidence = confidence,
        createdAt = createdAt,
        isFavorite = isFavorite,
        briefOverview = briefOverview,
        detailedSummary = detailedSummary,
        keyInsights = keyInsights,
        actionItems = actionItems,
        keywords = keywords,
        isPartial = isPartial,
        processedSections = processedSections,
        totalSections = totalSections,
        processingStatus = processingStatus.name
    )
}
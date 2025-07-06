package com.example.sumup.domain.repository

import com.example.sumup.domain.model.ProcessingStatus
import com.example.sumup.domain.model.SectionSummary
import com.example.sumup.domain.model.StreamingEvent
import com.example.sumup.domain.model.Summary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for streaming summary results
 */
interface StreamingSummaryRepository {
    
    /**
     * Save a section result as part of a larger summary
     */
    suspend fun saveSectionResult(
        summaryId: String,
        sectionIndex: Int,
        sectionSummary: SectionSummary
    )
    
    /**
     * Observe summary progress with streaming updates
     */
    fun observeSummaryProgress(summaryId: String): Flow<Summary>
    
    /**
     * Observe streaming events for real-time updates
     */
    fun observeStreamingEvents(summaryId: String): Flow<StreamingEvent>
    
    /**
     * Get current sections for a summary
     */
    suspend fun getSections(summaryId: String): List<SectionSummary>
    
    /**
     * Update section status
     */
    suspend fun updateSectionStatus(
        summaryId: String,
        sectionId: String,
        status: ProcessingStatus,
        error: String? = null
    )
    
    /**
     * Mark summary as complete with final overall summary
     */
    suspend fun markSummaryComplete(
        summaryId: String,
        overallSummary: String,
        overallBulletPoints: List<String>
    )
    
    /**
     * Create a partial summary placeholder
     */
    suspend fun createPartialSummary(
        summaryId: String,
        originalText: String,
        totalSections: Int,
        persona: com.example.sumup.domain.model.SummaryPersona
    ): Summary
    
    /**
     * Get partial summary by ID
     */
    suspend fun getPartialSummary(summaryId: String): Summary?
    
    /**
     * Delete all data for a summary
     */
    suspend fun deleteSummary(summaryId: String)
}
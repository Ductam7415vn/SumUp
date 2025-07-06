package com.example.sumup.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.sumup.domain.model.ProcessingStatus
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.StreamingSummaryRepository
import com.example.sumup.domain.repository.SummaryRepository
import com.example.sumup.domain.usecase.ParallelSectionProcessor
import com.example.sumup.domain.usecase.SmartSectioningUseCase
import com.example.sumup.domain.usecase.SummarizeTextUseCase
import com.example.sumup.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Worker for background summarization with pause/resume support
 */
@HiltWorker
class ResumableSummarizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository,
    private val streamingSummaryRepository: StreamingSummaryRepository,
    private val smartSectioningUseCase: SmartSectioningUseCase,
    private val parallelSectionProcessor: ParallelSectionProcessor,
    private val summarizeTextUseCase: SummarizeTextUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {
    
    companion object {
        // Static map to track paused jobs
        private val pausedJobs = ConcurrentHashMap<String, Boolean>()
        private val cancelledJobs = ConcurrentHashMap<String, Boolean>()
        
        const val KEY_DOCUMENT_ID = "document_id"
        const val KEY_TEXT = "text"
        const val KEY_PERSONA = "persona"
        const val KEY_TITLE = "title"
        const val KEY_RESUME_FROM_SECTION = "resume_from_section"
        
        const val KEY_PROGRESS = "progress"
        const val KEY_CURRENT_SECTION = "current_section"
        const val KEY_TOTAL_SECTIONS = "total_sections"
        const val KEY_SUMMARY_ID = "summary_id"
        const val KEY_IS_PAUSED = "is_paused"
        
        const val PAUSE_CHECK_INTERVAL = 500L // Check pause state every 500ms
        
        fun createWorkRequest(
            documentId: String,
            text: String,
            title: String? = null,
            persona: SummaryPersona = SummaryPersona.GENERAL,
            resumeFromSection: Int = 0
        ): OneTimeWorkRequest {
            val data = workDataOf(
                KEY_DOCUMENT_ID to documentId,
                KEY_TEXT to text,
                KEY_PERSONA to persona.name,
                KEY_TITLE to title,
                KEY_RESUME_FROM_SECTION to resumeFromSection
            )
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            return OneTimeWorkRequestBuilder<ResumableSummarizationWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .addTag("resumable_summarization_$documentId")
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()
        }
        
        fun pauseJob(documentId: String) {
            pausedJobs[documentId] = true
            android.util.Log.d("ResumableSummarizationWorker", "Job paused: $documentId")
        }
        
        fun resumeJob(documentId: String) {
            pausedJobs[documentId] = false
            android.util.Log.d("ResumableSummarizationWorker", "Job resumed: $documentId")
        }
        
        fun cancelJob(documentId: String) {
            cancelledJobs[documentId] = true
            pausedJobs.remove(documentId)
            android.util.Log.d("ResumableSummarizationWorker", "Job cancelled: $documentId")
        }
        
        fun isJobPaused(documentId: String): Boolean = pausedJobs[documentId] == true
        fun isJobCancelled(documentId: String): Boolean = cancelledJobs[documentId] == true
    }
    
    override suspend fun doWork(): Result {
        val documentId = inputData.getString(KEY_DOCUMENT_ID) ?: return Result.failure()
        val text = inputData.getString(KEY_TEXT) ?: return Result.failure()
        val personaName = inputData.getString(KEY_PERSONA) ?: SummaryPersona.GENERAL.name
        val title = inputData.getString(KEY_TITLE) ?: "Document"
        val resumeFromSection = inputData.getInt(KEY_RESUME_FROM_SECTION, 0)
        
        val persona = try {
            SummaryPersona.valueOf(personaName)
        } catch (e: Exception) {
            SummaryPersona.GENERAL
        }
        
        try {
            android.util.Log.d("ResumableSummarizationWorker", 
                "Starting resumable summarization for document: $documentId (resume from section: $resumeFromSection)")
            
            // Check if this is a resume operation
            val existingSummary = if (resumeFromSection > 0) {
                streamingSummaryRepository.getPartialSummary(documentId)
            } else {
                null
            }
            
            // Show notification
            notificationHelper.showProgressNotification(
                documentId = documentId,
                title = "Summarizing: $title",
                progress = 0,
                currentSection = resumeFromSection,
                totalSections = 0,
                isIndeterminate = existingSummary == null
            )
            
            // Process based on document size
            return if (text.length < SmartSectioningUseCase.SECTION_THRESHOLD) {
                processSingleDocument(documentId, text, persona, title)
            } else {
                processResumableSectionedDocument(
                    documentId, text, persona, title, 
                    existingSummary, resumeFromSection
                )
            }
            
        } catch (e: Exception) {
            android.util.Log.e("ResumableSummarizationWorker", "Failed to summarize document", e)
            
            // Check if cancelled
            if (isJobCancelled(documentId)) {
                cleanupJob(documentId)
                return Result.failure(workDataOf("error" to "Cancelled by user"))
            }
            
            // Show error notification
            notificationHelper.showErrorNotification(
                documentId = documentId,
                title = "Summarization Failed",
                message = "Failed to summarize: ${e.message}"
            )
            
            // Retry if network error
            if (e is java.net.UnknownHostException || e is java.net.SocketTimeoutException) {
                return Result.retry()
            }
            
            return Result.failure(workDataOf("error" to (e.message ?: "Unknown error")))
        } finally {
            cleanupJob(documentId)
        }
    }
    
    private suspend fun processSingleDocument(
        documentId: String,
        text: String,
        persona: SummaryPersona,
        title: String
    ): Result {
        // Check for pause/cancel
        checkPauseOrCancel(documentId)
        
        val result = summarizeTextUseCase(text, persona)
        
        return result.fold(
            onSuccess = { summary ->
                // Save to database
                summaryRepository.saveSummary(summary.copy(id = documentId))
                
                // Update notification
                notificationHelper.showCompletionNotification(
                    documentId = documentId,
                    title = "Summarization Complete",
                    message = "$title has been summarized successfully"
                )
                
                Result.success(workDataOf(KEY_SUMMARY_ID to documentId))
            },
            onFailure = { error ->
                Result.failure(workDataOf("error" to error.message))
            }
        )
    }
    
    private suspend fun processResumableSectionedDocument(
        documentId: String,
        text: String,
        persona: SummaryPersona,
        title: String,
        existingSummary: com.example.sumup.domain.model.Summary?,
        resumeFromSection: Int
    ): Result {
        // Create sections
        val sections = smartSectioningUseCase.createSmartSections(text).let { allSections ->
            if (resumeFromSection > 0) {
                // Skip already processed sections
                allSections.drop(resumeFromSection)
            } else {
                allSections
            }
        }
        
        val totalSections = sections.size + resumeFromSection
        
        // Create or update partial summary
        val summaryId = existingSummary?.id ?: documentId
        if (existingSummary == null) {
            streamingSummaryRepository.createPartialSummary(
                summaryId = summaryId,
                originalText = text,
                totalSections = totalSections,
                persona = persona
            )
        }
        
        // Process sections with pause support
        var processedCount = resumeFromSection
        val sectionResults = mutableListOf<com.example.sumup.domain.model.SectionSummary>()
        
        for ((index, section) in sections.withIndex()) {
            // Check pause/cancel state
            checkPauseOrCancel(documentId)
            
            // Wait while paused
            while (isJobPaused(documentId)) {
                android.util.Log.d("ResumableSummarizationWorker", 
                    "Job paused at section ${processedCount + 1}/$totalSections")
                
                // Update progress with paused state
                setProgress(workDataOf(
                    KEY_PROGRESS to ((processedCount * 100) / totalSections),
                    KEY_CURRENT_SECTION to processedCount,
                    KEY_TOTAL_SECTIONS to totalSections,
                    KEY_IS_PAUSED to true
                ))
                
                // Update notification
                notificationHelper.showProgressNotification(
                    documentId = documentId,
                    title = "Paused: $title",
                    progress = (processedCount * 100) / totalSections,
                    currentSection = processedCount,
                    totalSections = totalSections
                )
                
                delay(PAUSE_CHECK_INTERVAL)
            }
            
            // Process section
            android.util.Log.d("ResumableSummarizationWorker", 
                "Processing section ${processedCount + 1}/$totalSections")
            
            // Update section status to processing
            val sectionId = "section_${resumeFromSection + index}"
            streamingSummaryRepository.updateSectionStatus(
                summaryId = summaryId,
                sectionId = sectionId,
                status = ProcessingStatus.PROCESSING
            )
            
            // Summarize section
            val sectionResult = try {
                val summaryResult = summarizeTextUseCase(section.content, persona)
                summaryResult.getOrNull()?.let { summary ->
                    com.example.sumup.domain.model.SectionSummary(
                        id = sectionId,
                        title = section.title,
                        content = section.content,
                        summary = summary.summary,
                        bulletPoints = summary.bulletPoints,
                        startIndex = section.startIndex,
                        endIndex = section.endIndex,
                        status = ProcessingStatus.COMPLETED,
                        processingTime = System.currentTimeMillis()
                    )
                } ?: throw Exception("Failed to summarize section")
            } catch (e: Exception) {
                // Mark section as failed
                streamingSummaryRepository.updateSectionStatus(
                    summaryId = summaryId,
                    sectionId = sectionId,
                    status = ProcessingStatus.FAILED,
                    error = e.message
                )
                throw e
            }
            
            // Save section result
            streamingSummaryRepository.saveSectionResult(
                summaryId = summaryId,
                sectionIndex = processedCount,
                sectionSummary = sectionResult
            )
            
            sectionResults.add(sectionResult)
            processedCount++
            
            // Update progress
            val progress = (processedCount * 100) / totalSections
            setProgress(workDataOf(
                KEY_PROGRESS to progress,
                KEY_CURRENT_SECTION to processedCount,
                KEY_TOTAL_SECTIONS to totalSections,
                KEY_IS_PAUSED to false
            ))
            
            // Update notification
            notificationHelper.showProgressNotification(
                documentId = documentId,
                title = "Summarizing: $title",
                progress = progress,
                currentSection = processedCount,
                totalSections = totalSections
            )
        }
        
        // Generate overall summary if all sections are complete
        if (processedCount == totalSections) {
            val allSections = streamingSummaryRepository.getSections(summaryId)
            val combinedText = allSections.joinToString("\n\n") { section ->
                "${section.title}\n${section.summary}"
            }
            
            val overallResult = summarizeTextUseCase(combinedText, persona)
            overallResult.fold(
                onSuccess = { summary ->
                    streamingSummaryRepository.markSummaryComplete(
                        summaryId = summaryId,
                        overallSummary = summary.summary,
                        overallBulletPoints = summary.bulletPoints
                    )
                },
                onFailure = { /* Keep partial results */ }
            )
            
            // Show completion notification
            notificationHelper.showCompletionNotification(
                documentId = documentId,
                title = "Summarization Complete",
                message = "$title has been summarized successfully ($totalSections sections)"
            )
        }
        
        return Result.success(workDataOf(
            KEY_SUMMARY_ID to summaryId,
            KEY_CURRENT_SECTION to processedCount,
            KEY_TOTAL_SECTIONS to totalSections
        ))
    }
    
    private suspend fun checkPauseOrCancel(documentId: String) {
        if (isJobCancelled(documentId)) {
            throw CancellationException("Job cancelled by user")
        }
    }
    
    private fun cleanupJob(documentId: String) {
        pausedJobs.remove(documentId)
        cancelledJobs.remove(documentId)
    }
    
    private class CancellationException(message: String) : Exception(message)
}
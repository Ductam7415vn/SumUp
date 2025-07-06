package com.example.sumup.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.sumup.domain.model.DocumentSection
import com.example.sumup.domain.model.SectionedSummary
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import com.example.sumup.domain.usecase.ParallelSectionProcessor
import com.example.sumup.domain.usecase.SmartSectioningUseCase
import com.example.sumup.domain.usecase.SummarizeTextUseCase
import com.example.sumup.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Worker for background summarization of large documents
 */
@HiltWorker
class SummarizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository,
    private val smartSectioningUseCase: SmartSectioningUseCase,
    private val parallelSectionProcessor: ParallelSectionProcessor,
    private val summarizeTextUseCase: SummarizeTextUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {
    
    companion object {
        const val KEY_DOCUMENT_ID = "document_id"
        const val KEY_TEXT = "text"
        const val KEY_PERSONA = "persona"
        const val KEY_TITLE = "title"
        
        const val KEY_PROGRESS = "progress"
        const val KEY_CURRENT_SECTION = "current_section"
        const val KEY_TOTAL_SECTIONS = "total_sections"
        const val KEY_SUMMARY_ID = "summary_id"
        
        const val NOTIFICATION_ID_BASE = 1000
        
        fun createWorkRequest(
            documentId: String,
            text: String,
            title: String? = null,
            persona: SummaryPersona = SummaryPersona.GENERAL
        ): OneTimeWorkRequest {
            val data = workDataOf(
                KEY_DOCUMENT_ID to documentId,
                KEY_TEXT to text,
                KEY_PERSONA to persona.name,
                KEY_TITLE to title
            )
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            return OneTimeWorkRequestBuilder<SummarizationWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .addTag("summarization_$documentId")
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()
        }
    }
    
    override suspend fun doWork(): Result {
        val documentId = inputData.getString(KEY_DOCUMENT_ID) ?: return Result.failure()
        val text = inputData.getString(KEY_TEXT) ?: return Result.failure()
        val personaName = inputData.getString(KEY_PERSONA) ?: SummaryPersona.GENERAL.name
        val title = inputData.getString(KEY_TITLE) ?: "Document"
        val persona = try {
            SummaryPersona.valueOf(personaName)
        } catch (e: Exception) {
            SummaryPersona.GENERAL
        }
        
        // Show initial notification
        notificationHelper.showProgressNotification(
            documentId = documentId,
            title = "Summarizing: $title",
            progress = 0,
            currentSection = 0,
            totalSections = 0,
            isIndeterminate = true
        )
        
        try {
            android.util.Log.d("SummarizationWorker", "Starting summarization for document: $documentId")
            
            // Update progress
            setProgress(workDataOf(KEY_PROGRESS to 0))
            
            // Check if document needs sectioning
            if (text.length < SmartSectioningUseCase.SECTION_THRESHOLD) {
                // Simple summarization
                return processSingleDocument(documentId, text, persona, title)
            } else {
                // Sectioned summarization
                return processSectionedDocument(documentId, text, persona, title)
            }
            
        } catch (e: Exception) {
            android.util.Log.e("SummarizationWorker", "Failed to summarize document", e)
            
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
            
            return Result.failure(
                workDataOf("error" to (e.message ?: "Unknown error"))
            )
        }
    }
    
    private suspend fun processSingleDocument(
        documentId: String,
        text: String,
        persona: SummaryPersona,
        title: String
    ): Result {
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
                
                // Set final progress
                setProgress(workDataOf(
                    KEY_PROGRESS to 100,
                    KEY_SUMMARY_ID to documentId
                ))
                
                Result.success(workDataOf(KEY_SUMMARY_ID to documentId))
            },
            onFailure = { error ->
                Result.failure(workDataOf("error" to error.message))
            }
        )
    }
    
    private suspend fun processSectionedDocument(
        documentId: String,
        text: String,
        persona: SummaryPersona,
        title: String
    ): Result {
        var lastProgress = 0
        
        // Collect results from flow
        val sectioningResults = smartSectioningUseCase(text, persona).first { result ->
            when (result) {
                is SmartSectioningUseCase.SectioningResult.Progress -> {
                    val progress = (result.currentSection * 100) / result.totalSections
                    
                    // Update worker progress
                    setProgress(workDataOf(
                        KEY_PROGRESS to progress,
                        KEY_CURRENT_SECTION to result.currentSection,
                        KEY_TOTAL_SECTIONS to result.totalSections
                    ))
                    
                    // Update notification
                    notificationHelper.showProgressNotification(
                        documentId = documentId,
                        title = "Summarizing: $title",
                        progress = progress,
                        currentSection = result.currentSection,
                        totalSections = result.totalSections
                    )
                    
                    lastProgress = progress
                    false // Continue collecting
                }
                is SmartSectioningUseCase.SectioningResult.Success -> {
                    true // Stop collecting, we have the result
                }
                is SmartSectioningUseCase.SectioningResult.Error -> {
                    throw Exception(result.message)
                }
            }
        }
        
        // Extract the success result
        val sectionedSummary = when (sectioningResults) {
            is SmartSectioningUseCase.SectioningResult.Success -> sectioningResults.sectionedSummary
            else -> throw Exception("Unexpected result type")
        }
        
        // Save to database
        val summaryId = saveSectionedSummary(documentId, sectionedSummary, title)
        
        // Show completion notification
        notificationHelper.showCompletionNotification(
            documentId = documentId,
            title = "Summarization Complete",
            message = "$title has been summarized successfully (${sectionedSummary.sectionCount} sections)"
        )
        
        // Set final progress
        setProgress(workDataOf(
            KEY_PROGRESS to 100,
            KEY_SUMMARY_ID to summaryId
        ))
        
        return Result.success(workDataOf(KEY_SUMMARY_ID to summaryId))
    }
    
    private suspend fun saveSectionedSummary(
        documentId: String,
        sectionedSummary: SectionedSummary,
        title: String
    ): String {
        // Save the overall summary with sections info
        val summary = sectionedSummary.overallSummary.copy(
            id = documentId,
            originalText = buildString {
                append("Title: $title\n\n")
                append("Total sections: ${sectionedSummary.sectionCount}\n")
                append("Total characters: ${sectionedSummary.totalCharacters}\n\n")
                sectionedSummary.sections.forEach { section ->
                    append("=== ${section.title} ===\n")
                    append("${section.summary.summary}\n\n")
                }
            }
        )
        
        summaryRepository.saveSummary(summary)
        return documentId
    }
}
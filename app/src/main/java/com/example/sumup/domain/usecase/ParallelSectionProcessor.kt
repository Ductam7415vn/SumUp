package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.DocumentSection
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processes document sections in parallel with rate limiting and error handling
 */
@Singleton
class ParallelSectionProcessor @Inject constructor(
    private val summarizeTextUseCase: SummarizeTextUseCase
) {
    companion object {
        private const val DEFAULT_BATCH_SIZE = 3
        private const val MAX_CONCURRENT_REQUESTS = 5
        private const val RETRY_ATTEMPTS = 2
    }
    
    // Semaphore to limit concurrent API requests
    private val apiSemaphore = Semaphore(MAX_CONCURRENT_REQUESTS)
    
    data class ProcessingProgress(
        val currentSection: Int,
        val totalSections: Int,
        val processedSections: Int,
        val failedSections: Int
    )
    
    /**
     * Process sections in parallel batches with progress tracking
     */
    suspend fun processInBatches(
        sections: List<DocumentSection>,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        batchSize: Int = DEFAULT_BATCH_SIZE,
        onProgress: suspend (ProcessingProgress) -> Unit = {}
    ): List<DocumentSection> = coroutineScope {
        var processedCount = 0
        var failedCount = 0
        val totalSections = sections.size
        
        android.util.Log.d("ParallelSectionProcessor", 
            "Starting parallel processing of $totalSections sections in batches of $batchSize")
        
        // Process sections in batches
        sections.chunked(batchSize).flatMapIndexed { batchIndex, batch ->
            android.util.Log.d("ParallelSectionProcessor", 
                "Processing batch ${batchIndex + 1} with ${batch.size} sections")
            
            // Launch parallel processing for each section in the batch
            val deferredResults = batch.mapIndexed { indexInBatch, section ->
                async {
                    // Use semaphore to limit concurrent API calls
                    apiSemaphore.withPermit {
                        val result = processSection(
                            section = section,
                            persona = persona,
                            sectionIndex = batchIndex * batchSize + indexInBatch,
                            onComplete = {
                                synchronized(this@coroutineScope) {
                                    processedCount++
                                }
                            },
                            onError = {
                                synchronized(this@coroutineScope) {
                                    failedCount++
                                    processedCount++
                                }
                            }
                        )
                        // Report progress after processing
                        onProgress(ProcessingProgress(
                            currentSection = processedCount,
                            totalSections = totalSections,
                            processedSections = processedCount,
                            failedSections = failedCount
                        ))
                        result
                    }
                }
            }
            
            // Wait for all sections in the batch to complete
            deferredResults.awaitAll()
        }
    }
    
    /**
     * Process a single section with retry logic
     */
    private suspend fun processSection(
        section: DocumentSection,
        persona: SummaryPersona,
        sectionIndex: Int,
        retryCount: Int = 0,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): DocumentSection {
        return try {
            android.util.Log.d("ParallelSectionProcessor", 
                "Processing section $sectionIndex: ${section.title}")
            
            val startTime = System.currentTimeMillis()
            val summaryResult = summarizeTextUseCase(section.content, persona)
            val processingTime = System.currentTimeMillis() - startTime
            
            android.util.Log.d("ParallelSectionProcessor", 
                "Section $sectionIndex processed in ${processingTime}ms")
            
            summaryResult.fold(
                onSuccess = { summary ->
                    onComplete()
                    section.copy(summary = summary)
                },
                onFailure = { error ->
                    if (retryCount < RETRY_ATTEMPTS) {
                        android.util.Log.w("ParallelSectionProcessor", 
                            "Retrying section $sectionIndex after error: ${error.message}")
                        // Retry with exponential backoff
                        kotlinx.coroutines.delay(1000L * (retryCount + 1))
                        processSection(section, persona, sectionIndex, retryCount + 1, onComplete, onError)
                    } else {
                        android.util.Log.e("ParallelSectionProcessor", 
                            "Failed to process section $sectionIndex after $RETRY_ATTEMPTS attempts", error)
                        onError()
                        section // Return original section on failure
                    }
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("ParallelSectionProcessor", 
                "Exception processing section $sectionIndex", e)
            if (retryCount < RETRY_ATTEMPTS) {
                kotlinx.coroutines.delay(1000L * (retryCount + 1))
                processSection(section, persona, sectionIndex, retryCount + 1, onComplete, onError)
            } else {
                onError()
                section
            }
        }
    }
    
    /**
     * Process all sections concurrently with a maximum limit
     */
    suspend fun processAllConcurrently(
        sections: List<DocumentSection>,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        onProgress: suspend (ProcessingProgress) -> Unit = {}
    ): List<DocumentSection> = coroutineScope {
        var completedCount = 0
        var failedCount = 0
        
        sections.mapIndexed { index, section ->
            async {
                apiSemaphore.withPermit {
                    val result = processSection(
                        section = section,
                        persona = persona,
                        sectionIndex = index,
                        onComplete = {
                            synchronized(this@coroutineScope) {
                                completedCount++
                            }
                        },
                        onError = {
                            synchronized(this@coroutineScope) {
                                failedCount++
                                completedCount++
                            }
                        }
                    )
                    // Report progress after processing
                    onProgress(ProcessingProgress(
                        currentSection = completedCount,
                        totalSections = sections.size,
                        processedSections = completedCount,
                        failedSections = failedCount
                    ))
                    result
                }
            }
        }.awaitAll()
    }
}
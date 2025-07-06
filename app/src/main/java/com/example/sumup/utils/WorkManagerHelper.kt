package com.example.sumup.utils

import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.worker.SummarizationWorker
import com.example.sumup.domain.worker.ResumableSummarizationWorker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for managing WorkManager tasks
 */
@Singleton
class WorkManagerHelper @Inject constructor(
    private val workManager: WorkManager
) {
    
    companion object {
        const val UNIQUE_WORK_PREFIX = "summarization_"
    }
    
    /**
     * Enqueue a summarization task
     */
    fun enqueueSummarization(
        documentId: String,
        text: String,
        title: String? = null,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        requiresUnmeteredNetwork: Boolean = false
    ): LiveData<WorkInfo?> {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (requiresUnmeteredNetwork) NetworkType.UNMETERED 
                else NetworkType.CONNECTED
            )
            .build()
        
        val request = SummarizationWorker.createWorkRequest(
            documentId = documentId,
            text = text,
            title = title,
            persona = persona
        )
        
        // Use unique work to prevent duplicate processing
        workManager.enqueueUniqueWork(
            "$UNIQUE_WORK_PREFIX$documentId",
            ExistingWorkPolicy.KEEP, // Don't restart if already running
            request
        )
        
        return workManager.getWorkInfoByIdLiveData(request.id)
    }
    
    /**
     * Get work info for a document
     */
    fun getWorkInfo(documentId: String): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosByTagLiveData("summarization_$documentId")
    }
    
    /**
     * Cancel summarization for a document
     */
    fun cancelSummarization(documentId: String) {
        workManager.cancelUniqueWork("$UNIQUE_WORK_PREFIX$documentId")
    }
    
    /**
     * Cancel all summarization tasks
     */
    fun cancelAllSummarizations() {
        workManager.cancelAllWorkByTag("summarization")
    }
    
    /**
     * Check if a document is being processed
     */
    suspend fun isProcessing(documentId: String): Boolean {
        val workInfos = workManager.getWorkInfosByTag("summarization_$documentId").get()
        return workInfos.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }
    }
    
    /**
     * Get progress for a document
     */
    fun getProgress(documentId: String): LiveData<WorkInfo?> {
        return object : LiveData<WorkInfo?>() {
            private val workInfoLiveData = workManager.getWorkInfosByTagLiveData("summarization_$documentId")
            
            private val observer = { workInfos: List<WorkInfo>? ->
                value = workInfos?.firstOrNull { 
                    it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED 
                }
            }
            
            override fun onActive() {
                workInfoLiveData.observeForever(observer)
            }
            
            override fun onInactive() {
                workInfoLiveData.removeObserver(observer)
            }
        }
    }
    
    /**
     * Enqueue a resumable summarization task
     */
    fun enqueueResumableSummarization(
        documentId: String,
        text: String,
        title: String? = null,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        resumeFromSection: Int = 0
    ): LiveData<WorkInfo?> {
        val request = ResumableSummarizationWorker.createWorkRequest(
            documentId = documentId,
            text = text,
            title = title,
            persona = persona,
            resumeFromSection = resumeFromSection
        )
        
        // Use unique work to prevent duplicate processing
        workManager.enqueueUniqueWork(
            "resumable_$documentId",
            ExistingWorkPolicy.KEEP,
            request
        )
        
        return workManager.getWorkInfoByIdLiveData(request.id)
    }
    
    /**
     * Pause a summarization job
     */
    fun pauseSummarization(documentId: String) {
        ResumableSummarizationWorker.pauseJob(documentId)
    }
    
    /**
     * Resume a paused summarization job
     */
    fun resumeSummarization(documentId: String) {
        ResumableSummarizationWorker.resumeJob(documentId)
    }
    
    /**
     * Check if a job is paused
     */
    fun isJobPaused(documentId: String): Boolean {
        return ResumableSummarizationWorker.isJobPaused(documentId)
    }
}
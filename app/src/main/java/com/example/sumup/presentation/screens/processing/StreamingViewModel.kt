package com.example.sumup.presentation.screens.processing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.StreamingEvent
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.repository.StreamingSummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for observing streaming summary results
 */
@HiltViewModel
class StreamingViewModel @Inject constructor(
    private val streamingSummaryRepository: StreamingSummaryRepository
) : ViewModel() {
    
    private val summaryFlows = mutableMapOf<String, StateFlow<Summary?>>()
    private val eventFlows = mutableMapOf<String, Flow<StreamingEvent>>()
    
    /**
     * Observe summary progress for a document
     */
    fun observeSummaryProgress(summaryId: String): StateFlow<Summary?> {
        return summaryFlows.getOrPut(summaryId) {
            streamingSummaryRepository.observeSummaryProgress(summaryId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null
                )
        }
    }
    
    /**
     * Observe streaming events for a document
     */
    fun observeStreamingEvents(summaryId: String): Flow<StreamingEvent> {
        return eventFlows.getOrPut(summaryId) {
            streamingSummaryRepository.observeStreamingEvents(summaryId)
        }
    }
    
    /**
     * Get partial summary
     */
    suspend fun getPartialSummary(summaryId: String): Summary? {
        return streamingSummaryRepository.getPartialSummary(summaryId)
    }
}
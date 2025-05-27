package com.example.sumup.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.AppError
import com.example.sumup.domain.usecase.SummarizeTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val summarizeTextUseCase: SummarizeTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun updateText(text: String) {
        // Enforce character limit
        val trimmedText = if (text.length > 5000) text.take(5000) else text
        
        _uiState.update { 
            it.copy(
                inputText = trimmedText,
                canSummarize = trimmedText.trim().length >= 50,
                error = null
            )
        }
    }

    fun clearText() {
        _uiState.update { 
            it.copy(
                inputText = "",
                canSummarize = false,
                error = null,
                summary = null
            )
        }
    }
    
    fun showClearDialog() {
        if (uiState.value.inputText.isNotEmpty()) {
            _uiState.update { it.copy(showClearDialog = true) }
        }
    }
    
    fun confirmClear() {
        _uiState.update { 
            it.copy(
                inputText = "",
                canSummarize = false,
                showClearDialog = false,
                error = null,
                summary = null
            )
        }
    }

    fun dismissClearDialog() {
        _uiState.update { it.copy(showClearDialog = false) }
    }

    fun summarize() {
        val text = uiState.value.inputText.trim()
        if (text.length < 50) {
            _uiState.update { 
                it.copy(error = AppError.TextTooShortError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    navigateToProcessing = true,
                    error = null
                )
            }

            // Store the text to summarize for when we return from processing
            summarizeTextUseCase(text)
                .onSuccess { summary ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            summary = summary
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = AppError.UnknownError(exception.message ?: "Unknown error")
                        )
                    }
                }
        }
    }

    private suspend fun simulateProgress() {
        // Simulate AI processing stages
        _uiState.update { 
            it.copy(
                processingProgress = 0.15f,
                processingMessage = "Reading your text..."
            )
        }
        delay(500)

        _uiState.update { 
            it.copy(
                processingProgress = 0.5f,
                processingMessage = "Understanding context..."
            )
        }
        delay(1000)

        _uiState.update { 
            it.copy(
                processingProgress = 0.85f,
                processingMessage = "Creating summary..."
            )
        }
        delay(500)

        _uiState.update { 
            it.copy(
                processingProgress = 0.95f,
                processingMessage = "Almost done..."
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun setScannedText(text: String) {
        updateText(text)
    }
    
    fun showInfoDialog() {
        _uiState.update { it.copy(showInfoDialog = true) }
    }
    
    fun dismissInfoDialog() {
        _uiState.update { it.copy(showInfoDialog = false) }
    }
    
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToProcessing = false) }
    }
}
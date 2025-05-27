package com.example.sumup.presentation.screens.ocr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.AppError
import com.example.sumup.domain.model.OcrState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        _uiState.update { it.copy(isPermissionGranted = true) }
    }

    fun onPermissionDenied() {
        _uiState.update { it.copy(isPermissionGranted = false, showPermissionRationale = true) }
    }

    fun onTextDetected(text: String, confidence: Float) {
        _uiState.update { 
            it.copy(
                detectedText = text,
                confidence = confidence,
                ocrState = OcrState.Ready
            )
        }
    }

    fun startTextRecognition() {
        _uiState.update { it.copy(isProcessing = true, ocrState = OcrState.Processing) }
        
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(2000)
                val mockText = "This is sample detected text from the camera. " +
                        "It would normally come from ML Kit OCR processing."
                
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        detectedText = mockText,
                        confidence = 0.87f,
                        ocrState = OcrState.Ready
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        error = AppError.OCRFailedError,
                        ocrState = OcrState.Searching
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun updateOcrState(state: OcrState) {
        _uiState.update { it.copy(ocrState = state) }
    }
}
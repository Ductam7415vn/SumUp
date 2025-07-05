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
class OcrViewModel @Inject constructor(
    private val savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()
    
    private var _scannedText: String = ""

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
        // This function is now primarily used for manual capture trigger
        // Real-time detection is handled by CameraXPreview
        _uiState.update { it.copy(isProcessing = true, ocrState = OcrState.Processing) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun updateOcrState(state: OcrState) {
        _uiState.update { it.copy(ocrState = state) }
    }
    
    fun processDetectedText() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            
            try {
                val text = _uiState.value.detectedText
                
                // Validate text
                if (text.length < 20) {
                    _uiState.update { 
                        it.copy(
                            isProcessing = false,
                            error = AppError.TextTooShortError
                        )
                    }
                    return@launch
                }
                
                // Clean and prepare text
                val cleanedText = cleanExtractedText(text)
                _scannedText = cleanedText
                
                // Save to state for navigation
                savedStateHandle["scanned_text"] = cleanedText
                
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        navigateToMain = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        error = AppError.OCRFailedError
                    )
                }
            }
        }
    }
    
    fun getScannedText(): String = _scannedText
    
    private fun cleanExtractedText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ") // Replace multiple whitespace with single space
            .replace(Regex("[\u0000-\u001f\u007f-\u009f]"), "") // Remove control characters
            .trim()
            .take(5000) // Respect app's character limit
    }
    
    fun resetNavigation() {
        _uiState.update { it.copy(navigateToMain = false) }
    }
}
package com.example.sumup.presentation.screens.ocr

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.components.ErrorDialog
import com.example.sumup.presentation.screens.ocr.components.CameraPreview
import com.example.sumup.presentation.screens.ocr.components.PermissionRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(
    onNavigateBack: () -> Unit,
    onTextScanned: (String) -> Unit,
    viewModel: OcrViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showReviewDialog by remember { mutableStateOf(false) }
    var scannedText by remember { mutableStateOf("") }
    var textConfidence by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !uiState.isPermissionGranted -> {
                PermissionRequest(
                    onPermissionGranted = viewModel::onPermissionGranted,
                    onPermissionDenied = viewModel::onPermissionDenied,
                    onNavigateBack = onNavigateBack
                )
            }
            else -> {
                CameraPreview(
                    uiState = uiState,
                    onTextDetected = viewModel::onTextDetected,
                    onCaptureClick = viewModel::startTextRecognition,
                    onNavigateBack = onNavigateBack,
                    onTextScanned = { text ->
                        scannedText = text
                        textConfidence = uiState.confidence
                        showReviewDialog = true
                    }
                )
            }
        }

        // Error dialog overlay
        uiState.error?.let { error ->
            ErrorDialog(
                error = error,
                onDismiss = viewModel::dismissError,
                onRetry = { viewModel.startTextRecognition() }
            )
        }
        
        // Review dialog
        if (showReviewDialog && scannedText.isNotEmpty()) {
            OcrReviewDialog(
                detectedText = scannedText,
                confidence = textConfidence,
                onDismiss = { showReviewDialog = false },
                onRetake = { 
                    showReviewDialog = false
                    scannedText = ""
                },
                onContinue = { editedText ->
                    showReviewDialog = false
                    onTextScanned(editedText)
                    onNavigateBack()
                }
            )
        }
    }
}
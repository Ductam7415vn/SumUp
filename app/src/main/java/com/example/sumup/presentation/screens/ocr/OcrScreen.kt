package com.example.sumup.presentation.screens.ocr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.components.AdvancedErrorDialog
import com.example.sumup.presentation.components.PredictiveBackGestureHandler
import com.example.sumup.presentation.screens.ocr.components.CameraPreview
import com.example.sumup.presentation.screens.ocr.components.EnhancedOcrReviewDialog
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

    PredictiveBackGestureHandler(
        enabled = true,
        onBackPressed = onNavigateBack
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = !uiState.isPermissionGranted,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                PermissionRequest(
                    onPermissionGranted = viewModel::onPermissionGranted,
                    onPermissionDenied = viewModel::onPermissionDenied,
                    onNavigateBack = onNavigateBack
                )
            }
            
            AnimatedVisibility(
                visible = uiState.isPermissionGranted,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(dampingRatio = 0.8f)
                ),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(300)
                )
            ) {
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

        // Error dialog overlay with enhanced styling
        uiState.error?.let { error ->
            AdvancedErrorDialog(
                error = error,
                onDismiss = viewModel::dismissError,
                onRetry = { viewModel.startTextRecognition() }
            )
        }
        
        // Enhanced review dialog with animations
        AnimatedVisibility(
            visible = showReviewDialog && scannedText.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = 0.8f)
            ),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(200)
            )
        ) {
            EnhancedOcrReviewDialog(
                detectedText = scannedText,
                confidence = textConfidence,
                onDismiss = { 
                    showReviewDialog = false 
                },
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
}
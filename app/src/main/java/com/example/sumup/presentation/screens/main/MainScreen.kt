package com.example.sumup.presentation.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.screens.main.components.BottomActionBar
import com.example.sumup.presentation.screens.main.components.MainScreenDialogs
import com.example.sumup.presentation.screens.main.components.TextInputSection
import com.example.sumup.presentation.screens.main.components.InputTypeSelector
import com.example.sumup.presentation.screens.main.components.PdfUploadSection
import com.example.sumup.presentation.components.SmartErrorHandler
import com.example.sumup.presentation.components.SharedElementKeys
import com.example.sumup.presentation.components.animations.*
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToOcr: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProcessing: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val hapticManager = rememberHapticFeedback()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SmartErrorHandler(
        error = uiState.error,
        onDismiss = viewModel::dismissError,
        onRetry = when (uiState.error) {
            is com.example.sumup.domain.model.AppError.NetworkError,
            is com.example.sumup.domain.model.AppError.ServerError -> {{ viewModel.summarize() }}
            else -> null
        },
        isFormContext = true,
        hapticManager = hapticManager
    ) {
        Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("SumUp") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedFab(
                onClick = { 
                    hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                    onNavigateToOcr() 
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag(SharedElementKeys.MAIN_SCAN_FAB)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scan")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            BottomActionBar(
                canSummarize = uiState.canSummarize,
                onClear = viewModel::showClearDialog,
                onSummarize = viewModel::summarize,
                isLoading = uiState.isLoading,
                hasText = uiState.hasContent,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input type selector
            InputTypeSelector(
                selectedType = uiState.inputType,
                onTypeSelected = viewModel::selectInputType
            )
            
            // Content based on input type
            when (uiState.inputType) {
                MainUiState.InputType.TEXT -> {
                    TextInputSection(
                        text = uiState.inputText,
                        onTextChange = viewModel::updateText,
                        isError = uiState.inputText.length > 5000,
                        inlineError = when (uiState.error) {
                            is com.example.sumup.domain.model.AppError.TextTooShortError,
                            is com.example.sumup.domain.model.AppError.InvalidInputError -> uiState.error
                            else -> null
                        },
                        onHelpClick = viewModel::showInfoDialog,
                        modifier = Modifier.weight(1f)
                    )
                }
                MainUiState.InputType.PDF -> {
                    PdfUploadSection(
                        selectedPdfName = uiState.selectedPdfName,
                        onPdfSelected = viewModel::selectPdf,
                        onClear = viewModel::clearPdf,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                MainUiState.InputType.OCR -> {
                    TextInputSection(
                        text = uiState.inputText,
                        onTextChange = viewModel::updateText,
                        isError = uiState.inputText.length > 5000,
                        inlineError = when (uiState.error) {
                            is com.example.sumup.domain.model.AppError.TextTooShortError,
                            is com.example.sumup.domain.model.AppError.InvalidInputError,
                            is com.example.sumup.domain.model.AppError.OCRFailedError -> uiState.error
                            else -> null
                        },
                        onHelpClick = viewModel::showInfoDialog,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Show dialogs
        if (uiState.showClearDialog) {
            MainScreenDialogs.ClearConfirmationDialog(
                onConfirm = {
                    viewModel.clearText()
                    viewModel.dismissClearDialog()
                },
                onDismiss = viewModel::dismissClearDialog
            )
        }
        
        if (uiState.showInfoDialog) {
            MainScreenDialogs.InfoDialog(
                onDismiss = viewModel::dismissInfoDialog
            )
        }
        
        if (uiState.showDraftRecoveryDialog) {
            MainScreenDialogs.DraftRecoveryDialog(
                draftText = uiState.recoverableDraftText,
                onRecover = viewModel::recoverDraft,
                onDismiss = viewModel::dismissDraftRecovery
            )
        }
        
        // Handle navigation after successful summarization
        LaunchedEffect(uiState.navigateToProcessing) {
            if (uiState.navigateToProcessing) {
                onNavigateToProcessing()
                viewModel.onNavigationHandled()
            }
        }
        }
    }
}
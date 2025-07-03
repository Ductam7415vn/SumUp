package com.example.sumup.presentation.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.screens.main.components.*
import com.example.sumup.presentation.components.*
import com.example.sumup.presentation.utils.AdaptiveLayoutInfo
import com.example.sumup.presentation.utils.DeviceType
import com.example.sumup.utils.haptic.rememberHapticFeedback

/**
 * Adaptive MainScreen that adjusts layout for tablets and foldables
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveMainScreen(
    onNavigateToOcr: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProcessing: () -> Unit = {},
    adaptiveInfo: com.example.sumup.presentation.utils.AdaptiveLayoutInfo? = null,
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
        if (adaptiveInfo?.deviceType == DeviceType.TABLET || 
            (adaptiveInfo?.deviceType == DeviceType.FOLDABLE && adaptiveInfo.isLandscape == true)) {
            // Two-pane layout for tablets and large screens
            AdaptiveTwoPane(
                first = {
                    MainInputPane(
                        uiState = uiState,
                        viewModel = viewModel,
                        onNavigateToOcr = onNavigateToOcr,
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToHistory = onNavigateToHistory,
                        modifier = Modifier.fillMaxSize()
                    )
                },
                second = {
                    MainPreviewPane(
                        uiState = uiState,
                        onNavigateToProcessing = onNavigateToProcessing,
                        modifier = Modifier.fillMaxSize()
                    )
                },
                splitRatio = 0.6f
            )
        } else {
            // Single-pane layout for phones
            MainSinglePane(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToOcr = onNavigateToOcr,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToProcessing = onNavigateToProcessing,
                modifier = Modifier.fillMaxSize()
            )
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

/**
 * Input pane for two-pane layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainInputPane(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateToOcr: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("SumUp - Input") },
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
            FloatingActionButton(
                onClick = onNavigateToOcr,
                modifier = Modifier.testTag(SharedElementKeys.MAIN_SCAN_FAB)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(responsivePadding()),
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
                        onPdfSelected = { uri, _ -> viewModel.selectPdf(android.net.Uri.parse(uri)) },
                        onClear = viewModel::clearPdf,
                        uploadState = uiState.fileUploadState,
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
    }
}

/**
 * Preview pane for two-pane layout showing summary preview
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainPreviewPane(
    uiState: MainUiState,
    onNavigateToProcessing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Preview") }
            )
        },
        bottomBar = {
            BottomActionBar(
                canSummarize = uiState.canSummarize,
                onClear = { /* Clear handled in input pane */ },
                onSummarize = onNavigateToProcessing,
                isLoading = uiState.isLoading,
                hasText = uiState.hasContent,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(responsivePadding()),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.hasContent) {
                AdaptiveCard(
                    compactContent = {
                        SummaryPreviewCard(
                            inputText = uiState.inputText,
                            inputType = uiState.inputType.name,
                            canSummarize = uiState.canSummarize,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    expandedContent = {
                        SummaryPreviewCard(
                            inputText = uiState.inputText,
                            inputType = uiState.inputType.name,
                            canSummarize = uiState.canSummarize,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                )
            } else {
                CompactEmptyState(
                    type = EmptyStateType.NO_SUMMARIES
                )
            }
        }
    }
}

/**
 * Single-pane layout for compact screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainSinglePane(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateToOcr: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProcessing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.imePadding(),
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
            FABTransition(
                isFAB = uiState.inputType != MainUiState.InputType.OCR,
                onClick = onNavigateToOcr,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag(SharedElementKeys.MAIN_SCAN_FAB),
                fabContent = {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan")
                },
                buttonContent = {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Text")
                }
            )
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
            
            // Content based on input type (same as original MainScreen)
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
                        onPdfSelected = { uri, _ -> viewModel.selectPdf(android.net.Uri.parse(uri)) },
                        onClear = viewModel::clearPdf,
                        uploadState = uiState.fileUploadState,
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
    }
}

/**
 * Summary preview card for the preview pane
 */
@Composable
private fun SummaryPreviewCard(
    inputText: String,
    inputType: String,
    canSummarize: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ready to Summarize",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Input Type: $inputType",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = if (inputText.length > 100) {
                    "${inputText.take(100)}..."
                } else inputText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (canSummarize) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "✓ Ready for summarization",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}


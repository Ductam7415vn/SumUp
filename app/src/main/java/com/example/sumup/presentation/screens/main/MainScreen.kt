package com.example.sumup.presentation.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.screens.main.components.BottomActionBar
import com.example.sumup.presentation.screens.main.components.MainScreenDialogs
import com.example.sumup.presentation.screens.main.components.TextInputSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToOcr: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProcessing: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            ExtendedFloatingActionButton(
                onClick = onNavigateToOcr,
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                text = { Text("Scan") },
                modifier = Modifier.navigationBarsPadding()
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            BottomActionBar(
                canSummarize = uiState.canSummarize,
                onClear = viewModel::showClearDialog,
                onSummarize = viewModel::summarize,
                isLoading = uiState.isLoading,
                hasText = uiState.inputText.isNotEmpty(),
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { paddingValues ->
        TextInputSection(
            text = uiState.inputText,
            onTextChange = viewModel::updateText,
            isError = uiState.inputText.length > 5000,
            onHelpClick = viewModel::showInfoDialog,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
        
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
        
        // Handle navigation after successful summarization
        LaunchedEffect(uiState.navigateToProcessing) {
            if (uiState.navigateToProcessing) {
                onNavigateToProcessing()
                viewModel.onNavigationHandled()
            }
        }
    }
}
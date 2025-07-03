package com.example.sumup.presentation.screens.processing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.domain.model.ProcessingState
import com.example.sumup.domain.model.AppError
import com.example.sumup.presentation.components.AnimatedProcessingIcon
import com.example.sumup.presentation.components.SharedElementKeys
import com.example.sumup.presentation.components.ContainerTransform
import com.example.sumup.presentation.components.ConfirmationBackHandler
import com.example.sumup.presentation.components.animations.LoadingDots
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Configuration constants
object ProcessingConfig {
    const val TIMEOUT_FIRST_MESSAGE = 3000L
    const val TIMEOUT_SECOND_MESSAGE = 6000L  
    const val TIMEOUT_THIRD_MESSAGE = 10000L
    const val MOCK_PROCESSING_DURATION = 3000L
    const val MOCK_PROGRESS_STEPS = 10
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingScreen(
    onCancel: () -> Unit = {},
    onComplete: (String?) -> Unit = {},
    viewModel: com.example.sumup.presentation.screens.main.MainViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    isMockMode: Boolean = false // Developer mode flag
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    // Use mock or real progress based on mode
    var mockProgress by remember { mutableFloatStateOf(0f) }
    var mockMessage by remember { mutableStateOf("Initializing...") }
    
    val progress = if (isMockMode) mockProgress else uiState.processingProgress
    val message = if (isMockMode) mockMessage else uiState.processingMessage
    
    // Debug logging
    LaunchedEffect(progress) {
        android.util.Log.d("ProcessingScreen", "Progress updated: $progress (from uiState: ${uiState.processingProgress}), Message: $message")
        android.util.Log.d("ProcessingScreen", "Mock mode: $isMockMode, Mock progress: $mockProgress")
    }
    
    // Additional debug info and trigger processing if not started
    LaunchedEffect(Unit) {
        android.util.Log.d("ProcessingScreen", "Screen loaded. Mock mode: $isMockMode")
        android.util.Log.d("ProcessingScreen", "Initial progress: ${uiState.processingProgress}")
        android.util.Log.d("ProcessingScreen", "Initial message: ${uiState.processingMessage}")
        android.util.Log.d("ProcessingScreen", "ViewModel instance: ${viewModel.hashCode()}")
        android.util.Log.d("ProcessingScreen", "Is loading: ${uiState.isLoading}")
        android.util.Log.d("ProcessingScreen", "Navigate to result: ${uiState.navigateToResult}")
        android.util.Log.d("ProcessingScreen", "Summary ID: ${uiState.summaryId}")
        
        // If processing hasn't started, trigger it now
        if (!uiState.isLoading && uiState.processingProgress == 0f && !isMockMode) {
            android.util.Log.d("ProcessingScreen", "Processing not started, triggering now...")
            viewModel.onNavigationHandled()
        }
    }
    
    var timeoutLevel by remember { mutableIntStateOf(0) }
    var showTimeoutMessage by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Mock processing simulation
    if (isMockMode) {
        LaunchedEffect(Unit) {
            val messages = listOf(
                "Reading your text...",
                "Analyzing content...",
                "Understanding context...",
                "Identifying key points...",
                "Creating summary...",
                "Polishing results...",
                "Almost ready...",
                "Finalizing...",
                "Quality check...",
                "Done!"
            )
            
            messages.forEachIndexed { index, msg ->
                mockMessage = msg
                mockProgress = (index + 1) / messages.size.toFloat()
                delay(ProcessingConfig.MOCK_PROCESSING_DURATION / messages.size)
            }
            
            // Simulate successful completion
            delay(200)
            onComplete("mock_summary_id_${System.currentTimeMillis()}")
        }
    } else {
        // Handle real completion when navigating to result
        LaunchedEffect(uiState.navigateToResult, uiState.summaryId) {
            android.util.Log.d("ProcessingScreen", "Navigation check - navigateToResult: ${uiState.navigateToResult}, summaryId: ${uiState.summaryId}")
            if (uiState.navigateToResult && uiState.summaryId != null) {
                android.util.Log.d("ProcessingScreen", "Calling onComplete with summaryId: ${uiState.summaryId}")
                onComplete(uiState.summaryId)
                // Reset navigation state after handling
                viewModel.onResultNavigationHandled()
            }
        }
        
        // Handle errors from ViewModel
        LaunchedEffect(uiState.error) {
            uiState.error?.let { error ->
                showError = true
                errorMessage = when (error) {
                    is AppError.NetworkError -> "Network error. Please check your connection."
                    is AppError.ServerError -> "Server error occurred. Please try again."
                    is AppError.RateLimitError -> "Daily limit reached. Please try again later."
                    is AppError.TextTooShortError -> "Text is too short. Please provide more content."
                    is AppError.ApiKeyError -> "API key required. Please add it in settings."
                    is AppError.InvalidApiKeyError -> "Invalid API key. Please check your settings."
                    is AppError.UnknownError -> "Error: ${error.message}"
                    else -> "An error occurred. Please try again."
                }
            }
        }
    }
    
    // Timeout management - only show if actually processing
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading && !isMockMode) {
            delay(ProcessingConfig.TIMEOUT_FIRST_MESSAGE)
            if (uiState.isLoading && progress < 0.9f) {
                timeoutLevel = 1
                showTimeoutMessage = true
            }
            
            delay(ProcessingConfig.TIMEOUT_SECOND_MESSAGE - ProcessingConfig.TIMEOUT_FIRST_MESSAGE)
            if (uiState.isLoading && progress < 0.9f) {
                timeoutLevel = 2
            }
            
            delay(ProcessingConfig.TIMEOUT_THIRD_MESSAGE - ProcessingConfig.TIMEOUT_SECOND_MESSAGE)
            if (uiState.isLoading && progress < 0.9f) {
                timeoutLevel = 3
            }
        }
    }
    
    ConfirmationBackHandler(
        enabled = true,
        requiresConfirmation = progress > 0.5f,
        confirmationMessage = "Processing is in progress. Are you sure you want to cancel?",
        onBackPressed = {
            if (!isMockMode) {
                viewModel.cancelSummarization()
            }
            onCancel()
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("processing_screen"),
            color = MaterialTheme.colorScheme.background
        ) {
            ContainerTransform(
                visible = true,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Mock mode indicator
                    if (isMockMode) {
                        Card(
                            modifier = Modifier.padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Mock Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    
                    // Animated Logo with accessibility
                    AnimatedProcessingIcon(
                        modifier = Modifier
                            .size(120.dp)
                            .testTag(SharedElementKeys.PROCESSING_AI_ICON)
                            .semantics {
                                contentDescription = "AI processing animation"
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Progress message with loading dots
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.semantics {
                            contentDescription = "$message Processing ${(progress * 100).toInt()} percent complete"
                        }
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (progress < 1f) {
                            LoadingDots(
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Simple loading indicator instead of progress bar
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .testTag(SharedElementKeys.PROCESSING_PROGRESS),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                    
                    // Error state
                    AnimatedVisibility(
                        visible = showError && errorMessage != null,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                                .testTag("error_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Error",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage ?: "An error occurred",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(
                                        onClick = {
                                            showError = false
                                            if (!isMockMode) {
                                                viewModel.retrySummarization()
                                            }
                                        },
                                        modifier = Modifier.testTag("retry_button")
                                    ) {
                                        Text("Retry")
                                    }
                                    
                                    OutlinedButton(
                                        onClick = {
                                            if (!isMockMode) {
                                                viewModel.cancelSummarization()
                                            }
                                            onCancel()
                                        },
                                        modifier = Modifier.testTag("cancel_error_button")
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                    
                    // Timeout messages
                    AnimatedVisibility(
                        visible = showTimeoutMessage && !showError,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 32.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.semantics {
                                    contentDescription = when (timeoutLevel) {
                                        1 -> "Processing is taking longer than usual"
                                        2 -> "Almost there. Large texts take more time"
                                        3 -> "Still processing. You might want to try with shorter text"
                                        else -> ""
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (timeoutLevel) {
                                        1 -> "This is taking longer than usual..."
                                        2 -> "Almost there... Large texts take more time"
                                        3 -> "Still processing. You can try with shorter text"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            if (timeoutLevel >= 3) {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = onCancel,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier
                                        .testTag("try_shorter_text_button")
                                        .semantics {
                                            contentDescription = "Try with shorter text"
                                        }
                                ) {
                                    Text("Try Shorter Text")
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Cancel button with accessibility
                    TextButton(
                        onClick = {
                            if (!isMockMode) {
                                viewModel.cancelSummarization()
                            }
                            onCancel()
                        },
                        modifier = Modifier
                            .alpha(0.7f)
                            .testTag("cancel_button")
                            .semantics {
                                contentDescription = "Cancel the summarization process"
                            }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

// Preview Composables
@ThemePreview
@Composable
fun ProcessingScreenPreview() {
    PreviewWrapper {
        ProcessingScreen(
            onCancel = {},
            onComplete = { _ -> }
        )
    }
}

@Preview(name = "Processing - Loading State", showBackground = true)
@Composable
fun ProcessingScreenLoadingPreview() {
    PreviewWrapper {
        ProcessingScreen(
            onCancel = {},
            onComplete = { _ -> }
        )
    }
}

@Preview(name = "Processing - Error State", showBackground = true)
@Composable
fun ProcessingScreenErrorPreview() {
    PreviewWrapper {
        ProcessingScreen(
            onCancel = {},
            onComplete = { _ -> }
        )
    }
}
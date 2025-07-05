package com.example.sumup.presentation.screens.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import com.example.sumup.presentation.screens.main.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { 
            Text(
                "SumUp",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onHistoryClick) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        },
        modifier = modifier
    )
}

// InputTypeSelector is now in a separate file: InputTypeSelector.kt

@Composable
fun OcrSection(
    onOcrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        onClick = onOcrClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Camera",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Tap to scan text with camera",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Point your camera at any text to capture and summarize it",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SummaryLengthSelector(
    selectedLength: SummaryLength,
    onLengthSelected: (SummaryLength) -> Unit,
    onSelectorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Summary Length",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectorClick() },
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryLength.values().forEach { length ->
                FilterChip(
                    selected = selectedLength == length,
                    onClick = { 
                        onLengthSelected(length)
                        onSelectorClick()
                    },
                    label = { Text(length.displayName) }
                )
            }
        }
    }
}

// Commented out to avoid conflict with BottomActionBar.SummarizeButton
/*
@Composable
fun SummarizeButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Summarize",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
*/

@Composable
fun DraftRecoveryDialog(
    draftText: String,
    onRecover: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Restore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Recover Draft?") },
        text = {
            Column {
                Text("We found an unsaved draft from your previous session:")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = draftText.take(200) + if (draftText.length > 200) "..." else "",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onRecover) {
                Text("Recover")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Discard")
            }
        }
    )
}

@Composable
fun ClearConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Clear All Text?") },
        text = { Text("This will delete all your input. This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("How to Use SumUp") },
        text = {
            Text(
                """
                • Works best with 100-2000 words
                • Supports articles, emails, documents
                • Preserves key information
                • Choose summary length for different needs
                
                Minimum: 50 characters
                Maximum: 30,000 characters
                """.trimIndent()
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

enum class SummaryLength(val displayName: String, val multiplier: Float) {
    BRIEF("Brief", 0.2f),
    STANDARD("Standard", 0.3f),
    DETAILED("Detailed", 0.4f)
}
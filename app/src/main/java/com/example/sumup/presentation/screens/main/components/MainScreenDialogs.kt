package com.example.sumup.presentation.screens.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.presentation.components.ModernInfoDialog

object MainScreenDialogs {
    
    @Composable
    fun ClearConfirmationDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Clear All Text?") },
            text = { 
                Text(
                    "This will delete all your input. This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
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
            title = { Text("How to Use SumUp") },
            text = {
                Text(
                    "• Works best with 100-2000 words\n" +
                    "• Supports articles, emails, documents\n" +
                    "• Preserves key information\n" +
                    "• Choose persona style for different summaries\n\n" +
                    "Minimum: 50 characters\n" +
                    "Maximum: 30,000 characters",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Got it")
                }
            }
        )
    }
    
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
            title = { 
                Text(
                    "Recover Draft?",
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "We found an unsaved draft from your previous session:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = if (draftText.length > 100) {
                                "${draftText.take(100)}..."
                            } else draftText,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        "Would you like to recover this text?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onRecover
                ) {
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
}
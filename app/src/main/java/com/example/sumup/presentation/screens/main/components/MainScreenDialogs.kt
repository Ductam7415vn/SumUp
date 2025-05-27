package com.example.sumup.presentation.screens.main.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign

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
            title = { Text("Text Summarization Tips") },
            text = {
                Text(
                    "• Works best with 100-2000 words\n" +
                    "• Supports articles, emails, documents\n" +
                    "• Preserves key information\n" +
                    "• Choose persona style for different summaries\n\n" +
                    "Minimum: 50 characters\n" +
                    "Maximum: 5000 characters",
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
}
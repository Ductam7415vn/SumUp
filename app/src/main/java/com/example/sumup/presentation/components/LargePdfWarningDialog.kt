package com.example.sumup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class PdfProcessingOption {
    PROCESS_ALL,
    PROCESS_FIRST_50_PAGES,
    PROCESS_CUSTOM_RANGE,
    CANCEL
}

@Composable
fun LargePdfWarningDialog(
    pageCount: Int,
    estimatedTime: Long, // in seconds
    onOptionSelected: (PdfProcessingOption, IntRange?) -> Unit,
    onDismiss: () -> Unit
) {
    var showCustomRangeInput by remember { mutableStateOf(false) }
    var startPage by remember { mutableStateOf("1") }
    var endPage by remember { mutableStateOf("50") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Large PDF Detected",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "This PDF has $pageCount pages and may take approximately ${formatDuration(estimatedTime)} to process.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Processing Options:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            text = "• Process all pages (may use more API tokens)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• Process first 50 pages only",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• Select custom page range",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                if (showCustomRangeInput) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Select Page Range",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = startPage,
                                    onValueChange = { startPage = it.filter { char -> char.isDigit() } },
                                    label = { Text("Start Page") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                
                                Text("to", style = MaterialTheme.typography.bodyMedium)
                                
                                OutlinedTextField(
                                    value = endPage,
                                    onValueChange = { endPage = it.filter { char -> char.isDigit() } },
                                    label = { Text("End Page") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            
                            Text(
                                text = "Valid range: 1 to $pageCount",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!showCustomRangeInput) {
                    TextButton(
                        onClick = {
                            onOptionSelected(PdfProcessingOption.PROCESS_ALL, null)
                        }
                    ) {
                        Text("Process All")
                    }
                    
                    FilledTonalButton(
                        onClick = {
                            onOptionSelected(
                                PdfProcessingOption.PROCESS_FIRST_50_PAGES,
                                1..minOf(50, pageCount)
                            )
                        }
                    ) {
                        Text("First 50 Pages")
                    }
                    
                    OutlinedButton(
                        onClick = { showCustomRangeInput = true }
                    ) {
                        Text("Custom Range")
                    }
                } else {
                    FilledTonalButton(
                        onClick = {
                            val start = startPage.toIntOrNull() ?: 1
                            val end = endPage.toIntOrNull() ?: 50
                            val validStart = start.coerceIn(1, pageCount)
                            val validEnd = end.coerceIn(validStart, pageCount)
                            
                            onOptionSelected(
                                PdfProcessingOption.PROCESS_CUSTOM_RANGE,
                                validStart..validEnd
                            )
                        }
                    ) {
                        Text("Process Range")
                    }
                    
                    TextButton(
                        onClick = { showCustomRangeInput = false }
                    ) {
                        Text("Back")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onOptionSelected(PdfProcessingOption.CANCEL, null)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDuration(seconds: Long): String {
    val duration = seconds.seconds
    return when {
        duration.inWholeMinutes == 0L -> "${duration.inWholeSeconds} seconds"
        duration.inWholeSeconds < 120 -> "${duration.inWholeMinutes} minute"
        else -> "${duration.inWholeMinutes} minutes"
    }
}
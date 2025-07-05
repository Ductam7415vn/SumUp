package com.example.sumup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

enum class PdfProcessingOption {
    PROCESS_ALL,
    PROCESS_FIRST_50_PAGES,
    PROCESS_CUSTOM_RANGE,
    CANCEL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargePdfWarningDialog(
    pageCount: Int,
    estimatedTime: Long, // in seconds
    onOptionSelected: (PdfProcessingOption, IntRange?) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                // Title
                Text(
                    text = "Large PDF",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Message
                Text(
                    text = "This ${pageCount}-page PDF may take a while to process. For best results, we recommend processing the first 50 pages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                // Actions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Primary action - Recommended option
                    Button(
                        onClick = {
                            onOptionSelected(
                                PdfProcessingOption.PROCESS_FIRST_50_PAGES,
                                1..minOf(50, pageCount)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Process First 50 Pages")
                    }
                    
                    // Secondary action
                    OutlinedButton(
                        onClick = {
                            onOptionSelected(PdfProcessingOption.PROCESS_ALL, null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Process All ${pageCount} Pages")
                    }
                    
                    // Dismiss action
                    TextButton(
                        onClick = {
                            onOptionSelected(PdfProcessingOption.CANCEL, null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}


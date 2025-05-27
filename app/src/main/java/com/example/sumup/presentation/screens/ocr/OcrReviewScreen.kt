package com.example.sumup.presentation.screens.ocr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrReviewScreen(
    detectedText: String,
    confidence: Float,
    onRetake: () -> Unit,
    onEdit: () -> Unit,
    onContinue: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(detectedText) }
    var isEditing by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Scanned Text") },
                navigationIcon = {
                    IconButton(onClick = onRetake) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    if (isEditing) {
                        TextButton(onClick = { 
                            isEditing = false 
                        }) {
                            Text("Done")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Detected text preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = { Text("Enter text...") }
                    )
                } else {
                    SelectionContainer {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = editedText.ifEmpty { "No text detected" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            
            // Confidence indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Assessment,
                    contentDescription = "Confidence",
                    tint = when {
                        confidence > 0.8f -> MaterialTheme.colorScheme.primary
                        confidence > 0.5f -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confidence: ${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (confidence < 0.5f) {
                    Spacer(modifier = Modifier.width(16.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text("Low Quality", style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }
            
            // Tips for low confidence
            if (confidence < 0.7f) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tip: For better results, ensure good lighting and steady camera",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retake")
                }
                
                if (!isEditing) {
                    OutlinedButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                }
                
                Button(
                    onClick = { onContinue(editedText) },
                    modifier = Modifier.weight(1f),
                    enabled = editedText.isNotEmpty()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun OcrReviewDialog(
    detectedText: String,
    confidence: Float,
    onDismiss: () -> Unit,
    onRetake: () -> Unit,
    onContinue: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(detectedText) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review Scanned Text") },
        text = {
            Column {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Scanned Text") },
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = "Confidence",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Confidence: ${(confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onContinue(editedText) },
                enabled = editedText.isNotEmpty()
            ) {
                Text("Use This Text")
            }
        },
        dismissButton = {
            TextButton(onClick = onRetake) {
                Text("Retake Photo")
            }
        }
    )
}
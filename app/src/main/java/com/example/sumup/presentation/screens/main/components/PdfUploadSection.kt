package com.example.sumup.presentation.screens.main.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.FileUploadState
import com.example.sumup.domain.model.ProcessingStage

@Composable
fun PdfUploadSection(
    selectedPdfName: String?,
    onPdfSelected: (String, String) -> Unit,
    onClear: () -> Unit,
    uploadState: FileUploadState? = null,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { pdfUri ->
            val fileName = pdfUri.lastPathSegment?.substringAfterLast('/') ?: "document.pdf"
            onPdfSelected(pdfUri.toString(), fileName)
        }
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uploadState) {
                is FileUploadState.Processing -> {
                    // Processing state with progress
                    ProcessingCard(
                        stage = uploadState.stage,
                        progress = uploadState.progress,
                        fileName = selectedPdfName ?: "document.pdf"
                    )
                }
                is FileUploadState.Success -> {
                    // Success state with extracted text preview
                    SuccessCard(
                        fileName = selectedPdfName ?: "document.pdf",
                        extractedText = uploadState.extractedText,
                        onClear = onClear
                    )
                }
                is FileUploadState.Error -> {
                    // Error state with retry option
                    ErrorCard(
                        fileName = selectedPdfName ?: "document.pdf",
                        error = uploadState.error,
                        onRetry = { launcher.launch("application/pdf") },
                        onClear = onClear
                    )
                }
                else -> {
                    // Default state - file selection or selected file display
                    if (selectedPdfName == null) {
                        // Upload area
                        UploadArea(onSelect = { launcher.launch("application/pdf") })
                    } else {
                        // Selected PDF display
                        SelectedFileCard(
                            fileName = selectedPdfName,
                            onClear = onClear
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadArea(
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Tap to select PDF",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Max 10MB • Text-based PDFs only",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SelectedFileCard(
    fileName: String,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Ready to process",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onClear,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove PDF",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ProcessingCard(
    stage: ProcessingStage,
    progress: Float,
    fileName: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (stage) {
                        ProcessingStage.READING_FILE -> "Reading PDF..."
                        ProcessingStage.EXTRACTING_TEXT -> "Extracting text..."
                        ProcessingStage.CLEANING_TEXT -> "Cleaning text..."
                        ProcessingStage.PREPARING_SUMMARY -> "Preparing for summary..."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun SuccessCard(
    fileName: String,
    extractedText: String,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PDF processed successfully",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        // Text preview
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (extractedText.length > 150) {
                    "${extractedText.take(150)}..."
                } else extractedText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorCard(
    fileName: String,
    error: com.example.sumup.domain.model.FileUploadError,
    onRetry: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (error) {
                        is com.example.sumup.domain.model.FileUploadError.FileTooLarge -> "File too large"
                        is com.example.sumup.domain.model.FileUploadError.UnsupportedFormat -> "Invalid file type"
                        is com.example.sumup.domain.model.FileUploadError.NoTextFound -> "No text found in PDF"
                        is com.example.sumup.domain.model.FileUploadError.ProcessingFailed -> "Processing failed"
                        is com.example.sumup.domain.model.FileUploadError.PasswordProtected -> "Password protected"
                        is com.example.sumup.domain.model.FileUploadError.CorruptedFile -> "Corrupted file"
                        is com.example.sumup.domain.model.FileUploadError.ExtractionFailed -> "Extraction failed"
                        is com.example.sumup.domain.model.FileUploadError.NetworkError -> "Network error"
                        is com.example.sumup.domain.model.FileUploadError.UnknownError -> "Unknown error"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = when (error) {
                        is com.example.sumup.domain.model.FileUploadError.FileTooLarge -> "Maximum size is 10MB"
                        is com.example.sumup.domain.model.FileUploadError.UnsupportedFormat -> "Please select a PDF file"
                        is com.example.sumup.domain.model.FileUploadError.NoTextFound -> "Try a text-based PDF"
                        is com.example.sumup.domain.model.FileUploadError.ProcessingFailed -> error.details
                        is com.example.sumup.domain.model.FileUploadError.PasswordProtected -> "Cannot read protected files"
                        is com.example.sumup.domain.model.FileUploadError.CorruptedFile -> "File appears damaged"
                        is com.example.sumup.domain.model.FileUploadError.ExtractionFailed -> "Could not extract text"
                        is com.example.sumup.domain.model.FileUploadError.NetworkError -> error.details
                        is com.example.sumup.domain.model.FileUploadError.UnknownError -> error.details
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text("Try Another")
            }
            Button(
                onClick = onClear,
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }
    }
}
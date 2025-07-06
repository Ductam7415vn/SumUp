package com.example.sumup.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sumup.domain.model.Document
import com.example.sumup.domain.model.DocumentType
import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import com.example.sumup.ui.theme.Dimensions
import kotlinx.coroutines.delay

data class DocxPreviewState(
    val isLoading: Boolean = true,
    val document: Document? = null,
    val textPreview: String = "",
    val wordCount: Int = 0,
    val characterCount: Int = 0,
    val paragraphCount: Int = 0,
    val estimatedPages: Int = 0,
    val error: String? = null
)

@Composable
fun DocxPreviewDialog(
    documentUri: String,
    documentName: String,
    onDismiss: () -> Unit,
    onExtractText: suspend (String) -> String
) {
    var previewState by remember { mutableStateOf(DocxPreviewState()) }
    
    LaunchedEffect(documentUri) {
        try {
            // Extract text for preview
            val fullText = onExtractText(documentUri)
            
            // Calculate metrics
            val words = fullText.split("\\s+".toRegex()).filter { it.isNotBlank() }
            val paragraphs = fullText.split("\n\n").filter { it.isNotBlank() }
            val preview = fullText.take(500) + if (fullText.length > 500) "..." else ""
            
            previewState = DocxPreviewState(
                isLoading = false,
                textPreview = preview,
                wordCount = words.size,
                characterCount = fullText.length,
                paragraphCount = paragraphs.size,
                estimatedPages = (fullText.length / 3000).coerceAtLeast(1)
            )
        } catch (e: Exception) {
            previewState = DocxPreviewState(
                isLoading = false,
                error = e.message
            )
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(Dimensions.radiusXl),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationMd)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                DocxPreviewHeader(
                    documentName = documentName,
                    onClose = onDismiss
                )
                
                HorizontalDivider()
                
                // Content
                if (previewState.isLoading) {
                    LoadingContent()
                } else if (previewState.error != null) {
                    ErrorContent(error = previewState.error ?: "Unknown error")
                } else {
                    PreviewContent(state = previewState)
                }
            }
        }
    }
}

@Composable
private fun DocxPreviewHeader(
    documentName: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.spacingLg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = documentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "DOCX Document Preview (Text Only)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

@Composable
private fun ErrorContent(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.spacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(Dimensions.spacingMd))
        Text(
            text = "Failed to load preview",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PreviewContent(state: DocxPreviewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Accuracy Warning
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacingMd),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacingMd),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Limited DOCX Support",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "• Only plain text is extracted\n• Formatting, images, and tables are not preserved\n• Complex documents may have reduced accuracy\n• DOC format is not supported",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
        // Document Metrics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacingMd),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacingMd),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    icon = Icons.AutoMirrored.Filled.Article,
                    label = "Pages",
                    value = state.estimatedPages.toString() + " (est.)"
                )
                MetricItem(
                    icon = Icons.Default.TextFields,
                    label = "Words",
                    value = state.wordCount.toString()
                )
                MetricItem(
                    icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                    label = "Paragraphs",
                    value = state.paragraphCount.toString()
                )
            }
        }
        
        // Text Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.spacingMd),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.spacingMd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
                ) {
                    Icon(
                        Icons.Default.Preview,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Content Preview",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimensions.spacingSm))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = state.textPreview,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(Dimensions.spacingMd),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Character Count Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacingMd),
            colors = CardDefaults.cardColors(
                containerColor = if (state.characterCount > 5000) 
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else 
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacingMd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (state.characterCount > 5000) Icons.Default.Warning else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (state.characterCount > 5000) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSm))
                Column {
                    Text(
                        text = "${state.characterCount} characters",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (state.characterCount > 5000) {
                        Text(
                            text = "Large document - processing may take longer",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.spacingMd))
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
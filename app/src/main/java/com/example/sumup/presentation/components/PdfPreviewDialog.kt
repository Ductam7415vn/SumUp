package com.example.sumup.presentation.components

/**
 * Legacy PdfPreviewDialog wrapper for backward compatibility.
 * Uses ModernPdfPreviewDialog internally.
 */

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.model.PdfExtractionResult
import com.example.sumup.domain.usecase.StructuredPdfData
import com.example.sumup.domain.usecase.DocumentType
import com.example.sumup.domain.usecase.ReadingLevel
import com.example.sumup.domain.usecase.ProcessingMetrics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewDialog(
    pdfDocument: PdfDocument,
    extractionResult: PdfExtractionResult? = null,
    structuredData: StructuredPdfData? = null,
    metrics: ProcessingMetrics? = null,
    onDismiss: () -> Unit,
    onConfirmProcess: () -> Unit
) {
    // Use the legacy dialog for now
    LegacyPdfPreviewDialog(
        pdfDocument = pdfDocument,
        extractionResult = extractionResult,
        structuredData = structuredData,
        metrics = metrics,
        onDismiss = onDismiss,
        onConfirmProcess = onConfirmProcess
    )
}

// Original implementation kept for reference
@Composable
private fun LegacyPdfPreviewDialog(
    pdfDocument: PdfDocument,
    extractionResult: PdfExtractionResult? = null,
    structuredData: StructuredPdfData? = null,
    metrics: ProcessingMetrics? = null,
    onDismiss: () -> Unit,
    onConfirmProcess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Content", "Structure", "Metrics")
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pdfDocument.fileName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${formatFileSize(pdfDocument.sizeBytes)} • ${pdfDocument.pageCount} pages",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
                
                // Tabs
                if (extractionResult != null) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            )
                        }
                    }
                }
                
                // Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> PdfOverviewTab(pdfDocument, extractionResult, structuredData)
                        1 -> PdfContentTab(extractionResult)
                        2 -> PdfStructureTab(structuredData)
                        3 -> PdfMetricsTab(metrics, extractionResult)
                    }
                }
                
                // Actions
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = onConfirmProcess,
                            enabled = extractionResult?.success == true
                        ) {
                            Icon(
                                Icons.Default.Summarize,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Process PDF")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfOverviewTab(
    pdfDocument: PdfDocument,
    extractionResult: PdfExtractionResult?,
    structuredData: StructuredPdfData?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Document Info Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Document Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                InfoRow("File Size", formatFileSize(pdfDocument.sizeBytes))
                InfoRow("Pages", pdfDocument.pageCount.toString())
                InfoRow("Protected", if (pdfDocument.isPasswordProtected) "Yes" else "No")
                
                if (structuredData != null) {
                    InfoRow("Type", formatDocumentType(structuredData.metadata.documentType))
                    InfoRow("Language", structuredData.metadata.language)
                    InfoRow("Reading Level", formatReadingLevel(structuredData.metadata.readingLevel))
                }
            }
        }
        
        // Extraction Status
        if (extractionResult != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (extractionResult.success) 
                        MaterialTheme.colorScheme.primaryContainer
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (extractionResult.success) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (extractionResult.success)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Column {
                        Text(
                            if (extractionResult.success) "Text Extracted Successfully" else "Extraction Failed",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (extractionResult.success)
                                "Confidence: ${(extractionResult.confidence * 100).toInt()}%"
                            else
                                extractionResult.errorMessage ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        // Key Points
        if (structuredData != null && structuredData.keyPoints.isNotEmpty()) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Key Points",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    structuredData.keyPoints.take(3).forEach { point ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("•", color = MaterialTheme.colorScheme.primary)
                            Text(
                                point,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfContentTab(extractionResult: PdfExtractionResult?) {
    if (extractionResult == null || !extractionResult.success) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "No content available",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = extractionResult.extractedText.take(2000) + 
                        if (extractionResult.extractedText.length > 2000) "..." else "",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun PdfStructureTab(structuredData: StructuredPdfData?) {
    if (structuredData == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Document Structure
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Document Structure",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${structuredData.sections.size} sections found",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    structuredData.sections.take(5).forEach { section ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        section.sectionType.name,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.height(32.dp)
                            )
                            Text(
                                section.title,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            
            // Tables Found
            if (structuredData.tables.isNotEmpty()) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${structuredData.tables.size} Tables Detected",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Tables will be processed separately for better accuracy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfMetricsTab(
    metrics: ProcessingMetrics?,
    extractionResult: PdfExtractionResult?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (metrics != null) {
            // Performance Metrics
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Performance Metrics",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    MetricRow("Extraction Time", "${metrics.extractionTimeMs}ms")
                    MetricRow("Analysis Time", "${metrics.analysisTimeMs}ms")
                    MetricRow("Total Time", "${metrics.extractionTimeMs + metrics.analysisTimeMs}ms")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Quality Indicators
                    QualityIndicator("Text Quality", metrics.textQualityScore)
                    QualityIndicator("Structure Complexity", metrics.structureComplexity)
                }
            }
        }
        
        // Recommendations
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        "Processing Recommendations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                val confidence = extractionResult?.confidence ?: 0f
                when {
                    confidence < 0.5f -> {
                        Text(
                            "• Consider using OCR for scanned documents",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Check if PDF contains actual text",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    confidence < 0.8f -> {
                        Text(
                            "• Some content may be missing",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Review extracted text before processing",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    else -> {
                        Text(
                            "• High quality extraction successful",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Ready for summarization",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun QualityIndicator(label: String, score: Float) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "${(score * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = score,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                score < 0.5f -> MaterialTheme.colorScheme.error
                score < 0.8f -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}

private fun formatDocumentType(type: DocumentType): String {
    return type.name.replace("_", " ").lowercase()
        .split(" ").joinToString(" ") { it.capitalize() }
}

private fun formatReadingLevel(level: ReadingLevel): String {
    return level.name.capitalize()
}

// End of LegacyPdfPreviewDialog
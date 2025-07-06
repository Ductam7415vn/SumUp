package com.example.sumup.presentation.screens.result

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.domain.model.ProcessingStatus
import com.example.sumup.domain.model.SectionSummary
import com.example.sumup.domain.model.Summary
import com.example.sumup.presentation.screens.processing.StreamingViewModel

/**
 * Screen to display partial results while processing continues
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialResultScreen(
    summaryId: String,
    onBack: () -> Unit = {},
    onViewProcessing: () -> Unit = {},
    streamingViewModel: StreamingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val summary by streamingViewModel.observeSummaryProgress(summaryId)
        .collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partial Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (summary?.isPartial == true) {
                        TextButton(onClick = onViewProcessing) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Progress")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        summary?.let { partialSummary ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress overview
                item {
                    ProgressOverviewCard(partialSummary)
                }
                
                // Overall summary (if available)
                if (partialSummary.summary.isNotBlank() && partialSummary.summary != "Processing...") {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Summarize,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Overall Summary",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = partialSummary.summary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                if (partialSummary.bulletPoints.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    partialSummary.bulletPoints.forEach { bullet ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Text("• ", style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                text = bullet,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Section results
                if (partialSummary.sections.isNotEmpty()) {
                    item {
                        Text(
                            "Section Summaries",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(
                        items = partialSummary.sections,
                        key = { it.id }
                    ) { section ->
                        SectionCard(section)
                    }
                }
                
                // Empty state
                if (partialSummary.sections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Processing sections...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ProgressOverviewCard(summary: Summary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Processing Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = if (summary.totalSections > 0) {
                    summary.processedSections.toFloat() / summary.totalSections
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${summary.processedSections} of ${summary.totalSections} sections",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    "${(summary.processedSections * 100) / summary.totalSections.coerceAtLeast(1)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (summary.processingStatus != ProcessingStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Results will update as processing continues",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(section: SectionSummary) {
    val statusColor = when (section.status) {
        ProcessingStatus.COMPLETED -> MaterialTheme.colorScheme.primary
        ProcessingStatus.PROCESSING -> MaterialTheme.colorScheme.secondary
        ProcessingStatus.FAILED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (section.status) {
                            ProcessingStatus.COMPLETED -> Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = statusColor
                            )
                            ProcessingStatus.PROCESSING -> CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = statusColor
                            )
                            ProcessingStatus.FAILED -> Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = statusColor
                            )
                            else -> Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = statusColor
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = section.status.name.lowercase().capitalize(),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor
                        )
                    }
                }
            }
            
            when (section.status) {
                ProcessingStatus.COMPLETED -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = section.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (section.bulletPoints.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        section.bulletPoints.take(3).forEach { bullet ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text("• ", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = bullet,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        
                        if (section.bulletPoints.size > 3) {
                            Text(
                                "+${section.bulletPoints.size - 3} more",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                ProcessingStatus.FAILED -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = section.error ?: "Failed to process section",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Waiting to process...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
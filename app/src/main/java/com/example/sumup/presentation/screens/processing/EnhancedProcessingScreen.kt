package com.example.sumup.presentation.screens.processing

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.livedata.observeAsState
import androidx.work.WorkInfo
import com.example.sumup.domain.model.ProcessingStatus
import com.example.sumup.domain.model.StreamingEvent
import com.example.sumup.presentation.components.AnimatedProcessingIcon
import com.example.sumup.utils.WorkManagerHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced processing screen with detailed progress tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedProcessingScreen(
    documentId: String,
    documentTitle: String,
    isBackground: Boolean = false,
    onCancel: () -> Unit = {},
    onViewPartialResults: () -> Unit = {},
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onComplete: (String) -> Unit = {},
    workManagerHelper: WorkManagerHelper,
    streamingViewModel: StreamingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Observe work progress
    val workInfo by workManagerHelper.getProgress(documentId).observeAsState()
    val isPaused by remember { mutableStateOf(workManagerHelper.isJobPaused(documentId)) }
    
    // Observe streaming events
    val streamingEvents by streamingViewModel.observeStreamingEvents(documentId)
        .collectAsStateWithLifecycle(initialValue = null)
    
    // Extract progress data
    val progress = workInfo?.progress?.getInt("progress", 0) ?: 0
    val currentSection = workInfo?.progress?.getInt("current_section", 0) ?: 0
    val totalSections = workInfo?.progress?.getInt("total_sections", 0) ?: 0
    val isWorkPaused = workInfo?.progress?.getBoolean("is_paused", false) ?: false
    
    // Track processing time
    var elapsedTime by remember { mutableStateOf(0L) }
    LaunchedEffect(workInfo?.state) {
        if (workInfo?.state == WorkInfo.State.RUNNING && !isWorkPaused) {
            while (true) {
                delay(1000)
                elapsedTime++
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Processing Document") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    if (totalSections > 0 && currentSection > 0) {
                        TextButton(onClick = onViewPartialResults) {
                            Text("View Progress")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Document info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = documentTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Animated processing icon
            AnimatedProcessingIcon(
                modifier = Modifier.size(120.dp)
            )
            
            // Overall progress
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        isWorkPaused -> "Processing Paused"
                        totalSections > 0 -> "Processing Section $currentSection of $totalSections"
                        else -> "Analyzing document..."
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    strokeCap = StrokeCap.Round
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$progress%",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = formatElapsedTime(elapsedTime),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            // Section details (if available)
            if (totalSections > 0) {
                SectionProgressCard(
                    currentSection = currentSection,
                    totalSections = totalSections,
                    streamingEvent = streamingEvents
                )
            }
            
            // Estimated time remaining
            if (totalSections > 0 && currentSection > 0 && elapsedTime > 0) {
                val averageTimePerSection = elapsedTime / currentSection
                val remainingSections = totalSections - currentSection
                val estimatedRemaining = averageTimePerSection * remainingSections
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Elapsed",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                formatElapsedTime(elapsedTime),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Remaining",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                formatElapsedTime(estimatedRemaining),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isWorkPaused) {
                    OutlinedButton(
                        onClick = {
                            workManagerHelper.pauseSummarization(documentId)
                            onPause()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause")
                    }
                } else {
                    Button(
                        onClick = {
                            workManagerHelper.resumeSummarization(documentId)
                            onResume()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resume")
                    }
                }
                
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
            
            // Background processing option
            if (!isBackground) {
                TextButton(
                    onClick = {
                        // Navigate away while keeping the work running
                        onCancel()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Continue in Background")
                }
            }
        }
    }
    
    // Handle completion
    LaunchedEffect(workInfo?.state) {
        workInfo?.let { info ->
            if (info.state == WorkInfo.State.SUCCEEDED) {
                val summaryId = info.outputData.getString("summary_id")
                if (summaryId != null) {
                    onComplete(summaryId)
                }
            }
        }
    }
}

@Composable
fun SectionProgressCard(
    currentSection: Int,
    totalSections: Int,
    streamingEvent: StreamingEvent?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Section Progress",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Visual section indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..minOf(totalSections, 10)) {
                    SectionIndicator(
                        isCompleted = i <= currentSection,
                        isProcessing = i == currentSection && 
                            streamingEvent is StreamingEvent.SectionStarted,
                        isFailed = streamingEvent is StreamingEvent.SectionFailed && 
                            streamingEvent.sectionId == i.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                if (totalSections > 10) {
                    Text(
                        "+${totalSections - 10}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            
            // Current section details
            streamingEvent?.let { event ->
                Spacer(modifier = Modifier.height(8.dp))
                
                when (event) {
                    is StreamingEvent.SectionStarted -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Processing section ${event.sectionIndex + 1}...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    is StreamingEvent.SectionCompleted -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Green
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Section completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Green
                            )
                        }
                    }
                    is StreamingEvent.SectionFailed -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                event.error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun SectionIndicator(
    isCompleted: Boolean,
    isProcessing: Boolean,
    isFailed: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = when {
            isCompleted -> 1f
            isProcessing -> 0.5f
            else -> 0f
        },
        animationSpec = tween(300)
    )
    
    val color = when {
        isFailed -> MaterialTheme.colorScheme.error
        isCompleted -> Color.Green
        isProcessing -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Surface(
        modifier = modifier.height(4.dp),
        color = color.copy(alpha = 0.3f + (0.7f * animatedProgress)),
        shape = MaterialTheme.shapes.small
    ) {}
}

private fun formatElapsedTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    
    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, secs)
        else -> String.format("%d:%02d", minutes, secs)
    }
}

@Composable
private fun WorkInfo.observeAsState(): State<WorkInfo?> {
    return produceState<WorkInfo?>(initialValue = null) {
        // This would be connected to the actual LiveData in the real implementation
        value = this@observeAsState
    }
}
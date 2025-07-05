package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

// Enhanced processing states with academic sophistication
enum class ProcessingPhase {
    INITIALIZING,
    ANALYZING_STRUCTURE,
    EXTRACTING_TEXT,
    APPLYING_ALGORITHMS,
    OPTIMIZING_RESULTS,
    FINALIZING,
    COMPLETED,
    ERROR
}

data class ProcessingStep(
    val phase: ProcessingPhase,
    val title: String,
    val description: String,
    val estimatedDuration: Long,
    val algorithmName: String? = null,
    val complexity: String? = null
)

@Composable
fun AdvancedProcessingScreen(
    currentStep: ProcessingStep,
    progress: Float,
    steps: List<ProcessingStep>,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStepIndex = steps.indexOf(currentStep)
    var timeElapsed by remember { mutableStateOf(0L) }
    var estimatedTimeRemaining by remember { mutableStateOf(0L) }
    
    LaunchedEffect(currentStep) {
        timeElapsed = 0L
        while (currentStep.phase != ProcessingPhase.COMPLETED && currentStep.phase != ProcessingPhase.ERROR) {
            delay(100)
            timeElapsed += 100
            
            // Calculate ETA based on current progress and historical data
            estimatedTimeRemaining = if (progress > 0f) {
                ((timeElapsed / progress) * (1f - progress)).toLong()
            } else {
                currentStep.estimatedDuration
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Enhanced animated processing indicator
            AdvancedProcessingIndicator(
                phase = currentStep.phase,
                progress = progress,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Process information card
            ProcessingInfoCard(
                currentStep = currentStep,
                progress = progress,
                timeElapsed = timeElapsed,
                estimatedTimeRemaining = estimatedTimeRemaining,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Step progress indicator
            StepProgressIndicator(
                steps = steps,
                currentStepIndex = currentStepIndex,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Performance metrics
            PerformanceMetrics(
                currentStep = currentStep,
                timeElapsed = timeElapsed,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action buttons
            ProcessingActions(
                phase = currentStep.phase,
                onCancel = onCancel,
                progress = progress
            )
        }
    }
}

@Composable
private fun AdvancedProcessingIndicator(
    phase: ProcessingPhase,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (phase) {
                    ProcessingPhase.INITIALIZING -> 2000
                    ProcessingPhase.ANALYZING_STRUCTURE -> 1500
                    ProcessingPhase.EXTRACTING_TEXT -> 1200
                    ProcessingPhase.APPLYING_ALGORITHMS -> 1000
                    ProcessingPhase.OPTIMIZING_RESULTS -> 800
                    ProcessingPhase.FINALIZING -> 1500
                    else -> 2000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer rotating ring with algorithm visualization
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
        ) {
            drawAdvancedIndicator(
                phase = phase,
                progress = progress,
                pulseScale = pulseScale
            )
        }
        
        // Center icon
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (phase) {
                    ProcessingPhase.INITIALIZING -> Icons.Default.Settings
                    ProcessingPhase.ANALYZING_STRUCTURE -> Icons.Default.Analytics
                    ProcessingPhase.EXTRACTING_TEXT -> Icons.Default.TextFields
                    ProcessingPhase.APPLYING_ALGORITHMS -> Icons.Default.Psychology
                    ProcessingPhase.OPTIMIZING_RESULTS -> Icons.Default.Tune
                    ProcessingPhase.FINALIZING -> Icons.Default.CheckCircle
                    ProcessingPhase.COMPLETED -> Icons.Default.Done
                    ProcessingPhase.ERROR -> Icons.Default.Error
                },
                contentDescription = phase.name,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private fun DrawScope.drawAdvancedIndicator(
    phase: ProcessingPhase,
    progress: Float,
    pulseScale: Float
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val radius = min(size.width, size.height) / 2f - 20.dp.toPx()
    
    // Outer progress ring
    drawArc(
        color = Color(0xFF2196F3).copy(alpha = 0.3f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
        topLeft = Offset(centerX - radius, centerY - radius),
        size = Size(radius * 2, radius * 2)
    )
    
    // Progress arc
    drawArc(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFF4CAF50),
                Color(0xFF2196F3),
                Color(0xFF9C27B0)
            )
        ),
        startAngle = -90f,
        sweepAngle = progress * 360f,
        useCenter = false,
        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
        topLeft = Offset(centerX - radius, centerY - radius),
        size = Size(radius * 2, radius * 2)
    )
    
    // Algorithm visualization particles
    repeat(8) { index ->
        val angle = (index * 45f) + (progress * 360f)
        val particleRadius = radius * 0.7f
        val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * particleRadius
        val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * particleRadius
        
        val particleSize = (4 + 2 * sin(progress * PI * 4 + index)).dp.toPx()
        val alpha = 0.3f + 0.7f * (1f - abs(sin(progress * PI * 2 + index * 0.5f)))
        
        drawCircle(
            color = when (phase) {
                ProcessingPhase.ANALYZING_STRUCTURE -> Color(0xFF4CAF50).copy(alpha = alpha.toFloat())
                ProcessingPhase.EXTRACTING_TEXT -> Color(0xFF2196F3).copy(alpha = alpha.toFloat())
                ProcessingPhase.APPLYING_ALGORITHMS -> Color(0xFF9C27B0).copy(alpha = alpha.toFloat())
                else -> Color(0xFFFF9800).copy(alpha = alpha.toFloat())
            },
            radius = particleSize,
            center = Offset(x, y)
        )
    }
}

@Composable
private fun ProcessingInfoCard(
    currentStep: ProcessingStep,
    progress: Float,
    timeElapsed: Long,
    estimatedTimeRemaining: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Title and description
            Text(
                text = currentStep.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = currentStep.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Algorithm information
            currentStep.algorithmName?.let { algorithm ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = "Algorithm",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Algorithm: $algorithm",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Complexity information
            currentStep.complexity?.let { complexity ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Speed,
                        contentDescription = "Complexity",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complexity: $complexity",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress and timing information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Elapsed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(timeElapsed),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ETA",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(estimatedTimeRemaining),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressIndicator(
    steps: List<ProcessingStep>,
    currentStepIndex: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Processing Pipeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Step indicator
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = when {
                                    index < currentStepIndex -> MaterialTheme.colorScheme.primary
                                    index == currentStepIndex -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            index < currentStepIndex -> {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Completed",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                            index == currentStepIndex -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            }
                            else -> {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Step info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (index == currentStepIndex) FontWeight.Bold else FontWeight.Normal,
                            color = if (index <= currentStepIndex) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        
                        if (step.algorithmName != null && index == currentStepIndex) {
                            Text(
                                text = step.algorithmName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                if (index < steps.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Connection line
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .width(2.dp)
                            .height(16.dp)
                            .background(
                                color = if (index < currentStepIndex) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun PerformanceMetrics(
    currentStep: ProcessingStep,
    timeElapsed: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    icon = Icons.Default.Speed,
                    label = "Efficiency",
                    value = "${calculateEfficiency(timeElapsed, currentStep.estimatedDuration)}%",
                    color = MaterialTheme.colorScheme.primary
                )
                
                MetricItem(
                    icon = Icons.Default.Memory,
                    label = "Memory",
                    value = "Optimized",
                    color = MaterialTheme.colorScheme.secondary
                )
                
                MetricItem(
                    icon = Icons.Default.Science,
                    label = "Algorithm",
                    value = "Advanced",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProcessingActions(
    phase: ProcessingPhase,
    onCancel: () -> Unit,
    progress: Float
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (phase == ProcessingPhase.ERROR) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Retry")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        } else if (phase != ProcessingPhase.COMPLETED) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.alpha(if (progress > 0.8f) 0.5f else 1f)
            ) {
                Icon(Icons.Default.Cancel, contentDescription = "Cancel")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel")
            }
        }
    }
}

// Utility functions
private fun formatTime(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
        else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
    }
}

private fun calculateEfficiency(elapsed: Long, estimated: Long): Int {
    return if (estimated > 0) {
        ((estimated.toFloat() / maxOf(elapsed, 1L)) * 100).toInt().coerceAtMost(100)
    } else 100
}

// Default processing steps for the application
fun getDefaultProcessingSteps(): List<ProcessingStep> = listOf(
    ProcessingStep(
        phase = ProcessingPhase.INITIALIZING,
        title = "Initializing System",
        description = "Setting up processing environment and loading models",
        estimatedDuration = 1000L,
        algorithmName = "System Bootstrap",
        complexity = "O(1)"
    ),
    ProcessingStep(
        phase = ProcessingPhase.ANALYZING_STRUCTURE,
        title = "Analyzing Document Structure",
        description = "Applying spatial clustering and layout detection algorithms",
        estimatedDuration = 2000L,
        algorithmName = "DBSCAN + Layout Analysis",
        complexity = "O(n log n)"
    ),
    ProcessingStep(
        phase = ProcessingPhase.EXTRACTING_TEXT,
        title = "Extracting Text Content",
        description = "Processing with ML Kit and advanced OCR techniques",
        estimatedDuration = 3000L,
        algorithmName = "ML Kit + Custom Enhancement",
        complexity = "O(n)"
    ),
    ProcessingStep(
        phase = ProcessingPhase.APPLYING_ALGORITHMS,
        title = "Applying AI Algorithms",
        description = "Running intelligent text analysis and optimization",
        estimatedDuration = 4000L,
        algorithmName = "Transformer + NLP Pipeline",
        complexity = "O(n²)"
    ),
    ProcessingStep(
        phase = ProcessingPhase.OPTIMIZING_RESULTS,
        title = "Optimizing Results",
        description = "Applying quality enhancement and final processing",
        estimatedDuration = 1500L,
        algorithmName = "Quality Optimization",
        complexity = "O(n)"
    ),
    ProcessingStep(
        phase = ProcessingPhase.FINALIZING,
        title = "Finalizing Output",
        description = "Preparing final results and cleanup",
        estimatedDuration = 500L,
        algorithmName = "Result Compilation",
        complexity = "O(1)"
    )
)
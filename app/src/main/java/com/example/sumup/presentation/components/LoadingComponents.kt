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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

// Specialized loading states for different operations
enum class LoadingType {
    PDF_PROCESSING,
    OCR_SCANNING,
    AI_SUMMARIZATION,
    FILE_UPLOAD,
    NETWORK_REQUEST,
    DATA_ANALYSIS,
    IMAGE_PROCESSING
}

@Composable
fun SmartLoadingIndicator(
    type: LoadingType,
    progress: Float = -1f, // -1 indicates indeterminate
    message: String? = null,
    subMessage: String? = null,
    showMetrics: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isIndeterminate = progress < 0f
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Specialized indicator based on type
            when (type) {
                LoadingType.PDF_PROCESSING -> PdfProcessingIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
                LoadingType.OCR_SCANNING -> OcrScanningIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
                LoadingType.AI_SUMMARIZATION -> AiSummarizationIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
                LoadingType.FILE_UPLOAD -> FileUploadIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
                LoadingType.NETWORK_REQUEST -> NetworkRequestIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
                LoadingType.DATA_ANALYSIS -> DataAnalysisIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
                LoadingType.IMAGE_PROCESSING -> ImageProcessingIndicator(
                    progress = progress,
                    isIndeterminate = isIndeterminate
                )
            }
            
            if (message != null || subMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                message?.let { msg ->
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
                
                subMessage?.let { subMsg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            if (!isIndeterminate) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress bar with percentage
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = getProgressColor(progress, type),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            
            if (showMetrics && !isIndeterminate) {
                Spacer(modifier = Modifier.height(16.dp))
                LoadingMetrics(type = type, progress = progress)
            }
        }
    }
}

@Composable
private fun PdfProcessingIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Document pages animation
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPdfProcessingAnimation(
                progress = if (isIndeterminate) rotation / 360f else progress,
                isIndeterminate = isIndeterminate
            )
        }
        
        Icon(
            Icons.Default.PictureAsPdf,
            contentDescription = "PDF Processing",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun OcrScanningIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawOcrScanningAnimation(
                scanPosition = if (isIndeterminate) scanLine else progress,
                isIndeterminate = isIndeterminate
            )
        }
        
        Icon(
            Icons.Default.Scanner,
            contentDescription = "OCR Scanning",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun AiSummarizationIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val brainWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAiProcessingAnimation(
                wavePhase = brainWave,
                progress = if (isIndeterminate) 0.5f else progress
            )
        }
        
        Icon(
            Icons.Default.Psychology,
            contentDescription = "AI Processing",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun FileUploadIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val uploadOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFileUploadAnimation(
                uploadProgress = if (isIndeterminate) uploadOffset else progress
            )
        }
        
        Icon(
            Icons.Default.CloudUpload,
            contentDescription = "File Upload",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun NetworkRequestIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawNetworkAnimation(
                pulseRadius = pulseRadius,
                progress = if (isIndeterminate) 0.5f else progress
            )
        }
        
        Icon(
            Icons.Default.Wifi,
            contentDescription = "Network Request",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun DataAnalysisIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val dataFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDataAnalysisAnimation(
                dataFlow = dataFlow,
                progress = if (isIndeterminate) 0.5f else progress
            )
        }
        
        Icon(
            Icons.Default.Analytics,
            contentDescription = "Data Analysis",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun ImageProcessingIndicator(
    progress: Float,
    isIndeterminate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val processingFrame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawImageProcessingAnimation(
                processingFrame = processingFrame,
                progress = if (isIndeterminate) 0.5f else progress
            )
        }
        
        Icon(
            Icons.Default.Image,
            contentDescription = "Image Processing",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun LoadingMetrics(
    type: LoadingType,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val metrics = getLoadingMetrics(type, progress)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            metrics.forEach { metric ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Animation drawing functions
private fun DrawScope.drawPdfProcessingAnimation(
    progress: Float,
    isIndeterminate: Boolean
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val pageWidth = size.width * 0.6f
    val pageHeight = size.height * 0.7f
    
    // Draw multiple pages with processing effect
    repeat(3) { pageIndex ->
        val offset = pageIndex * 8.dp.toPx()
        val alpha = if (isIndeterminate) {
            0.3f + 0.7f * ((progress + pageIndex * 0.33f) % 1f)
        } else {
            if (progress > pageIndex * 0.33f) 1f else 0.3f
        }
        
        drawRoundRect(
            color = Color(0xFF2196F3).copy(alpha = alpha),
            topLeft = Offset(centerX - pageWidth/2 + offset, centerY - pageHeight/2 + offset),
            size = Size(pageWidth, pageHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawOcrScanningAnimation(
    scanPosition: Float,
    isIndeterminate: Boolean
) {
    val scanLineY = size.height * 0.2f + (size.height * 0.6f * scanPosition)
    
    // Draw scan line
    drawLine(
        color = Color(0xFF4CAF50),
        start = Offset(size.width * 0.1f, scanLineY),
        end = Offset(size.width * 0.9f, scanLineY),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
    
    // Draw scan area
    drawRect(
        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
        topLeft = Offset(size.width * 0.1f, size.height * 0.2f),
        size = Size(size.width * 0.8f, scanLineY - size.height * 0.2f)
    )
}

private fun DrawScope.drawAiProcessingAnimation(
    wavePhase: Float,
    progress: Float
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val radius = size.minDimension / 3f
    
    // Draw brain wave pattern
    repeat(8) { i ->
        val angle = (i * 45f + wavePhase * 30f) * PI / 180f
        val waveRadius = radius + sin(wavePhase + i * 0.5f) * 10.dp.toPx()
        val x = centerX + cos(angle).toFloat() * waveRadius
        val y = centerY + sin(angle).toFloat() * waveRadius
        
        drawCircle(
            color = Color(0xFF9C27B0).copy(alpha = 0.6f),
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawFileUploadAnimation(
    uploadProgress: Float
) {
    val centerX = size.width / 2f
    val arrowHeight = size.height * 0.4f
    val arrowY = size.height * 0.7f - (arrowHeight * uploadProgress)
    
    // Draw upload arrow
    val arrowPath = Path().apply {
        moveTo(centerX, arrowY)
        lineTo(centerX - 15.dp.toPx(), arrowY + 15.dp.toPx())
        moveTo(centerX, arrowY)
        lineTo(centerX + 15.dp.toPx(), arrowY + 15.dp.toPx())
        moveTo(centerX, arrowY)
        lineTo(centerX, arrowY + arrowHeight)
    }
    
    drawPath(
        path = arrowPath,
        color = Color(0xFF2196F3),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawNetworkAnimation(
    pulseRadius: Float,
    progress: Float
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    
    // Draw network pulses
    repeat(3) { i ->
        val radius = (size.minDimension / 6f) * (1f + pulseRadius * (i + 1))
        val alpha = (1f - pulseRadius) * (1f - i * 0.3f)
        
        drawCircle(
            color = Color(0xFF2196F3).copy(alpha = alpha.coerceAtLeast(0f)),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawDataAnalysisAnimation(
    dataFlow: Float,
    progress: Float
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    
    // Draw data flow lines
    repeat(6) { i ->
        val startAngle = i * 60f
        val endAngle = startAngle + 30f
        val radius = size.minDimension / 3f
        
        val flowProgress = (dataFlow + i * 0.2f) % 1f
        val alpha = sin(flowProgress * PI).toFloat()
        
        drawArc(
            color = Color(0xFF9C27B0).copy(alpha = alpha),
            startAngle = startAngle,
            sweepAngle = endAngle - startAngle,
            useCenter = false,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            topLeft = Offset(centerX - radius, centerY - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}

private fun DrawScope.drawImageProcessingAnimation(
    processingFrame: Float,
    progress: Float
) {
    val frameSize = size.minDimension * 0.6f
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    
    // Draw processing grid
    val gridSize = 8
    val cellSize = frameSize / gridSize
    
    repeat(gridSize) { row ->
        repeat(gridSize) { col ->
            val cellProgress = ((processingFrame + (row + col) * 0.1f) % 1f)
            val alpha = sin(cellProgress * PI).toFloat()
            
            drawRect(
                color = Color(0xFF4CAF50).copy(alpha = alpha * 0.5f),
                topLeft = Offset(
                    centerX - frameSize/2 + col * cellSize,
                    centerY - frameSize/2 + row * cellSize
                ),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

// Utility functions
private fun getProgressColor(progress: Float, type: LoadingType): Color {
    return when (type) {
        LoadingType.PDF_PROCESSING -> Color(0xFF2196F3)
        LoadingType.OCR_SCANNING -> Color(0xFF4CAF50)
        LoadingType.AI_SUMMARIZATION -> Color(0xFF9C27B0)
        LoadingType.FILE_UPLOAD -> Color(0xFF2196F3)
        LoadingType.NETWORK_REQUEST -> Color(0xFF00BCD4)
        LoadingType.DATA_ANALYSIS -> Color(0xFF9C27B0)
        LoadingType.IMAGE_PROCESSING -> Color(0xFF4CAF50)
    }
}

private data class LoadingMetric(
    val label: String,
    val value: String
)

private fun getLoadingMetrics(type: LoadingType, progress: Float): List<LoadingMetric> {
    return when (type) {
        LoadingType.PDF_PROCESSING -> listOf(
            LoadingMetric("Algorithm", "DBSCAN"),
            LoadingMetric("Complexity", "O(n log n)"),
            LoadingMetric("Quality", "High")
        )
        LoadingType.OCR_SCANNING -> listOf(
            LoadingMetric("Engine", "ML Kit"),
            LoadingMetric("Accuracy", "${85 + (progress * 10).toInt()}%"),
            LoadingMetric("Speed", "Fast")
        )
        LoadingType.AI_SUMMARIZATION -> listOf(
            LoadingMetric("Model", "Gemini"),
            LoadingMetric("Tokens", "${(progress * 1000).toInt()}"),
            LoadingMetric("Quality", "Premium")
        )
        else -> listOf(
            LoadingMetric("Status", "Processing"),
            LoadingMetric("Stage", "${(progress * 5).toInt() + 1}/5"),
            LoadingMetric("Quality", "Optimal")
        )
    }
}

// Enhanced Loading Components with Shimmer Support

@Composable
fun EnhancedLoadingState(
    isLoading: Boolean,
    hasData: Boolean,
    shimmerContent: @Composable () -> Unit,
    actualContent: @Composable () -> Unit,
    loadingContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            isLoading && !hasData -> {
                // First time loading - show shimmer
                shimmerContent()
            }
            isLoading && hasData -> {
                // Subsequent loading - show actual content with loading overlay
                actualContent()
                if (loadingContent != null) {
                    loadingContent()
                }
            }
            else -> {
                // Not loading - show actual content
                actualContent()
            }
        }
    }
}

@Composable
fun LoadingOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    }
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
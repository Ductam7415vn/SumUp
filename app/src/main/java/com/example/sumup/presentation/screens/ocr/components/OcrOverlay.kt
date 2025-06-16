package com.example.sumup.presentation.screens.ocr.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.OcrState

enum class CaptureMode {
    Auto, Manual
}

@Composable
fun OcrOverlay(
    uiState: com.example.sumup.presentation.screens.ocr.OcrUiState,
    onCaptureClick: () -> Unit,
    onTextScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var captureMode by remember { mutableStateOf(CaptureMode.Auto) }
    var flashEnabled by remember { mutableStateOf(false) }
    var showTextPreview by remember { mutableStateOf(false) }
    
    // Auto-capture logic for high confidence text
    LaunchedEffect(uiState.confidence, captureMode) {
        if (captureMode == CaptureMode.Auto && 
            uiState.confidence > 0.85f && 
            uiState.ocrState == OcrState.Ready &&
            uiState.detectedText.isNotBlank()) {
            
            kotlinx.coroutines.delay(1000) // Wait 1 second for stable text
            onTextScanned(uiState.detectedText)
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Dark overlay with cutout
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                // Draw dark overlay
                canvas.drawRect(
                    0f,
                    0f,
                    size.width,
                    size.height,
                    androidx.compose.ui.graphics.Paint().apply {
                        color = Color.Black.copy(alpha = 0.5f)
                    }
                )
            }
        }
        
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Close button handled in parent
            Spacer(modifier = Modifier.width(48.dp))
            
            Row {
                // Flash button
                IconButton(
                    onClick = { flashEnabled = !flashEnabled },
                    modifier = Modifier
                        .background(
                            color = if (flashEnabled) Color.Yellow.copy(alpha = 0.3f) 
                                   else Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (flashEnabled) Color.Yellow else Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Text preview toggle
                IconButton(
                    onClick = { showTextPreview = !showTextPreview },
                    modifier = Modifier
                        .background(
                            color = if (showTextPreview) Color.Blue.copy(alpha = 0.3f) 
                                   else Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        if (showTextPreview) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Preview Text",
                        tint = if (showTextPreview) Color.Blue else Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Import from gallery
                IconButton(
                    onClick = { /* TODO: Import from gallery */ },
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = "Import",
                        tint = Color.White
                    )
                }
            }
        }
        
        // Enhanced guide frame with better visual feedback
        EnhancedGuideFrame(
            state = uiState.ocrState,
            confidence = uiState.confidence,
            detectedText = uiState.detectedText,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f)
                .align(Alignment.Center)
        )
        
        // Real-time text preview overlay
        if (showTextPreview && uiState.detectedText.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detected Text",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                        
                        // Confidence indicator
                        ConfidenceIndicator(
                            confidence = uiState.confidence,
                            modifier = Modifier
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = uiState.detectedText.take(200) + 
                               if (uiState.detectedText.length > 200) "..." else "",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 4
                    )
                }
            }
        }
        
        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // OCR State indicator with enhanced info
            OcrStateIndicator(
                state = uiState.ocrState,
                confidence = uiState.confidence,
                wordCount = uiState.detectedText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Capture mode toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { captureMode = CaptureMode.Manual },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (captureMode == CaptureMode.Manual) 
                            Color.White else Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Manual")
                }
                
                Text(
                    text = if (captureMode == CaptureMode.Auto) "Auto" else "Auto",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                Switch(
                    checked = captureMode == CaptureMode.Auto,
                    onCheckedChange = { 
                        captureMode = if (it) CaptureMode.Auto else CaptureMode.Manual 
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Capture button
            CaptureButton(
                enabled = uiState.ocrState == OcrState.Ready || 
                         captureMode == CaptureMode.Manual,
                onClick = onCaptureClick
            )
        }
    }
}

@Composable
private fun EnhancedGuideFrame(
    state: OcrState,
    confidence: Float,
    detectedText: String,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OcrState.Searching -> MaterialTheme.colorScheme.primary
            OcrState.Focusing -> MaterialTheme.colorScheme.tertiary
            OcrState.Ready -> Color(0xFF4CAF50) // Green
            OcrState.Processing -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(400)
    )
    
    val strokeWidth by animateFloatAsState(
        targetValue = when (state) {
            OcrState.Ready -> 4f
            OcrState.Processing -> 3f
            else -> 2f
        },
        animationSpec = tween(300)
    )
    
    val cornerRadius by animateFloatAsState(
        targetValue = if (state == OcrState.Ready) 20f else 16f,
        animationSpec = spring(dampingRatio = 0.7f)
    )

    Box(modifier = modifier) {
        // Main frame with enhanced styling
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pathEffect = when (state) {
                OcrState.Searching -> PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                else -> null
            }
            
            // Background with subtle fill
            drawRoundRect(
                color = borderColor.copy(alpha = 0.1f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(cornerRadius.dp.toPx())
            )
            
            // Border
            drawRoundRect(
                color = borderColor,
                topLeft = Offset(strokeWidth.dp.toPx() / 2, strokeWidth.dp.toPx() / 2),
                size = Size(
                    size.width - strokeWidth.dp.toPx(),
                    size.height - strokeWidth.dp.toPx()
                ),
                cornerRadius = CornerRadius(cornerRadius.dp.toPx()),
                style = Stroke(
                    width = strokeWidth.dp.toPx(),
                    pathEffect = pathEffect
                )
            )
            
            // Corner guides for better alignment
            val cornerSize = 30.dp.toPx()
            val cornerStroke = 3.dp.toPx()
            val margin = 20.dp.toPx()
            
            // Top-left corner
            drawLine(
                color = borderColor,
                start = Offset(margin, margin + cornerSize),
                end = Offset(margin, margin),
                strokeWidth = cornerStroke
            )
            drawLine(
                color = borderColor,
                start = Offset(margin, margin),
                end = Offset(margin + cornerSize, margin),
                strokeWidth = cornerStroke
            )
            
            // Top-right corner
            drawLine(
                color = borderColor,
                start = Offset(size.width - margin - cornerSize, margin),
                end = Offset(size.width - margin, margin),
                strokeWidth = cornerStroke
            )
            drawLine(
                color = borderColor,
                start = Offset(size.width - margin, margin),
                end = Offset(size.width - margin, margin + cornerSize),
                strokeWidth = cornerStroke
            )
            
            // Bottom-left corner
            drawLine(
                color = borderColor,
                start = Offset(margin, size.height - margin - cornerSize),
                end = Offset(margin, size.height - margin),
                strokeWidth = cornerStroke
            )
            drawLine(
                color = borderColor,
                start = Offset(margin, size.height - margin),
                end = Offset(margin + cornerSize, size.height - margin),
                strokeWidth = cornerStroke
            )
            
            // Bottom-right corner
            drawLine(
                color = borderColor,
                start = Offset(size.width - margin - cornerSize, size.height - margin),
                end = Offset(size.width - margin, size.height - margin),
                strokeWidth = cornerStroke
            )
            drawLine(
                color = borderColor,
                start = Offset(size.width - margin, size.height - margin),
                end = Offset(size.width - margin, size.height - margin - cornerSize),
                strokeWidth = cornerStroke
            )
        }
        
        // Enhanced guide content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main instruction text
            Text(
                text = when (state) {
                    OcrState.Searching -> "🔍 Position text within frame"
                    OcrState.Focusing -> "📸 Hold steady..."
                    OcrState.Ready -> "✨ Perfect! Ready to capture"
                    OcrState.Processing -> "⚙️ Processing text..."
                    else -> "📄 Align document here"
                },
                color = borderColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
            
            if (confidence > 0.3f) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Confidence indicator with visual bar
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = borderColor.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${(confidence * 100).toInt()}% Quality",
                            color = borderColor,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        LinearProgressIndicator(
                            progress = { confidence },
                            modifier = Modifier.width(100.dp),
                            color = borderColor,
                            trackColor = borderColor.copy(alpha = 0.3f)
                        )
                    }
                }
            }
            
            // Word count for detected text
            if (detectedText.isNotBlank() && confidence > 0.4f) {
                val wordCount = detectedText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📝 $wordCount words detected",
                    color = borderColor.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun OcrStateIndicator(
    state: OcrState,
    confidence: Float,
    wordCount: Int
) {
    val (icon, text) = when (state) {
        OcrState.Searching -> "🔍" to "Looking for text..."
        OcrState.Focusing -> "📸" to "Focusing..."
        OcrState.Ready -> "✓" to "Ready to capture!"
        OcrState.Processing -> "⚙️" to "Processing..."
        else -> "🔍" to "Looking for text..."
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Additional info for detected text
        if (wordCount > 0 && confidence > 0.3f) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$wordCount words detected",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ConfidenceIndicator(
    confidence: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        confidence > 0.8f -> Color.Green
        confidence > 0.6f -> Color.Yellow
        confidence > 0.3f -> Color(0xFFFFA500) // Orange
        else -> Color.Red
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier.size(8.dp)
        ) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${(confidence * 100).toInt()}%",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CaptureButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = tween(300)
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(72.dp)
            .alpha(alpha),
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Icon(
            Icons.Default.PhotoCamera,
            contentDescription = "Capture",
            modifier = Modifier.size(32.dp)
        )
    }
}
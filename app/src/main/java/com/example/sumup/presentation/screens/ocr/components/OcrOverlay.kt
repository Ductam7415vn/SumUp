package com.example.sumup.presentation.screens.ocr.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
                    onClick = { /* TODO: Toggle flash */ },
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = Color.White
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
        
        // Guide frame
        GuideFrame(
            state = uiState.ocrState,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .align(Alignment.Center)
        )
        
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
            // OCR State indicator
            OcrStateIndicator(state = uiState.ocrState)
            
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
private fun GuideFrame(
    state: OcrState,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OcrState.Searching -> Color.Yellow
            OcrState.Focusing -> Color(0xFFFFA500) // Orange
            OcrState.Ready -> Color.Green
            OcrState.Processing -> Color.Blue
            else -> Color.Yellow
        },
        animationSpec = tween(300)
    )
    
    val strokeWidth by animateFloatAsState(
        targetValue = if (state == OcrState.Ready) 4f else 3f,
        animationSpec = tween(300)
    )
    
    Canvas(
        modifier = modifier
            .border(
                width = strokeWidth.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        val pathEffect = if (state == OcrState.Searching) {
            PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        } else null
        
        drawRoundRect(
            color = borderColor,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(8.dp.toPx()),
            style = Stroke(
                width = strokeWidth.dp.toPx(),
                pathEffect = pathEffect
            )
        )
    }
    
    // Guide text
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "← Align text here →",
            color = borderColor.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OcrStateIndicator(
    state: OcrState
) {
    val (icon, text) = when (state) {
        OcrState.Searching -> "🔍" to "Looking for text..."
        OcrState.Focusing -> "📸" to "Focusing..."
        OcrState.Ready -> "✓" to "Ready to capture!"
        OcrState.Processing -> "⚙️" to "Processing..."
        else -> "🔍" to "Looking for text..."
    }
    
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
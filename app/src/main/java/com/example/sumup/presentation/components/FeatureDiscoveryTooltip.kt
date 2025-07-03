package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import kotlinx.coroutines.delay

/**
 * Feature discovery tooltip that highlights new features
 */
@Composable
fun FeatureDiscoveryTooltip(
    isVisible: Boolean,
    title: String,
    description: String,
    targetOffsetX: Int = 0,
    targetOffsetY: Int = 0,
    onDismiss: () -> Unit,
    onNextTip: (() -> Unit)? = null,
    showNextButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    val hapticManager = rememberHapticFeedback()
    val density = LocalDensity.current
    
    // Animation values
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "tooltip_alpha"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tooltip_scale"
    )
    
    // Pulse animation for attention
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    if (isVisible || animatedAlpha > 0.01f) {
        Popup(
            alignment = Alignment.TopStart,
            offset = IntOffset(targetOffsetX, targetOffsetY),
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = modifier
                    .widthIn(max = 280.dp)
                    .graphicsLayer {
                        alpha = animatedAlpha
                        scaleX = animatedScale * pulseScale
                        scaleY = animatedScale * pulseScale
                    }
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = {
                                hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                onDismiss()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Description
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                    
                    // Action buttons
                    if (showNextButton && onNextTip != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            ) {
                                Text("Skip")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = {
                                    hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                    onNextTip()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    "Next",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                
                // Arrow pointer
                Box(
                    modifier = Modifier
                        .offset(x = 20.dp, y = (-8).dp)
                        .size(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .graphicsLayer {
                            rotationZ = 45f
                        }
                )
            }
        }
    }
}

/**
 * Feature discovery overlay that highlights specific UI elements
 */
@Composable
fun FeatureDiscoveryOverlay(
    isVisible: Boolean,
    highlightBounds: Rect? = null,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        ) {
            // Cut out highlighted area if bounds provided
            highlightBounds?.let { bounds ->
                Box(
                    modifier = Modifier
                        .offset { IntOffset(bounds.left.toInt(), bounds.top.toInt()) }
                        .size(
                            width = (bounds.right - bounds.left).dp,
                            height = (bounds.bottom - bounds.top).dp
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
        }
    }
}

/**
 * Simple rect class for highlight bounds
 */
data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

/**
 * Feature discovery controller to manage tooltip sequence
 */
@Composable
fun rememberFeatureDiscoveryController(): FeatureDiscoveryController {
    return remember { FeatureDiscoveryController() }
}

class FeatureDiscoveryController {
    private val _currentTip = mutableStateOf<FeatureTip?>(null)
    val currentTip: State<FeatureTip?> = _currentTip
    
    private val tipQueue = mutableListOf<FeatureTip>()
    
    fun showTip(tip: FeatureTip) {
        _currentTip.value = tip
    }
    
    fun showTips(tips: List<FeatureTip>) {
        tipQueue.clear()
        tipQueue.addAll(tips)
        showNextTip()
    }
    
    fun dismissCurrentTip() {
        _currentTip.value = null
        showNextTip()
    }
    
    private fun showNextTip() {
        if (tipQueue.isNotEmpty()) {
            _currentTip.value = tipQueue.removeFirst()
        }
    }
    
    fun hasMoreTips(): Boolean = tipQueue.isNotEmpty()
}

data class FeatureTip(
    val id: String,
    val title: String,
    val description: String,
    val targetOffsetX: Int = 0,
    val targetOffsetY: Int = 0,
    val highlightBounds: Rect? = null
)

/**
 * Predefined feature tips for the app
 */
object AppFeatureTips {
    val summarizeButton = FeatureTip(
        id = "summarize_button",
        title = "Quick Summarize",
        description = "Tap here to instantly summarize your text using AI",
        targetOffsetX = 100,
        targetOffsetY = 400
    )
    
    val pdfUpload = FeatureTip(
        id = "pdf_upload",
        title = "PDF Support",
        description = "You can upload PDF files for summarization",
        targetOffsetX = 50,
        targetOffsetY = 300
    )
    
    val summaryLength = FeatureTip(
        id = "summary_length",
        title = "Customize Length",
        description = "Choose between brief, moderate, or detailed summaries",
        targetOffsetX = 50,
        targetOffsetY = 350
    )
    
    val ocrButton = FeatureTip(
        id = "ocr_button",
        title = "Scan Text",
        description = "Use your camera to capture and summarize printed text",
        targetOffsetX = 280,
        targetOffsetY = 400
    )
    
    val bottomNav = FeatureTip(
        id = "bottom_nav",
        title = "Easy Navigation",
        description = "Access your history and settings from the bottom navigation",
        targetOffsetX = 150,
        targetOffsetY = 600
    )
}
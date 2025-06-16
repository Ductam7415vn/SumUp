package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * Shared element transition keys for consistent animation
 */
object SharedElementKeys {
    const val MAIN_SUMMARY_BUTTON = "main_summary_button"
    const val PROCESSING_PROGRESS = "processing_progress"
    const val RESULT_SUMMARY_CARD = "result_summary_card"
    const val MAIN_TEXT_INPUT = "main_text_input"
    const val RESULT_TEXT_DISPLAY = "result_text_display"
    const val MAIN_APP_BAR = "main_app_bar"
    const val SETTINGS_APP_BAR = "settings_app_bar"
    const val HISTORY_APP_BAR = "history_app_bar"
    const val OCR_CAMERA_BUTTON = "ocr_camera_button"
    const val MAIN_SCAN_FAB = "main_scan_fab"
    const val PROCESSING_AI_ICON = "processing_ai_icon"
    const val RESULT_METRICS_CARD = "result_metrics_card"
}

/**
 * Shared element transition state management
 */
@Composable
fun rememberSharedElementTransition(): SharedElementTransitionState {
    return remember { SharedElementTransitionState() }
}

class SharedElementTransitionState {
    private val _elements = mutableMapOf<String, SharedElementInfo>()
    
    fun registerElement(key: String, bounds: SharedElementBounds) {
        _elements[key] = SharedElementInfo(bounds, System.currentTimeMillis())
    }
    
    fun getElement(key: String): SharedElementInfo? = _elements[key]
    
    fun clearElement(key: String) {
        _elements.remove(key)
    }
}

data class SharedElementInfo(
    val bounds: SharedElementBounds,
    val timestamp: Long
)

data class SharedElementBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

/**
 * Enhanced container transform animation for screen transitions
 */
@Composable
fun ContainerTransform(
    visible: Boolean,
    modifier: Modifier = Modifier,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
    animationSpec: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = animationSpec,
        label = "container_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = animationSpec,
        label = "container_alpha"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                this.transformOrigin = transformOrigin
            }
    ) {
        if (visible || alpha > 0f) {
            content()
        }
    }
}

/**
 * Morphing card transition for summary cards
 */
@Composable
fun MorphingCard(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    expandedContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "morphing_progress"
    )
    
    val cornerRadius by animateFloatAsState(
        targetValue = if (isExpanded) 16f else 28f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "corner_radius"
    )
    
    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = 0.8f + (0.2f * progress)
                scaleY = 0.8f + (0.2f * progress)
            },
        shape = RoundedCornerShape(cornerRadius.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = (4 + 8 * progress).dp
        )
    ) {
        Box {
            // Collapsed content
            if (progress < 0.5f) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = 1f - (progress * 2f)
                        }
                ) {
                    collapsedContent()
                }
            }
            
            // Expanded content
            if (progress > 0.5f) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = (progress - 0.5f) * 2f
                        }
                ) {
                    expandedContent()
                }
            }
        }
    }
}

/**
 * FAB to button transition for scan functionality
 */
@Composable
fun FABTransition(
    isFAB: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    fabContent: @Composable () -> Unit,
    buttonContent: @Composable () -> Unit
) {
    val fabScale by animateFloatAsState(
        targetValue = if (isFAB) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_scale"
    )
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isFAB) 0f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // FAB version
        if (fabScale > 0.01f) {
            FloatingActionButton(
                onClick = onClick,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                        alpha = fabScale
                    }
                    .zIndex(if (isFAB) 1f else 0f)
            ) {
                fabContent()
            }
        }
        
        // Button version
        if (buttonScale > 0.01f) {
            Button(
                onClick = onClick,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                        alpha = buttonScale
                    }
                    .zIndex(if (!isFAB) 1f else 0f)
            ) {
                buttonContent()
            }
        }
    }
}

/**
 * Progress indicator morphing animation
 */
@Composable
fun ProgressMorph(
    isIndeterminate: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier
) {
    val morphProgress by animateFloatAsState(
        targetValue = if (isIndeterminate) 0f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "progress_morph"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Circular progress (indeterminate)
        if (morphProgress < 0.5f) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size((56 - 16 * morphProgress * 2).dp)
                    .graphicsLayer {
                        alpha = 1f - (morphProgress * 2f)
                    }
            )
        }
        
        // Linear progress (determinate)
        if (morphProgress > 0.5f) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .graphicsLayer {
                        alpha = (morphProgress - 0.5f) * 2f
                    }
            )
        }
    }
}

/**
 * Text transition animation for input to result text
 */
@Composable
fun TextTransition(
    isResult: Boolean,
    inputText: String,
    resultText: String,
    modifier: Modifier = Modifier
) {
    val textProgress by animateFloatAsState(
        targetValue = if (isResult) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "text_transition"
    )
    
    Box(modifier = modifier) {
        // Input text
        if (textProgress < 0.5f) {
            Text(
                text = inputText,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = 1f - (textProgress * 2f)
                        scaleX = 1f - (textProgress * 0.1f)
                        scaleY = 1f - (textProgress * 0.1f)
                    },
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Result text
        if (textProgress > 0.5f) {
            Text(
                text = resultText,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = (textProgress - 0.5f) * 2f
                        scaleX = 0.9f + ((textProgress - 0.5f) * 0.2f)
                        scaleY = 0.9f + ((textProgress - 0.5f) * 0.2f)
                    },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
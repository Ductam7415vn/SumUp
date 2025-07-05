package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Interactive button with enhanced animations and haptic feedback
 */
@Composable
fun InteractiveButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animations
    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.95f
            isPressed -> 0.92f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isPressed -> 2.dp
            else -> 6.dp
        },
        label = "button_elevation"
    )
    
    Surface(
        onClick = {
            if (enabled) {
                hapticFeedback()
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(24.dp),
        color = if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        shadowElevation = elevation,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (enabled) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * Interactive card with press effects
 */
@Composable
fun InteractiveCard(
    onClick: () -> Unit,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "card_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        label = "card_elevation"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                hapticFeedback()
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        content = content
    )
}

/**
 * Animated checkbox with smooth transitions
 */
@Composable
fun AnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        finishedListener = {
            if (checked) hapticFeedback()
        },
        label = "checkbox_scale"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.scale(scale)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Floating label text field with animations
 */
@Composable
fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val labelScale by animateFloatAsState(
        targetValue = if (isFocused || value.isNotEmpty()) 0.8f else 1f,
        label = "label_scale"
    )
    
    val labelOffset by animateDpAsState(
        targetValue = if (isFocused || value.isNotEmpty()) (-20).dp else 0.dp,
        label = "label_offset"
    )
    
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (value.isEmpty() && it.isNotEmpty()) {
                    hapticFeedback()
                }
                onValueChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
        
        // Animated label
        Text(
            text = label,
            modifier = Modifier
                .padding(start = 16.dp)
                .graphicsLayer {
                    scaleX = labelScale
                    scaleY = labelScale
                    translationY = labelOffset.toPx()
                },
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFocused) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            }
        )
    }
}

/**
 * Progress button with loading state
 */
@Composable
fun ProgressButton(
    onClick: () -> Unit,
    text: String,
    isLoading: Boolean = false,
    progress: Float? = null,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "loading_rotation"
    )
    
    Button(
        onClick = {
            if (!isLoading) {
                hapticFeedback()
                onClick()
            }
        },
        modifier = modifier.height(48.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Progress or loading indicator
            if (isLoading || progress != null) {
                CircularProgressIndicator(
                    progress = progress ?: 0f,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            if (isLoading && progress == null) {
                                rotationZ = rotation
                            }
                        },
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            }
            
            // Text with fade animation
            androidx.compose.animation.AnimatedVisibility(
                visible = !isLoading && progress == null,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut()
            ) {
                Text(text = text)
            }
        }
    }
}
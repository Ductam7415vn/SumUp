package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Composable
fun ModernInfoDialog(
    onDismiss: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var isVisible by remember { mutableStateOf(false) }
    
    // Trigger haptic feedback and animation on show
    LaunchedEffect(Unit) {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        isVisible = true
    }
    
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "info_animation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_rotation"
    )
    
    Dialog(
        onDismissRequest = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    // Gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Animated Icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            // Glow effect
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(1.2f)
                                    .alpha(glowAlpha),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            ) {}
                            
                            // Icon background
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 4.dp
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .graphicsLayer {
                                            rotationZ = iconRotation
                                        },
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Title
                        Text(
                            text = "Character Limit Guide",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Content with animations
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .heightIn(max = 400.dp) // Constrain height instead of using scroll
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Character limits card
                            AnimatedLimitCard(
                                title = "Optimal Range",
                                minLimit = 100,
                                maxLimit = 5000,
                                description = "For best summarization results",
                                color = MaterialTheme.colorScheme.primary,
                                delay = 0
                            )
                            
                            // Color indicators section
                            Text(
                                text = "Color Indicators",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            
                            ColorIndicatorItem(
                                color = Color.Gray,
                                range = "100 - 4,500 characters",
                                label = "Normal",
                                description = "Perfect for summarization",
                                delay = 100
                            )
                            
                            ColorIndicatorItem(
                                color = Color(0xFFFF9800),
                                range = "4,501 - 4,999 characters",
                                label = "Warning",
                                description = "Approaching limit",
                                delay = 200
                            )
                            
                            ColorIndicatorItem(
                                color = MaterialTheme.colorScheme.error,
                                range = "< 100 or ≥ 5,000 characters",
                                label = "Error",
                                description = "Outside valid range",
                                delay = 300
                            )
                            
                            // Tips section
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
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
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Pro Tips",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    
                                    Text(
                                        "• Aim for 500-2000 characters for optimal summaries",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "• Longer texts produce more detailed summaries",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "• Break very long texts into sections for better results",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Animated button
                        var buttonPressed by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(
                            targetValue = if (buttonPressed) 0.95f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "button_scale"
                        )
                        
                        Button(
                            onClick = {
                                buttonPressed = true
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 24.dp)
                                .scale(buttonScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Got it!",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedLimitCard(
    title: String,
    minLimit: Int,
    maxLimit: Int,
    description: String,
    color: Color,
    delay: Int
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.2f)
                    )
                )
            )
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
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$minLimit - ${maxLimit.toString().replace("000", "k")}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorIndicatorItem(
    color: Color,
    range: String,
    label: String,
    description: String,
    delay: Int
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated color indicator
            val infiniteTransition = rememberInfiniteTransition(label = "color_pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "indicator_scale"
            )
            
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .scale(scale),
                shape = CircleShape,
                color = color.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = color
                    ) {}
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = range,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
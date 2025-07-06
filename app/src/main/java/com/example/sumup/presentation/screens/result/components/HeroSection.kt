package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import com.example.sumup.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroSection(
    onNavigateBack: () -> Unit,
    onMoreOptions: () -> Unit,
    contentType: String = "Summary",
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "hero_gradient")
    
    // Animated gradient colors
    val color1 by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        targetValue = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_color_1"
    )
    
    val color2 by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
        targetValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_color_2"
    )
    
    // Animated gradient position
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.topBarBigBoy)
    ) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color1,
                            color2,
                            MaterialTheme.colorScheme.surface
                        ),
                        center = Offset(
                            x = 0.5f + gradientOffset * 0.3f,
                            y = 0.3f + gradientOffset * 0.2f
                        ),
                        radius = 800f
                    )
                )
        )
        
        // Blur overlay for glass effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                        )
                    )
                )
        )
        
        // Content
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(600)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 18.sp,
                            modifier = Modifier.graphicsLayer {
                                val scale = 1f + 0.1f * kotlin.math.sin(System.currentTimeMillis() / 1000f)
                                scaleX = scale
                                scaleY = scale
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Summary Ready",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isVisible) 1f else 0f
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onMoreOptions,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isVisible) 1f else 0f
                    }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}


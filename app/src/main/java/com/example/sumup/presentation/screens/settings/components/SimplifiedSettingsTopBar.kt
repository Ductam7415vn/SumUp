package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.sumup.presentation.components.HapticIconButton
import com.example.sumup.utils.haptic.HapticFeedbackType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplifiedSettingsTopBar(
    onNavigateBack: () -> Unit,
    scrollOffset: Float = 0f,
    modifier: Modifier = Modifier
) {
    // Animation states based on scroll
    val isScrolled = scrollOffset > 50f
    val scrollProgress = (scrollOffset / 200f).coerceIn(0f, 1f)
    
    // Dynamic height animation
    val topBarHeight by animateDpAsState(
        targetValue = if (isScrolled) 56.dp else 72.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "topbar_height"
    )
    
    // Get colors from Material Theme (supports Dynamic Colors)
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    // Container that extends behind status bar
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.95f),
                        secondaryColor.copy(alpha = 0.9f),
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(topBarHeight)
                .shadow(
                    elevation = if (isScrolled) 4.dp else 0.dp,
                    ambientColor = primaryColor.copy(alpha = 0.1f),
                    spotColor = primaryColor.copy(alpha = 0.1f)
                ),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Subtle overlay for depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(scrollProgress * 0.1f)
                        .background(surfaceColor)
                )
                
                // Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    HapticIconButton(
                        onClick = onNavigateBack,
                        hapticType = HapticFeedbackType.NAVIGATION,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (primaryColor.luminance() > 0.5f) {
                                Color.Black.copy(alpha = 0.87f)
                            } else {
                                Color.White
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Title with simple animation
                    AnimatedTitle(
                        isScrolled = isScrolled,
                        scrollProgress = scrollProgress,
                        contentColor = if (primaryColor.luminance() > 0.5f) {
                            Color.Black.copy(alpha = 0.87f)
                        } else {
                            Color.White
                        }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Optional: Simple icon on the right (no complex animations)
                    IconButton(
                        onClick = { /* Could open a help dialog or other simple action */ },
                        modifier = Modifier
                            .size(48.dp)
                            .alpha(0.7f)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = if (primaryColor.luminance() > 0.5f) {
                                Color.Black.copy(alpha = 0.54f)
                            } else {
                                Color.White.copy(alpha = 0.7f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedTitle(
    isScrolled: Boolean,
    scrollProgress: Float,
    contentColor: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isScrolled) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "title_scale"
    )
    
    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = 1f - scrollProgress * 0.1f
            }
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = if (isScrolled) 20.sp else 24.sp
            ),
            color = contentColor
        )
        
        // Subtitle fades out when scrolling
        AnimatedVisibility(
            visible = !isScrolled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = "Manage your preferences",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// Extension function to calculate luminance for contrast
private fun Color.luminance(): Float {
    val red = red * 0.299f
    val green = green * 0.587f
    val blue = blue * 0.114f
    return red + green + blue
}
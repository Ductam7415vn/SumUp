package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.Achievement
import kotlinx.coroutines.delay

@Composable
fun AchievementNotification(
    achievement: Achievement?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = achievement != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        achievement?.let {
            var showContent by remember { mutableStateOf(false) }
            
            LaunchedEffect(achievement) {
                showContent = true
                delay(4000) // Show for 4 seconds
                onDismiss()
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box {
                    // Gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        achievement.color.copy(alpha = 0.3f),
                                        achievement.color.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated icon
                        AchievementIcon(
                            achievement = achievement,
                            isAnimated = showContent
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Content
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Achievement Unlocked!",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = achievement.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = achievement.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            
                            // Tier indicator
                            if (achievement.tier.multiplier > 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TierBadge(tier = achievement.tier)
                            }
                        }
                        
                        // Points
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "+${10 * achievement.tier.multiplier}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = achievement.color
                            )
                            Text(
                                text = "points",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementIcon(
    achievement: Achievement,
    isAnimated: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "icon_scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isAnimated) 360f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "icon_rotation"
    )
    
    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        // Background circle with tier color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(achievement.tier.color.copy(alpha = 0.2f))
        )
        
        // Icon
        Icon(
            achievement.icon,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp),
            tint = achievement.color
        )
        
        // Sparkle effect
        if (isAnimated) {
            SparkleEffect(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TierBadge(tier: com.example.sumup.domain.model.AchievementTier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tier.color.copy(alpha = 0.2f),
        modifier = Modifier
    ) {
        Text(
            text = tier.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = tier.color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SparkleEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0
                1f at 1000
                0f at 2000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_alpha"
    )
    
    Canvas(modifier = modifier) {
        // Draw sparkles around the icon
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2
        
        for (i in 0..7) {
            val angle = (i * 45f).toRadians()
            val x = centerX + kotlin.math.cos(angle) * radius * 0.8f
            val y = centerY + kotlin.math.sin(angle) * radius * 0.8f
            
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.8f),
                radius = 2.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

private fun Float.toRadians(): Float = this * (kotlin.math.PI / 180f).toFloat()

@Composable
fun AchievementSummary(
    totalPoints: Int,
    unlockedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total points
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalPoints.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total Points",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            
            // Progress
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$unlockedCount/$totalCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
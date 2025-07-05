package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun AnimatedKPICard(
    originalWords: Int,
    summaryWords: Int,
    originalReadTime: Int,
    summaryReadTime: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    val percentReduction = if (originalWords > 0) {
        ((originalWords - summaryWords) / originalWords.toFloat() * 100).toInt()
    } else 0
    
    val timeSaved = originalReadTime - summaryReadTime
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Success header with animation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Analysis Complete",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Here's what we achieved",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    PulsingSuccessIcon()
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Primary metric with circular progress
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressMetric(
                        percentage = percentReduction,
                        modifier = Modifier.size(140.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Secondary metrics grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        icon = Icons.Outlined.Timer,
                        value = "$timeSaved min",
                        label = "Time Saved",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    MetricCard(
                        icon = Icons.Outlined.TrendingDown,
                        value = "${summaryWords}",
                        label = "Words",
                        subLabel = "from $originalWords",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Additional insights
                InsightChips(
                    timeSaved = timeSaved,
                    percentReduction = percentReduction
                )
            }
        }
    }
}

@Composable
private fun PulsingSuccessIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "success_pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "success_scale"
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
        )
    }
}

@Composable
private fun CircularProgressMetric(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    var animatedPercentage by remember { mutableStateOf(0) }
    
    LaunchedEffect(percentage) {
        val step = kotlin.math.max(percentage / 20, 1)
        for (i in 0..percentage step step) {
            animatedPercentage = i
            delay(30)
        }
        animatedPercentage = percentage
    }
    
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Background circle
            drawCircle(
                color = Color.Gray.copy(alpha = 0.1f),
                radius = radius,
                center = center,
                style = Stroke(strokeWidth)
            )
            
            // Progress arc
            val sweepAngle = (animatedPercentage / 100f) * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF4CAF50),
                        Color(0xFF2196F3),
                        Color(0xFF4CAF50)
                    )
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        // Center content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$animatedPercentage%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "shorter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    subLabel: String? = null,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(400)
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                subLabel?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightChips(
    timeSaved: Int,
    percentReduction: Int
) {
    val insights = remember(timeSaved, percentReduction) {
        buildList {
            when {
                timeSaved >= 10 -> add("📚 Saved a coffee break!")
                timeSaved >= 5 -> add("⚡ Quick read achieved")
                timeSaved >= 2 -> add("⏱️ Efficient summary")
            }
            
            when {
                percentReduction >= 80 -> add("🎯 Ultra-concise")
                percentReduction >= 60 -> add("💎 Highly condensed")
                percentReduction >= 40 -> add("✨ Well optimized")
            }
        }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        insights.forEach { insight ->
            var isVisible by remember { mutableStateOf(false) }
            
            LaunchedEffect(insight) {
                delay(600)
                isVisible = true
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInHorizontally()
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(insight, fontSize = 12.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    border = null
                )
            }
        }
    }
}
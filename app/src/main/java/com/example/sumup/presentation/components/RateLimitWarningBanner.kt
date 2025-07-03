package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.RateLimitStatus

@Composable
fun RateLimitWarningBanner(
    rateLimitStatus: RateLimitStatus,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = rateLimitStatus.isNearLimit,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        val backgroundColor = when {
            rateLimitStatus.isOverLimit -> MaterialTheme.colorScheme.error
            rateLimitStatus.isNearLimit -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.tertiary
        }
        
        val contentColor = when {
            rateLimitStatus.isOverLimit -> MaterialTheme.colorScheme.onError
            rateLimitStatus.isNearLimit -> MaterialTheme.colorScheme.onError
            else -> MaterialTheme.colorScheme.onTertiary
        }
        
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToSettings() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated warning icon
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "icon_scale"
                )
                
                Icon(
                    if (rateLimitStatus.isOverLimit) Icons.Default.Error else Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(if (rateLimitStatus.isOverLimit) scale else 1f)
                )
                
                // Message
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            rateLimitStatus.isOverLimit -> "API Rate Limit Reached"
                            rateLimitStatus.isNearLimit -> "Warning: Approaching rate limit"
                            else -> "Rate limit warning"
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    
                    if (rateLimitStatus.isOverLimit) {
                        Text(
                            text = "Please wait or upgrade your plan",
                            fontSize = 12.sp,
                            color = contentColor.copy(alpha = 0.9f)
                        )
                    }
                }
                
                // Action
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RateLimitIndicator(
    percentageUsed: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        percentageUsed >= 1f -> MaterialTheme.colorScheme.error
        percentageUsed >= 0.95f -> Color(0xFFFF6B6B)
        percentageUsed >= 0.8f -> Color(0xFFFFA726)
        else -> MaterialTheme.colorScheme.primary
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${(percentageUsed * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
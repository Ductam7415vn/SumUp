package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.ApiUsageStats
import com.example.sumup.domain.model.RateLimitStatus
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlinx.coroutines.launch

@Composable
fun ApiUsageDashboard(
    usageStats: ApiUsageStats,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    android.util.Log.d("ApiUsageDashboard", "Rendering ApiUsageDashboard with stats: $usageStats")
    var selectedPeriod by remember { mutableStateOf(UsagePeriod.TODAY) }
    val refreshRotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "API Usage",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                IconButton(
                    onClick = {
                        scope.launch {
                            refreshRotation.animateTo(
                                targetValue = refreshRotation.value + 360f,
                                animationSpec = tween(500)
                            )
                        }
                        onRefresh()
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.graphicsLayer {
                            rotationZ = refreshRotation.value
                        }
                    )
                }
            }
            
            // Period selector
            UsagePeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodChange = { selectedPeriod = it }
            )
            
            // Main usage display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular progress for requests
                UsageCircularProgress(
                    label = "Requests",
                    current = when (selectedPeriod) {
                        UsagePeriod.TODAY -> usageStats.requestsToday
                        UsagePeriod.THIS_WEEK -> usageStats.requestsThisWeek
                        UsagePeriod.THIS_MONTH -> usageStats.requestsThisMonth
                    },
                    limit = when (selectedPeriod) {
                        UsagePeriod.TODAY -> 60 // per minute limit shown as daily
                        UsagePeriod.THIS_WEEK -> 420 // 60 * 7 approximation
                        UsagePeriod.THIS_MONTH -> 1800 // 60 * 30 approximation
                    },
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                // Circular progress for tokens
                UsageCircularProgress(
                    label = "Tokens",
                    current = when (selectedPeriod) {
                        UsagePeriod.TODAY -> usageStats.tokensToday
                        UsagePeriod.THIS_WEEK -> usageStats.tokensThisWeek
                        UsagePeriod.THIS_MONTH -> usageStats.tokensThisMonth
                    },
                    limit = when (selectedPeriod) {
                        UsagePeriod.TODAY -> 33333 // 1M / 30 days
                        UsagePeriod.THIS_WEEK -> 233333 // 1M / 30 * 7
                        UsagePeriod.THIS_MONTH -> 1000000 // 1M monthly limit
                    },
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Additional stats
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Success rate
                StatsRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Success Rate",
                    value = "${usageStats.successRate}%",
                    color = Color(0xFF4CAF50)
                )
                
                // Average response time
                StatsRow(
                    icon = Icons.Default.Speed,
                    label = "Avg Response Time",
                    value = "${usageStats.averageResponseTime}ms",
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                // Last request
                StatsRow(
                    icon = Icons.Default.AccessTime,
                    label = "Last Request",
                    value = formatLastRequestTime(usageStats.lastRequestTime),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Warning if approaching limits
            if (shouldShowWarning(usageStats, selectedPeriod)) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Approaching usage limits. Consider upgrading for more capacity.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "API Usage Analytics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Monitor your API consumption",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onRefresh) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RateLimitWarning(
    rateLimitStatus: RateLimitStatus
) {
    AnimatedVisibility(
        visible = true,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    rateLimitStatus.isOverLimit -> MaterialTheme.colorScheme.errorContainer
                    rateLimitStatus.requestsPerMinute >= rateLimitStatus.limit * 0.95 -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = when {
                        rateLimitStatus.isOverLimit -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    },
                    modifier = Modifier.size(24.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            rateLimitStatus.isOverLimit -> "Rate Limit Reached"
                            rateLimitStatus.requestsPerMinute >= rateLimitStatus.limit * 0.95 -> "Approaching Rate Limit"
                            else -> "High API Usage"
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${rateLimitStatus.requestsPerMinute}/${rateLimitStatus.limit} requests per minute",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun UsageCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TokenUsageCard(
    tokenUsage: com.example.sumup.domain.model.TokenUsage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Token,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Token Usage",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Progress Bar
            val progress = tokenUsage.percentage
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "progress"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Used",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTokenCount(tokenUsage.used.toLong()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Limit",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTokenCount(tokenUsage.limit.toLong()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Token breakdown - removed as not part of current model
        }
    }
}

@Composable
private fun TokenTypeChip(
    label: String,
    count: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTokenCount(count),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun UsageChart(
    usageStats: ApiUsageStats
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Usage Trend",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Simple bar chart
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                drawUsageChart(usageStats)
            }
        }
    }
}

private fun DrawScope.drawUsageChart(usageStats: ApiUsageStats) {
    val barWidth = size.width / 7 * 0.6f
    val spacing = size.width / 7
    val maxHeight = size.height * 0.8f
    
    // Mock data for last 7 days
    val dailyUsage = listOf(45, 52, 38, 60, 55, 42, usageStats.requestsToday)
    val maxUsage = 60 // Daily limit
    
    dailyUsage.forEachIndexed { index, usage ->
        val barHeight = (usage.toFloat() / maxUsage) * maxHeight
        val x = index * spacing + (spacing - barWidth) / 2
        val y = size.height - barHeight
        
        // Draw bar
        drawRoundRect(
            color = if (index == 6) Color(0xFF4CAF50) else Color(0xFF2196F3),
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )
        
        // Draw value label
        if (index == 6) {
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 10.sp.toPx()
                    color = Color.White.toArgb()
                }
                drawText(
                    usage.toString(),
                    x + barWidth / 2,
                    y + 20,
                    paint
                )
            }
        }
    }
}

@Composable
private fun ResetTimerCard(
    resetTime: Date
) {
    var timeRemaining by remember { mutableStateOf("") }
    
    LaunchedEffect(resetTime) {
        while (true) {
            val now = System.currentTimeMillis()
            val diff = resetTime.time - now
            
            if (diff > 0) {
                val hours = diff / (1000 * 60 * 60)
                val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (diff % (1000 * 60)) / 1000
                
                timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                timeRemaining = "00:00:00"
            }
            
            kotlinx.coroutines.delay(1000)
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Limit resets in",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = timeRemaining,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun UsagePeriodSelector(
    selectedPeriod: UsagePeriod,
    onPeriodChange: (UsagePeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UsagePeriod.values().forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodChange(period) },
                label = { Text(period.displayName) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun UsageCircularProgress(
    label: String,
    current: Int,
    limit: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val percentage = min(100f, (current.toFloat() / limit) * 100)
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            CircularProgressIndicator(
                progress = animatedPercentage / 100f,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${animatedPercentage.toInt()}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = formatNumber(current),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "${formatNumber(current)} / ${formatNumber(limit)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatsRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Text(
                text = label,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

private enum class UsagePeriod(val displayName: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month")
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${DecimalFormat("#.#").format(number / 1_000_000.0)}M"
        number >= 1_000 -> "${DecimalFormat("#.#").format(number / 1_000.0)}K"
        else -> number.toString()
    }
}

private fun formatLastRequestTime(timestamp: Long?): String {
    if (timestamp == null) return "Never"
    
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}

private fun shouldShowWarning(stats: ApiUsageStats, period: UsagePeriod): Boolean {
    return when (period) {
        UsagePeriod.TODAY -> stats.requestsToday > 50 || stats.tokensToday > 30000
        UsagePeriod.THIS_WEEK -> stats.requestsThisWeek > 350 || stats.tokensThisWeek > 200000
        UsagePeriod.THIS_MONTH -> stats.requestsThisMonth > 1500 || stats.tokensThisMonth > 800000
    }
}

private fun formatTokenCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
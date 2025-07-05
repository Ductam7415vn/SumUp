package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.Summary

// Extension properties for SummaryViewMode
val SummaryViewMode.icon: androidx.compose.ui.graphics.vector.ImageVector
    get() = when (this) {
        SummaryViewMode.BRIEF -> Icons.Default.ShortText
        SummaryViewMode.STANDARD -> Icons.Default.Subject
        SummaryViewMode.DETAILED -> Icons.Default.Article
        SummaryViewMode.FULL -> Icons.Default.MenuBook
    }

val SummaryViewMode.displayName: String
    get() = when (this) {
        SummaryViewMode.BRIEF -> "Brief"
        SummaryViewMode.STANDARD -> "Standard"
        SummaryViewMode.DETAILED -> "Detailed"
        SummaryViewMode.FULL -> "Full"
    }

val SummaryViewMode.description: String
    get() = when (this) {
        SummaryViewMode.BRIEF -> "Quick overview"
        SummaryViewMode.STANDARD -> "Balanced detail"
        SummaryViewMode.DETAILED -> "Full analysis"
        SummaryViewMode.FULL -> "Complete content"
    }

val SummaryViewMode.estimatedReadTime: String
    get() = when (this) {
        SummaryViewMode.BRIEF -> "~30 sec"
        SummaryViewMode.STANDARD -> "~1 min"
        SummaryViewMode.DETAILED -> "~2 min"
        SummaryViewMode.FULL -> "~3 min"
    }

@Composable
fun ImprovedMultiTierSummaryCard(
    summary: Summary,
    initialViewMode: SummaryViewMode = SummaryViewMode.STANDARD,
    onViewModeChange: (SummaryViewMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentMode by remember { mutableStateOf(initialViewMode) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enhanced View Mode Selector with visual indicators
            ImprovedViewModeSelector(
                currentMode = currentMode,
                onModeChange = { mode ->
                    currentMode = mode
                    onViewModeChange(mode)
                }
            )
            
            // Visual indicator of content length
            ContentLengthIndicator(currentMode)
            
            // Animated content with clear visual differences
            AnimatedContent(
                targetState = currentMode,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + 
                     slideInVertically(animationSpec = tween(300))) togetherWith
                    (fadeOut(animationSpec = tween(200)) + 
                     slideOutVertically(animationSpec = tween(200)))
                },
                label = "summary_content"
            ) { mode ->
                when (mode) {
                    SummaryViewMode.BRIEF -> {
                        BriefContent(summary)
                    }
                    SummaryViewMode.STANDARD -> {
                        StandardContent(summary)
                    }
                    SummaryViewMode.DETAILED -> {
                        DetailedContent(summary)
                    }
                    SummaryViewMode.FULL -> {
                        DetailedContent(summary) // For now, FULL shows same as DETAILED
                    }
                }
            }
        }
    }
}

@Composable
private fun ImprovedViewModeSelector(
    currentMode: SummaryViewMode,
    onModeChange: (SummaryViewMode) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = "Summary View",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Mode selector cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryViewMode.values().forEach { mode ->
                val isSelected = currentMode == mode
                
                Card(
                    onClick = { onModeChange(mode) },
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else Modifier
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            mode.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = mode.displayName,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = mode.estimatedReadTime,
                            fontSize = 11.sp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentLengthIndicator(mode: SummaryViewMode) {
    val progress by animateFloatAsState(
        targetValue = when (mode) {
            SummaryViewMode.BRIEF -> 0.25f
            SummaryViewMode.STANDARD -> 0.5f
            SummaryViewMode.DETAILED -> 0.75f
            SummaryViewMode.FULL -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "length_indicator"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun BriefContent(summary: Summary) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Length indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Quick 2-minute overview",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        // Brief content
        Text(
            text = summary.briefOverview ?: summary.summary.take(150) + "...",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Top 3 key points only
        if (summary.bulletPoints.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Key Highlights",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                summary.bulletPoints.take(3).forEach { point ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = point,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardContent(summary: Summary) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Length indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Balanced 5-minute read",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        
        // Standard summary
        Text(
            text = summary.summary,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // All bullet points
        if (summary.bulletPoints.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Key Points (${summary.bulletPoints.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                summary.bulletPoints.forEach { point ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("•", color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = point,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedContent(summary: Summary) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Length indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Comprehensive 10-minute analysis",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Overview section
        summary.briefOverview?.let { brief ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Overview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    text = brief,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
        
        // Detailed analysis
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Detailed Analysis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = summary.detailedSummary ?: summary.summary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // All bullet points with categories
        if (summary.bulletPoints.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "All Key Points (${summary.bulletPoints.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                summary.bulletPoints.forEachIndexed { index, point ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            "${index + 1}.",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = point,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Key insights if available
        summary.keyInsights?.let { insights ->
            if (insights.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Key Insights",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    insights.forEach { insight ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = insight,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Action items if available
        summary.actionItems?.let { actions ->
            if (actions.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "Action Items",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    
                    actions.forEach { action ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = action,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
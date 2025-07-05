package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.Summary

enum class SummaryViewMode {
    BRIEF, STANDARD, DETAILED, FULL
}

@Composable
fun MultiTierSummaryCard(
    summary: Summary,
    initialViewMode: SummaryViewMode = SummaryViewMode.STANDARD,
    onViewModeChange: (SummaryViewMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentMode by remember { mutableStateOf(initialViewMode) }
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with view mode selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // View mode toggle
                ViewModeToggle(
                    currentMode = currentMode,
                    onModeChange = { mode ->
                        currentMode = mode
                        onViewModeChange(mode)
                    }
                )
            }
            
            // Content based on view mode
            AnimatedContent(
                targetState = currentMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
                },
                label = "summary_content"
            ) { mode ->
                when (mode) {
                    SummaryViewMode.BRIEF -> {
                        // Show only brief overview
                        Text(
                            text = summary.briefOverview ?: summary.summary.take(100),
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    SummaryViewMode.STANDARD -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Show standard summary
                            Text(
                                text = summary.summary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            // Key points with animation
                            AnimatedBulletPoints(
                                points = summary.bulletPoints.take(5),
                                icon = Icons.Default.Check
                            )
                        }
                    }
                    
                    SummaryViewMode.DETAILED -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Brief overview if available
                            summary.briefOverview?.let { brief ->
                                SummarySection(
                                    title = "Overview",
                                    content = brief,
                                    icon = Icons.Default.Lightbulb,
                                    iconColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            
                            // Detailed summary
                            SummarySection(
                                title = "Detailed Analysis",
                                content = summary.detailedSummary ?: summary.summary,
                                icon = Icons.Default.Description
                            )
                            
                            // All bullet points
                            if (summary.bulletPoints.isNotEmpty()) {
                                SummarySection(
                                    title = "Key Points",
                                    icon = Icons.Default.List
                                ) {
                                    AnimatedBulletPoints(
                                        points = summary.bulletPoints,
                                        icon = Icons.Default.KeyboardArrowRight
                                    )
                                }
                            }
                            
                            // Key insights if available
                            summary.keyInsights?.let { insights ->
                                if (insights.isNotEmpty()) {
                                    SummarySection(
                                        title = "Key Insights",
                                        icon = Icons.Default.Psychology,
                                        iconColor = MaterialTheme.colorScheme.secondary
                                    ) {
                                        AnimatedBulletPoints(
                                            points = insights,
                                            icon = Icons.Default.StarOutline,
                                            iconColor = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    SummaryViewMode.FULL -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Everything from detailed
                            summary.briefOverview?.let { brief ->
                                SummarySection(
                                    title = "Overview",
                                    content = brief,
                                    icon = Icons.Default.Lightbulb,
                                    iconColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            
                            SummarySection(
                                title = "Detailed Analysis",
                                content = summary.detailedSummary ?: summary.summary,
                                icon = Icons.Default.Description
                            )
                            
                            if (summary.bulletPoints.isNotEmpty()) {
                                SummarySection(
                                    title = "Key Points",
                                    icon = Icons.Default.List
                                ) {
                                    AnimatedBulletPoints(
                                        points = summary.bulletPoints,
                                        icon = Icons.Default.KeyboardArrowRight
                                    )
                                }
                            }
                            
                            summary.keyInsights?.let { insights ->
                                if (insights.isNotEmpty()) {
                                    SummarySection(
                                        title = "Key Insights",
                                        icon = Icons.Default.Psychology,
                                        iconColor = MaterialTheme.colorScheme.secondary
                                    ) {
                                        AnimatedBulletPoints(
                                            points = insights,
                                            icon = Icons.Default.StarOutline,
                                            iconColor = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                            
                            // Action items if available
                            summary.actionItems?.let { actions ->
                                if (actions.isNotEmpty()) {
                                    SummarySection(
                                        title = "Action Items",
                                        icon = Icons.Default.TaskAlt,
                                        iconColor = MaterialTheme.colorScheme.error
                                    ) {
                                        AnimatedBulletPoints(
                                            points = actions,
                                            icon = Icons.Default.RadioButtonUnchecked,
                                            iconColor = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            
                            // Keywords
                            summary.keywords?.let { keywords ->
                                if (keywords.isNotEmpty()) {
                                    SummarySection(
                                        title = "Keywords",
                                        icon = Icons.Default.Tag
                                    ) {
                                        KeywordChips(keywords = keywords)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Expand/Collapse button for mobile
            if (currentMode != SummaryViewMode.BRIEF) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExpanded) "Show Less" else "Show More Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewModeToggle(
    currentMode: SummaryViewMode,
    onModeChange: (SummaryViewMode) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(2.dp)
    ) {
        SummaryViewMode.values().forEach { mode ->
            val isSelected = currentMode == mode
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (mode) {
                        SummaryViewMode.BRIEF -> Icons.Default.ShortText
                        SummaryViewMode.STANDARD -> Icons.Default.Subject
                        SummaryViewMode.DETAILED -> Icons.Default.Article
                        SummaryViewMode.FULL -> Icons.Default.MenuBook
                    },
                    contentDescription = mode.name,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    title: String,
    content: String? = null,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    contentSection: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        content?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        contentSection?.invoke()
    }
}

@Composable
private fun AnimatedBulletPoints(
    points: List<String>,
    icon: ImageVector = Icons.Default.Circle,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        points.forEachIndexed { index, point ->
            var isVisible by remember { mutableStateOf(false) }
            
            LaunchedEffect(key1 = index) {
                kotlinx.coroutines.delay(index * 100L)
                isVisible = true
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInHorizontally(
                    initialOffsetX = { -30 }
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Text(
                        text = point,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeywordChips(keywords: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keywords.forEach { keyword ->
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = keyword,
                        fontSize = 12.sp
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                border = null,
                modifier = Modifier.height(28.dp)
            )
        }
    }
}
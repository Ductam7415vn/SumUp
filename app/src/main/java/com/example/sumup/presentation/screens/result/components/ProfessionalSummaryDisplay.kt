package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.Summary

@Composable
fun ProfessionalSummaryDisplay(
    summary: Summary,
    modifier: Modifier = Modifier,
    initialViewMode: String = "STANDARD",
    onViewModeChange: ((String) -> Unit)? = null
) {
    // Map initial view mode to tab index
    val initialTab = when (initialViewMode) {
        "BRIEF" -> 0
        "STANDARD" -> 1
        "DETAILED" -> 2
        else -> 1
    }
    var selectedTab by remember { mutableStateOf(initialTab) }
    
    // Extract actual different content from the summary
    val briefContent = summary.briefOverview ?: summary.summary.take(100).trim() + "..."
    val standardContent = summary.summary
    val detailedContent = summary.detailedSummary ?: summary.summary
    
    // Calculate actual lengths
    val briefLength = briefContent.length
    val standardLength = standardContent.length
    val detailedLength = detailedContent.length
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Professional header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AI-Generated Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Choose your preferred reading length",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Tab selector with animations
        ProfessionalTabSelector(
            selectedTab = selectedTab,
            onTabSelected = { index ->
                selectedTab = index
                // Notify view mode change
                val viewMode = when (index) {
                    0 -> "BRIEF"
                    1 -> "STANDARD"
                    2 -> "DETAILED"
                    else -> "STANDARD"
                }
                onViewModeChange?.invoke(viewMode)
            },
            briefLength = briefLength,
            standardLength = standardLength,
            detailedLength = detailedLength
        )
        
        // Content area with animation
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "summary_content"
        ) { tab ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (tab) {
                    0 -> BriefSummaryContent(
                        content = briefContent,
                        bulletPoints = summary.bulletPoints.take(3)
                    )
                    1 -> StandardSummaryContent(
                        content = standardContent,
                        bulletPoints = summary.bulletPoints.take(5),
                        keywords = summary.keywords?.take(5)
                    )
                    2 -> DetailedSummaryContent(
                        briefOverview = briefContent,
                        detailedContent = detailedContent,
                        bulletPoints = summary.bulletPoints,
                        keyInsights = summary.keyInsights,
                        actionItems = summary.actionItems,
                        keywords = summary.keywords
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfessionalTabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    briefLength: Int,
    standardLength: Int,
    detailedLength: Int
) {
    val tabs = listOf(
        TabInfo("Brief", briefLength, "~30 sec", Icons.Default.ShortText, Color(0xFF4CAF50)),
        TabInfo("Standard", standardLength, "~1 min", Icons.Default.Subject, Color(0xFF2196F3)),
        TabInfo("Detailed", detailedLength, "~2 min", Icons.Default.Article, Color(0xFF9C27B0))
    )
    
    Column {
        // Visual progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val progress by animateFloatAsState(
                targetValue = (selectedTab + 1) / 3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "progress"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                tabs[selectedTab].color,
                                tabs[selectedTab].color.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, tab ->
                TabItem(
                    tab = tab,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: TabInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .alpha(animatedAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) tab.color.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                tab.icon,
                contentDescription = null,
                tint = if (isSelected) tab.color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = tab.name,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "${tab.length} chars",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        
        Text(
            text = tab.readTime,
            fontSize = 11.sp,
            color = if (isSelected) tab.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun BriefSummaryContent(
    content: String,
    bulletPoints: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Length indicator card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
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
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Quick Overview",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }
                Text(
                    text = "30 seconds",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Brief content with larger font
        Text(
            text = content,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        if (bulletPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Top 3 Key Points",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                
                bulletPoints.forEach { point ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = point,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardSummaryContent(
    content: String,
    bulletPoints: List<String>,
    keywords: List<String>?
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Length indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
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
                        Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Balanced Summary",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2196F3)
                    )
                }
                Text(
                    text = "1 minute read",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Main summary content
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = content,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Key points section
        if (bulletPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Key Points (${bulletPoints.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
                
                bulletPoints.forEach { point ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "•",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
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
        
        // Keywords section
        keywords?.let { words ->
            if (words.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Keywords",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(words.size) { index ->
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = words[index],
                                        fontSize = 12.sp
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                                    labelColor = Color(0xFF2196F3)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = Color(0xFF2196F3).copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedSummaryContent(
    briefOverview: String,
    detailedContent: String,
    bulletPoints: List<String>,
    keyInsights: List<String>?,
    actionItems: List<String>?,
    keywords: List<String>?
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Length indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9C27B0).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
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
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Comprehensive Analysis",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9C27B0)
                    )
                }
                Text(
                    text = "2-3 minute read",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Executive summary
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Executive Summary",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = briefOverview,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Detailed analysis
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Full Analysis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0)
                )
            }
            Text(
                text = detailedContent,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // All bullet points
        if (bulletPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Complete Key Points (${bulletPoints.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                bulletPoints.forEachIndexed { index, point ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
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
        
        // Key insights
        keyInsights?.let { insights ->
            if (insights.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Deep Insights",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    insights.forEach { insight ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = insight,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Action items
        actionItems?.let { actions ->
            if (actions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Recommended Actions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    
                    actions.forEach { action ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = action,
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
        }
        
        // Keywords
        keywords?.let { words ->
            if (words.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Related Topics",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(words.size) { index ->
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = words[index],
                                        fontSize = 12.sp
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class TabInfo(
    val name: String,
    val length: Int,
    val readTime: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

// Preview Composables
@ThemePreview
@Composable
fun ProfessionalSummaryDisplayPreview() {
    PreviewWrapper {
        ProfessionalSummaryDisplay(
            summary = PreviewData.sampleSummary
        )
    }
}

@Preview(name = "Summary Display - Brief Tab", showBackground = true)
@Composable
fun ProfessionalSummaryDisplayBriefPreview() {
    PreviewWrapper {
        ProfessionalSummaryDisplay(
            summary = PreviewData.sampleSummary
        )
    }
}

@Preview(name = "Summary Display - Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfessionalSummaryDisplayDarkPreview() {
    PreviewWrapper(darkTheme = true) {
        ProfessionalSummaryDisplay(
            summary = PreviewData.sampleSummary
        )
    }
}
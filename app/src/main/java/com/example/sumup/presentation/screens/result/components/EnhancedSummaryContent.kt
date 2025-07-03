package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun EnhancedSummaryContent(
    summary: String,
    bulletPoints: List<String>,
    showAllBullets: Boolean,
    onToggleShowAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var summaryVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        summaryVisible = true
    }
    
    Column(modifier = modifier) {
        // Summary Section with enhanced styling
        AnimatedVisibility(
            visible = summaryVisible,
            enter = fadeIn(animationSpec = tween(600)) + 
                    expandVertically(animationSpec = tween(400))
        ) {
            Column {
                SectionHeader(
                    title = "Summary",
                    icon = Icons.Outlined.Description
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                EnhancedSummaryCard(summary = summary)
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Key Points Section
        SectionHeader(
            title = "Key Points",
            icon = Icons.Outlined.Lightbulb,
            action = if (bulletPoints.size > 3) {
                {
                    TextButton(onClick = onToggleShowAll) {
                        Text(
                            if (showAllBullets) "Show less" else "Show all ${bulletPoints.size} points",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            } else null
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Animated bullet points
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val displayedPoints = if (showAllBullets) bulletPoints else bulletPoints.take(3)
            
            displayedPoints.forEachIndexed { index, point ->
                EnhancedBulletPoint(
                    text = point,
                    index = index,
                    category = categorizePoint(point)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        action?.invoke()
    }
}

@Composable
private fun EnhancedSummaryCard(summary: String) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Animated text with typewriter effect
                AnimatedSummaryText(text = summary)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Copy button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    AnimatedVisibility(
                        visible = copied,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Text(
                            "Copied!",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(summary))
                            copied = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (copied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                            contentDescription = "Copy summary",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
    
    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }
}

@Composable
private fun AnimatedSummaryText(text: String) {
    var displayedText by remember { mutableStateOf("") }
    val words = remember(text) { text.split(" ") }
    
    LaunchedEffect(text) {
        displayedText = ""
        val totalWords = words.size
        val delayPerWord = minOf(1000 / totalWords, 50) // Max 50ms per word
        
        words.forEachIndexed { index, word ->
            displayedText = words.take(index + 1).joinToString(" ")
            delay(delayPerWord.toLong())
        }
    }
    
    Text(
        text = displayedText,
        style = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 26.sp,
            letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun EnhancedBulletPoint(
    text: String,
    index: Int,
    category: PointCategory
) {
    var visible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(400L + index * 50L) // Staggered animation
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it / 2 })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = category.backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (expanded) 4.dp else 1.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Category icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(category.iconBackground)
                ) {
                    Icon(
                        category.icon,
                        contentDescription = null,
                        tint = category.iconColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    AnimatedVisibility(visible = expanded) {
                        Text(
                            text = "Tap to highlight in main text",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                // Expand indicator
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    label = "expand_rotation"
                )
                Icon(
                    if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )
            }
        }
    }
}

private enum class PointCategory(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val iconBackground: Color,
    val backgroundColor: Color
) {
    INSIGHT(
        Icons.Outlined.Lightbulb,
        Color(0xFFFFA726),
        Color(0xFFFFA726).copy(alpha = 0.2f),
        Color(0xFFFFA726).copy(alpha = 0.05f)
    ),
    DATA(
        Icons.Outlined.Analytics,
        Color(0xFF42A5F5),
        Color(0xFF42A5F5).copy(alpha = 0.2f),
        Color(0xFF42A5F5).copy(alpha = 0.05f)
    ),
    ACTION(
        Icons.Outlined.PlayArrow,
        Color(0xFF66BB6A),
        Color(0xFF66BB6A).copy(alpha = 0.2f),
        Color(0xFF66BB6A).copy(alpha = 0.05f)
    ),
    WARNING(
        Icons.Outlined.Warning,
        Color(0xFFEF5350),
        Color(0xFFEF5350).copy(alpha = 0.2f),
        Color(0xFFEF5350).copy(alpha = 0.05f)
    ),
    INFO(
        Icons.Outlined.Info,
        Color(0xFF9C27B0),
        Color(0xFF9C27B0).copy(alpha = 0.2f),
        Color(0xFF9C27B0).copy(alpha = 0.05f)
    )
}

private fun categorizePoint(text: String): PointCategory {
    val lowerText = text.lowercase()
    return when {
        lowerText.contains("data") || lowerText.contains("statistic") || 
        lowerText.contains("number") || lowerText.contains("%") -> PointCategory.DATA
        
        lowerText.contains("should") || lowerText.contains("must") || 
        lowerText.contains("action") || lowerText.contains("do") -> PointCategory.ACTION
        
        lowerText.contains("warning") || lowerText.contains("caution") || 
        lowerText.contains("risk") -> PointCategory.WARNING
        
        lowerText.contains("insight") || lowerText.contains("finding") || 
        lowerText.contains("discovered") -> PointCategory.INSIGHT
        
        else -> PointCategory.INFO
    }
}
package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.BorderStroke
import com.example.sumup.domain.model.Achievement
import com.example.sumup.domain.model.AchievementType
import kotlinx.coroutines.delay

@Composable
fun SettingsAchievementsCard(
    totalPoints: Int,
    unlockedCount: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "card_scale"
    )
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Achievements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Track your progress and milestones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AnimatedTrophyIcon()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AchievementStat(
                    value = totalPoints.toString(),
                    label = "Points",
                    icon = Icons.Default.Stars,
                    color = MaterialTheme.colorScheme.primary
                )
                
                AchievementStat(
                    value = "$unlockedCount/$totalCount",
                    label = "Unlocked",
                    icon = Icons.Default.EmojiEvents,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                val percentage = if (totalCount > 0) {
                    (unlockedCount * 100) / totalCount
                } else 0
                
                AchievementStat(
                    value = "$percentage%",
                    label = "Complete",
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun AnimatedTrophyIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_rotation"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_scale"
    )
    
    Icon(
        Icons.Default.EmojiEvents,
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .rotate(rotation)
            .scale(scale),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun AchievementStat(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AchievementsDialog(
    visible: Boolean,
    achievements: List<Achievement>,
    onDismiss: () -> Unit
) {
    if (visible) {
        var selectedTab by remember { mutableStateOf(0) }
        
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    AchievementsHeader(
                        onClose = onDismiss
                    )
                    
                    // Tabs
                    AchievementsTabs(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                    
                    // Content
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with 
                            fadeOut(animationSpec = tween(200))
                        },
                        label = "tab_content"
                    ) { tab ->
                        val filteredAchievements = when (tab) {
                            0 -> achievements
                            1 -> achievements.filter { it.isUnlocked }
                            2 -> achievements.filter { !it.isUnlocked }
                            else -> emptyList()
                        }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredAchievements) { achievement ->
                                AnimatedAchievementItem(
                                    achievement = achievement,
                                    index = filteredAchievements.indexOf(achievement)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementsHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your journey milestones",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onClose) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementsTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("All", "Unlocked", "Locked")
    
    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
private fun AnimatedAchievementItem(
    achievement: Achievement,
    index: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (achievement.isUnlocked) {
                achievement.tier.color.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            border = BorderStroke(
                width = 1.dp,
                color = if (achievement.isUnlocked) {
                    achievement.tier.color.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                AnimatedAchievementIcon(
                    achievement = achievement,
                    isUnlocked = achievement.isUnlocked
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (achievement.isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (achievement.isUnlocked) 1f else 0.7f
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Progress bar
                    if (!achievement.isUnlocked) {
                        LinearProgressIndicator(
                            progress = { 
                                achievement.currentProgress.toFloat() / achievement.requirement 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = achievement.color,
                            trackColor = achievement.color.copy(alpha = 0.2f)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "${achievement.currentProgress}/${achievement.requirement}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Points
                if (achievement.isUnlocked) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "+${10 * achievement.tier.multiplier}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = achievement.tier.color
                        )
                        Text(
                            text = "points",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedAchievementIcon(
    achievement: Achievement,
    isUnlocked: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isUnlocked) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "icon_scale"
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isUnlocked) {
                    achievement.tier.color.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            getAchievementIcon(achievement.type),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (isUnlocked) {
                achievement.color
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        )
    }
}

private fun getAchievementIcon(type: AchievementType): ImageVector {
    return when (type) {
        AchievementType.FIRST_SUMMARY -> Icons.Default.Description
        AchievementType.SPEED_READER -> Icons.Default.Timer
        AchievementType.PDF_MASTER -> Icons.Default.PictureAsPdf
        AchievementType.OCR_WIZARD -> Icons.Default.CameraAlt
        AchievementType.STREAK_KEEPER -> Icons.Default.TrendingUp
        AchievementType.EXPORT_EXPERT -> Icons.Default.Download
        AchievementType.PERSONA_EXPLORER -> Icons.Default.Psychology
        AchievementType.TIME_SAVER -> Icons.Default.Schedule
        AchievementType.WORD_CRUNCHER -> Icons.Default.TextFields
        AchievementType.POWER_USER -> Icons.Default.PowerSettingsNew
    }
}
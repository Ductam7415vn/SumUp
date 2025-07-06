package com.example.sumup.presentation.components.drawer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.sumup.domain.model.Summary
import com.example.sumup.presentation.screens.main.MainUiState
import com.example.sumup.utils.haptic.HapticFeedbackManager
import com.example.sumup.utils.haptic.HapticFeedbackType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DrawerItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val badge: Int? = null,
    val onClick: () -> Unit
)

data class HistoryGroup(
    val title: String,
    val items: List<Summary>,
    val isExpanded: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    summaryHistory: List<Summary>,
    userEmail: String? = null,
    totalSummaries: Int = 0,
    storageUsed: String = "0 MB",
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSummary: (Summary) -> Unit,
    onStartNewSummary: (MainUiState.InputType) -> Unit,
    hapticManager: HapticFeedbackManager? = null,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(0.85f),
                windowInsets = WindowInsets(0) // Remove default insets to overlay status bar
            ) {
                // Add background that extends under status bar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DrawerContent(
                    currentRoute = currentRoute,
                    summaryHistory = summaryHistory,
                    userEmail = userEmail,
                    totalSummaries = totalSummaries,
                    storageUsed = storageUsed,
                    onNavigateToHome = {
                        onNavigateToHome()
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToHistory = {
                        onNavigateToHistory()
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToSettings = {
                        onNavigateToSettings()
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToSummary = { summary ->
                        onNavigateToSummary(summary)
                        scope.launch { drawerState.close() }
                    },
                    onStartNewSummary = { inputType ->
                        onStartNewSummary(inputType)
                        scope.launch { drawerState.close() }
                    },
                    hapticManager = hapticManager
                )
                }
            }
        },
        content = content,
        gesturesEnabled = true,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)
    )
}

@Composable
private fun DrawerContent(
    currentRoute: String,
    summaryHistory: List<Summary>,
    userEmail: String?,
    totalSummaries: Int,
    storageUsed: String,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSummary: (Summary) -> Unit,
    onStartNewSummary: (MainUiState.InputType) -> Unit,
    hapticManager: HapticFeedbackManager?
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedGroups by remember { mutableStateOf(setOf("Today", "Yesterday")) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Status bar spacer with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                DrawerHeader(
                    userEmail = userEmail ?: "Guest User",
                    totalSummaries = totalSummaries,
                    storageUsed = storageUsed
                )
            }
            
            // Quick Actions
            item {
                QuickActionsSection(
                    onStartNewSummary = onStartNewSummary,
                    hapticManager = hapticManager
                )
            }
            
            // Divider
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            
            // History Section
            item {
                HistorySectionHeader(
                    onSearchQueryChange = { searchQuery = it },
                    searchQuery = searchQuery
                )
            }
            
            // Grouped History Items
            val groupedHistory = groupHistoryByDate(summaryHistory)
                .filter { group ->
                    searchQuery.isEmpty() || 
                    group.items.any { 
                        it.originalText.contains(searchQuery, ignoreCase = true) ||
                        it.summaryText.contains(searchQuery, ignoreCase = true)
                    }
                }
            
            groupedHistory.forEach { group ->
                item {
                    HistoryGroupHeader(
                        title = group.title,
                        count = group.items.size,
                        isExpanded = expandedGroups.contains(group.title),
                        onToggle = {
                            expandedGroups = if (expandedGroups.contains(group.title)) {
                                expandedGroups - group.title
                            } else {
                                expandedGroups + group.title
                            }
                        }
                    )
                }
                
                if (expandedGroups.contains(group.title)) {
                    items(
                        items = group.items.filter { 
                            searchQuery.isEmpty() || 
                            it.originalText.contains(searchQuery, ignoreCase = true) ||
                            it.summaryText.contains(searchQuery, ignoreCase = true) 
                        },
                        key = { it.id }
                    ) { summary ->
                        HistoryItem(
                            summary = summary,
                            onClick = { onNavigateToSummary(summary) },
                            hapticManager = hapticManager
                        )
                    }
                }
            }
            
            // Bottom Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Settings and Other Options
            item {
                DrawerMenuItem(
                    icon = Icons.Outlined.History,
                    title = "View All History",
                    isSelected = currentRoute == "history",
                    onClick = onNavigateToHistory,
                    hapticManager = hapticManager
                )
            }
            
            item {
                DrawerMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "Settings",
                    isSelected = currentRoute == "settings",
                    onClick = onNavigateToSettings,
                    hapticManager = hapticManager
                )
            }
            
            // Premium feature hidden for now
            /*
            item {
                DrawerMenuItem(
                    icon = Icons.Outlined.Diamond,
                    title = "Upgrade to Premium",
                    onClick = { /* TODO: Navigate to premium */ },
                    hapticManager = hapticManager,
                    badge = "PRO"
                )
            }
            */
            
            item {
                var showHelpDialog by remember { mutableStateOf(false) }
                
                DrawerMenuItem(
                    icon = Icons.AutoMirrored.Outlined.HelpOutline,
                    title = "Help & Feedback",
                    onClick = { 
                        hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                        showHelpDialog = true
                    },
                    hapticManager = hapticManager
                )
                
                if (showHelpDialog) {
                    HelpAndFeedbackDialog(
                        onDismiss = { showHelpDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    userEmail: String,
    totalSummaries: Int,
    storageUsed: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar with gradient border
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        ) {
                            Text(
                                text = userEmail.firstOrNull()?.uppercase() ?: "G",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Column {
                    Text(
                        text = "SumUp Pro",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = userEmail,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    value = totalSummaries.toString(),
                    label = "Summaries",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = storageUsed,
                    label = "Used",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onStartNewSummary: (MainUiState.InputType) -> Unit,
    hapticManager: HapticFeedbackManager?
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Primary Action with gradient
        Surface(
            onClick = { 
                hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                onStartNewSummary(MainUiState.InputType.TEXT)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "New Summary",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        
        // Secondary Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                    onStartNewSummary(MainUiState.InputType.OCR)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Scan", fontSize = 14.sp)
            }
            
            OutlinedButton(
                onClick = { 
                    hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                    onStartNewSummary(MainUiState.InputType.DOCUMENT)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun HistorySectionHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Recent Summaries",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(
            onClick = { /* TODO: Toggle search */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryGroupHeader(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Badge(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(count.toString())
        }
    }
}

@Composable
private fun HistoryItem(
    summary: Summary,
    onClick: () -> Unit,
    hapticManager: HapticFeedbackManager?
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .scale(scale)
            .clickable {
                hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 4.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = summary.originalText.take(50) + "...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = summary.summaryText.take(60) + "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(summary.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Type indicator based on original text length
            Icon(
                imageVector = when {
                    summary.originalText.length > 1000 -> Icons.Default.Description
                    summary.originalText.length > 500 -> Icons.Default.TextFields
                    else -> Icons.AutoMirrored.Default.ShortText
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean = false,
    badge: String? = null,
    onClick: () -> Unit,
    hapticManager: HapticFeedbackManager?
) {
    var isHovered by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        onClick = {
            hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
            onClick()
        },
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            
            badge?.let {
                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Text(it, fontSize = 11.sp)
                }
            }
        }
    }
}

// Helper functions
private fun groupHistoryByDate(summaries: List<Summary>): List<HistoryGroup> {
    val now = System.currentTimeMillis()
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    val yesterday = today - 24 * 60 * 60 * 1000
    val thisWeek = today - 7 * 24 * 60 * 60 * 1000
    
    val groups = mutableListOf<HistoryGroup>()
    
    val todayItems = summaries.filter { it.createdAt >= today }
    if (todayItems.isNotEmpty()) {
        groups.add(HistoryGroup("Today", todayItems))
    }
    
    val yesterdayItems = summaries.filter { it.createdAt in yesterday until today }
    if (yesterdayItems.isNotEmpty()) {
        groups.add(HistoryGroup("Yesterday", yesterdayItems))
    }
    
    val thisWeekItems = summaries.filter { it.createdAt in thisWeek until yesterday }
    if (thisWeekItems.isNotEmpty()) {
        groups.add(HistoryGroup("This Week", thisWeekItems))
    }
    
    val olderItems = summaries.filter { it.createdAt < thisWeek }
    if (olderItems.isNotEmpty()) {
        groups.add(HistoryGroup("Older", olderItems))
    }
    
    return groups
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

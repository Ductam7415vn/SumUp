package com.example.sumup.presentation.screens.result

import android.content.Intent
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.presentation.screens.result.components.*
import com.example.sumup.presentation.screens.result.utils.rememberResultScreenHaptics
import com.example.sumup.presentation.components.*
import com.example.sumup.presentation.components.animations.ConfettiCelebration
import com.example.sumup.utils.clipboard.ClipboardManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.rememberLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCelebration by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }
    var showInsightsDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    val haptics = rememberResultScreenHaptics()
    val lazyListState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Show celebration when summary is first loaded
    LaunchedEffect(uiState.summary) {
        if (uiState.summary != null && !showCelebration) {
            showCelebration = true
            haptics.success()
            delay(5000) // Increased duration for better effect
            showCelebration = false
        }
    }
    
    // Handle refresh
    val handleRefresh: () -> Unit = {
        scope.launch {
            isRefreshing = true
            haptics.lightTap()
            viewModel.regenerateSummary()
            delay(1000) // Minimum refresh time for UX
            isRefreshing = false
        }
        Unit
    }
    
    SwipeableResultContent(
        onSwipeBack = {
            haptics.swipe()
            onNavigateBack()
        },
        onSwipeToHistory = {
            haptics.swipe()
            onNavigateToHistory()
        },
        swipeEnabled = !uiState.isLoading,
        hapticFeedback = { haptics.lightTap() }
    ) {
        PullToRefreshResult(
            isRefreshing = isRefreshing || uiState.isLoading,
            onRefresh = handleRefresh,
            hapticFeedback = { haptics.lightTap() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Enhanced Hero Section
                HeroSection(
                    onNavigateBack = {
                        haptics.lightTap()
                        onNavigateBack()
                    },
                    onMoreOptions = { 
                        haptics.lightTap()
                        viewModel.showMoreOptions()
                    },
                    contentType = "Text" // TODO: Add sourceType to Summary model
                )
            
            // Main content with padding
            EnhancedLoadingState(
                isLoading = uiState.isLoading,
                hasData = uiState.summary != null,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(top = 8.dp),
                shimmerContent = {
                    // Enhanced shimmer with animations
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // KPI cards shimmer
                        item {
                            ShimmerKPIRow()
                        }
                        
                        // Persona selector shimmer
                        item {
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        
                        // Summary content shimmer
                        item {
                            ShimmerResultCard()
                        }
                    }
                },
                actualContent = {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Enhanced Animated KPI Card
                        item {
                            AnimatedKPICard(
                                originalWords = uiState.summary?.metrics?.originalWordCount ?: 0,
                                summaryWords = uiState.summary?.metrics?.summaryWordCount ?: 0,
                                originalReadTime = uiState.summary?.metrics?.originalReadingTime ?: 0,
                                summaryReadTime = uiState.summary?.metrics?.summaryReadingTime ?: 0
                            )
                        }
                    
                        // Enhanced Persona Selector with animation
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Optimize for audience",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        
                                        // Auto-save indicator
                                        if (uiState.autoSaveEnabled) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            val lastSaveTime = uiState.lastAutoSaveTime
                                            AutoSaveIndicator(
                                                saveState = if (lastSaveTime != null && 
                                                    System.currentTimeMillis() - lastSaveTime < 3000) {
                                                    SaveState.SAVED
                                                } else {
                                                    SaveState.IDLE
                                                },
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    PersonaSelector(
                                        currentPersona = uiState.selectedPersona,
                                        onPersonaChange = { persona ->
                                            haptics.selection()
                                            viewModel.selectPersona(persona)
                                        }
                                    )
                                }
                            }
                        }
                    
                        // Professional Summary Display
                        item {
                            uiState.summary?.let { summary ->
                                ProfessionalSummaryDisplay(
                                    summary = summary,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    initialViewMode = uiState.savedViewMode,
                                    onViewModeChange = { mode ->
                                        haptics.selection()
                                        viewModel.updateSummaryViewMode(mode)
                                    }
                                )
                            }
                        }
                    }
                },
                loadingContent = {
                    // Enhanced loading overlay with progress
                    if (uiState.isLoading && uiState.summary != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Regenerating summary...",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "Using ${uiState.selectedPersona.displayName} perspective",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            )
            }
        }
        
        // Enhanced Floating Action Menu
        FloatingActionMenu(
            summaryText = buildSummaryText(uiState.summary),
            onCopy = { 
                haptics.copy()
                viewModel.copySummary(context) 
            },
            onShare = { 
                haptics.lightTap()
                shareSummary(context, uiState.summary) 
            },
            onSave = { 
                haptics.lightTap()
                viewModel.saveSummary() 
            },
            onRegenerate = { 
                haptics.lightTap()
                viewModel.regenerateSummary() 
            },
            onExport = { 
                haptics.lightTap()
                showExportDialog = true
            },
            hapticFeedback = { haptics.lightTap() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp)
        )
        
        // Enhanced Confetti celebration overlay
        ConfettiCelebration(
            isActive = showCelebration,
            modifier = Modifier.fillMaxSize()
        )
        
        // Achievement notification
        uiState.showAchievement?.let { achievement ->
            AchievementNotification(
                achievement = achievement,
                onDismiss = { viewModel.dismissAchievement() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )
        }
        
        // Show success snackbar
        if (uiState.showCopySuccess) {
            LaunchedEffect(Unit) {
                delay(2000)
                viewModel.dismissCopySuccess()
            }
            
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Summary copied to clipboard!")
                }
            }
        }
        
        // Export success handling
        LaunchedEffect(uiState.showExportSuccess) {
            if (uiState.showExportSuccess) {
                val file = uiState.exportedFile
                if (file != null) {
                    haptics.success()
                    // Auto share the exported file
                    viewModel.shareExportedFile(file)
                    viewModel.dismissExportSuccess()
                }
            }
        }
    }
    
    // Export Dialog
    ExportDialog(
        isVisible = showExportDialog,
        onDismiss = { 
            showExportDialog = false
            viewModel.clearExportError()
        },
        onExport = { format ->
            haptics.lightTap()
            viewModel.exportSummary(format)
        },
        isExporting = uiState.isExporting,
        exportError = uiState.exportError
    )
    
    // More Options Menu
    if (uiState.showMoreOptionsMenu) {
        MoreOptionsMenu(
            onDismiss = { viewModel.dismissMoreOptions() },
            onAutoSaveToggle = { 
                haptics.lightTap()
                viewModel.toggleAutoSave()
            },
            onViewInsights = { 
                haptics.lightTap()
                viewModel.viewInsights()
            },
            onExportToClipboard = {
                haptics.lightTap()
                viewModel.exportToClipboard()
            },
            isAutoSaveEnabled = uiState.autoSaveEnabled
        )
    }
    
    // Insights Dialog
    if (uiState.showInsightsDialog) {
        InsightsDialog(
            summary = uiState.summary,
            onDismiss = { viewModel.dismissInsights() }
        )
    }
}

private fun buildSummaryText(summary: com.example.sumup.domain.model.Summary?): String {
    if (summary == null) return ""
    
    return buildString {
        // Add brief overview if available
        summary.briefOverview?.let {
            appendLine("=== BRIEF ===")
            appendLine(it)
            appendLine()
        }
        
        // Add summary paragraph
        appendLine("=== SUMMARY ===")
        appendLine(summary.summary)
        appendLine()
        
        // Add detailed summary if available
        summary.detailedSummary?.let {
            appendLine("=== DETAILED ANALYSIS ===")
            appendLine(it)
            appendLine()
        }
        
        // Add bullet points
        appendLine("=== KEY POINTS ===")
        summary.bulletPoints.forEach { bullet ->
            appendLine("• $bullet")
        }
        
        // Add insights if available
        summary.keyInsights?.let { insights ->
            if (insights.isNotEmpty()) {
                appendLine()
                appendLine("=== KEY INSIGHTS ===")
                insights.forEach { insight ->
                    appendLine("• $insight")
                }
            }
        }
        
        // Add action items if available
        summary.actionItems?.let { actions ->
            if (actions.isNotEmpty()) {
                appendLine()
                appendLine("=== ACTION ITEMS ===")
                actions.forEach { action ->
                    appendLine("□ $action")
                }
            }
        }
        
        // Add keywords if available
        summary.keywords?.let { keywords ->
            if (keywords.isNotEmpty()) {
                appendLine()
                appendLine("=== KEYWORDS ===")
                appendLine(keywords.joinToString(", "))
            }
        }
    }
}

private fun shareSummary(
    context: android.content.Context,
    summary: com.example.sumup.domain.model.Summary?
) {
    summary?.let { sum ->
        val shareText = buildString {
            appendLine("Summary created with SumUp:")
            appendLine()
            
            // Include summary paragraph
            appendLine("=== SUMMARY ===")
            appendLine(sum.summary)
            appendLine()
            
            // Include key points
            appendLine("=== KEY POINTS ===")
            sum.bulletPoints.forEach { bullet ->
                appendLine("• $bullet")
            }
            appendLine()
            appendLine("---")
            appendLine("Original: ${sum.metrics.originalWordCount} words")
            appendLine("Summary: ${sum.metrics.summaryWordCount} words (${sum.metrics.reductionPercentage}% reduction)")
            appendLine("Time saved: ~${sum.metrics.originalReadingTime - sum.metrics.summaryReadingTime} minutes")
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Summary from SumUp")
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share summary via...")
        context.startActivity(chooser)
    }
}

// Preview Composables
@ThemePreview
@Composable
fun ResultScreenPreview() {
    PreviewWrapper {
        ResultScreen(
            onNavigateBack = {},
            onNavigateToHistory = {},
        )
    }
}

@Preview(name = "Result Screen - Loading", showBackground = true)
@Composable
fun ResultScreenLoadingPreview() {
    PreviewWrapper {
        ResultScreen(
            onNavigateBack = {},
            onNavigateToHistory = {},
        )
    }
}

@AllDevicePreview
@Composable
fun ResultScreenDevicePreview() {
    PreviewWrapper {
        ResultScreen(
            onNavigateBack = {},
            onNavigateToHistory = {},
        )
    }
}
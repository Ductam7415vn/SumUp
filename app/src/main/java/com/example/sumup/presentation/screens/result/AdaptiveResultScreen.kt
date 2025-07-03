package com.example.sumup.presentation.screens.result

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.screens.result.components.*
import com.example.sumup.presentation.components.*

/**
 * Adaptive ResultScreen that provides better layout for tablets and foldables
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    adaptiveInfo: com.example.sumup.presentation.utils.AdaptiveLayoutInfo? = null,
    summaryId: String? = null,
    viewModel: ResultViewModel = hiltViewModel()
) {
    // Load specific summary if ID is provided
    LaunchedEffect(summaryId) {
        summaryId?.let {
            viewModel.loadSummary(it)
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (adaptiveInfo?.deviceType == com.example.sumup.presentation.utils.DeviceType.TABLET ||
        adaptiveInfo?.deviceType == com.example.sumup.presentation.utils.DeviceType.DESKTOP) {
        // Two-pane layout for tablets and large screens
        TabletResultScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToHistory = onNavigateToHistory,
            adaptiveInfo = adaptiveInfo,
            viewModel = viewModel
        )
    } else {
        // Single-pane layout for compact screens
        ResultScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToHistory = onNavigateToHistory,
            viewModel = viewModel
        )
    }
}

/**
 * Summary pane showing the main summary content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultSummaryPane(
    uiState: ResultUiState,
    viewModel: ResultViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Summary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Share functionality */ }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            SummaryActionBar(
                summaryText = uiState.summary?.summaryText ?: "",
                onCopy = { /* Copy summary */ },
                onShare = { /* Share summary */ },
                onSave = { /* Save to history */ },
                onRegenerate = { /* Regenerate summary */ },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(responsivePadding())
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.summary != null -> {
                    val summary = uiState.summary // Smart cast
                    AdaptiveCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(responsivePadding()),
                        compactContent = {
                            CompactSummaryContent(
                                summary = summary,
                                selectedPersona = uiState.selectedPersona,
                                onPersonaChange = viewModel::selectPersona,
                                modifier = Modifier.padding(16.dp)
                            )
                        },
                        expandedContent = {
                            ExpandedSummaryContent(
                                summary = summary,
                                selectedPersona = uiState.selectedPersona,
                                onPersonaChange = viewModel::selectPersona,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Failed to load summary",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "No summary available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Details pane showing metrics and additional information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultDetailsPane(
    uiState: ResultUiState,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.summary != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(responsivePadding())
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Metrics card
                uiState.summary?.let { summary ->
                    val originalWords = summary.originalText.split("\\s+".toRegex()).size
                    val summaryWords = summary.summaryText.split("\\s+".toRegex()).size
                    val wordsPerMinute = 200 // Average reading speed
                    
                    SummaryKPICard(
                        originalWords = originalWords,
                        summaryWords = summaryWords,
                        originalReadTime = originalWords / wordsPerMinute,
                        summaryReadTime = summaryWords / wordsPerMinute,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(SharedElementKeys.RESULT_METRICS_CARD)
                    )
                }
                
                // Persona selector
                PersonaSelector(
                    currentPersona = uiState.selectedPersona,
                    onPersonaChange = { /* Handle persona change in details view */ },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Additional insights card
                AdaptiveCard(
                    compactContent = {
                        InsightsCard(
                            summary = uiState.summary,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    expandedContent = {
                        ExpandedInsightsCard(
                            summary = uiState.summary,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                )
                
                // Reading time card
                uiState.summary?.let { summary ->
                    ReadingTimeCard(
                        originalWordCount = summary.metrics.originalWordCount,
                        summaryWordCount = summary.metrics.summaryWordCount,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Details will appear here",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Single-pane layout for compact screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultSinglePane(
    uiState: ResultUiState,
    viewModel: ResultViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Summary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(
                        onClick = { /* Share functionality */ }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            SummaryActionBar(
                summaryText = uiState.summary?.summaryText ?: "",
                onCopy = { /* Copy summary */ },
                onShare = { /* Share summary */ },
                onSave = { /* Save to history */ },
                onRegenerate = { /* Regenerate summary */ },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.summary != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary content
                    CompactSummaryContent(
                        summary = uiState.summary,
                        selectedPersona = uiState.selectedPersona,
                        onPersonaChange = viewModel::selectPersona,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Metrics
                    uiState.summary?.let { summary ->
                        val originalWords = summary.originalText.split("\\s+".toRegex()).size
                        val summaryWords = summary.summaryText.split("\\s+".toRegex()).size
                        val wordsPerMinute = 200 // Average reading speed
                        
                        SummaryKPICard(
                            originalWords = originalWords,
                            summaryWords = summaryWords,
                            originalReadTime = originalWords / wordsPerMinute,
                            summaryReadTime = summaryWords / wordsPerMinute,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Persona selector
                    PersonaSelector(
                        currentPersona = uiState.selectedPersona,
                        onPersonaChange = viewModel::selectPersona,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Failed to load summary",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "No summary available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact summary content for small screens
 */
@Composable
private fun CompactSummaryContent(
    summary: com.example.sumup.domain.model.Summary,
    selectedPersona: com.example.sumup.domain.model.SummaryPersona,
    onPersonaChange: (com.example.sumup.domain.model.SummaryPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = summary.summaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (summary.bulletPoints.isNotEmpty()) {
                Text(
                    text = "Key Points:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                summary.bulletPoints.forEach { point ->
                    Text(
                        text = "• $point",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Expanded summary content for large screens
 */
@Composable
private fun ExpandedSummaryContent(
    summary: com.example.sumup.domain.model.Summary,
    selectedPersona: com.example.sumup.domain.model.SummaryPersona,
    onPersonaChange: (com.example.sumup.domain.model.SummaryPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title section
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        HorizontalDivider()
        
        // Content section
        Text(
            text = summary.summaryText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
        )
        
        if (summary.bulletPoints.isNotEmpty()) {
            HorizontalDivider()
            
            Text(
                text = "Key Points",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                summary.bulletPoints.forEach { point ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(6.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Insights card for additional information
 */
@Composable
private fun InsightsCard(
    summary: com.example.sumup.domain.model.Summary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Insights",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Compression: ${summary.metrics.reductionPercentage}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Reading time saved: ~${summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime} minutes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Expanded insights card with more details
 */
@Composable
private fun ExpandedInsightsCard(
    summary: com.example.sumup.domain.model.Summary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Document Analysis",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InsightItem(
                label = "Original Length",
                value = "${summary.originalText.length} chars"
            )
            InsightItem(
                label = "Summary Length", 
                value = "${summary.summaryText.length} chars"
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InsightItem(
                label = "Compression Ratio",
                value = "${summary.metrics.reductionPercentage}%"
            )
            InsightItem(
                label = "Key Points",
                value = "${summary.bulletPoints.size}"
            )
        }
    }
}

/**
 * Individual insight item
 */
@Composable
private fun InsightItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Reading time estimation card
 */
@Composable
private fun ReadingTimeCard(
    originalWordCount: Int,
    summaryWordCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Text(
                text = "Time Saved",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            val originalMinutes = (originalWordCount / 200).coerceAtLeast(1)
            val summaryMinutes = (summaryWordCount / 200).coerceAtLeast(1)
            val timeSaved = originalMinutes - summaryMinutes
            
            Text(
                text = "${timeSaved.coerceAtLeast(1)} minute${if (timeSaved != 1) "s" else ""}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Text(
                text = "$originalMinutes min → $summaryMinutes min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}
package com.example.sumup.presentation.screens.settings

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.presentation.components.*

/**
 * Demo screen showcasing predictive back gesture functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictiveBackDemo(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var demoMode by remember { mutableStateOf(DemoMode.BASIC) }
    var showContent by remember { mutableStateOf(true) }
    
    when (demoMode) {
        DemoMode.BASIC -> {
            BasicPredictiveBackDemo(
                onNavigateBack = onNavigateBack,
                onChangeDemoMode = { demoMode = it },
                modifier = modifier
            )
        }
        DemoMode.CONFIRMATION -> {
            ConfirmationPredictiveBackDemo(
                onNavigateBack = onNavigateBack,
                onChangeDemoMode = { demoMode = it },
                modifier = modifier
            )
        }
        DemoMode.SWIPE -> {
            SwipePredictiveBackDemo(
                onNavigateBack = onNavigateBack,
                onChangeDemoMode = { demoMode = it },
                modifier = modifier
            )
        }
        DemoMode.ANIMATION -> {
            AnimatedPredictiveBackDemo(
                onNavigateBack = onNavigateBack,
                onChangeDemoMode = { demoMode = it },
                showContent = showContent,
                onToggleContent = { showContent = !showContent },
                modifier = modifier
            )
        }
    }
}

private enum class DemoMode {
    BASIC, CONFIRMATION, SWIPE, ANIMATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicPredictiveBackDemo(
    onNavigateBack: () -> Unit,
    onChangeDemoMode: (DemoMode) -> Unit,
    modifier: Modifier = Modifier
) {
    PredictiveBackGestureHandler(
        enabled = true,
        onBackPressed = onNavigateBack,
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Basic Predictive Back") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DemoModeSelector(
                    selectedMode = DemoMode.BASIC,
                    onModeSelected = onChangeDemoMode
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Basic Gesture",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        
                        Text(
                            "Swipe from the left edge or use the back button to see the predictive back animation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            "• Visual feedback with scaling animation\n" +
                            "• Back gesture indicator\n" +
                            "• Smooth transitions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                repeat(10) { index ->
                    ElevatedCard {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Demo Content ${index + 1}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "This is sample content to demonstrate the predictive back gesture animation. " +
                                "Notice how the entire screen scales and fades during the gesture.",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmationPredictiveBackDemo(
    onNavigateBack: () -> Unit,
    onChangeDemoMode: (DemoMode) -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmationBackHandler(
        enabled = true,
        requiresConfirmation = true,
        confirmationMessage = "This is a confirmation dialog for the predictive back gesture. Are you sure you want to leave?",
        onBackPressed = onNavigateBack,
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Confirmation Back") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DemoModeSelector(
                    selectedMode = DemoMode.CONFIRMATION,
                    onModeSelected = onChangeDemoMode
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "Confirmation Required",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        Text(
                            "This demo shows how to require confirmation before allowing the back gesture to complete.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Text(
                            "Try using the back gesture - you'll see a confirmation dialog!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                repeat(8) { index ->
                    ElevatedCard {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    "Protected Content ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "This content is protected by confirmation dialog.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipePredictiveBackDemo(
    onNavigateBack: () -> Unit,
    onChangeDemoMode: (DemoMode) -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeBackDetector(
        onSwipeBack = onNavigateBack,
        threshold = 0.3f,
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Swipe Back Demo") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DemoModeSelector(
                    selectedMode = DemoMode.SWIPE,
                    onModeSelected = onChangeDemoMode
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Swipe,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Swipe Detection",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Text(
                            "This demo shows custom swipe back detection with visual feedback.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            "Swipe from the left edge to see the custom animation!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedPredictiveBackDemo(
    onNavigateBack: () -> Unit,
    onChangeDemoMode: (DemoMode) -> Unit,
    showContent: Boolean,
    onToggleContent: () -> Unit,
    modifier: Modifier = Modifier
) {
    PredictiveBackGestureHandler(
        enabled = true,
        onBackPressed = onNavigateBack,
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Animated Back") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleContent) {
                            Icon(
                                if (showContent) Icons.Default.VisibilityOff 
                                else Icons.Default.Visibility,
                                contentDescription = "Toggle content"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DemoModeSelector(
                    selectedMode = DemoMode.ANIMATION,
                    onModeSelected = onChangeDemoMode
                )
                
                AnimatedBackTransition(
                    isVisible = showContent
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Animation,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    "Custom Animations",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            
                            Text(
                                "This demo combines predictive back gestures with custom content animations.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            
                            Text(
                                "Use the visibility toggle to see transition animations!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoModeSelector(
    selectedMode: DemoMode,
    onModeSelected: (DemoMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Demo Modes",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DemoMode.values().forEach { mode ->
                    FilterChip(
                        onClick = { onModeSelected(mode) },
                        label = { 
                            Text(
                                mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        selected = selectedMode == mode,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
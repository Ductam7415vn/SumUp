package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsExportImportDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    if (visible) {
        var selectedAction by remember { mutableStateOf<ExportImportAction?>(null) }
        
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            AnimatedContent(
                targetState = selectedAction,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(200))
                },
                label = "export_import_content"
            ) { action ->
                when (action) {
                    null -> ExportImportSelection(
                        onExportSelected = { selectedAction = ExportImportAction.EXPORT },
                        onImportSelected = { selectedAction = ExportImportAction.IMPORT },
                        onDismiss = onDismiss
                    )
                    ExportImportAction.EXPORT -> ExportProgressDialog(
                        onComplete = {
                            onExport()
                            onDismiss()
                        },
                        onCancel = { selectedAction = null }
                    )
                    ExportImportAction.IMPORT -> ImportProgressDialog(
                        onComplete = {
                            onImport()
                            onDismiss()
                        },
                        onCancel = { selectedAction = null }
                    )
                }
            }
        }
    }
}

private enum class ExportImportAction {
    EXPORT, IMPORT
}

@Composable
private fun ExportImportSelection(
    onExportSelected: () -> Unit,
    onImportSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Export option
            AnimatedActionCard(
                title = "Export Settings",
                description = "Save your settings and preferences to a file",
                icon = Icons.Default.Upload,
                color = MaterialTheme.colorScheme.primary,
                onClick = onExportSelected,
                animationDelay = 0
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Import option
            AnimatedActionCard(
                title = "Import Settings",
                description = "Restore settings from a backup file",
                icon = Icons.Default.Download,
                color = MaterialTheme.colorScheme.secondary,
                onClick = onImportSelected,
                animationDelay = 100
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun AnimatedActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    animationDelay: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = when {
            !isVisible -> 0.8f
            isPressed -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "card_alpha"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .graphicsLayer { this.alpha = alpha }
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                isPressed = true
                onClick()
            },
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f),
                    color.copy(alpha = 0.1f)
                )
            )
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(if (isPressed) 15f else 0f),
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = color
            )
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
private fun ExportProgressDialog(
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var currentStep by remember { mutableStateOf("Preparing export...") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            // Simulate export process
            val steps = listOf(
                "Collecting settings..." to 0.2f,
                "Gathering preferences..." to 0.4f,
                "Compiling data..." to 0.6f,
                "Creating backup file..." to 0.8f,
                "Finalizing export..." to 1f
            )
            
            steps.forEach { (step, targetProgress) ->
                currentStep = step
                while (progress < targetProgress) {
                    progress += 0.01f
                    delay(10)
                }
                delay(200)
            }
            
            onComplete()
        }
    }
    
    ProgressDialog(
        title = "Exporting Settings",
        currentStep = currentStep,
        progress = progress,
        icon = Icons.Default.Upload,
        color = MaterialTheme.colorScheme.primary,
        onCancel = onCancel
    )
}

@Composable
private fun ImportProgressDialog(
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var currentStep by remember { mutableStateOf("Reading backup file...") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            // Simulate import process
            val steps = listOf(
                "Validating backup..." to 0.2f,
                "Parsing settings..." to 0.4f,
                "Applying preferences..." to 0.6f,
                "Updating configuration..." to 0.8f,
                "Completing import..." to 1f
            )
            
            steps.forEach { (step, targetProgress) ->
                currentStep = step
                while (progress < targetProgress) {
                    progress += 0.01f
                    delay(10)
                }
                delay(200)
            }
            
            onComplete()
        }
    }
    
    ProgressDialog(
        title = "Importing Settings",
        currentStep = currentStep,
        progress = progress,
        icon = Icons.Default.Download,
        color = MaterialTheme.colorScheme.secondary,
        onCancel = onCancel
    )
}

@Composable
private fun ProgressDialog(
    title: String,
    currentStep: String,
    progress: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated icon
            AnimatedProgressIcon(
                icon = icon,
                color = color,
                progress = progress
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = currentStep,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (progress < 1f) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun AnimatedProgressIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    progress: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_animation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "icon_rotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = 0.8f + (progress * 0.2f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "icon_scale"
    )
    
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(40.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.2f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
        )
        
        // Rotating icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .scale(scale)
                .graphicsLayer {
                    rotationZ = if (progress < 1f) rotation else 0f
                },
            tint = color
        )
        
        // Progress ring
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 3.dp,
            trackColor = Color.Transparent
        )
    }
}
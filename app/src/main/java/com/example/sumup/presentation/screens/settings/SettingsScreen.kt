package com.example.sumup.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sumup.presentation.components.*
import com.example.sumup.presentation.components.animations.AnimatedSwitch
import com.example.sumup.presentation.components.animations.AnimatedSettingToggle
import com.example.sumup.utils.haptic.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    adaptiveInfo: com.example.sumup.presentation.utils.AdaptiveLayoutInfo? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    HapticIconButton(
                        onClick = onNavigateBack,
                        hapticType = HapticFeedbackType.NAVIGATION
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        EnhancedLoadingState(
            isLoading = uiState.error != null, // Simple loading state check
            hasData = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            shimmerContent = {
                // Show shimmer while loading settings
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Settings sections shimmer
                    items(4) {
                        ShimmerSettingsSection(itemCount = 2)
                    }
                }
            },
            actualContent = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
            // Appearance Section
            item {
                SettingsSection("Appearance") {
                    ThemePreference(
                        currentTheme = uiState.themeMode,
                        onClick = { viewModel.showThemeDialog() }
                    )
                    
                    DynamicColorPreference(
                        enabled = uiState.isDynamicColorEnabled,
                        onToggle = { viewModel.setDynamicColorEnabled(it) }
                    )
                }
            }
            
            // Summarization Section
            item {
                SettingsSection("Summarization") {
                    DefaultLengthPreference(
                        currentLength = uiState.summaryLength,
                        onClick = { viewModel.showLengthDialog() }
                    )
                    
                    LanguagePreference(
                        currentLanguage = uiState.language,
                        onClick = { viewModel.showLanguageDialog() }
                    )
                }
            }
            
            // Data & Storage Section
            item {
                SettingsSection("Data & Storage") {
                    StorageUsageItem(
                        storageUsage = uiState.storageUsage
                    )
                    
                    ClearHistoryPreference(
                        onClick = { viewModel.showClearDataDialog() }
                    )
                    
                    ExportDataItem(
                        onClick = { viewModel.exportData() }
                    )
                }
            }
            
            // About Section
            item {
                SettingsSection("About") {
                    VersionInfo(
                        version = uiState.appVersion
                    )
                    
                    SendFeedbackItem()
                }
            }
                }
            }
        )
        
        // Dialogs
        if (uiState.showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = uiState.themeMode,
                onThemeSelected = { viewModel.setThemeMode(it) },
                onDismiss = { viewModel.hideThemeDialog() }
            )
        }
        
        if (uiState.showLengthDialog) {
            LengthSelectionDialog(
                currentLength = uiState.summaryLength,
                onLengthSelected = { viewModel.setSummaryLength(it) },
                onDismiss = { viewModel.hideLengthDialog() }
            )
        }
        
        if (uiState.showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = uiState.language,
                onLanguageSelected = { viewModel.setLanguage(it) },
                onDismiss = { viewModel.hideLanguageDialog() }
            )
        }
        
        if (uiState.showClearDataDialog) {
            ClearDataConfirmationDialog(
                onConfirm = { viewModel.clearAllData() },
                onDismiss = { viewModel.hideClearDataDialog() },
                isClearing = uiState.isClearing
            )
        }
        
        if (uiState.clearDataSuccess) {
            SuccessDialog(
                message = "Data cleared successfully",
                onDismiss = { viewModel.dismissSuccess() }
            )
        }
        
        uiState.error?.let { error ->
            ErrorDialog(
                message = error,
                onDismiss = { viewModel.dismissError() }
            )
        }
    }
}

// Settings Section Component
@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

// Preference Item Components
@Composable
fun ThemePreference(
    currentTheme: ThemeMode,
    onClick: () -> Unit
) {
    PreferenceItem(
        title = "Theme",
        subtitle = currentTheme.displayName,
        icon = Icons.Default.Palette,
        onClick = onClick
    )
}

@Composable
fun DynamicColorPreference(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    AnimatedSettingToggle(
        title = "Dynamic Colors",
        subtitle = if (enabled) "Match system colors" else "Use default theme",
        checked = enabled,
        onCheckedChange = onToggle
    )
}

@Composable
fun DefaultLengthPreference(
    currentLength: Float,
    onClick: () -> Unit
) {
    val lengthText = when {
        currentLength <= 0.3f -> "Short"
        currentLength <= 0.7f -> "Medium"
        else -> "Long"
    }
    
    PreferenceItem(
        title = "Default Summary Length",
        subtitle = lengthText,
        icon = Icons.Default.TextFields,
        onClick = onClick
    )
}

@Composable
fun LanguagePreference(
    currentLanguage: String,
    onClick: () -> Unit
) {
    val languageDisplay = when (currentLanguage) {
        "en" -> "English"
        "vi" -> "Tiếng Việt"
        "es" -> "Español"
        "fr" -> "Français"
        else -> currentLanguage
    }
    
    PreferenceItem(
        title = "Language",
        subtitle = languageDisplay,
        icon = Icons.Default.Language,
        onClick = onClick
    )
}

@Composable
fun StorageUsageItem(
    storageUsage: Long
) {
    val usageText = "${storageUsage / 1024} KB used"
    
    PreferenceItem(
        title = "Storage Usage",
        subtitle = usageText,
        icon = Icons.Default.Storage
    )
}

@Composable
fun ClearHistoryPreference(
    onClick: () -> Unit
) {
    PreferenceItem(
        title = "Clear All Data",
        subtitle = "Remove all summaries and settings",
        icon = Icons.Default.Delete,
        onClick = onClick
    )
}

@Composable
fun ExportDataItem(
    onClick: () -> Unit
) {
    PreferenceItem(
        title = "Export Data",
        subtitle = "Save your settings and data",
        icon = Icons.Default.Download,
        onClick = onClick
    )
}

@Composable
fun VersionInfo(
    version: String
) {
    PreferenceItem(
        title = "Version",
        subtitle = version,
        icon = Icons.Default.Info
    )
}

@Composable
fun SendFeedbackItem() {
    PreferenceItem(
        title = "Send Feedback",
        subtitle = "Help us improve SumUp",
        icon = Icons.Default.Feedback,
        onClick = {
            // In real app, would open email or feedback form
        }
    )
}

// Base Preference Item
@Composable
fun PreferenceItem(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }
    
    Row(
        modifier = clickableModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Dialog Components
@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Theme") },
        text = {
            Column {
                ThemeMode.values().forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = { onThemeSelected(theme) }
                        )
                        Text(
                            text = theme.displayName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun LengthSelectionDialog(
    currentLength: Float,
    onLengthSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val lengths = listOf(
        0.3f to "Short",
        0.5f to "Medium", 
        0.8f to "Long"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Summary Length") },
        text = {
            Column {
                lengths.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLengthSelected(value) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = kotlin.math.abs(value - currentLength) < 0.1f,
                            onClick = { onLengthSelected(value) }
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "vi" to "Tiếng Việt",
        "es" to "Español",
        "fr" to "Français"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Language") },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = code == currentLanguage,
                            onClick = { onLanguageSelected(code) }
                        )
                        Text(
                            text = name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ClearDataConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isClearing: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear All Data?") },
        text = {
            Text("This will permanently delete all your summaries and reset settings to defaults. This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isClearing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isClearing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Clear All")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isClearing
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
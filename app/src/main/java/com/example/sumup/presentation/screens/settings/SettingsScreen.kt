package com.example.sumup.presentation.screens.settings

import androidx.compose.animation.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.components.*
import com.example.sumup.presentation.screens.settings.components.*
import com.example.sumup.domain.model.Achievement
import com.example.sumup.domain.model.AchievementType
import com.example.sumup.utils.haptic.*
import com.example.sumup.presentation.components.EnhancedLoadingState
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    adaptiveInfo: com.example.sumup.presentation.utils.AdaptiveLayoutInfo? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    
    // Dialog states
    var showExportImportDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }
    var showRotationRemindersDialog by remember { mutableStateOf(false) }
    var exportImportMode by remember { mutableStateOf(ExportImportMode.EXPORT) }
    
    // Calculate scroll offset for parallax effect
    val scrollOffset = listState.firstVisibleItemScrollOffset.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                    )
                )
            )
    ) {
        // Background effects
        AnimatedBackgroundEffect(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.3f }
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                ModernSettingsTopBar(
                    onNavigateBack = onNavigateBack,
                    scrollOffset = scrollOffset,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    userName = uiState.userEmail?.substringBefore("@") ?: "User"
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
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
            // Profile Header
            item {
                ProfileHeaderSection(
                    userProfile = UserProfile(
                        name = "SumUp User",
                        email = uiState.userEmail ?: "",
                        totalSummaries = uiState.totalSummaries,
                        totalTimeSaved = uiState.totalTimeSaved
                    ),
                    onEditProfile = { viewModel.showEditProfileDialog() },
                    scrollOffset = scrollOffset,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Appearance Section
            item {
                AnimatedSettingsItem(content = {
                    AnimatedPreferenceCard(
                        title = "Appearance",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                    EnhancedSettingItem(
                        title = "Theme",
                        subtitle = uiState.themeMode.displayName,
                        icon = Icons.Default.Palette,
                        onClick = { viewModel.showThemeDialog() }
                    )
                    
                    AnimatedToggleSettingItem(
                        title = "Dynamic Colors",
                        subtitle = if (uiState.isDynamicColorEnabled) "Match system colors" else "Use default theme",
                        icon = Icons.Default.ColorLens,
                        checked = uiState.isDynamicColorEnabled,
                        onCheckedChange = { viewModel.setDynamicColorEnabled(it) }
                    )
                    }
                }, index = 0)
            }
            
            // API Configuration Section
            item {
                AnimatedSettingsItem(content = {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // API Key Management
                        ApiKeyManagementCard(
                            apiKeys = uiState.apiKeys,
                            activeKeyId = uiState.activeApiKeyId,
                            rotationReminders = uiState.keyRotationReminders,
                            onAddKey = viewModel::addApiKey,
                            onDeleteKey = viewModel::deleteApiKey,
                            onSetActiveKey = viewModel::setActiveApiKey,
                            onExport = { /* Handle export - needs password dialog */ },
                            onImport = { /* TODO: Implement file picker */ }
                        )
                        
                        // Show rotation warning badge if needed
                        if (uiState.showRotationWarning) {
                            Card(
                                onClick = { showRotationRemindersDialog = true },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Column {
                                            Text(
                                                text = "API Keys Need Rotation",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${uiState.keyRotationReminders.count { it.isOverdue }} keys are overdue",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        
                        // Usage Analytics Dashboard
                        uiState.apiUsageStats?.let { stats ->
                            ApiUsageDashboard(
                                usageStats = stats,
                                onRefresh = viewModel::refreshUsageStats
                            )
                        }
                    }
                }, index = 1)
            }
            
            // Summarization Section
            item {
                AnimatedSettingsItem(content = {
                    AnimatedPreferenceCard(
                    title = "Summarization",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    EnhancedSettingItem(
                        title = "Default Summary Length",
                        subtitle = when {
                            uiState.summaryLength <= 0.3f -> "Short"
                            uiState.summaryLength <= 0.7f -> "Medium"
                            else -> "Long"
                        },
                        icon = Icons.Default.TextFields,
                        onClick = { viewModel.showLengthDialog() }
                    )
                    
                    EnhancedSettingItem(
                        title = "Language",
                        subtitle = when (uiState.language) {
                            "en" -> "English"
                            "vi" -> "Tiếng Việt"
                            "es" -> "Español"
                            "fr" -> "Français"
                            else -> uiState.language
                        },
                        icon = Icons.Default.Language,
                        onClick = { viewModel.showLanguageDialog() }
                    )
                    }
                }, index = 2)
            }
            
            // Data & Storage Section
            item {
                AnimatedSettingsItem(content = {
                    AnimatedPreferenceCard(
                        title = "Data & Storage",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                    EnhancedSettingItem(
                        title = "Storage Usage",
                        subtitle = "${uiState.storageUsage / 1024} KB used",
                        icon = Icons.Default.Storage,
                        onClick = null
                    )
                    
                    EnhancedSettingItem(
                        title = "Clear All Data",
                        subtitle = "Remove all summaries and settings",
                        icon = Icons.Default.Delete,
                        onClick = { viewModel.showClearDataDialog() }
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            onClick = {
                                exportImportMode = ExportImportMode.EXPORT
                                showExportImportDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Upload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Export",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Save settings",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Card(
                            onClick = {
                                exportImportMode = ExportImportMode.IMPORT
                                showExportImportDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Import",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Restore settings",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    }
                }, index = 3)
            }
            
            // About Section
            item {
                AnimatedSettingsItem(content = {
                    AnimatedPreferenceCard(
                        title = "About",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                    EnhancedSettingItem(
                        title = "Version",
                        subtitle = uiState.appVersion,
                        icon = Icons.Default.Info,
                        onClick = null
                    )
                    
                    EnhancedSettingItem(
                        title = "Send Feedback",
                        subtitle = "Help us improve SumUp",
                        icon = Icons.Default.Feedback,
                        onClick = { /* TODO: Implement feedback */ }
                    )
                    
                    EnhancedSettingItem(
                        title = "Privacy Policy",
                        subtitle = "Learn how we protect your data",
                        icon = Icons.Default.Security,
                        onClick = { /* TODO: Open privacy policy */ }
                    )
                    }
                }, index = 4)
            }
            
            // Achievements Section
            item {
                AnimatedSettingsItem(content = {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        SettingsAchievementsCard(
                            totalPoints = uiState.achievementPoints,
                            unlockedCount = uiState.unlockedAchievements,
                            totalCount = uiState.totalAchievements,
                            onClick = { showAchievementsDialog = true }
                        )
                    }
                }, index = 5)
            }
            
            // Additional visual effects
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    AnimatedWaveEffect(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
                }
            }
        )
        }
        
        // Edit Profile Dialog
        EditProfileDialog(
            visible = uiState.showEditProfileDialog,
            currentProfile = UserProfile(
                name = uiState.userEmail?.substringBefore("@") ?: "User",
                email = uiState.userEmail ?: "",
                totalSummaries = uiState.totalSummaries,
                totalTimeSaved = uiState.totalTimeSaved
            ),
            onDismiss = { viewModel.hideEditProfileDialog() },
            onSave = { profile ->
                // TODO: Save profile changes
                viewModel.hideEditProfileDialog()
            }
        )
        
        // Enhanced Dialogs
        AnimatedSelectionDialog(
            visible = uiState.showThemeDialog,
            onDismiss = { viewModel.hideThemeDialog() },
            title = "Choose Theme",
            options = listOf(
                ThemeMode.SYSTEM.name to "System",
                ThemeMode.LIGHT.name to "Light",
                ThemeMode.DARK.name to "Dark"
            ),
            selectedOption = uiState.themeMode.name,
            onOptionSelected = { selectedMode ->
                viewModel.setThemeMode(ThemeMode.valueOf(selectedMode))
            }
        )
        
        AnimatedSelectionDialog(
            visible = uiState.showLengthDialog,
            onDismiss = { viewModel.hideLengthDialog() },
            title = "Summary Length",
            options = listOf(
                "0.3" to "Short",
                "0.5" to "Medium",
                "0.8" to "Long"
            ),
            selectedOption = when {
                uiState.summaryLength <= 0.3f -> "0.3"
                uiState.summaryLength <= 0.7f -> "0.5"
                else -> "0.8"
            },
            onOptionSelected = { viewModel.setSummaryLength(it.toFloat()) }
        )
        
        AnimatedSelectionDialog(
            visible = uiState.showLanguageDialog,
            onDismiss = { viewModel.hideLanguageDialog() },
            title = "Choose Language",
            options = listOf(
                "en" to "English",
                "vi" to "Tiếng Việt",
                "es" to "Español",
                "fr" to "Français"
            ),
            selectedOption = uiState.language,
            onOptionSelected = { viewModel.setLanguage(it) }
        )
        
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
            AlertDialog(
                onDismissRequest = { viewModel.dismissError() },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissError() }) {
                        Text("OK")
                    }
                }
            )
        }
        
        // Export/Import Dialog with Password
        ExportImportPasswordDialog(
            visible = showExportImportDialog,
            mode = exportImportMode,
            onDismiss = { 
                showExportImportDialog = false
                viewModel.clearExportData()
            },
            onExport = { password ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.exportApiKeys(password)
            },
            onImport = { fileContent, password ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.importApiKeys(fileContent, password)
            },
            exportData = uiState.exportData,
            isProcessing = uiState.isExporting || uiState.isImporting,
            error = uiState.exportImportError
        )
        
        // Achievements Dialog
        AchievementsDialog(
            visible = showAchievementsDialog,
            achievements = uiState.achievements,
            onDismiss = { showAchievementsDialog = false }
        )
        
        // Key Rotation Reminder Dialog
        KeyRotationReminderDialog(
            visible = showRotationRemindersDialog,
            reminders = uiState.keyRotationReminders,
            onDismiss = { showRotationRemindersDialog = false },
            onRotateKey = { keyId ->
                showRotationRemindersDialog = false
                // TODO: Navigate to key rotation flow
                viewModel.showApiKeyDialog()
            },
            onSnoozeReminder = { keyId, days ->
                viewModel.snoozeRotationReminder(keyId, days)
            }
        )
        
        // API Key Dialog
        ApiKeyDialog(
            visible = uiState.showApiKeyDialog,
            currentKey = uiState.apiKeyInput,
            isValidating = uiState.isValidatingApiKey,
            errorMessage = uiState.apiKeyError,
            hasExistingKey = uiState.hasValidApiKey,
            onKeyChange = { viewModel.updateApiKeyInput(it) },
            onValidate = { viewModel.validateApiKey() },
            onClear = { viewModel.clearApiKey() },
            onDismiss = { viewModel.hideApiKeyDialog() }
        )
        
        // API Key Validation Success
        if (uiState.apiKeyValidationSuccess) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissApiKeySuccess() },
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("API Key Validated") },
                text = { Text("Your Gemini API key has been successfully validated and saved.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissApiKeySuccess() }) {
                        Text("OK")
                    }
                }
            )
        }
        
        // Floating bubbles effect
        FloatingBubbles(
            bubbleCount = 5,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.2f }
        )
    }
}

// Enhanced Clear Data Dialog

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyDialog(
    visible: Boolean,
    currentKey: String,
    isValidating: Boolean,
    errorMessage: String?,
    hasExistingKey: Boolean,
    onKeyChange: (String) -> Unit,
    onValidate: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visible) {
        var showKey by remember { mutableStateOf(false) }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    Icons.Outlined.VpnKey,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Gemini API Key") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Enter your Gemini API key to enable AI-powered summarization.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = currentKey,
                        onValueChange = onKeyChange,
                        label = { Text("API Key") },
                        placeholder = { Text("Enter your Gemini API key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showKey) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { showKey = !showKey }) {
                                Icon(
                                    imageVector = if (showKey) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (showKey) "Hide key" else "Show key"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onValidate() }
                        ),
                        isError = errorMessage != null,
                        supportingText = errorMessage?.let { { Text(it) } },
                        enabled = !isValidating
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val context = LocalContext.current
                        AssistChip(
                            onClick = { 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://makersuite.google.com/app/apikey"))
                                context.startActivity(intent)
                            },
                            label = { Text("Get API Key") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        
                        if (hasExistingKey) {
                            AssistChip(
                                onClick = onClear,
                                label = { Text("Clear Key") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onValidate,
                    enabled = currentKey.isNotBlank() && !isValidating
                ) {
                    if (isValidating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Validate")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isValidating
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Preview Composables
@ThemePreview
@Composable
fun SettingsScreenPreview() {
    PreviewWrapper {
        SettingsScreen(
            onNavigateBack = {},
        )
    }
}

@Preview(name = "Settings - Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenDarkPreview() {
    PreviewWrapper(darkTheme = true) {
        SettingsScreen(
            onNavigateBack = {},
        )
    }
}

@AllDevicePreview
@Composable
fun SettingsScreenDevicePreview() {
    PreviewWrapper {
        SettingsScreen(
            onNavigateBack = {},
        )
    }
}
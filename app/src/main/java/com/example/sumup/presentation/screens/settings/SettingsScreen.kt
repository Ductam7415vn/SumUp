package com.example.sumup.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val isDynamicColorEnabled by viewModel.isDynamicColorEnabled.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showThemeDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    SettingItem(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "Theme",
                        subtitle = currentTheme.displayName,
                        onClick = { showThemeDialog = true }
                    )
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        SettingItem(
                            icon = Icons.Outlined.Star,
                            title = "Dynamic Colors",
                            subtitle = "Use Material You colors from wallpaper",
                            showToggle = true,
                            checked = isDynamicColorEnabled,
                            onCheckedChange = viewModel::setDynamicColorEnabled
                        )
                    }
                }
            }
            
            // Summarization Section
            item {
                SettingsSection(title = "Summarization") {
                    SettingItem(
                        icon = Icons.Outlined.Settings,
                        title = "Default Summary Length",
                        subtitle = "Standard",
                        onClick = { /* TODO: Add length selection */ }
                    )
                    
                    SettingItem(
                        icon = Icons.Outlined.Person,
                        title = "Language",
                        subtitle = "English",
                        onClick = { /* TODO: Add language selection */ }
                    )
                }
            }
            
            // Data & Storage Section
            item {
                SettingsSection(title = "Data & Storage") {
                    SettingItem(
                        icon = Icons.Outlined.Info,
                        title = "Storage Used",
                        subtitle = "${uiState.summaryCount} summaries • ${uiState.storageUsed}",
                        onClick = {}
                    )
                    
                    SettingItem(
                        icon = Icons.Outlined.Delete,
                        title = "Clear All Data",
                        subtitle = "Delete all summaries and reset settings",
                        onClick = viewModel::showClearDataDialog,
                        tintColor = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingItem(
                        icon = Icons.Outlined.Info,
                        title = "Version",
                        subtitle = "1.0.0 (Build 1)",
                        onClick = {}
                    )
                    
                    SettingItem(
                        icon = Icons.Outlined.Email,
                        title = "Send Feedback",
                        subtitle = "Report issues or suggest features",
                        onClick = {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:feedback@sumup.app")
                                putExtra(Intent.EXTRA_SUBJECT, "SumUp Feedback")
                            }
                            context.startActivity(emailIntent)
                        }
                    )
                    
                    SettingItem(
                        icon = Icons.Outlined.Lock,
                        title = "Privacy Policy",
                        subtitle = "How we protect your data",
                        onClick = {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://sumup.app/privacy")
                            )
                            context.startActivity(browserIntent)
                        }
                    )
                    
                    SettingItem(
                        icon = Icons.Outlined.Article,
                        title = "Terms of Service",
                        subtitle = "Terms and conditions",
                        onClick = {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://sumup.app/terms")
                            )
                            context.startActivity(browserIntent)
                        }
                    )
                }
            }
        }
        
        // Theme selection dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    viewModel.setThemeMode(theme)
                    showThemeDialog = false
                },
                onDismiss = { showThemeDialog = false }
            )
        }
        
        // Clear data confirmation dialog
        if (uiState.showClearDataDialog) {
            AlertDialog(
                onDismissRequest = viewModel::dismissClearDataDialog,
                title = { Text("Clear All Data?") },
                text = { 
                    Text("This will delete all ${uiState.summaryCount} summaries and reset all settings to defaults. This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = viewModel::clearAllData,
                        enabled = !uiState.isClearing,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (uiState.isClearing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Clear All")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = viewModel::dismissClearDataDialog,
                        enabled = !uiState.isClearing
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Success/Error handling
        LaunchedEffect(uiState.clearDataSuccess) {
            if (uiState.clearDataSuccess) {
                snackbarHostState.showSnackbar("All data cleared successfully")
                viewModel.dismissSuccess()
            }
        }
        
        LaunchedEffect(uiState.error) {
            uiState.error?.let { error ->
                snackbarHostState.showSnackbar(error)
                viewModel.dismissError()
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    showToggle: Boolean = false,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    tintColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !showToggle) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (showToggle) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

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
                ThemeMode.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = { onThemeSelected(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
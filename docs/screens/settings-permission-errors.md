# Settings Screen - Technical Specification

## 🎯 Overview
Keep it simple. Users change theme (30%), clear data when app misbehaves (5%), nothing else (65% never open settings).

## 📱 Layout & Implementation
```kotlin
@Composable
fun SettingsScreen() {
    LazyColumn {
        item {
            SettingsSection("Appearance") {
                ThemePreference()
                DynamicColorPreference()
            }
        }
        
        item {
            SettingsSection("Summarization") {
                DefaultLengthPreference()
                LanguagePreference()
            }
        }
        
        item {
            SettingsSection("Data & Storage") {
                StorageUsageItem()
                ClearHistoryPreference()
                ExportDataItem()
            }
        }
        
        item {
            SettingsSection("About") {
                VersionInfo()
                SendFeedbackItem()
            }
        }
    }
}

@Composable
fun ThemePreference() {
    var showDialog by remember { mutableStateOf(false) }
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    
    PreferenceItem(
        title = "Theme",
        subtitle = currentTheme.displayName,
        onClick = { showDialog = true }
    )
    
    if (showDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onThemeSelected = { theme ->
                themeViewModel.setTheme(theme)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}
```

## ⚠️ MVP Settings
- Theme (system/light/dark)
- Clear history with confirmation
- Version number
- Feedback link

**Skip**: Font size, advanced options, complex privacy controls
---

# Permission Screen - Technical Specification

## 🎯 Overview
Friendly pre-permission flow. 65% grant rate with good copy vs 40% with scary technical language.

## 🛠️ Implementation
```kotlin
@Composable
fun PermissionRequestScreen(onResult: (Boolean) -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onResult(granted)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📸",
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Scan Text Instantly",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Point your camera at any document to import text in seconds",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable Camera")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { onResult(false) }) {
            Text("Not Now")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "ℹ️ You can always type or paste text manually",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
```

---

# Error & Empty States - Technical Specification

## 🎯 Overview
Clear, actionable, friendly. No technical jargon.

## 🛠️ Implementation
```kotlin
sealed class AppError {
    object NetworkError : AppError()
    object ApiRateLimit : AppError()
    object TextTooShort : AppError()
    object OCRFailed : AppError()
    object StorageFull : AppError()
}

@Composable
fun ErrorHandler(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val (icon, title, message, primaryAction) = when (error) {
        AppError.NetworkError -> ErrorContent(
            icon = "📵",
            title = "No Internet Connection",
            message = "Check your connection and try again",
            primaryAction = "Try Again" to onRetry
        )
        AppError.ApiRateLimit -> ErrorContent(
            icon = "⏰",
            title = "Daily Limit Reached",
            message = "You've made 50 summaries today! Resets in 4 hours.",
            primaryAction = "Remind Me" to null
        )
        // ... other errors
    }
    
    ErrorDialog(
        icon = icon,
        title = title,
        message = message,
        primaryAction = primaryAction,
        onDismiss = onDismiss
    )
}

@Composable
fun EmptyStateScreen(type: EmptyStateType) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val (icon, title, subtitle, cta) = when (type) {
            EmptyStateType.FirstLaunch -> EmptyStateContent(
                icon = "📝",
                title = "Ready to save time?",
                subtitle = "Paste any text or scan a document to get started",
                cta = "Try Sample Text"
            )
            EmptyStateType.HistoryEmpty -> EmptyStateContent(
                icon = "📋",
                title = "No Summaries Yet",
                subtitle = "Your summarized texts will appear here",
                cta = "Create First Summary"
            )
        }
        
        AnimatedEmptyStateIcon(icon)
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { /* CTA action */ }) {
            Text(cta)
        }
    }
}
```

---

**Reality Check**: Users spend 2 seconds on error screens. Make the problem and solution instantly clear. One good CTA beats three confusing options.

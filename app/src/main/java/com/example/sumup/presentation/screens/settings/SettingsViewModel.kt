package com.example.sumup.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.repository.SettingsRepository
import com.example.sumup.domain.repository.SummaryRepository
import com.example.sumup.domain.model.Achievement
import com.example.sumup.domain.model.AchievementType
import com.example.sumup.domain.model.AchievementTier
import com.example.sumup.domain.model.SummaryViewPreference
// Old ApiKeyManager removed - using EnhancedApiKeyManager only
import com.example.sumup.utils.ApiKeyValidator
import com.example.sumup.utils.EnhancedApiKeyManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val summaryRepository: SummaryRepository,
    private val enhancedApiKeyManager: EnhancedApiKeyManager,
    private val apiKeyValidator: ApiKeyValidator,
    private val apiUsageTracker: com.example.sumup.utils.ApiUsageTracker
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadUserStats()
        loadAchievements()
        loadApiKeyStatus()
        observeApiKeys()
        checkRotationReminders()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.getThemeMode(),
                settingsRepository.isDynamicColorEnabled(),
                settingsRepository.summaryViewMode,
                settingsRepository.language
            ) { theme: ThemeMode, dynamicColor: Boolean, viewMode: String, language: String ->
                _uiState.update {
                    it.copy(
                        themeMode = theme,
                        isDynamicColorEnabled = dynamicColor,
                        summaryViewPreference = SummaryViewPreference.fromString(viewMode),
                        language = language
                    )
                }
            }.collect()
            
            // Load storage info
            val summaryCount = summaryRepository.getSummaryCount()
            val storageUsage = summaryCount * 1024L // Rough estimate
            
            _uiState.update {
                it.copy(
                    appVersion = settingsRepository.getAppVersion()
                )
            }
        }
    }
    
    private fun loadUserStats() {
        viewModelScope.launch {
            summaryRepository.getAllSummaries().collect { summaries ->
                val totalSummaries = summaries.size
                val totalTimeSaved = summaries.sumOf { summary ->
                    summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime
                }
                
                _uiState.update {
                    it.copy(
                        totalSummaries = totalSummaries,
                        totalTimeSaved = totalTimeSaved
                    )
                }
            }
        }
    }
    
    // Theme management
    fun showThemeDialog() {
        _uiState.update { it.copy(showThemeDialog = true) }
    }
    
    fun hideThemeDialog() {
        _uiState.update { it.copy(showThemeDialog = false) }
    }
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
            _uiState.update { it.copy(showThemeDialog = false) }
        }
    }
    
    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorEnabled(enabled)
            // No need to update UI state - it will flow through the combine
        }
    }
    
    // Summary view preference management
    fun showSummaryViewDialog() {
        _uiState.update { it.copy(showSummaryViewDialog = true) }
    }
    
    fun hideSummaryViewDialog() {
        _uiState.update { it.copy(showSummaryViewDialog = false) }
    }
    
    fun setSummaryViewPreference(preference: SummaryViewPreference) {
        viewModelScope.launch {
            settingsRepository.updateSummaryViewMode(preference.name)
            _uiState.update { it.copy(showSummaryViewDialog = false) }
        }
    }
    
    // Language management
    fun showLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = true) }
    }
    
    fun hideLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = false) }
    }
    
    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
            _uiState.update { it.copy(showLanguageDialog = false) }
        }
    }
    
    // History management
    fun showClearHistoryDialog() {
        viewModelScope.launch {
            // Get current summary count before showing dialog
            val count = summaryRepository.getSummaryCount()
            _uiState.update { 
                it.copy(
                    showClearHistoryDialog = true,
                    summaryCountToDelete = count
                )
            }
        }
    }
    
    fun hideClearHistoryDialog() {
        _uiState.update { it.copy(showClearHistoryDialog = false) }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true) }
            
            try {
                // Clear only summaries, keep settings intact
                summaryRepository.deleteAllSummaries()
                
                // Reload stats after clearing
                loadUserStats()
                
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        showClearHistoryDialog = false,
                        clearHistorySuccess = true,
                        totalSummaries = 0,
                        totalTimeSaved = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        error = "Failed to clear history: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun exportData() {
        viewModelScope.launch {
            try {
                val exportData = settingsRepository.exportData()
                // TODO: Handle exported data (save to file, share, etc.)
                _uiState.update { it.copy(exportSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Export failed: ${e.message}") }
            }
        }
    }
    
    fun dismissSuccess() {
        _uiState.update { it.copy(clearHistorySuccess = false) }
    }
    
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
    
    // Profile management
    fun showEditProfileDialog() {
        _uiState.update { it.copy(showEditProfileDialog = true) }
    }
    
    fun hideEditProfileDialog() {
        _uiState.update { it.copy(showEditProfileDialog = false) }
    }
    
    // Achievement management
    private fun loadAchievements() {
        viewModelScope.launch {
            // Mock achievements for now
            val mockAchievements = listOf(
                Achievement(
                    id = "first_summary",
                    type = AchievementType.FIRST_SUMMARY,
                    title = "First Summary",
                    description = "Create your first summary",
                    tier = AchievementTier.BRONZE,
                    requirement = 1,
                    currentProgress = 1,
                    isUnlocked = true,
                    icon = Icons.Default.Description,
                    color = Color(0xFF4CAF50)
                ),
                Achievement(
                    id = "summary_streak",
                    type = AchievementType.STREAK_KEEPER,
                    title = "3-Day Streak",
                    description = "Use SumUp for 3 consecutive days",
                    tier = AchievementTier.SILVER,
                    requirement = 3,
                    currentProgress = 2,
                    isUnlocked = false,
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFF2196F3)
                ),
                Achievement(
                    id = "time_saved",
                    type = AchievementType.TIME_SAVER,
                    title = "Time Saver",
                    description = "Save 60 minutes of reading time",
                    tier = AchievementTier.GOLD,
                    requirement = 60,
                    currentProgress = 45,
                    isUnlocked = false,
                    icon = Icons.Default.Timer,
                    color = Color(0xFFFFC107)
                )
            )
            
            val unlockedCount = mockAchievements.count { it.isUnlocked }
            val totalPoints = mockAchievements.filter { it.isUnlocked }
                .sumOf { 10 * it.tier.multiplier }
            
            _uiState.update {
                it.copy(
                    achievements = mockAchievements,
                    unlockedAchievements = unlockedCount,
                    totalAchievements = mockAchievements.size,
                    achievementPoints = totalPoints
                )
            }
        }
    }
    
    // Export/Import management
    fun exportSettings() {
        viewModelScope.launch {
            try {
                val exportData = settingsRepository.exportData()
                // TODO: Save to file or share
                _uiState.update { it.copy(exportSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Export failed: ${e.message}") }
            }
        }
    }
    
    fun importSettings() {
        viewModelScope.launch {
            try {
                // TODO: Implement file picker and import logic
                _uiState.update { it.copy(importSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Import failed: ${e.message}") }
            }
        }
    }
    
    // API Key management
    private fun loadApiKeyStatus() {
        viewModelScope.launch {
            // Check from EnhancedApiKeyManager
            val activeKey = enhancedApiKeyManager.getActiveApiKey()
            val hasValidKey = activeKey != null
            
            _uiState.update {
                it.copy(
                    hasValidApiKey = hasValidKey,
                    currentApiKey = activeKey ?: ""
                )
            }
        }
    }
    
    fun showApiKeyDialog() {
        _uiState.update { it.copy(showApiKeyDialog = true) }
    }
    
    fun hideApiKeyDialog() {
        _uiState.update { it.copy(showApiKeyDialog = false) }
    }
    
    fun updateApiKeyInput(key: String) {
        _uiState.update { it.copy(apiKeyInput = key) }
    }
    
    fun validateApiKey() {
        viewModelScope.launch {
            _uiState.update { it.copy(isValidatingApiKey = true, apiKeyError = null) }
            
            val result = apiKeyValidator.validateApiKey(_uiState.value.apiKeyInput)
            
            if (result.isValid) {
                // Add to EnhancedApiKeyManager with a default name
                val keyName = "API Key ${_uiState.value.apiKeys.size + 1}"
                enhancedApiKeyManager.addApiKey(keyName, _uiState.value.apiKeyInput)
                
                _uiState.update {
                    it.copy(
                        isValidatingApiKey = false,
                        apiKeyValidationSuccess = true,
                        hasValidApiKey = true,
                        currentApiKey = _uiState.value.apiKeyInput,
                        showApiKeyDialog = false,
                        apiKeyError = null,
                        apiKeyInput = "" // Clear input
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isValidatingApiKey = false,
                        apiKeyError = result.errorMessage
                    )
                }
            }
        }
    }
    
    fun clearApiKey() {
        viewModelScope.launch {
            // Clear the active key in EnhancedApiKeyManager
            val activeKeyId = enhancedApiKeyManager.activeKeyId.value
            if (activeKeyId != null) {
                enhancedApiKeyManager.deleteApiKey(activeKeyId)
            }
            _uiState.update {
                it.copy(
                    hasValidApiKey = false,
                    currentApiKey = "",
                    apiKeyInput = "",
                    showApiKeyDialog = false
                )
            }
        }
    }
    
    fun dismissApiKeySuccess() {
        _uiState.update { it.copy(apiKeyValidationSuccess = false) }
    }
    
    // Enhanced API Key Management
    private fun observeApiKeys() {
        viewModelScope.launch {
            combine(
                enhancedApiKeyManager.apiKeys,
                enhancedApiKeyManager.activeKeyId
            ) { keys, activeId ->
                android.util.Log.d("SettingsViewModel", "API keys updated: ${keys.size} keys, active: $activeId")
                
                // Get stats from both sources
                val enhancedStats = enhancedApiKeyManager.getUsageStats()
                val simpleTotal = apiUsageTracker.getTotalUsage()
                val simpleToday = apiUsageTracker.getTodayUsage()
                
                // Use the maximum value from both sources
                val correctedStats = enhancedStats.copy(
                    requestsToday = maxOf(enhancedStats.requestsToday, simpleToday),
                    requestsThisMonth = maxOf(enhancedStats.requestsThisMonth, simpleTotal)
                )
                
                _uiState.update {
                    it.copy(
                        apiKeys = keys,
                        activeApiKeyId = activeId,
                        apiUsageStats = correctedStats
                    )
                }
            }.collect()
        }
    }
    
    private fun checkRotationReminders() {
        viewModelScope.launch {
            if (enhancedApiKeyManager.shouldShowRotationReminder()) {
                val reminders = enhancedApiKeyManager.getRotationReminders()
                _uiState.update {
                    it.copy(
                        keyRotationReminders = reminders,
                        showRotationWarning = reminders.any { it.isOverdue }
                    )
                }
            }
        }
    }
    
    fun addApiKey(name: String, key: String) {
        viewModelScope.launch {
            android.util.Log.d("SettingsViewModel", "Adding API key: name=$name")
            _uiState.update { it.copy(isValidatingApiKey = true, apiKeyError = null) }
            
            // Validate the key first
            val result = apiKeyValidator.validateApiKey(key)
            android.util.Log.d("SettingsViewModel", "Validation result: ${result.isValid}, error: ${result.errorMessage}")
            
            if (result.isValid) {
                val newKey = enhancedApiKeyManager.addApiKey(name, key)
                android.util.Log.d("SettingsViewModel", "API key added successfully: ${newKey.id}")
                _uiState.update {
                    it.copy(
                        isValidatingApiKey = false,
                        apiKeyValidationSuccess = true,
                        showApiKeyDialog = false,
                        apiKeyError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isValidatingApiKey = false,
                        apiKeyError = result.errorMessage
                    )
                }
            }
        }
    }
    
    fun deleteApiKey(keyId: String) {
        viewModelScope.launch {
            enhancedApiKeyManager.deleteApiKey(keyId)
        }
    }
    
    fun setActiveApiKey(keyId: String) {
        viewModelScope.launch {
            enhancedApiKeyManager.setActiveKey(keyId)
        }
    }
    
    fun refreshUsageStats() {
        viewModelScope.launch {
            // Get stats from EnhancedApiKeyManager
            val enhancedStats = enhancedApiKeyManager.getUsageStats()
            
            // Get stats from simple ApiUsageTracker
            val simpleTotal = apiUsageTracker.getTotalUsage()
            val simpleToday = apiUsageTracker.getTodayUsage()
            
            android.util.Log.d("SettingsViewModel", "=== REFRESH USAGE STATS ===")
            android.util.Log.d("SettingsViewModel", "Enhanced stats - Today: ${enhancedStats.requestsToday}, Total: ${enhancedStats.requestsThisMonth}")
            android.util.Log.d("SettingsViewModel", "Simple tracker - Today: $simpleToday, Total: $simpleTotal")
            
            // Use the maximum value from both sources (in case one is working better than the other)
            val actualToday = maxOf(enhancedStats.requestsToday, simpleToday)
            val actualTotal = maxOf(enhancedStats.requestsThisMonth, simpleTotal)
            
            // Create updated stats with corrected values
            val correctedStats = enhancedStats.copy(
                requestsToday = actualToday,
                requestsThisMonth = actualTotal,
                tokenUsage = enhancedStats.tokenUsage.copy(
                    used = actualTotal * 2000, // Estimate tokens
                    percentage = minOf(100f, (actualTotal * 2000f / 1_000_000) * 100)
                )
            )
            
            _uiState.update { it.copy(apiUsageStats = correctedStats) }
        }
    }
    
    fun exportApiKeys(password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, exportImportError = null) }
            
            try {
                val exportData = enhancedApiKeyManager.exportSettings(password)
                val json = com.google.gson.Gson().toJson(exportData)
                
                // Save to file
                val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
                    .format(java.util.Date())
                val fileName = "sumup_settings_$timestamp.json"
                
                // In a real app, you would save to external storage or use Storage Access Framework
                // For now, we'll just update the state to show success
                _uiState.update { 
                    it.copy(
                        exportData = json,
                        exportSuccess = true,
                        isExporting = false,
                        exportFileName = fileName
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        exportImportError = "Export failed: ${e.message}",
                        isExporting = false
                    )
                }
            }
        }
    }
    
    fun importApiKeys(jsonData: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, exportImportError = null) }
            
            try {
                val gson = com.google.gson.Gson()
                val exportData = gson.fromJson(jsonData, com.example.sumup.domain.model.ExportData::class.java)
                
                val success = enhancedApiKeyManager.importSettings(exportData, password)
                
                if (success) {
                    _uiState.update { 
                        it.copy(
                            importSuccess = true,
                            isImporting = false,
                            showExportImportDialog = false
                        )
                    }
                    // Refresh API keys list
                    observeApiKeys()
                } else {
                    _uiState.update { 
                        it.copy(
                            exportImportError = "Invalid file or incorrect password",
                            isImporting = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        exportImportError = "Import failed: ${e.message}",
                        isImporting = false
                    )
                }
            }
        }
    }
    
    fun clearExportData() {
        _uiState.update { 
            it.copy(
                exportData = null,
                exportSuccess = false,
                importSuccess = false,
                exportImportError = null
            )
        }
    }
    
    fun snoozeRotationReminder(keyId: String, days: Int) {
        viewModelScope.launch {
            enhancedApiKeyManager.snoozeRotationReminder(keyId, days)
            checkRotationReminders() // Refresh reminders
        }
    }
}

data class SettingsUiState(
    // Profile
    val userEmail: String? = null,
    val totalSummaries: Int = 0,
    val totalTimeSaved: Int = 0, // in minutes
    val showEditProfileDialog: Boolean = false,
    
    // Theme
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicColorEnabled: Boolean = false,
    val showThemeDialog: Boolean = false,
    
    // Summarization
    val summaryViewPreference: SummaryViewPreference = SummaryViewPreference.STANDARD,
    val language: String = "en",
    val showSummaryViewDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    
    // History
    val showClearHistoryDialog: Boolean = false,
    val isClearing: Boolean = false,
    val clearHistorySuccess: Boolean = false,
    val summaryCountToDelete: Int = 0,
    
    // About
    val appVersion: String = "1.0.0",
    
    // Common
    val error: String? = null,
    
    // Achievements
    val achievements: List<Achievement> = emptyList(),
    val unlockedAchievements: Int = 0,
    val totalAchievements: Int = 0,
    val achievementPoints: Int = 0,
    
    // Export/Import
    val exportSuccess: Boolean = false,
    val importSuccess: Boolean = false,
    
    // API Key
    val hasValidApiKey: Boolean = false,
    val currentApiKey: String = "",
    val apiKeyInput: String = "",
    val showApiKeyDialog: Boolean = false,
    val isValidatingApiKey: Boolean = false,
    val apiKeyError: String? = null,
    val apiKeyValidationSuccess: Boolean = false,
    
    // Enhanced API Key Management
    val apiKeys: List<com.example.sumup.domain.model.ApiKeyInfo> = emptyList(),
    val activeApiKeyId: String? = null,
    val apiUsageStats: com.example.sumup.domain.model.ApiUsageStats? = null,
    val keyRotationReminders: List<com.example.sumup.domain.model.KeyRotationReminder> = emptyList(),
    val showRotationWarning: Boolean = false,
    val exportData: String? = null,
    val exportFileName: String? = null,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportImportError: String? = null,
    val showExportImportDialog: Boolean = false
)


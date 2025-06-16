package com.example.sumup.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.repository.SettingsRepository
import com.example.sumup.domain.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val summaryRepository: SummaryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.getThemeMode(),
                settingsRepository.isDynamicColorEnabled(),
                settingsRepository.defaultSummaryLength,
                settingsRepository.language
            ) { theme: ThemeMode, dynamicColor: Boolean, length: Float, language: String ->
                _uiState.update {
                    it.copy(
                        themeMode = theme,
                        isDynamicColorEnabled = dynamicColor,
                        summaryLength = length,
                        language = language
                    )
                }
            }.collect()
            
            // Load storage info
            val summaryCount = summaryRepository.getSummaryCount()
            val storageUsage = summaryCount * 1024L // Rough estimate
            
            _uiState.update {
                it.copy(
                    storageUsage = storageUsage,
                    appVersion = settingsRepository.getAppVersion()
                )
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
            _uiState.update { it.copy(themeMode = mode, showThemeDialog = false) }
        }
    }
    
    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorEnabled(enabled)
            _uiState.update { it.copy(isDynamicColorEnabled = enabled) }
        }
    }
    
    // Summary length management
    fun showLengthDialog() {
        _uiState.update { it.copy(showLengthDialog = true) }
    }
    
    fun hideLengthDialog() {
        _uiState.update { it.copy(showLengthDialog = false) }
    }
    
    fun setSummaryLength(length: Float) {
        viewModelScope.launch {
            settingsRepository.updateDefaultSummaryLength(length)
            _uiState.update { it.copy(summaryLength = length, showLengthDialog = false) }
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
            _uiState.update { it.copy(language = language, showLanguageDialog = false) }
        }
    }
    
    // Data management
    fun showClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = true) }
    }
    
    fun hideClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = false) }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true) }
            
            try {
                // Clear all summaries
                summaryRepository.deleteAllSummaries()
                
                // Clear all data including settings
                settingsRepository.clearAllData()
                
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        showClearDataDialog = false,
                        clearDataSuccess = true,
                        storageUsage = 0L
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        error = "Failed to clear data: ${e.message}"
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
                _uiState.update { it.copy(clearDataSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Export failed: ${e.message}") }
            }
        }
    }
    
    fun dismissSuccess() {
        _uiState.update { it.copy(clearDataSuccess = false) }
    }
    
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SettingsUiState(
    // Theme
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicColorEnabled: Boolean = false,
    val showThemeDialog: Boolean = false,
    
    // Summarization
    val summaryLength: Float = 0.5f, // Medium by default
    val language: String = "en",
    val showLengthDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    
    // Data & Storage
    val storageUsage: Long = 0L,
    val showClearDataDialog: Boolean = false,
    val isClearing: Boolean = false,
    val clearDataSuccess: Boolean = false,
    
    // About
    val appVersion: String = "1.0.0",
    
    // Common
    val error: String? = null
)


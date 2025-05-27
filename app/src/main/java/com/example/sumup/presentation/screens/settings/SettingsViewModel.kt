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
    
    val currentTheme = settingsRepository.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )
    
    val isDynamicColorEnabled = settingsRepository.isDynamicColorEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    init {
        loadStorageInfo()
    }
    
    private fun loadStorageInfo() {
        viewModelScope.launch {
            val summaryCount = summaryRepository.getSummaryCount()
            _uiState.update { 
                it.copy(
                    summaryCount = summaryCount,
                    storageUsed = calculateStorageUsed(summaryCount)
                )
            }
        }
    }
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }
    
    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorEnabled(enabled)
        }
    }
    
    fun showClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = true) }
    }
    
    fun dismissClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = false) }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true) }
            
            try {
                // Clear all summaries
                summaryRepository.deleteAllSummaries()
                
                // Reset settings to defaults
                settingsRepository.resetToDefaults()
                
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        showClearDataDialog = false,
                        clearDataSuccess = true,
                        summaryCount = 0,
                        storageUsed = "0 KB"
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
    
    fun dismissSuccess() {
        _uiState.update { it.copy(clearDataSuccess = false) }
    }
    
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
    
    private fun calculateStorageUsed(summaryCount: Int): String {
        // Rough estimate: 1KB per summary
        val kb = summaryCount
        return when {
            kb < 1024 -> "$kb KB"
            else -> "${kb / 1024} MB"
        }
    }
}

data class SettingsUiState(
    val summaryCount: Int = 0,
    val storageUsed: String = "0 KB",
    val showClearDataDialog: Boolean = false,
    val isClearing: Boolean = false,
    val clearDataSuccess: Boolean = false,
    val error: String? = null
)

enum class ThemeMode(val displayName: String) {
    SYSTEM("System default"),
    LIGHT("Light"),
    DARK("Dark")
}
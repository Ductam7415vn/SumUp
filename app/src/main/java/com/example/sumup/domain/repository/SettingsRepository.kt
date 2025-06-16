package com.example.sumup.domain.repository

import com.example.sumup.presentation.screens.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Basic settings flows
    val theme: Flow<String>
    val dynamicColors: Flow<Boolean>
    val defaultSummaryLength: Flow<Float>
    val language: Flow<String>
    val autoDeleteDays: Flow<Int>
    val isOnboardingCompleted: Flow<Boolean>
    
    // Theme and appearance
    fun getThemeMode(): Flow<ThemeMode>
    fun isDynamicColorEnabled(): Flow<Boolean>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColorEnabled(enabled: Boolean)
    
    // Basic settings
    suspend fun updateTheme(theme: String)
    suspend fun updateDynamicColors(enabled: Boolean)
    suspend fun updateDefaultSummaryLength(length: Float)
    suspend fun updateLanguage(language: String)
    suspend fun updateAutoDeleteDays(days: Int)
    
    // Storage and data
    suspend fun clearAllData()
    suspend fun exportData(): String
    fun getStorageUsage(): Flow<Long>
    
    // App info
    fun getAppVersion(): String
    
    // Reset functionality
    suspend fun resetToDefaults()
    
    // Onboarding
    suspend fun setOnboardingCompleted(completed: Boolean)
}
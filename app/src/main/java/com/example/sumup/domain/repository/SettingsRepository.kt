package com.example.sumup.domain.repository

import com.example.sumup.presentation.screens.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<String>
    val dynamicColors: Flow<Boolean>
    val defaultSummaryLength: Flow<Float>
    val language: Flow<String>
    val autoDeleteDays: Flow<Int>
    
    suspend fun updateTheme(theme: String)
    suspend fun updateDynamicColors(enabled: Boolean)
    suspend fun updateDefaultSummaryLength(length: Float)
    suspend fun updateLanguage(language: String)
    suspend fun updateAutoDeleteDays(days: Int)
    
    // Additional methods for settings screen
    fun getThemeMode(): Flow<ThemeMode>
    fun isDynamicColorEnabled(): Flow<Boolean>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColorEnabled(enabled: Boolean)
    suspend fun resetToDefaults()
}
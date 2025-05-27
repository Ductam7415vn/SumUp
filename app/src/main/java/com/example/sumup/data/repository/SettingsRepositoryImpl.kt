package com.example.sumup.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.sumup.domain.repository.SettingsRepository
import com.example.sumup.presentation.screens.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val DYNAMIC_COLORS_KEY = booleanPreferencesKey("dynamic_colors")
        val DEFAULT_SUMMARY_LENGTH_KEY = floatPreferencesKey("default_summary_length")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val AUTO_DELETE_DAYS_KEY = intPreferencesKey("auto_delete_days")
    }

    override val theme: Flow<String> = dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: "system" }

    override val dynamicColors: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[DYNAMIC_COLORS_KEY] ?: true }

    override val defaultSummaryLength: Flow<Float> = dataStore.data
        .map { preferences -> preferences[DEFAULT_SUMMARY_LENGTH_KEY] ?: 0.5f }

    override val language: Flow<String> = dataStore.data
        .map { preferences -> preferences[LANGUAGE_KEY] ?: "auto" }

    override val autoDeleteDays: Flow<Int> = dataStore.data
        .map { preferences -> preferences[AUTO_DELETE_DAYS_KEY] ?: 0 }
    override suspend fun updateTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    override suspend fun updateDynamicColors(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLORS_KEY] = enabled
        }
    }

    override suspend fun updateDefaultSummaryLength(length: Float) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_SUMMARY_LENGTH_KEY] = length
        }
    }

    override suspend fun updateLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    override suspend fun updateAutoDeleteDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[AUTO_DELETE_DAYS_KEY] = days
        }
    }
    
    override fun getThemeMode(): Flow<ThemeMode> = theme.map { themeString ->
        when (themeString) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }
    
    override fun isDynamicColorEnabled(): Flow<Boolean> = dynamicColors
    
    override suspend fun setThemeMode(mode: ThemeMode) {
        val themeString = when (mode) {
            ThemeMode.LIGHT -> "light"
            ThemeMode.DARK -> "dark"
            ThemeMode.SYSTEM -> "system"
        }
        updateTheme(themeString)
    }
    
    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        updateDynamicColors(enabled)
    }
    
    override suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
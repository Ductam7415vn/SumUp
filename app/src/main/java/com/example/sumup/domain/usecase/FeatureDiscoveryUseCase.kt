package com.example.sumup.domain.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for managing feature discovery states
 */
@Singleton
class FeatureDiscoveryUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SHOWN_TIPS_KEY = stringSetPreferencesKey("shown_tips")
        private val FEATURE_DISCOVERY_ENABLED = booleanPreferencesKey("feature_discovery_enabled")
    }
    
    /**
     * Check if a specific tip has been shown
     */
    suspend fun hasShownTip(tipId: String): Boolean {
        return dataStore.data.map { preferences ->
            val shownTips = preferences[SHOWN_TIPS_KEY] ?: emptySet()
            tipId in shownTips
        }.first()
    }
    
    /**
     * Get all shown tips
     */
    fun getShownTips(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[SHOWN_TIPS_KEY] ?: emptySet()
        }
    }
    
    /**
     * Mark a tip as shown
     */
    suspend fun markTipAsShown(tipId: String) {
        dataStore.edit { preferences ->
            val currentTips = preferences[SHOWN_TIPS_KEY] ?: emptySet()
            preferences[SHOWN_TIPS_KEY] = currentTips + tipId
        }
    }
    
    /**
     * Mark multiple tips as shown
     */
    suspend fun markTipsAsShown(tipIds: List<String>) {
        dataStore.edit { preferences ->
            val currentTips = preferences[SHOWN_TIPS_KEY] ?: emptySet()
            preferences[SHOWN_TIPS_KEY] = currentTips + tipIds.toSet()
        }
    }
    
    /**
     * Check if feature discovery is enabled
     */
    fun isFeatureDiscoveryEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[FEATURE_DISCOVERY_ENABLED] ?: true
        }
    }
    
    /**
     * Enable or disable feature discovery
     */
    suspend fun setFeatureDiscoveryEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[FEATURE_DISCOVERY_ENABLED] = enabled
        }
    }
    
    /**
     * Reset all shown tips (useful for testing or user request)
     */
    suspend fun resetShownTips() {
        dataStore.edit { preferences ->
            preferences[SHOWN_TIPS_KEY] = emptySet()
        }
    }
    
    /**
     * Get tips that haven't been shown yet
     */
    suspend fun getUnshownTips(allTips: List<String>): List<String> {
        val shownTips = dataStore.data.map { preferences ->
            preferences[SHOWN_TIPS_KEY] ?: emptySet()
        }.first()
        
        return allTips.filter { it !in shownTips }
    }
}
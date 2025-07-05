package com.example.sumup.data.remote.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.sumup.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure API key provider that uses multiple layers of security:
 * 1. Never stores keys in BuildConfig for production
 * 2. Uses Firebase Remote Config for key distribution
 * 3. Falls back to encrypted SharedPreferences
 * 4. Implements key rotation support
 */
@Singleton
class SecureApiKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_api_keys",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private val remoteConfig = Firebase.remoteConfig.apply {
        setDefaultsAsync(
            mapOf(
                KEY_GEMINI_API to "",
                KEY_API_VERSION to 1L,
                KEY_RATE_LIMIT to 60L
            )
        )
    }
    
    companion object {
        private const val KEY_GEMINI_API = "gemini_api_key"
        private const val KEY_API_VERSION = "api_key_version"
        private const val KEY_RATE_LIMIT = "api_rate_limit"
        private const val PREF_CACHED_KEY = "cached_api_key"
        private const val PREF_KEY_VERSION = "cached_key_version"
    }
    
    /**
     * Get the API key securely
     * Priority: Remote Config > Encrypted Storage > BuildConfig (dev only)
     */
    suspend fun getApiKey(): String? {
        return try {
            // Try to fetch from Remote Config
            val remoteKey = fetchRemoteApiKey()
            if (!remoteKey.isNullOrEmpty()) {
                // Cache in encrypted storage
                cacheApiKey(remoteKey)
                return remoteKey
            }
            
            // Fall back to cached key
            val cachedKey = getCachedApiKey()
            if (!cachedKey.isNullOrEmpty()) {
                return cachedKey
            }
            
            // Only use BuildConfig in debug builds
            if (BuildConfig.DEBUG) {
                return BuildConfig.GEMINI_API_KEY
            }
            
            null
        } catch (e: Exception) {
            android.util.Log.e("SecureApiKeyProvider", "Failed to get API key", e)
            // Return cached key as last resort
            getCachedApiKey()
        }
    }
    
    private suspend fun fetchRemoteApiKey(): String? {
        return try {
            remoteConfig.fetchAndActivate().await()
            val key = remoteConfig.getString(KEY_GEMINI_API)
            val version = remoteConfig.getLong(KEY_API_VERSION)
            
            // Check if we need to update cached version
            val cachedVersion = encryptedPrefs.getLong(PREF_KEY_VERSION, 0)
            if (version > cachedVersion && key.isNotEmpty()) {
                encryptedPrefs.edit()
                    .putLong(PREF_KEY_VERSION, version)
                    .apply()
                return key
            }
            
            null
        } catch (e: Exception) {
            android.util.Log.w("SecureApiKeyProvider", "Remote config fetch failed", e)
            null
        }
    }
    
    private fun getCachedApiKey(): String? {
        return encryptedPrefs.getString(PREF_CACHED_KEY, null)
    }
    
    private fun cacheApiKey(key: String) {
        encryptedPrefs.edit()
            .putString(PREF_CACHED_KEY, key)
            .apply()
    }
    
    /**
     * Get rate limit from remote config
     */
    suspend fun getRateLimit(): Long {
        return try {
            remoteConfig.fetchAndActivate().await()
            remoteConfig.getLong(KEY_RATE_LIMIT)
        } catch (e: Exception) {
            60L // Default rate limit
        }
    }
    
    /**
     * Clear cached keys (for logout/reset)
     */
    fun clearCachedKeys() {
        encryptedPrefs.edit().clear().apply()
    }
}
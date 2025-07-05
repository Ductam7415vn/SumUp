package com.example.sumup.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.sumup.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages API keys securely using Android Keystore
 * In production, API keys should be fetched from a secure backend
 * 
 * @deprecated Use EnhancedApiKeyManager instead. This class is kept for migration purposes only.
 */
@Deprecated(
    message = "Use EnhancedApiKeyManager instead",
    replaceWith = ReplaceWith("EnhancedApiKeyManager"),
    level = DeprecationLevel.WARNING
)
@Singleton
class ApiKeyManager @Inject constructor(
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
    
    companion object {
        private const val KEY_GEMINI_API = "gemini_api_key"
        private const val DEFAULT_KEY = "your_gemini_api_key_here"
    }
    
    /**
     * Gets the Gemini API key
     * Order of precedence:
     * 1. Encrypted storage (user-provided)
     * 2. BuildConfig (compile-time)
     * 3. Default placeholder
     */
    fun getGeminiApiKey(): String {
        // First check encrypted storage
        val storedKey = encryptedPrefs.getString(KEY_GEMINI_API, null)
        if (!storedKey.isNullOrBlank() && storedKey != DEFAULT_KEY) {
            return storedKey
        }
        
        // Fallback to BuildConfig
        val buildKey = BuildConfig.GEMINI_API_KEY
        if (buildKey.isNotBlank() && buildKey != DEFAULT_KEY) {
            // Optionally save to encrypted storage for future use
            saveGeminiApiKey(buildKey)
            return buildKey
        }
        
        return DEFAULT_KEY
    }
    
    /**
     * Saves API key securely
     * In production, this should validate the key with backend first
     */
    fun saveGeminiApiKey(apiKey: String) {
        if (apiKey.isNotBlank() && apiKey != DEFAULT_KEY) {
            encryptedPrefs.edit()
                .putString(KEY_GEMINI_API, apiKey)
                .apply()
        }
    }
    
    /**
     * Checks if a valid API key is available
     */
    fun hasValidApiKey(): Boolean {
        val key = getGeminiApiKey()
        return key.isNotBlank() && key != DEFAULT_KEY
    }
    
    /**
     * Clears stored API key
     */
    fun clearApiKey() {
        encryptedPrefs.edit()
            .remove(KEY_GEMINI_API)
            .apply()
    }
}
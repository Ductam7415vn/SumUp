package com.example.sumup.utils.migration

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.sumup.BuildConfig
import com.example.sumup.utils.EnhancedApiKeyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles one-time migration from old ApiKeyManager to EnhancedApiKeyManager
 */
@Singleton
class ApiKeyMigration @Inject constructor(
    @ApplicationContext private val context: Context,
    private val enhancedApiKeyManager: EnhancedApiKeyManager,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val MIGRATION_COMPLETED_KEY = "api_key_migration_completed"
        private const val OLD_KEY_STORAGE = "secure_api_keys"
        private const val OLD_KEY_NAME = "gemini_api_key"
        private const val DEFAULT_KEY = "your_gemini_api_key_here"
    }

    /**
     * Performs migration if not already completed
     * @return true if migration was performed, false if already completed
     */
    suspend fun migrateIfNeeded(): Boolean {
        // Check if migration already completed
        if (isMigrationCompleted()) {
            android.util.Log.d("ApiKeyMigration", "Migration already completed")
            return false
        }

        android.util.Log.d("ApiKeyMigration", "Starting API key migration")
        
        try {
            // Get API key from old storage
            val oldApiKey = getOldApiKey()
            
            if (oldApiKey != null && oldApiKey != DEFAULT_KEY) {
                android.util.Log.d("ApiKeyMigration", "Found API key in old storage, migrating...")
                
                // Check if this key already exists in new system
                val existingKeys = enhancedApiKeyManager.apiKeys.value
                val keyAlreadyExists = existingKeys.any { 
                    enhancedApiKeyManager.getActiveApiKey() == oldApiKey 
                }
                
                if (!keyAlreadyExists) {
                    // Add to new system
                    enhancedApiKeyManager.addApiKey(
                        name = "Migrated API Key",
                        key = oldApiKey
                    )
                    android.util.Log.d("ApiKeyMigration", "API key migrated successfully")
                } else {
                    android.util.Log.d("ApiKeyMigration", "API key already exists in new system")
                }
            } else {
                // Check BuildConfig as fallback
                val buildConfigKey = BuildConfig.GEMINI_API_KEY
                if (buildConfigKey.isNotBlank() && buildConfigKey != DEFAULT_KEY) {
                    android.util.Log.d("ApiKeyMigration", "Found API key in BuildConfig, migrating...")
                    enhancedApiKeyManager.addApiKey(
                        name = "BuildConfig API Key",
                        key = buildConfigKey
                    )
                } else {
                    android.util.Log.d("ApiKeyMigration", "No API key found to migrate")
                }
            }
            
            // Clean up old storage
            cleanupOldStorage()
            
            // Mark migration as completed
            markMigrationCompleted()
            
            android.util.Log.d("ApiKeyMigration", "Migration completed successfully")
            return true
            
        } catch (e: Exception) {
            android.util.Log.e("ApiKeyMigration", "Migration failed", e)
            // Don't mark as completed so it can be retried
            return false
        }
    }

    private fun getOldApiKey(): String? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            val encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                OLD_KEY_STORAGE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            encryptedPrefs.getString(OLD_KEY_NAME, null)
        } catch (e: Exception) {
            android.util.Log.e("ApiKeyMigration", "Failed to read old API key", e)
            null
        }
    }

    private fun cleanupOldStorage() {
        try {
            // Delete the old encrypted shared preferences file
            val oldPrefsFile = context.getSharedPreferencesPath(OLD_KEY_STORAGE)
            if (oldPrefsFile.exists()) {
                oldPrefsFile.delete()
                android.util.Log.d("ApiKeyMigration", "Old storage cleaned up")
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiKeyMigration", "Failed to cleanup old storage", e)
        }
    }

    private fun isMigrationCompleted(): Boolean {
        return sharedPreferences.getBoolean(MIGRATION_COMPLETED_KEY, false)
    }

    private fun markMigrationCompleted() {
        sharedPreferences.edit()
            .putBoolean(MIGRATION_COMPLETED_KEY, true)
            .apply()
    }

    /**
     * Force reset migration (useful for testing)
     */
    fun resetMigration() {
        sharedPreferences.edit()
            .remove(MIGRATION_COMPLETED_KEY)
            .apply()
    }
}

/**
 * Extension function to get SharedPreferences path
 */
private fun Context.getSharedPreferencesPath(name: String): java.io.File {
    return java.io.File(applicationInfo.dataDir, "shared_prefs/$name.xml")
}
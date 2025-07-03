package com.example.sumup.utils

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.example.sumup.domain.model.*
import kotlinx.coroutines.flow.*
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class EnhancedApiKeyManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val keyAlias = "SumUpApiKeyAlias"
    private val _apiKeys = MutableStateFlow<List<ApiKeyInfo>>(emptyList())
    val apiKeys: StateFlow<List<ApiKeyInfo>> = _apiKeys.asStateFlow()
    
    private val _activeKeyId = MutableStateFlow<String?>(null)
    val activeKeyId: StateFlow<String?> = _activeKeyId.asStateFlow()
    
    init {
        loadApiKeys()
        loadActiveKeyId()
        logMigrationStatus()
    }
    
    private fun logMigrationStatus() {
        val keyCount = _apiKeys.value.size
        val activeKey = _activeKeyId.value
        android.util.Log.d("EnhancedApiKeyManager", "=== API Key Status ===")
        android.util.Log.d("EnhancedApiKeyManager", "Total keys: $keyCount")
        android.util.Log.d("EnhancedApiKeyManager", "Active key ID: $activeKey")
        android.util.Log.d("EnhancedApiKeyManager", "Has active key: ${getActiveApiKey() != null}")
        
        if (keyCount == 0) {
            android.util.Log.d("EnhancedApiKeyManager", "No API keys found. Migration may be pending or user hasn't added keys yet.")
        }
    }
    
    fun addApiKey(name: String, key: String): ApiKeyInfo {
        android.util.Log.d("EnhancedApiKeyManager", "Adding API key: name=$name, key=${key.take(10)}...")
        val newKey = ApiKeyInfo(
            id = UUID.randomUUID().toString(),
            name = name,
            key = encryptKey(key),
            isActive = false,
            createdAt = Date(),
            lastUsedAt = null,
            usageCount = 0,
            dailyUsage = emptyMap()
        )
        
        _apiKeys.value = _apiKeys.value + newKey
        android.util.Log.d("EnhancedApiKeyManager", "Total keys after add: ${_apiKeys.value.size}")
        saveApiKeys()
        
        // If this is the first key, make it active
        if (_apiKeys.value.size == 1) {
            android.util.Log.d("EnhancedApiKeyManager", "Setting first key as active")
            setActiveKey(newKey.id)
        }
        
        return newKey
    }
    
    fun deleteApiKey(keyId: String) {
        _apiKeys.value = _apiKeys.value.filter { it.id != keyId }
        saveApiKeys()
        
        // If deleted key was active, set another as active
        if (_activeKeyId.value == keyId) {
            _activeKeyId.value = _apiKeys.value.firstOrNull()?.id
            saveActiveKeyId()
        }
    }
    
    fun setActiveKey(keyId: String) {
        if (_apiKeys.value.any { it.id == keyId }) {
            _activeKeyId.value = keyId
            saveActiveKeyId()
        }
    }
    
    fun getActiveApiKey(): String? {
        val activeId = _activeKeyId.value ?: return null
        val activeKey = _apiKeys.value.find { it.id == activeId } ?: return null
        return decryptKey(activeKey.key)
    }
    
    fun updateKeyUsage(keyId: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        _apiKeys.value = _apiKeys.value.map { key ->
            if (key.id == keyId) {
                val updatedDailyUsage = key.dailyUsage.toMutableMap()
                updatedDailyUsage[today] = (updatedDailyUsage[today] ?: 0) + 1
                
                key.copy(
                    lastUsedAt = Date(),
                    usageCount = key.usageCount + 1,
                    dailyUsage = updatedDailyUsage
                )
            } else key
        }
        saveApiKeys()
    }
    
    fun getUsageStats(): ApiUsageStats {
        val activeKey = _activeKeyId.value?.let { id ->
            _apiKeys.value.find { it.id == id }
        }
        
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayUsage = activeKey?.dailyUsage?.get(today) ?: 0
        
        // Calculate weekly usage
        val calendar = Calendar.getInstance()
        val weeklyUsage = mutableMapOf<String, Int>()
        
        for (i in 0..6) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            weeklyUsage[date] = activeKey?.dailyUsage?.get(date) ?: 0
        }
        
        val totalRequests = activeKey?.usageCount ?: 0
        val estimatedTokens = totalRequests * 2000 // Rough estimate
        
        return ApiUsageStats(
            totalKeys = _apiKeys.value.size,
            activeKeyName = activeKey?.name ?: "No Active Key",
            tokenUsage = TokenUsage(
                used = estimatedTokens,
                limit = 1_000_000, // Free tier limit
                percentage = min(100f, (estimatedTokens.toFloat() / 1_000_000) * 100)
            ),
            requestsToday = todayUsage,
            requestsThisWeek = weeklyUsage.values.sum(),
            rateLimitStatus = RateLimitStatus(
                requestsPerMinute = todayUsage / (24 * 60), // Rough estimate
                limit = 60, // Free tier limit
                isNearLimit = todayUsage > 50,
                isOverLimit = todayUsage >= 60,
                resetTime = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 1)
                }.time
            ),
            weeklyUsage = weeklyUsage
        )
    }
    
    fun shouldShowRotationReminder(): Boolean {
        return _apiKeys.value.any { key ->
            val daysSinceCreation = daysBetween(key.createdAt, Date())
            daysSinceCreation >= 83 && !isKeyRotationSnoozed(key.id) // Show reminder 7 days before 90-day mark
        }
    }
    
    fun getRotationReminders(): List<KeyRotationReminder> {
        return _apiKeys.value.mapNotNull { key ->
            val daysSinceCreation = daysBetween(key.createdAt, Date())
            
            if (daysSinceCreation >= 83 && !isKeyRotationSnoozed(key.id)) {
                val daysUntilRotation = 90 - daysSinceCreation
                KeyRotationReminder(
                    keyId = key.id,
                    keyName = key.name,
                    lastRotationDate = key.createdAt,
                    daysUntilRotation = daysUntilRotation,
                    isOverdue = daysUntilRotation < 0,
                    daysOverdue = if (daysUntilRotation < 0) -daysUntilRotation else 0
                )
            } else null
        }
    }
    
    fun exportSettings(password: String): ExportData {
        val encryptedKeys = _apiKeys.value.map { key ->
            ExportedApiKey(
                name = key.name,
                encryptedKey = encryptKeyWithPassword(decryptKey(key.key), password),
                createdAt = key.createdAt,
                lastUsedAt = key.lastUsedAt,
                usageCount = key.usageCount,
                dailyUsage = key.dailyUsage
            )
        }
        
        return ExportData(
            version = 1,
            exportDate = Date(),
            apiKeys = encryptedKeys
        )
    }
    
    fun importSettings(exportData: ExportData, password: String): Boolean {
        return try {
            val validKeys = exportData.apiKeys.filter { 
                decryptKeyWithPassword(it.encryptedKey, password).isNotEmpty() 
            }
            
            if (validKeys.isEmpty()) return false
            
            validKeys.forEach { keyData ->
                val decryptedKey = decryptKeyWithPassword(keyData.encryptedKey, password)
                val apiKeyInfo = ApiKeyInfo(
                    id = UUID.randomUUID().toString(),
                    name = keyData.name,
                    key = encryptKey(decryptedKey),
                    isActive = false,
                    createdAt = keyData.createdAt,
                    lastUsedAt = keyData.lastUsedAt,
                    usageCount = keyData.usageCount,
                    dailyUsage = keyData.dailyUsage
                )
                
                _apiKeys.value = _apiKeys.value + apiKeyInfo
                saveApiKeys()
            }
            
            // Set first imported key as active if no active key
            if (_activeKeyId.value == null && validKeys.isNotEmpty()) {
                _activeKeyId.value = _apiKeys.value.first().id
                saveActiveKeyId()
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun snoozeRotationReminder(keyId: String, days: Int) {
        val snoozedUntil = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, days)
        }.time
        
        val key = "rotation_snooze_$keyId"
        sharedPreferences.edit()
            .putLong(key, snoozedUntil.time)
            .apply()
    }
    
    private fun isKeyRotationSnoozed(keyId: String): Boolean {
        val key = "rotation_snooze_$keyId"
        val snoozedUntil = sharedPreferences.getLong(key, 0)
        
        return if (snoozedUntil > 0) {
            Date(snoozedUntil).after(Date())
        } else {
            false
        }
    }
    
    private fun daysBetween(start: Date, end: Date): Int {
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
    
    private fun encryptKey(key: String): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        if (!keyStore.containsAlias(keyAlias)) {
            generateKey()
        }
        
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encryption = cipher.doFinal(key.toByteArray())
        
        val combined = ByteArray(iv.size + encryption.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryption, 0, combined, iv.size, encryption.size)
        
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
    
    private fun decryptKey(encryptedKey: String): String {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            val secretKey = keyStore.getKey(keyAlias, null) as SecretKey
            val combined = Base64.decode(encryptedKey, Base64.DEFAULT)
            
            val iv = combined.sliceArray(0..11)
            val encrypted = combined.sliceArray(12 until combined.size)
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            String(cipher.doFinal(encrypted))
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun encryptKeyWithPassword(key: String, password: String): String {
        // Simple XOR encryption for demo (use proper encryption in production)
        val encrypted = key.toByteArray().mapIndexed { index, byte ->
            (byte.toInt() xor password[index % password.length].code).toByte()
        }.toByteArray()
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }
    
    private fun decryptKeyWithPassword(encryptedKey: String, password: String): String {
        return try {
            val encrypted = Base64.decode(encryptedKey, Base64.DEFAULT)
            val decrypted = encrypted.mapIndexed { index, byte ->
                (byte.toInt() xor password[index % password.length].code).toByte()
            }.toByteArray()
            String(decrypted)
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    
    private fun saveApiKeys() {
        val json = com.google.gson.Gson().toJson(_apiKeys.value)
        android.util.Log.d("EnhancedApiKeyManager", "Saving API keys: $json")
        val result = sharedPreferences.edit()
            .putString("api_keys", json)
            .commit() // Use commit instead of apply to ensure immediate save
        android.util.Log.d("EnhancedApiKeyManager", "Save result: $result")
    }
    
    private fun loadApiKeys() {
        val json = sharedPreferences.getString("api_keys", null)
        android.util.Log.d("EnhancedApiKeyManager", "Loading API keys: $json")
        if (json == null) {
            android.util.Log.d("EnhancedApiKeyManager", "No saved API keys found")
            return
        }
        val type = object : com.google.gson.reflect.TypeToken<List<ApiKeyInfo>>() {}.type
        _apiKeys.value = com.google.gson.Gson().fromJson(json, type) ?: emptyList()
        android.util.Log.d("EnhancedApiKeyManager", "Loaded ${_apiKeys.value.size} API keys")
    }
    
    private fun saveActiveKeyId() {
        sharedPreferences.edit()
            .putString("active_key_id", _activeKeyId.value)
            .apply()
    }
    
    private fun loadActiveKeyId() {
        _activeKeyId.value = sharedPreferences.getString("active_key_id", null)
    }
    
    private fun SimpleDateFormat(pattern: String, locale: Locale): java.text.SimpleDateFormat {
        return java.text.SimpleDateFormat(pattern, locale)
    }
}
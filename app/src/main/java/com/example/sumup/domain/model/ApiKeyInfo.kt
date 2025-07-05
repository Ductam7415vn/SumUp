package com.example.sumup.domain.model

import java.util.Date

data class ApiKeyInfo(
    val id: String,
    val name: String,
    val key: String,
    val isActive: Boolean,
    val createdAt: Date,
    val lastUsedAt: Date? = null,
    val usageCount: Int = 0,
    val dailyUsage: Map<String, Int> = emptyMap(), // date -> count
    val isValid: Boolean = true,
    val expiresAt: Date? = null
)

data class ApiUsageStats(
    val totalKeys: Int,
    val activeKeyName: String,
    val tokenUsage: TokenUsage,
    val requestsToday: Int,
    val requestsThisWeek: Int,
    val requestsThisMonth: Int,
    val tokensToday: Int,
    val tokensThisWeek: Int,
    val tokensThisMonth: Int,
    val successRate: Int,
    val averageResponseTime: Int,
    val lastRequestTime: Long?,
    val rateLimitStatus: RateLimitStatus,
    val weeklyUsage: Map<String, Int> = emptyMap()
)

data class TokenUsage(
    val used: Int,
    val limit: Int,
    val percentage: Float
)

data class RateLimitStatus(
    val requestsPerMinute: Int,
    val limit: Int,
    val isNearLimit: Boolean,
    val isOverLimit: Boolean,
    val resetTime: Date
)

data class KeyRotationReminder(
    val keyId: String,
    val keyName: String,
    val lastRotationDate: Date,
    val daysUntilRotation: Int,
    val isOverdue: Boolean,
    val daysOverdue: Int = 0
)

data class ExportData(
    val version: Int = 1,
    val exportDate: Date,
    val apiKeys: List<ExportedApiKey>
)

data class ExportedApiKey(
    val name: String,
    val encryptedKey: String,
    val createdAt: Date,
    val lastUsedAt: Date? = null,
    val usageCount: Int = 0,
    val dailyUsage: Map<String, Int> = emptyMap()
)
package com.example.sumup.domain.model

/**
 * Indicates which type of API service is being used
 */
enum class ServiceType {
    REAL_API,
    MOCK_API
}

/**
 * Provides information about the current service configuration
 */
data class ServiceInfo(
    val type: ServiceType,
    val isUsingMockData: Boolean = type == ServiceType.MOCK_API,
    val message: String = when (type) {
        ServiceType.MOCK_API -> "Demo Mode: Add API key in settings for real summaries"
        ServiceType.REAL_API -> "Connected to Gemini AI"
    }
)
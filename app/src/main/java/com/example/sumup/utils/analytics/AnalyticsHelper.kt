package com.example.sumup.utils.analytics

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun logError(throwable: Throwable, message: String? = null)
}
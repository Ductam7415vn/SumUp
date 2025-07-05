package com.example.sumup.utils

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar
import java.util.Locale

/**
 * Simple API usage tracker that directly manages usage counts
 */
@Singleton
class ApiUsageTracker @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val PREF_PREFIX = "api_usage_"
        private const val TOTAL_USAGE_KEY = "api_total_usage"
    }
    
    fun trackUsage() {
        android.util.Log.d("ApiUsageTracker", "=== TRACKING API USAGE ===")
        
        // Increment total usage
        val currentTotal = sharedPreferences.getInt(TOTAL_USAGE_KEY, 0)
        val newTotal = currentTotal + 1
        sharedPreferences.edit()
            .putInt(TOTAL_USAGE_KEY, newTotal)
            .apply()
        
        android.util.Log.d("ApiUsageTracker", "Total usage: $currentTotal -> $newTotal")
        
        // Increment today's usage
        val todayKey = getTodayKey()
        val currentToday = sharedPreferences.getInt(todayKey, 0)
        val newToday = currentToday + 1
        sharedPreferences.edit()
            .putInt(todayKey, newToday)
            .apply()
        
        android.util.Log.d("ApiUsageTracker", "Today's usage ($todayKey): $currentToday -> $newToday")
    }
    
    fun getTotalUsage(): Int {
        return sharedPreferences.getInt(TOTAL_USAGE_KEY, 0)
    }
    
    fun getTodayUsage(): Int {
        val todayKey = getTodayKey()
        return sharedPreferences.getInt(todayKey, 0)
    }
    
    fun getWeeklyUsage(): Map<String, Int> {
        val usage = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()
        
        for (i in 0..6) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val key = getDateKey(calendar)
            usage[key] = sharedPreferences.getInt(key, 0)
            calendar.add(Calendar.DAY_OF_YEAR, i) // Reset
        }
        
        return usage
    }
    
    private fun getTodayKey(): String {
        return getDateKey(Calendar.getInstance())
    }
    
    private fun getDateKey(calendar: Calendar): String {
        return PREF_PREFIX + String.format(
            Locale.US,
            "%04d_%02d_%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
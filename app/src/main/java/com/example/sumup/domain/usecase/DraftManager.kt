package com.example.sumup.domain.usecase

import android.content.Context
import android.content.SharedPreferences
import com.example.sumup.presentation.screens.main.MainUiState.InputType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("draft_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_DRAFT_TEXT = "draft_text"
        private const val KEY_DRAFT_TYPE = "draft_type"
        private const val KEY_DRAFT_TIMESTAMP = "draft_timestamp"
        private const val KEY_HAS_SEEN_WELCOME = "has_seen_welcome_card"
    }
    
    suspend fun saveDraft(text: String, type: InputType) {
        prefs.edit()
            .putString(KEY_DRAFT_TEXT, text)
            .putString(KEY_DRAFT_TYPE, type.name)
            .putLong(KEY_DRAFT_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
    
    suspend fun recoverDraft(): String {
        val timestamp = prefs.getLong(KEY_DRAFT_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()
        
        // Only recover drafts less than 24 hours old
        return if (currentTime - timestamp < 24 * 60 * 60 * 1000) {
            prefs.getString(KEY_DRAFT_TEXT, "") ?: ""
        } else {
            clearDraft()
            ""
        }
    }
    
    suspend fun clearDraft() {
        prefs.edit()
            .remove(KEY_DRAFT_TEXT)
            .remove(KEY_DRAFT_TYPE)
            .remove(KEY_DRAFT_TIMESTAMP)
            .apply()
    }
    
    fun hasDraft(): Boolean {
        return prefs.contains(KEY_DRAFT_TEXT) && 
               prefs.getString(KEY_DRAFT_TEXT, "")?.isNotEmpty() == true
    }
    
    suspend fun getHasSeenWelcomeCard(): Boolean {
        return prefs.getBoolean(KEY_HAS_SEEN_WELCOME, false)
    }
    
    suspend fun setHasSeenWelcomeCard(seen: Boolean) {
        prefs.edit()
            .putBoolean(KEY_HAS_SEEN_WELCOME, seen)
            .apply()
    }
}
package com.example.sumup.utils.drafts

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.sumup.domain.model.BulletStyle
import com.example.sumup.domain.model.SummaryPersona
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Draft data structure for auto-save functionality
 */
data class TextDraft(
    val id: String,
    val content: String,
    val inputType: DraftInputType,
    val timestamp: Long,
    val characterCount: Int,
    val wordCount: Int,
    val isAutoSaved: Boolean = true,
    // Summary preferences
    val selectedPersona: SummaryPersona = SummaryPersona.GENERAL,
    val bulletStyle: BulletStyle = BulletStyle.BALANCED,
    val summaryLength: Float = 0.5f
) {
    companion object {
        fun empty(inputType: DraftInputType = DraftInputType.TEXT): TextDraft {
            return TextDraft(
                id = "current_draft",
                content = "",
                inputType = inputType,
                timestamp = System.currentTimeMillis(),
                characterCount = 0,
                wordCount = 0
            )
        }
    }
}

/**
 * Types of input that can be drafted
 */
enum class DraftInputType {
    TEXT,           // Manual text input
    PDF_EXTRACTED,  // Text extracted from PDF
    OCR_SCANNED,    // Text from camera OCR
    PASTED         // Pasted from clipboard
}

/**
 * Draft manager for auto-saving user input
 */
@Singleton
class DraftManager @Inject constructor(
    private val context: Context
) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drafts")
    
    // Auto-save coroutine scope
    private val autoSaveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Auto-save delay (2 seconds after user stops typing)
    private val autoSaveDelayMs = 2000L
    
    // DataStore keys
    private object Keys {
        val DRAFT_CONTENT = stringPreferencesKey("draft_content")
        val DRAFT_INPUT_TYPE = stringPreferencesKey("draft_input_type")
        val DRAFT_TIMESTAMP = longPreferencesKey("draft_timestamp")
        val DRAFT_CHARACTER_COUNT = intPreferencesKey("draft_character_count")
        val DRAFT_WORD_COUNT = intPreferencesKey("draft_word_count")
        val DRAFT_PERSONA = stringPreferencesKey("draft_persona")
        val DRAFT_BULLET_STYLE = stringPreferencesKey("draft_bullet_style")
        val DRAFT_SUMMARY_LENGTH = floatPreferencesKey("draft_summary_length")
        val HAS_UNSAVED_DRAFT = booleanPreferencesKey("has_unsaved_draft")
    }
    
    // Auto-save job for debouncing
    private var autoSaveJob: Job? = null
    
    /**
     * Get current draft as Flow
     */
    fun getCurrentDraft(): Flow<TextDraft> {
        return context.dataStore.data.map { preferences ->
            TextDraft(
                id = "current_draft",
                content = preferences[Keys.DRAFT_CONTENT] ?: "",
                inputType = try {
                    DraftInputType.valueOf(
                        preferences[Keys.DRAFT_INPUT_TYPE] ?: DraftInputType.TEXT.name
                    )
                } catch (e: IllegalArgumentException) {
                    DraftInputType.TEXT
                },
                timestamp = preferences[Keys.DRAFT_TIMESTAMP] ?: System.currentTimeMillis(),
                characterCount = preferences[Keys.DRAFT_CHARACTER_COUNT] ?: 0,
                wordCount = preferences[Keys.DRAFT_WORD_COUNT] ?: 0,
                selectedPersona = try {
                    SummaryPersona.valueOf(
                        preferences[Keys.DRAFT_PERSONA] ?: SummaryPersona.GENERAL.name
                    )
                } catch (e: IllegalArgumentException) {
                    SummaryPersona.GENERAL
                },
                bulletStyle = try {
                    BulletStyle.valueOf(
                        preferences[Keys.DRAFT_BULLET_STYLE] ?: BulletStyle.BALANCED.name
                    )
                } catch (e: IllegalArgumentException) {
                    BulletStyle.BALANCED
                },
                summaryLength = preferences[Keys.DRAFT_SUMMARY_LENGTH] ?: 0.5f,
                isAutoSaved = true
            )
        }.catch { emit(TextDraft.empty()) }
    }
    
    /**
     * Get latest draft (non-Flow version for one-time access)
     */
    suspend fun getLatestDraft(): TextDraft? {
        return context.dataStore.data.first().let { preferences ->
            val content = preferences[Keys.DRAFT_CONTENT] ?: ""
            if (content.isEmpty()) {
                null
            } else {
                TextDraft(
                    id = "current_draft",
                    content = content,
                    inputType = try {
                        DraftInputType.valueOf(
                            preferences[Keys.DRAFT_INPUT_TYPE] ?: DraftInputType.TEXT.name
                        )
                    } catch (e: IllegalArgumentException) {
                        DraftInputType.TEXT
                    },
                    timestamp = preferences[Keys.DRAFT_TIMESTAMP] ?: System.currentTimeMillis(),
                    characterCount = preferences[Keys.DRAFT_CHARACTER_COUNT] ?: 0,
                    wordCount = preferences[Keys.DRAFT_WORD_COUNT] ?: 0,
                    selectedPersona = try {
                        SummaryPersona.valueOf(
                            preferences[Keys.DRAFT_PERSONA] ?: SummaryPersona.GENERAL.name
                        )
                    } catch (e: IllegalArgumentException) {
                        SummaryPersona.GENERAL
                    },
                    bulletStyle = try {
                        BulletStyle.valueOf(
                            preferences[Keys.DRAFT_BULLET_STYLE] ?: BulletStyle.BALANCED.name
                        )
                    } catch (e: IllegalArgumentException) {
                        BulletStyle.BALANCED
                    },
                    summaryLength = preferences[Keys.DRAFT_SUMMARY_LENGTH] ?: 0.5f,
                    isAutoSaved = true
                )
            }
        }
    }
    
    /**
     * Check if there's an unsaved draft
     */
    fun hasUnsavedDraft(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.HAS_UNSAVED_DRAFT] ?: false
        }.catch { emit(false) }
    }
    
    /**
     * Auto-save draft with debouncing
     */
    fun autoSaveDraft(
        content: String,
        inputType: DraftInputType = DraftInputType.TEXT,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        bulletStyle: BulletStyle = BulletStyle.BALANCED,
        summaryLength: Float = 0.5f
    ) {
        // Cancel previous auto-save job
        autoSaveJob?.cancel()
        
        // Create new debounced auto-save job
        autoSaveJob = autoSaveScope.launch {
            delay(autoSaveDelayMs)
            saveDraft(content, inputType, persona, bulletStyle, summaryLength)
        }
    }
    
    /**
     * Immediately save draft
     */
    suspend fun saveDraft(
        content: String,
        inputType: DraftInputType = DraftInputType.TEXT,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        bulletStyle: BulletStyle = BulletStyle.BALANCED,
        summaryLength: Float = 0.5f
    ) {
        val characterCount = content.length
        val wordCount = if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size
        
        context.dataStore.edit { preferences ->
            preferences[Keys.DRAFT_CONTENT] = content
            preferences[Keys.DRAFT_INPUT_TYPE] = inputType.name
            preferences[Keys.DRAFT_TIMESTAMP] = System.currentTimeMillis()
            preferences[Keys.DRAFT_CHARACTER_COUNT] = characterCount
            preferences[Keys.DRAFT_WORD_COUNT] = wordCount
            preferences[Keys.DRAFT_PERSONA] = persona.name
            preferences[Keys.DRAFT_BULLET_STYLE] = bulletStyle.name
            preferences[Keys.DRAFT_SUMMARY_LENGTH] = summaryLength
            preferences[Keys.HAS_UNSAVED_DRAFT] = content.isNotBlank()
        }
    }
    
    /**
     * Clear current draft
     */
    suspend fun clearDraft() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.DRAFT_CONTENT)
            preferences.remove(Keys.DRAFT_INPUT_TYPE)
            preferences.remove(Keys.DRAFT_TIMESTAMP)
            preferences.remove(Keys.DRAFT_CHARACTER_COUNT)
            preferences.remove(Keys.DRAFT_WORD_COUNT)
            preferences.remove(Keys.DRAFT_PERSONA)
            preferences.remove(Keys.DRAFT_BULLET_STYLE)
            preferences.remove(Keys.DRAFT_SUMMARY_LENGTH)
            preferences[Keys.HAS_UNSAVED_DRAFT] = false
        }
    }
    
    /**
     * Mark draft as saved (when successfully processed)
     */
    suspend fun markDraftAsSaved() {
        context.dataStore.edit { preferences ->
            preferences[Keys.HAS_UNSAVED_DRAFT] = false
        }
    }
    
    /**
     * Get draft statistics
     */
    suspend fun getDraftStats(): DraftStats {
        val preferences = context.dataStore.data.first()
        return DraftStats(
            hasContent = !preferences[Keys.DRAFT_CONTENT].isNullOrBlank(),
            characterCount = preferences[Keys.DRAFT_CHARACTER_COUNT] ?: 0,
            wordCount = preferences[Keys.DRAFT_WORD_COUNT] ?: 0,
            lastSaved = preferences[Keys.DRAFT_TIMESTAMP] ?: 0L,
            inputType = DraftInputType.valueOf(
                preferences[Keys.DRAFT_INPUT_TYPE] ?: DraftInputType.TEXT.name
            )
        )
    }
    
    /**
     * Backup draft to multiple locations for safety
     */
    suspend fun backupDraft(content: String) {
        // Primary save to DataStore
        saveDraft(content)
        
        // TODO: Optional backup to local file for redundancy
        // Could implement local file backup for critical data
    }
    
    /**
     * Restore draft from backup if primary fails
     */
    suspend fun restoreDraftFromBackup(): TextDraft? {
        return try {
            getCurrentDraft().first()
        } catch (e: Exception) {
            // TODO: Implement backup file restoration
            null
        }
    }
    
    /**
     * Clean up old drafts (if implementing multiple drafts in future)
     */
    suspend fun cleanupOldDrafts(maxAge: Long = 7 * 24 * 60 * 60 * 1000L) {
        // Current implementation only has one draft
        // Future: Could implement multiple named drafts with cleanup
        val currentTime = System.currentTimeMillis()
        val preferences = context.dataStore.data.first()
        val draftTime = preferences[Keys.DRAFT_TIMESTAMP] ?: 0L
        
        if (currentTime - draftTime > maxAge) {
            clearDraft()
        }
    }
    
    /**
     * Cancel any pending auto-save operations
     */
    fun cancelAutoSave() {
        autoSaveJob?.cancel()
    }
}

/**
 * Draft statistics for UI display
 */
data class DraftStats(
    val hasContent: Boolean,
    val characterCount: Int,
    val wordCount: Int,
    val lastSaved: Long,
    val inputType: DraftInputType
) {
    val timeSinceLastSave: Long
        get() = System.currentTimeMillis() - lastSaved
        
    val lastSavedText: String
        get() = when {
            timeSinceLastSave < 60_000 -> "Auto-saved just now"
            timeSinceLastSave < 3600_000 -> "Auto-saved ${timeSinceLastSave / 60_000}m ago"
            timeSinceLastSave < 86400_000 -> "Auto-saved ${timeSinceLastSave / 3600_000}h ago"
            else -> "Auto-saved ${timeSinceLastSave / 86400_000}d ago"
        }
}

/**
 * Draft recovery information for user prompts
 */
data class DraftRecoveryInfo(
    val hasRecoverableDraft: Boolean,
    val draftLength: Int,
    val draftPreview: String,
    val lastModified: Long,
    val inputType: DraftInputType
) {
    val previewText: String
        get() = if (draftPreview.length > 100) {
            "${draftPreview.take(100)}..."
        } else {
            draftPreview
        }
}
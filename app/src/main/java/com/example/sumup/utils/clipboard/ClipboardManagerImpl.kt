package com.example.sumup.utils.clipboard

import android.content.ClipData
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.content.ClipboardManager as AndroidClipboardManager

@Singleton
class ClipboardManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ClipboardManager {
    
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager
    
    override fun copyToClipboard(text: String, label: String) {
        // Limit text length to prevent memory issues
        val sanitizedText = text.take(MAX_CLIPBOARD_LENGTH)
        val clip = ClipData.newPlainText(label, sanitizedText)
        clipboardManager.setPrimaryClip(clip)
        
        // Add flag for sensitive content on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            clip.description.extras = android.os.PersistableBundle().apply {
                putBoolean("android.content.extra.IS_SENSITIVE", true)
            }
        }
    }
    
    override fun pasteFromClipboard(): String? {
        return try {
            val primaryClip = clipboardManager.primaryClip
            if (primaryClip != null && primaryClip.itemCount > 0) {
                val text = primaryClip.getItemAt(0).text?.toString()
                // Validate and sanitize pasted content
                text?.take(MAX_CLIPBOARD_LENGTH)?.trim()
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle security exceptions or other clipboard access issues
            android.util.Log.e("ClipboardManager", "Failed to paste from clipboard", e)
            null
        }
    }
    
    companion object {
        private const val MAX_CLIPBOARD_LENGTH = 100_000 // 100K chars max
    }
    
    override fun clearClipboard() {
        // Android 13+ privacy feature - clear clipboard after pasting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            clipboardManager.clearPrimaryClip()
        } else {
            // For older versions, set empty clip
            val emptyClip = ClipData.newPlainText("", "")
            clipboardManager.setPrimaryClip(emptyClip)
        }
    }
}
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
        val clip = ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clip)
    }
    
    override fun pasteFromClipboard(): String? {
        val primaryClip = clipboardManager.primaryClip
        return if (primaryClip != null && primaryClip.itemCount > 0) {
            primaryClip.getItemAt(0).text?.toString()
        } else {
            null
        }
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
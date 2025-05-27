package com.example.sumup.utils.clipboard

interface ClipboardManager {
    fun copyToClipboard(text: String, label: String = "Copied text")
    fun pasteFromClipboard(): String?
    fun clearClipboard()
}
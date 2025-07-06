package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * RTF document processor - basic implementation
 * For full RTF parsing, a dedicated library would be needed
 */
class RtfDocumentProcessor(
    private val context: Context? = null
) : DocumentProcessor {
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val rawContent = reader.readText()
                    // Basic RTF stripping - removes common RTF control codes
                    stripRtfFormatting(rawContent)
                }
            } ?: throw IllegalStateException("Context is required for file access")
        } catch (e: Exception) {
            throw Exception("Failed to read RTF file: ${e.message}", e)
        }
    }
    
    private fun stripRtfFormatting(rtfContent: String): String {
        // Very basic RTF stripping - a proper implementation would use a dedicated RTF parser
        return rtfContent
            .replace(Regex("\\{\\\\[^{}]*\\}"), "") // Remove control groups
            .replace(Regex("\\\\[a-z]+(-?\\d+)?[ ]?"), "") // Remove control words
            .replace(Regex("\\\\[^a-z]"), "") // Remove special characters
            .replace(Regex("[{}]"), "") // Remove braces
            .trim()
    }
    
    override suspend fun getPageCount(uri: String): Int? {
        // RTF files don't have reliable page count
        return null
    }
    
    override suspend fun validateDocument(uri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                val buffer = ByteArray(10)
                val bytesRead = inputStream.read(buffer)
                // Check for RTF header
                bytesRead >= 6 && String(buffer, 0, 6) == "{\\rtf1"
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
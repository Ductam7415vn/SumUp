package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Simple text file processor
 */
class TxtDocumentProcessor(
    private val context: Context? = null
) : DocumentProcessor {
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: throw IllegalStateException("Context is required for file access")
        } catch (e: Exception) {
            throw Exception("Failed to read text file: ${e.message}", e)
        }
    }
    
    override suspend fun getPageCount(uri: String): Int? {
        // Text files don't have pages
        return null
    }
    
    override suspend fun validateDocument(uri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                // Try to read first few bytes to validate
                val buffer = ByteArray(512)
                val bytesRead = inputStream.read(buffer)
                bytesRead > 0
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
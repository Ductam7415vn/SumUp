package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple DOC processor that just informs users about limitations
 * This is the most practical solution without backend
 */
class SimpleDocProcessor(
    private val context: Context
) : DocumentProcessor {
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        throw UnsupportedOperationException(
            "DOC format is not supported. Please use one of these alternatives:\n\n" +
            "1. Save your document as DOCX in Microsoft Word\n" +
            "2. Convert online at: https://cloudconvert.com/doc-to-docx\n" +
            "3. Copy and paste the text directly into the app\n" +
            "4. Save as PDF or TXT format"
        )
    }
    
    override suspend fun getPageCount(uri: String): Int? = null
    
    override suspend fun validateDocument(uri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(Uri.parse(uri))?.use { inputStream ->
                val buffer = ByteArray(8)
                val bytesRead = inputStream.read(buffer)
                
                if (bytesRead >= 8) {
                    // Check for DOC header
                    val isDoc = buffer[0] == 0xD0.toByte() && 
                               buffer[1] == 0xCF.toByte() &&
                               buffer[2] == 0x11.toByte() &&
                               buffer[3] == 0xE0.toByte()
                               
                    if (isDoc) {
                        // Valid DOC file, but we can't process it
                        return@use true
                    }
                }
                false
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
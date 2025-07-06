package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.zwobble.mammoth.DocumentConverter
import org.zwobble.mammoth.Result
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Document processor for DOC and DOCX files
 * Uses Mammoth library for DOCX support (compatible with API 24+)
 * DOC format still requires server-side processing or API 26+
 */
class DocxDocumentProcessor(
    private val context: Context? = null
) : DocumentProcessor {
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        android.util.Log.d("DocxProcessor", "extractText called with uri: $uri")
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                val text = extractTextFromStream(inputStream, uri)
                android.util.Log.d("DocxProcessor", "Successfully extracted text, length: ${text.length}")
                text
            } ?: throw IllegalStateException("Context is required for file access")
        } catch (e: Exception) {
            android.util.Log.e("DocxProcessor", "Failed to extract text", e)
            throw Exception("Failed to extract text from document: ${e.message}", e)
        }
    }
    
    private fun extractTextFromStream(inputStream: InputStream, uri: String): String {
        android.util.Log.d("DocxProcessor", "extractTextFromStream called, checking URI: $uri")
        return when {
            uri.endsWith(".docx", ignoreCase = true) -> {
                android.util.Log.d("DocxProcessor", "URI ends with .docx, calling extractFromDocx")
                extractFromDocx(inputStream)
            }
            uri.endsWith(".doc", ignoreCase = true) -> {
                android.util.Log.d("DocxProcessor", "URI ends with .doc, calling extractFromDoc")
                extractFromDoc(inputStream)
            }
            else -> {
                android.util.Log.d("DocxProcessor", "URI doesn't have clear extension, trying DOCX first")
                // Try DOCX first, then DOC
                try {
                    inputStream.reset()
                    extractFromDocx(inputStream)
                } catch (e: Exception) {
                    android.util.Log.d("DocxProcessor", "DOCX extraction failed, trying DOC: ${e.message}")
                    try {
                        inputStream.reset()
                        extractFromDoc(inputStream)
                    } catch (docError: Exception) {
                        android.util.Log.e("DocxProcessor", "Both DOCX and DOC extraction failed", docError)
                        throw Exception("Unable to determine document format")
                    }
                }
            }
        }
    }
    
    private fun extractFromDocx(inputStream: InputStream): String {
        android.util.Log.d("DocxProcessor", "Starting extractFromDocx")
        return try {
            // Mammoth requires a file, so we need to create a temporary file
            val tempFile = File.createTempFile("temp_docx", ".docx", context?.cacheDir)
            android.util.Log.d("DocxProcessor", "Created temp file: ${tempFile.absolutePath}")
            
            try {
                // Copy input stream to temp file
                FileOutputStream(tempFile).use { outputStream ->
                    val bytesWritten = inputStream.copyTo(outputStream)
                    android.util.Log.d("DocxProcessor", "Copied $bytesWritten bytes to temp file")
                }
                
                // Use Mammoth to extract text
                android.util.Log.d("DocxProcessor", "Creating DocumentConverter")
                val converter = DocumentConverter()
                android.util.Log.d("DocxProcessor", "Calling extractRawText on temp file")
                val result: Result<String> = converter.extractRawText(tempFile)
                
                // Get the extracted text
                val extractedText = result.value ?: ""
                android.util.Log.d("DocxProcessor", "Extracted text length: ${extractedText.length}")
                
                // Check for any warnings
                if (result.warnings.isNotEmpty()) {
                    // Log warnings if needed
                    result.warnings.forEach { warning ->
                        android.util.Log.w("DocxProcessor", "Mammoth warning: $warning")
                    }
                }
                
                extractedText.ifEmpty {
                    throw Exception("No text content found in DOCX file")
                }
            } finally {
                // Clean up temp file
                val deleted = tempFile.delete()
                android.util.Log.d("DocxProcessor", "Temp file deleted: $deleted")
            }
        } catch (e: Exception) {
            android.util.Log.e("DocxProcessor", "Failed in extractFromDocx", e)
            throw Exception("Failed to extract text from DOCX: ${e.message}", e)
        }
    }
    
    private fun extractFromDoc(inputStream: InputStream): String {
        // DOC format is proprietary and requires Apache POI (API 26+)
        throw UnsupportedOperationException(
            "DOC_FORMAT_NOT_SUPPORTED" // Special error code for UI handling
        )
    }
    
    override suspend fun getPageCount(uri: String): Int? = withContext(Dispatchers.IO) {
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                when {
                    uri.endsWith(".docx", ignoreCase = true) -> {
                        // More accurate page estimation based on multiple factors
                        try {
                            val text = extractText(uri)
                            val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                            val paragraphs = text.split("\n\n").filter { it.isNotBlank() }.size
                            
                            // Average page contains:
                            // - 250-300 words (standard double-spaced)
                            // - 500-600 words (single-spaced)
                            // - We'll use 400 words as average
                            val wordsPerPage = 400
                            
                            // Also consider paragraph count (average 3-5 paragraphs per page)
                            val paragraphsPerPage = 4
                            
                            // Calculate estimates
                            val pagesByWords = (words.toFloat() / wordsPerPage).toInt()
                            val pagesByParagraphs = (paragraphs.toFloat() / paragraphsPerPage).toInt()
                            
                            // Use weighted average (words are more reliable)
                            val estimatedPages = ((pagesByWords * 0.7 + pagesByParagraphs * 0.3).toInt()).coerceAtLeast(1)
                            
                            estimatedPages
                        } catch (e: Exception) {
                            null
                        }
                    }
                    uri.endsWith(".doc", ignoreCase = true) -> {
                        // DOC files don't have reliable page count
                        null
                    }
                    else -> null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun validateDocument(uri: String): Boolean = withContext(Dispatchers.IO) {
        android.util.Log.d("DocxProcessor", "validateDocument called with uri: $uri")
        try {
            // Always try to validate as DOCX since this is DocxDocumentProcessor
            // The HybridDocumentProcessor already determined this is a DOCX file
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                android.util.Log.d("DocxProcessor", "Validating DOCX file")
                try {
                    val tempFile = File.createTempFile("validate_docx", ".docx", context?.cacheDir)
                    android.util.Log.d("DocxProcessor", "Created temp file for validation: ${tempFile.absolutePath}")
                    try {
                        FileOutputStream(tempFile).use { outputStream ->
                            val bytesWritten = inputStream.copyTo(outputStream)
                            android.util.Log.d("DocxProcessor", "Copied $bytesWritten bytes for validation")
                        }
                        
                        // Try to open with Mammoth - if it succeeds, it's valid
                        val converter = DocumentConverter()
                        val result = converter.extractRawText(tempFile)
                        android.util.Log.d("DocxProcessor", "Mammoth validation successful, text length: ${result.value?.length ?: 0}")
                        
                        // File is valid if Mammoth can read it
                        true
                    } catch (e: Exception) {
                        // If Mammoth can't read it, it's not a valid DOCX
                        android.util.Log.e("DocxProcessor", "Mammoth validation failed", e)
                        false
                    } finally {
                        val deleted = tempFile.delete()
                        android.util.Log.d("DocxProcessor", "Temp file deleted: $deleted")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DocxProcessor", "Validation temp file error", e)
                    false
                }
            } ?: false.also { android.util.Log.e("DocxProcessor", "Context is null, cannot validate") }
        } catch (e: Exception) {
            android.util.Log.e("DocxProcessor", "Validation exception", e)
            false
        }
    }
}
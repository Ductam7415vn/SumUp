package com.example.sumup.utils

import android.net.Uri
import android.content.Context
import java.io.File

/**
 * Validates user input to prevent issues before API calls
 */
object InputValidator {
    
    // Text input constraints
    const val MIN_TEXT_LENGTH = 50
    const val MAX_TEXT_LENGTH = 30000
    const val MAX_TEXT_LENGTH_EXTENDED = 50000 // For premium users
    
    // PDF constraints
    const val MAX_PDF_SIZE_MB = 10
    const val MAX_PDF_PAGES = 200
    const val MAX_PDF_PAGES_WARNING = 50
    
    /**
     * Validates text input for summarization
     */
    fun validateTextInput(text: String): ValidationResult {
        return when {
            text.isBlank() -> ValidationResult.Error("Please enter some text to summarize")
            text.length < MIN_TEXT_LENGTH -> ValidationResult.Error("Text too short. Please enter at least $MIN_TEXT_LENGTH characters")
            text.length > MAX_TEXT_LENGTH -> ValidationResult.Error("Text too long. Maximum $MAX_TEXT_LENGTH characters allowed")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates PDF file before processing
     */
    fun validatePdfFile(context: Context, uri: Uri): ValidationResult {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val sizeInBytes = inputStream.available()
                val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
                
                if (sizeInMB > MAX_PDF_SIZE_MB) {
                    return ValidationResult.Error("PDF file too large. Maximum ${MAX_PDF_SIZE_MB}MB allowed")
                }
            }
            
            // Note: Page count validation should be done after PDFBox reads the file
            return ValidationResult.Success
            
        } catch (e: Exception) {
            return ValidationResult.Error("Unable to read PDF file: ${e.message}")
        }
    }
    
    /**
     * Validates PDF page count
     */
    fun validatePdfPageCount(pageCount: Int): ValidationResult {
        return when {
            pageCount == 0 -> ValidationResult.Error("PDF appears to be empty")
            pageCount > MAX_PDF_PAGES -> ValidationResult.Error("PDF too large. Maximum $MAX_PDF_PAGES pages allowed")
            pageCount > MAX_PDF_PAGES_WARNING -> ValidationResult.Warning("Large PDF detected. Processing may take longer")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates API key format (basic check)
     */
    fun validateApiKeyFormat(apiKey: String): ValidationResult {
        return when {
            apiKey.isBlank() -> ValidationResult.Error("Please enter your API key")
            apiKey.length < 20 -> ValidationResult.Error("API key appears to be too short")
            !apiKey.matches(Regex("^[A-Za-z0-9_-]+$")) -> ValidationResult.Error("API key contains invalid characters")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validation result sealed class
     */
    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Warning(val message: String) : ValidationResult()
        data class Error(val message: String) : ValidationResult()
        
        val isValid: Boolean
            get() = this is Success || this is Warning
            
        val isError: Boolean
            get() = this is Error
    }
}
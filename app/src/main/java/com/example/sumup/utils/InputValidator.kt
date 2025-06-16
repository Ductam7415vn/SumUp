package com.example.sumup.utils

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates and sanitizes user input to prevent security issues and ensure data quality
 */
@Singleton
class InputValidator @Inject constructor() {
    
    /**
     * Validates text input for summarization
     */
    fun validateTextInput(text: String): ValidationResult {
        val trimmed = text.trim()
        
        // Check length constraints
        when {
            trimmed.isEmpty() -> {
                return ValidationResult.Error("Please enter some text to summarize")
            }
            trimmed.length < MIN_TEXT_LENGTH -> {
                return ValidationResult.Error("Text too short. Need at least $MIN_TEXT_LENGTH characters")
            }
            trimmed.length > MAX_TEXT_LENGTH -> {
                return ValidationResult.Error("Text too long. Maximum $MAX_TEXT_LENGTH characters allowed")
            }
        }
        
        // Check for suspicious patterns
        if (containsSuspiciousContent(trimmed)) {
            return ValidationResult.Error("Text contains invalid content")
        }
        
        // Check for excessive special characters
        val specialCharRatio = calculateSpecialCharRatio(trimmed)
        if (specialCharRatio > MAX_SPECIAL_CHAR_RATIO) {
            return ValidationResult.Error("Text contains too many special characters")
        }
        
        // Check for meaningful content
        val wordCount = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        if (wordCount < MIN_WORD_COUNT) {
            return ValidationResult.Error("Please enter at least $MIN_WORD_COUNT words")
        }
        
        return ValidationResult.Success(sanitizeText(trimmed))
    }
    
    /**
     * Validates PDF file properties
     */
    fun validatePdfFile(fileName: String, fileSize: Long): ValidationResult {
        // Validate file name
        if (fileName.isBlank()) {
            return ValidationResult.Error("Invalid file name")
        }
        
        // Check file extension
        if (!fileName.lowercase().endsWith(".pdf")) {
            return ValidationResult.Error("Please select a PDF file")
        }
        
        // Validate file name characters
        if (containsInvalidFileNameChars(fileName)) {
            return ValidationResult.Error("File name contains invalid characters")
        }
        
        // Check file size
        when {
            fileSize <= 0 -> {
                return ValidationResult.Error("Invalid file size")
            }
            fileSize > MAX_PDF_SIZE -> {
                return ValidationResult.Error("PDF too large. Maximum size is ${MAX_PDF_SIZE / (1024 * 1024)}MB")
            }
        }
        
        return ValidationResult.Success(sanitizeFileName(fileName))
    }
    
    /**
     * Sanitizes text by removing potentially harmful content
     */
    private fun sanitizeText(text: String): String {
        return text
            // Remove control characters except newlines and tabs
            .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"), "")
            // Normalize whitespace
            .replace(Regex("\\s+"), " ")
            // Remove zero-width characters
            .replace(Regex("[\\u200B-\\u200D\\uFEFF]"), "")
            // Limit consecutive newlines
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }
    
    /**
     * Sanitizes file names
     */
    private fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(Regex("[<>:\"|?*]"), "_")
            .replace(Regex("\\s+"), "_")
            .take(MAX_FILE_NAME_LENGTH)
    }
    
    /**
     * Checks for suspicious patterns that might indicate malicious input
     */
    private fun containsSuspiciousContent(text: String): Boolean {
        val suspiciousPatterns = listOf(
            // Script injection attempts
            Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE),
            Regex("javascript:", RegexOption.IGNORE_CASE),
            Regex("on\\w+\\s*=", RegexOption.IGNORE_CASE),
            // SQL injection patterns
            Regex("(union|select|insert|update|delete|drop)\\s+(from|into|table)", RegexOption.IGNORE_CASE),
            // Command injection
            Regex("[;&|]\\s*(rm|del|format|shutdown)", RegexOption.IGNORE_CASE),
            // Excessive repetition (spam)
            Regex("(.)\\1{20,}"),
            // Binary data
            Regex("[\\x00-\\x08\\x0E-\\x1F]{10,}")
        )
        
        return suspiciousPatterns.any { it.containsMatchIn(text) }
    }
    
    /**
     * Calculates the ratio of special characters to total characters
     */
    private fun calculateSpecialCharRatio(text: String): Float {
        val specialCharCount = text.count { !it.isLetterOrDigit() && !it.isWhitespace() }
        return specialCharCount.toFloat() / text.length
    }
    
    /**
     * Checks for invalid file name characters
     */
    private fun containsInvalidFileNameChars(fileName: String): Boolean {
        val invalidChars = "<>:\"|?*\u0000"
        return fileName.any { it in invalidChars }
    }
    
    sealed class ValidationResult {
        data class Success(val sanitizedValue: String) : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
    
    companion object {
        const val MIN_TEXT_LENGTH = 50
        const val MAX_TEXT_LENGTH = 30000
        const val MIN_WORD_COUNT = 10
        const val MAX_SPECIAL_CHAR_RATIO = 0.5f
        const val MAX_PDF_SIZE = 50L * 1024 * 1024 // 50MB
        const val MAX_FILE_NAME_LENGTH = 255
    }
}
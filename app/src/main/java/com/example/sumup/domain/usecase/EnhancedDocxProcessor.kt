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
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.NodeList

/**
 * Enhanced DOCX processor with better accuracy and validation
 */
class EnhancedDocxProcessor(
    private val context: Context? = null
) : DocumentProcessor {
    
    data class DocxValidationResult(
        val isValid: Boolean,
        val hasText: Boolean,
        val hasImages: Boolean,
        val hasTables: Boolean,
        val hasProtection: Boolean,
        val mammothCompatibility: Float // 0.0 to 1.0
    )
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                // First try Mammoth
                val mammothResult = extractWithMammoth(inputStream, uri)
                
                // If Mammoth fails or returns empty, try direct XML parsing
                if (mammothResult.isEmpty()) {
                    inputStream.reset()
                    extractWithDirectXml(inputStream, uri)
                } else {
                    mammothResult
                }
            } ?: throw IllegalStateException("Context is required for file access")
        } catch (e: Exception) {
            throw Exception("Failed to extract text from DOCX: ${e.message}", e)
        }
    }
    
    private fun extractWithMammoth(inputStream: InputStream, uri: String): String {
        return try {
            val tempFile = File.createTempFile("docx_", ".docx", context?.cacheDir)
            
            try {
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                
                val converter = DocumentConverter()
                val result: Result<String> = converter.extractRawText(tempFile)
                
                // Log warnings for debugging
                result.warnings.forEach { warning ->
                    android.util.Log.w("DocxProcessor", "Mammoth warning: $warning")
                }
                
                result.value ?: ""
            } finally {
                tempFile.delete()
            }
        } catch (e: Exception) {
            android.util.Log.e("DocxProcessor", "Mammoth extraction failed", e)
            ""
        }
    }
    
    /**
     * Direct XML parsing as fallback for better compatibility
     */
    private fun extractWithDirectXml(inputStream: InputStream, uri: String): String {
        val tempFile = File.createTempFile("docx_xml_", ".docx", context?.cacheDir)
        
        try {
            // Save to temp file
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            val textBuilder = StringBuilder()
            
            // Open as ZIP and extract document.xml
            ZipFile(tempFile).use { zipFile ->
                // Main document content
                zipFile.getEntry("word/document.xml")?.let { entry ->
                    zipFile.getInputStream(entry).use { xmlStream ->
                        val text = extractTextFromXml(xmlStream)
                        textBuilder.append(text)
                    }
                }
                
                // Headers
                zipFile.entries().asSequence()
                    .filter { it.name.startsWith("word/header") && it.name.endsWith(".xml") }
                    .forEach { entry ->
                        zipFile.getInputStream(entry).use { xmlStream ->
                            textBuilder.append("\n").append(extractTextFromXml(xmlStream))
                        }
                    }
                
                // Footers
                zipFile.entries().asSequence()
                    .filter { it.name.startsWith("word/footer") && it.name.endsWith(".xml") }
                    .forEach { entry ->
                        zipFile.getInputStream(entry).use { xmlStream ->
                            textBuilder.append("\n").append(extractTextFromXml(xmlStream))
                        }
                    }
                
                // Footnotes
                zipFile.getEntry("word/footnotes.xml")?.let { entry ->
                    zipFile.getInputStream(entry).use { xmlStream ->
                        textBuilder.append("\n\n--- Footnotes ---\n")
                        textBuilder.append(extractTextFromXml(xmlStream))
                    }
                }
            }
            
            return textBuilder.toString().ifEmpty {
                throw Exception("No text content found in DOCX")
            }
        } catch (e: Exception) {
            throw Exception("Failed to extract text via XML: ${e.message}", e)
        } finally {
            tempFile.delete()
        }
    }
    
    private fun extractTextFromXml(inputStream: InputStream): String {
        return try {
            val dbFactory = DocumentBuilderFactory.newInstance()
            dbFactory.isNamespaceAware = true
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(inputStream)
            
            val textBuilder = StringBuilder()
            
            // Extract text from w:t nodes
            val textNodes: NodeList = doc.getElementsByTagName("w:t")
            for (i in 0 until textNodes.length) {
                textBuilder.append(textNodes.item(i).textContent)
                textBuilder.append(" ")
            }
            
            // Extract text from w:tab (tabs)
            val tabNodes: NodeList = doc.getElementsByTagName("w:tab")
            if (tabNodes.length > 0) {
                // Replace markers with actual tabs
                var text = textBuilder.toString()
                for (i in 0 until tabNodes.length) {
                    text = text.replaceFirst(" ", "\t")
                }
                return text
            }
            
            textBuilder.toString()
        } catch (e: Exception) {
            ""
        }
    }
    
    suspend fun validateDocxComprehensive(uri: String): DocxValidationResult = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("validate_", ".docx", context?.cacheDir)
        
        try {
            context?.contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            var hasText = false
            var hasImages = false
            var hasTables = false
            var hasProtection = false
            var isValidZip = false
            
            try {
                ZipFile(tempFile).use { zipFile ->
                    isValidZip = true
                    
                    // Check for document.xml
                    val docEntry = zipFile.getEntry("word/document.xml")
                    hasText = docEntry != null
                    
                    // Check for images
                    hasImages = zipFile.entries().asSequence()
                        .any { it.name.startsWith("word/media/") }
                    
                    // Check for tables
                    if (docEntry != null) {
                        zipFile.getInputStream(docEntry).use { stream ->
                            val content = stream.bufferedReader().readText()
                            hasTables = content.contains("<w:tbl>") || content.contains("<w:tbl ")
                        }
                    }
                    
                    // Check for protection
                    val settingsEntry = zipFile.getEntry("word/settings.xml")
                    if (settingsEntry != null) {
                        zipFile.getInputStream(settingsEntry).use { stream ->
                            val content = stream.bufferedReader().readText()
                            hasProtection = content.contains("w:documentProtection")
                        }
                    }
                }
            } catch (e: Exception) {
                // Not a valid ZIP/DOCX
            }
            
            // Calculate Mammoth compatibility score
            val mammothCompatibility = when {
                !isValidZip -> 0.0f
                hasProtection -> 0.3f
                hasImages && hasTables -> 0.6f
                hasTables -> 0.7f
                hasImages -> 0.8f
                hasText -> 0.9f
                else -> 0.5f
            }
            
            DocxValidationResult(
                isValid = isValidZip && hasText,
                hasText = hasText,
                hasImages = hasImages,
                hasTables = hasTables,
                hasProtection = hasProtection,
                mammothCompatibility = mammothCompatibility
            )
        } finally {
            tempFile.delete()
        }
    }
    
    override suspend fun validateDocument(uri: String): Boolean {
        return validateDocxComprehensive(uri).isValid
    }
    
    override suspend fun getPageCount(uri: String): Int? = withContext(Dispatchers.IO) {
        // Enhanced page count with better accuracy
        try {
            val text = extractText(uri)
            val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            val lines = text.lines().size
            val paragraphs = text.split("\n\n").filter { it.isNotBlank() }.size
            
            // More sophisticated calculation
            // Consider: words per page, lines per page, paragraph density
            val wordsPerPage = when {
                lines.toFloat() / words > 0.15 -> 250 // Double-spaced
                else -> 500 // Single-spaced
            }
            
            val pagesByWords = (words.toFloat() / wordsPerPage).toInt()
            val pagesByParagraphs = (paragraphs.toFloat() / 4).toInt() // Avg 4 paragraphs/page
            
            // Weighted average with bounds checking
            val estimatedPages = ((pagesByWords * 0.8 + pagesByParagraphs * 0.2).toInt())
                .coerceAtLeast(1)
                .coerceAtMost(10000) // Reasonable upper limit
            
            estimatedPages
        } catch (e: Exception) {
            null
        }
    }
}
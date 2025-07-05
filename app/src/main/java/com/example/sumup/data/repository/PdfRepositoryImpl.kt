package com.example.sumup.data.repository

import android.content.Context
import android.net.Uri
import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.model.PdfExtractionResult
import com.example.sumup.domain.model.PdfProcessingState
import com.example.sumup.domain.repository.PdfRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfRepository {

    init {
        // Initialize PDFBox resources
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun extractTextFromPdf(pdfDocument: PdfDocument): PdfExtractionResult {
        return withContext(Dispatchers.IO) {
            var document: PDDocument? = null
            try {
                val uri = Uri.parse(pdfDocument.uri)
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext PdfExtractionResult(
                        extractedText = "",
                        pageCount = 0,
                        success = false,
                        errorMessage = "Cannot open PDF file. Please check file permissions."
                    )

                inputStream.use { stream ->
                    // Check file size before loading
                    val availableBytes = stream.available()
                    if (availableBytes > 50 * 1024 * 1024) { // 50MB limit
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = 0,
                            success = false,
                            errorMessage = "PDF file too large. Maximum size is 50MB."
                        )
                    }
                    
                    try {
                        document = PDDocument.load(stream)
                    } catch (e: OutOfMemoryError) {
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = 0,
                            success = false,
                            errorMessage = "PDF too complex. Try a simpler document."
                        )
                    } catch (e: Exception) {
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = 0,
                            success = false,
                            errorMessage = when {
                                e.message?.contains("PDF header signature not found") == true -> 
                                    "Invalid PDF file. Please select a valid PDF document."
                                e.message?.contains("corrupt") == true -> 
                                    "PDF file appears to be corrupted. Try another file."
                                else -> "Cannot read PDF: ${e.message}"
                            }
                        )
                    }
                    
                    val pageCount = document.numberOfPages
                    
                    // Check page count
                    if (pageCount == 0) {
                        document.close()
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = 0,
                            success = false,
                            errorMessage = "PDF has no pages"
                        )
                    }
                    
                    if (pageCount > 100) {
                        document.close()
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = pageCount,
                            success = false,
                            errorMessage = "PDF has too many pages (${pageCount}). Maximum is 100 pages."
                        )
                    }
                    
                    // Check if document is encrypted
                    if (document.isEncrypted) {
                        document.close()
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = pageCount,
                            success = false,
                            errorMessage = "Password-protected PDFs are not supported"
                        )
                    }

                    // Extract text from all pages with progress tracking
                    val stripper = PDFTextStripper()
                    val extractedText = try {
                        stripper.getText(document)
                    } catch (e: Exception) {
                        document.close()
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = pageCount,
                            success = false,
                            errorMessage = "Failed to extract text: ${e.message}"
                        )
                    }
                    
                    document.close()

                    // Check if we got any text
                    if (extractedText.isBlank()) {
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = pageCount,
                            success = false,
                            errorMessage = "No text found. This might be a scanned PDF with images only."
                        )
                    }

                    // Calculate confidence based on extracted content
                    val confidence = calculateExtractionConfidence(extractedText, pageCount)
                    val hasTableStructure = detectTableStructure(extractedText)

                    PdfExtractionResult(
                        extractedText = extractedText.trim(),
                        pageCount = pageCount,
                        success = true,
                        confidence = confidence,
                        hasTableStructure = hasTableStructure,
                        errorMessage = null
                    )
                }
            } catch (e: SecurityException) {
                PdfExtractionResult(
                    extractedText = "",
                    pageCount = 0,
                    success = false,
                    errorMessage = "Permission denied. Please grant storage permission to access PDF files."
                )
            } catch (e: Exception) {
                PdfExtractionResult(
                    extractedText = "",
                    pageCount = 0,
                    success = false,
                    errorMessage = "Unexpected error: ${e.javaClass.simpleName}"
                )
            } finally {
                // Ensure document is always closed
                try {
                    document?.close()
                } catch (e: Exception) {
                    // Ignore close errors
                }
            }
        }
    }

    override suspend fun validatePdfFile(uri: String): PdfDocument {
        return withContext(Dispatchers.IO) {
            val androidUri = Uri.parse(uri)
            val cursor = context.contentResolver.query(
                androidUri, null, null, null, null
            )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex("_display_name")
                    val sizeIndex = it.getColumnIndex("_size")
                    
                    val fileName = if (displayNameIndex >= 0) {
                        it.getString(displayNameIndex) ?: "document.pdf"
                    } else "document.pdf"
                    
                    val size = if (sizeIndex >= 0) {
                        it.getLong(sizeIndex)
                    } else 0L
                    
                    // Validate file properties
                    when (val validationResult = com.example.sumup.utils.InputValidator.validatePdfFile(context, androidUri)) {
                        is com.example.sumup.utils.InputValidator.ValidationResult.Success,
                        is com.example.sumup.utils.InputValidator.ValidationResult.Warning -> {
                            return@withContext PdfDocument(
                                uri = uri,
                                fileName = fileName,
                                sizeBytes = size,
                                processingState = PdfProcessingState.Idle
                            )
                        }
                        is com.example.sumup.utils.InputValidator.ValidationResult.Error -> {
                            throw Exception(validationResult.message)
                        }
                    }
                } else {
                    throw Exception("Cannot access PDF file")
                }
            } ?: throw Exception("Cannot read PDF file information")
        }
    }
    override suspend fun getPdfMetadata(uri: String): PdfDocument {
        return withContext(Dispatchers.IO) {
            val baseDocument = validatePdfFile(uri)
            
            try {
                val androidUri = Uri.parse(uri)
                val inputStream = context.contentResolver.openInputStream(androidUri)
                
                inputStream?.use { stream ->
                    val document = PDDocument.load(stream)
                    val pageCount = document.numberOfPages
                    val isPasswordProtected = document.isEncrypted
                    document.close()
                    
                    baseDocument.copy(
                        pageCount = pageCount,
                        isPasswordProtected = isPasswordProtected
                    )
                } ?: baseDocument
            } catch (e: Exception) {
                // If metadata extraction fails, return base document
                baseDocument
            }
        }
    }
    
    private fun calculateExtractionConfidence(text: String, pageCount: Int): Float {
        val textLength = text.length
        val wordsPerPage = if (pageCount > 0) textLength / pageCount else textLength
        
        return when {
            textLength < 100 -> 0.2f
            wordsPerPage < 50 -> 0.3f
            text.count { it.isDigit() } > text.length * 0.8 -> 0.4f
            text.count { !it.isLetterOrDigit() && !it.isWhitespace() } > text.length * 0.3 -> 0.5f
            else -> 0.9f
        }
    }
    
    private fun detectTableStructure(text: String): Boolean {
        val lines = text.split('\n')
        val tabCount = lines.count { it.contains('\t') }
        return tabCount > lines.size * 0.3
    }
    
    override suspend fun extractTextFromPdfRange(
        uri: String,
        startPage: Int,
        endPage: Int
    ): PdfExtractionResult {
        return withContext(Dispatchers.IO) {
            var document: PDDocument? = null
            try {
                val androidUri = Uri.parse(uri)
                val inputStream = context.contentResolver.openInputStream(androidUri)
                    ?: return@withContext PdfExtractionResult(
                        extractedText = "",
                        pageCount = 0,
                        success = false,
                        errorMessage = "Cannot open PDF file"
                    )
                
                inputStream.use { stream ->
                    try {
                        document = PDDocument.load(stream)
                    } catch (e: OutOfMemoryError) {
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = 0,
                            success = false,
                            errorMessage = "Not enough memory to process this PDF chunk"
                        )
                    }
                    
                    val totalPages = document.numberOfPages
                    
                    // Validate page range
                    if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                        document?.close()
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = totalPages,
                            success = false,
                            errorMessage = "Invalid page range: $startPage-$endPage (total pages: $totalPages)"
                        )
                    }
                    
                    // Extract text from specified range
                    val stripper = PDFTextStripper().apply {
                        this.startPage = startPage
                        this.endPage = endPage
                    }
                    
                    val extractedText = try {
                        stripper.getText(document)
                    } catch (e: Exception) {
                        document?.close()
                        return@withContext PdfExtractionResult(
                            extractedText = "",
                            pageCount = endPage - startPage + 1,
                            success = false,
                            errorMessage = "Failed to extract text from pages $startPage-$endPage: ${e.message}"
                        )
                    }
                    
                    document?.close()
                    
                    // Return result
                    PdfExtractionResult(
                        extractedText = extractedText.trim(),
                        pageCount = endPage - startPage + 1,
                        success = true,
                        confidence = calculateExtractionConfidence(extractedText, endPage - startPage + 1),
                        hasTableStructure = detectTableStructure(extractedText),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                PdfExtractionResult(
                    extractedText = "",
                    pageCount = 0,
                    success = false,
                    errorMessage = "Error processing PDF range: ${e.message}"
                )
            } finally {
                try {
                    document?.close()
                } catch (e: Exception) {
                    // Ignore close errors
                }
            }
        }
    }
}
package com.example.sumup.data.repository

import android.content.Context
import android.net.Uri
import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.model.PdfExtractionResult
import com.example.sumup.domain.model.PdfProcessingState
import com.example.sumup.domain.repository.PdfRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.android.PDFBoxResourceLoader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfRepository {

    init {
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun extractTextFromPdf(pdfDocument: PdfDocument): PdfExtractionResult {
        return withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            var document: PDDocument? = null
            var inputStream: InputStream? = null
            
            try {
                val uri = Uri.parse(pdfDocument.uri)
                inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Cannot open PDF file")
                
                document = PDDocument.load(inputStream)
                
                if (document.isEncrypted) {
                    throw Exception("Password-protected PDFs are not supported")
                }
                
                val stripper = PDFTextStripper()
                val text = stripper.getText(document)
                
                val wordCount = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                val pageCount = document.numberOfPages
                val extractionTime = System.currentTimeMillis() - startTime                val confidence = calculateExtractionConfidence(text, pageCount)
                
                PdfExtractionResult(
                    text = text.trim(),
                    wordCount = wordCount,
                    pageCount = pageCount,
                    hasImages = false,
                    hasTables = text.contains("\\t") || detectTableStructure(text),
                    extractionTimeMs = extractionTime,
                    confidence = confidence
                )
                
            } catch (exception: Exception) {
                throw Exception("PDF extraction failed: ${exception.message}", exception)
            } finally {
                try {
                    document?.close()
                    inputStream?.close()
                } catch (e: Exception) {
                    // Log but don't throw
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
                    
                    PdfDocument(
                        uri = uri,
                        fileName = fileName,
                        sizeBytes = size,
                        processingState = PdfProcessingState.PENDING
                    )
                } else {
                    throw Exception("Cannot access PDF file")
                }
            } ?: throw Exception("Cannot read PDF file information")
        }
    }
    override suspend fun getPdfMetadata(uri: String): PdfDocument {
        return withContext(Dispatchers.IO) {
            val baseDocument = validatePdfFile(uri)
            // For now, return basic document info
            baseDocument
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
}
package com.example.sumup.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.InputStream

object PdfUtils {
    init {
        // Initialize PDFBox (required for Android)
        try {
            PDFBoxResourceLoader.init(null)
        } catch (e: Exception) {
            // Already initialized
        }
    }
    
    fun getPageCount(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { document ->
                    document.numberOfPages
                }
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    fun getFileName(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "Unknown.pdf"
        } catch (e: Exception) {
            "Unknown.pdf"
        }
    }
    
    fun extractText(context: Context, uri: Uri, pageRange: IntRange? = null): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { document ->
                    val textStripper = com.tom_roush.pdfbox.text.PDFTextStripper()
                    
                    if (pageRange != null) {
                        textStripper.startPage = pageRange.first
                        textStripper.endPage = pageRange.last.coerceAtMost(document.numberOfPages)
                    }
                    
                    textStripper.getText(document)
                }
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
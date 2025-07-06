package com.example.sumup.domain.repository

import com.example.sumup.domain.model.Document
import com.example.sumup.domain.model.DocumentExtractionResult

/**
 * Generic repository interface for all document types
 */
interface DocumentRepository {
    suspend fun extractTextFromDocument(document: Document): DocumentExtractionResult
    suspend fun validateDocument(uri: String): Document
    suspend fun getDocumentMetadata(uri: String): Document
    suspend fun extractTextFromDocumentRange(
        uri: String,
        startPage: Int,
        endPage: Int
    ): DocumentExtractionResult
    suspend fun getSupportedFormats(): List<String>
}
package com.example.sumup.data.remote.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API interface for server-side document conversion and processing
 */
interface DocumentConversionApi {
    
    /**
     * Convert DOC to DOCX format
     */
    @Multipart
    @POST("api/v1/convert/doc-to-docx")
    suspend fun convertDocToDocx(
        @Part file: MultipartBody.Part,
        @Header("Authorization") apiKey: String
    ): Response<ConversionResponse>
    
    /**
     * Convert DOC to plain text
     */
    @Multipart
    @POST("api/v1/convert/doc-to-text")
    suspend fun convertDocToText(
        @Part file: MultipartBody.Part,
        @Header("Authorization") apiKey: String
    ): Response<TextExtractionResponse>
    
    /**
     * Get conversion status
     */
    @GET("api/v1/convert/status/{taskId}")
    suspend fun getConversionStatus(
        @Path("taskId") taskId: String,
        @Header("Authorization") apiKey: String
    ): Response<ConversionStatusResponse>
    
    /**
     * Download converted file
     */
    @Streaming
    @GET("api/v1/convert/download/{fileId}")
    suspend fun downloadConvertedFile(
        @Path("fileId") fileId: String,
        @Header("Authorization") apiKey: String
    ): Response<ResponseBody>
}

// Response models
data class ConversionResponse(
    val success: Boolean,
    val taskId: String,
    val message: String? = null,
    val estimatedTime: Int? = null // in seconds
)

data class TextExtractionResponse(
    val success: Boolean,
    val text: String? = null,
    val pageCount: Int? = null,
    val wordCount: Int? = null,
    val metadata: DocumentMetadata? = null,
    val error: String? = null
)

data class DocumentMetadata(
    val author: String? = null,
    val title: String? = null,
    val subject: String? = null,
    val createdDate: String? = null,
    val modifiedDate: String? = null,
    val format: String? = null
)

data class ConversionStatusResponse(
    val status: ConversionStatus,
    val progress: Float? = null,
    val fileId: String? = null,
    val error: String? = null
)

enum class ConversionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    EXPIRED
}
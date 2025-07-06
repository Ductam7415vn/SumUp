package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.sumup.data.remote.api.DocumentConversionApi
import com.example.sumup.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Server-side document processor for formats that require backend processing
 * Primary use case: DOC files that cannot be processed on Android API < 26
 */
class ServerDocProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val conversionApi: DocumentConversionApi,
    private val networkUtils: NetworkUtils
) : DocumentProcessor {
    
    data class ServerProcessingResult(
        val text: String,
        val pageCount: Int? = null,
        val wordCount: Int? = null,
        val metadata: Map<String, String>? = null
    )
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        if (!networkUtils.isNetworkAvailable()) {
            throw Exception("Network connection required for DOC file processing. Please check your internet connection.")
        }
        
        try {
            // Create temp file from URI
            val tempFile = createTempFileFromUri(uri)
            
            try {
                // Upload and convert
                val result = uploadAndConvertToText(tempFile)
                result.text
            } finally {
                tempFile.delete()
            }
        } catch (e: Exception) {
            throw Exception("Failed to process DOC file: ${e.message}", e)
        }
    }
    
    private suspend fun createTempFileFromUri(uri: String): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("doc_upload_", ".doc", context.cacheDir)
            
            context.contentResolver.openInputStream(Uri.parse(uri))?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: throw Exception("Cannot open document file")
            
            tempFile
        }
    }
    
    private suspend fun uploadAndConvertToText(file: File): ServerProcessingResult {
        val requestFile = file.asRequestBody("application/msword".toMediaType())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        
        // Get API key from somewhere (you'll need to inject this)
        val apiKey = getApiKey()
        
        val response = conversionApi.convertDocToText(body, apiKey)
        
        if (response.isSuccessful) {
            val extractionResponse = response.body()
            if (extractionResponse?.success == true && extractionResponse.text != null) {
                return ServerProcessingResult(
                    text = extractionResponse.text,
                    pageCount = extractionResponse.pageCount,
                    wordCount = extractionResponse.wordCount,
                    metadata = extractionResponse.metadata?.let { metadata ->
                        mapOf(
                            "author" to (metadata.author ?: ""),
                            "title" to (metadata.title ?: ""),
                            "created" to (metadata.createdDate ?: "")
                        )
                    }
                )
            } else {
                throw Exception(extractionResponse?.error ?: "Conversion failed")
            }
        } else {
            throw Exception("Server error: ${response.code()} - ${response.message()}")
        }
    }
    
    /**
     * Convert DOC to DOCX with progress tracking
     */
    suspend fun convertDocToDocxWithProgress(uri: String): Flow<ConversionProgress> = flow {
        emit(ConversionProgress.Starting)
        
        try {
            val tempFile = createTempFileFromUri(uri)
            
            try {
                // Start conversion
                val requestFile = tempFile.asRequestBody("application/msword".toMediaType())
                val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
                val apiKey = getApiKey()
                
                val conversionResponse = conversionApi.convertDocToDocx(body, apiKey)
                
                if (conversionResponse.isSuccessful) {
                    val conversion = conversionResponse.body()
                    if (conversion?.success == true) {
                        val taskId = conversion.taskId
                        emit(ConversionProgress.Processing(0f, "Conversion started..."))
                        
                        // Poll for status
                        var attempts = 0
                        val maxAttempts = 60 // 1 minute timeout
                        
                        while (attempts < maxAttempts) {
                            delay(1000) // Poll every second
                            
                            val statusResponse = conversionApi.getConversionStatus(taskId, apiKey)
                            if (statusResponse.isSuccessful) {
                                val status = statusResponse.body()
                                
                                when (status?.status) {
                                    com.example.sumup.data.remote.api.ConversionStatus.PROCESSING -> {
                                        val progress = status.progress ?: (attempts.toFloat() / maxAttempts)
                                        emit(ConversionProgress.Processing(
                                            progress,
                                            "Converting... ${(progress * 100).toInt()}%"
                                        ))
                                    }
                                    com.example.sumup.data.remote.api.ConversionStatus.COMPLETED -> {
                                        status.fileId?.let { fileId ->
                                            val docxFile = downloadConvertedFile(fileId, apiKey)
                                            emit(ConversionProgress.Completed(docxFile))
                                            return@flow
                                        }
                                    }
                                    com.example.sumup.data.remote.api.ConversionStatus.FAILED -> {
                                        throw Exception(status.error ?: "Conversion failed")
                                    }
                                    else -> {}
                                }
                            }
                            
                            attempts++
                        }
                        
                        throw Exception("Conversion timeout")
                    } else {
                        throw Exception(conversion?.message ?: "Conversion failed")
                    }
                } else {
                    throw Exception("Server error: ${conversionResponse.code()}")
                }
            } finally {
                tempFile.delete()
            }
        } catch (e: Exception) {
            emit(ConversionProgress.Failed(e.message ?: "Unknown error"))
        }
    }
    
    private suspend fun downloadConvertedFile(fileId: String, apiKey: String): File {
        val response = conversionApi.downloadConvertedFile(fileId, apiKey)
        
        if (response.isSuccessful) {
            val responseBody = response.body() ?: throw Exception("Empty response")
            
            val convertedFile = File.createTempFile("converted_", ".docx", context.cacheDir)
            
            FileOutputStream(convertedFile).use { output ->
                responseBody.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
            
            return convertedFile
        } else {
            throw Exception("Download failed: ${response.code()}")
        }
    }
    
    override suspend fun getPageCount(uri: String): Int? {
        // Server-side processing can provide accurate page count
        return try {
            val result = extractTextWithMetadata(uri)
            result.pageCount
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun validateDocument(uri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(Uri.parse(uri))?.use { inputStream ->
                // Check for DOC header
                val buffer = ByteArray(8)
                val bytesRead = inputStream.read(buffer)
                
                if (bytesRead >= 8) {
                    // Check for Microsoft Office header (D0CF11E0A1B11AE1)
                    buffer[0] == 0xD0.toByte() && 
                    buffer[1] == 0xCF.toByte() &&
                    buffer[2] == 0x11.toByte() &&
                    buffer[3] == 0xE0.toByte()
                } else {
                    false
                }
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    private suspend fun extractTextWithMetadata(uri: String): ServerProcessingResult {
        // Similar to extractText but returns full result
        return withContext(Dispatchers.IO) {
            val tempFile = createTempFileFromUri(uri)
            try {
                uploadAndConvertToText(tempFile)
            } finally {
                tempFile.delete()
            }
        }
    }
    
    private fun getApiKey(): String {
        // TODO: Get from your API key manager
        return "Bearer your-api-key-here"
    }
    
    sealed class ConversionProgress {
        object Starting : ConversionProgress()
        data class Processing(val progress: Float, val message: String) : ConversionProgress()
        data class Completed(val file: File) : ConversionProgress()
        data class Failed(val error: String) : ConversionProgress()
    }
}
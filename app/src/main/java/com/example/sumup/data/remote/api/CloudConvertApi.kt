package com.example.sumup.data.remote.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Cloud conversion services that don't require your own backend
 * Options:
 * 1. CloudConvert API - https://cloudconvert.com/api/v2
 * 2. Zamzar API - https://developers.zamzar.com/
 * 3. ConvertAPI - https://www.convertapi.com/
 */
interface CloudConvertApi {
    
    // CloudConvert API Example
    @Multipart
    @POST("v2/convert/doc/to/txt")
    suspend fun convertDocToText(
        @Header("Authorization") apiKey: String,
        @Part file: MultipartBody.Part
    ): Response<CloudConvertResponse>
    
    // ConvertAPI Example  
    @Multipart
    @POST("convert/doc/to/txt")
    suspend fun convertDocToTextSimple(
        @Query("Secret") apiKey: String,
        @Part("File") file: RequestBody
    ): Response<ConvertApiResponse>
}

data class CloudConvertResponse(
    val data: ConversionData
)

data class ConversionData(
    val id: String,
    val status: String,
    val output: OutputData?
)

data class OutputData(
    val url: String,
    val size: Long
)

data class ConvertApiResponse(
    val ConversionCost: Int,
    val File: String, // Base64 encoded result
    val FileExt: String,
    val FileSize: Int
)
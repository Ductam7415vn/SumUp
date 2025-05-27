package com.example.sumup.data.remote.dto

data class SummarizeRequest(
    val text: String,
    val style: String,
    val maxLength: Int,
    val language: String = "auto"
)

data class SummarizeResponse(
    val summary: String,
    val bullets: List<String>,
    val confidence: Float,
    val processingTime: Long
)
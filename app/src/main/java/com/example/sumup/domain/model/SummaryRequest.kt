package com.example.sumup.domain.model

data class SummaryRequest(
    val text: String? = null,
    val pdfUri: String? = null,
    val persona: SummaryPersona = SummaryPersona.GENERAL,
    val inputType: InputType = InputType.TEXT,
    val maxLength: Int? = null
) {
    enum class InputType {
        TEXT,
        PDF,
        OCR
    }
    
    fun isValid(): Boolean = when (inputType) {
        InputType.TEXT -> !text.isNullOrBlank() && text.length >= 50
        InputType.PDF -> !pdfUri.isNullOrBlank()
        InputType.OCR -> !text.isNullOrBlank() && text.length >= 50
    }
    
    fun getContentLength(): Int = when (inputType) {
        InputType.TEXT, InputType.OCR -> text?.length ?: 0
        InputType.PDF -> 0 // Will be calculated after extraction
    }
}
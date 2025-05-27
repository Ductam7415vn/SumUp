package com.example.sumup.domain.model

/**
 * Represents the state of a processing operation
 */
data class ProcessingState(
    val progress: Float,
    val message: String,
    val showTimeout: Boolean = false
)

/**
 * Represents the state of summary creation process
 */
sealed class SummaryCreationState {
    
    /**
     * Processing is in progress
     */
    data class Processing(
        val progress: Float,
        val message: String,
        val timeoutLevel: Int = 0
    ) : SummaryCreationState()
    
    /**
     * Summary creation was successful
     */
    data class Success(
        val summary: Summary
    ) : SummaryCreationState()
    
    /**
     * An error occurred during creation
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : SummaryCreationState()
}

/**
 * Represents different error types that can occur
 */
sealed class SumUpError : Exception() {
    object NetworkError : SumUpError()
    object ApiRateLimitError : SumUpError()
    object TextTooShortError : SumUpError()
    object OCRFailedError : SumUpError()
    object StorageFullError : SumUpError()
    data class UnknownError(override val message: String) : SumUpError()
}

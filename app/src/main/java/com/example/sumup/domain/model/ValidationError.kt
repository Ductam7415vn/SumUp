package com.example.sumup.domain.model

/**
 * Validation error types
 */
enum class ValidationError {
    TEXT_TOO_SHORT,
    TEXT_TOO_LONG,
    INVALID_FORMAT,
    EMPTY_CONTENT,
    FILE_TOO_LARGE,
    UNSUPPORTED_FORMAT
}
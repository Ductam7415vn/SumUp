# SumUp API Reference

This document provides a comprehensive reference for all APIs used in the SumUp application.

## 📋 Table of Contents
1. [Gemini AI API](#gemini-ai-api)
2. [Internal APIs](#internal-apis)
3. [Data Models](#data-models)
4. [Error Handling](#error-handling)

## 🤖 Gemini AI API

### Base Configuration

```kotlin
object GeminiConfig {
    const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"
    const val MODEL = "gemini-1.5-flash"
    const val API_VERSION = "v1beta"
}
```

### Endpoints

#### Generate Content (Text Summarization)

```kotlin
@POST("models/{model}:generateContent")
suspend fun generateContent(
    @Path("model") model: String,
    @Query("key") apiKey: String,
    @Body request: GenerateContentRequest
): GenerateContentResponse
```

**Request Model:**
```kotlin
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySettings>? = null
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val topK: Int = 40,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 1024
)
```

**Response Model:**
```kotlin
data class GenerateContentResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content,
    val finishReason: String,
    val safetyRatings: List<SafetyRating>
)
```

### Rate Limits

| Tier | Requests/Minute | Tokens/Day |
|------|----------------|------------|
| Free | 60 | 1,000,000 |
| Pro | 1000 | 4,000,000 |

## 🔧 Internal APIs

### Summary Repository

```kotlin
interface SummaryRepository {
    // Create new summary
    suspend fun createSummary(request: SummaryRequest): Result<Summary>
    
    // Get summary by ID
    suspend fun getSummaryById(id: String): Result<Summary>
    
    // Get all summaries
    fun getAllSummaries(): Flow<List<Summary>>
    
    // Search summaries
    fun searchSummaries(query: String): Flow<List<Summary>>
    
    // Delete summary
    suspend fun deleteSummary(id: String): Result<Unit>
    
    // Update favorite status
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean): Result<Unit>
}
```

### PDF Repository

```kotlin
interface PdfRepository {
    // Extract text from PDF
    suspend fun extractTextFromPdf(uri: Uri): Result<PdfExtractionResult>
    
    // Validate PDF
    suspend fun validatePdf(uri: Uri): Result<PdfValidation>
    
    // Get PDF metadata
    suspend fun getPdfMetadata(uri: Uri): Result<PdfMetadata>
}
```

### Settings Repository

```kotlin
interface SettingsRepository {
    // Theme preferences
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
    
    // Language preferences
    fun getPreferredLanguage(): Flow<String>
    suspend fun setPreferredLanguage(language: String)
    
    // API configuration
    fun getApiKey(): Flow<String?>
    suspend fun setApiKey(key: String)
    
    // Summary preferences
    fun getDefaultPersona(): Flow<SummaryPersona>
    suspend fun setDefaultPersona(persona: SummaryPersona)
}
```

## 📊 Data Models

### Core Models

#### Summary
```kotlin
data class Summary(
    val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val summaryText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val wordCount: Int,
    val reductionPercentage: Float,
    val persona: SummaryPersona = SummaryPersona.GENERAL,
    val inputType: InputType,
    val isFavorite: Boolean = false,
    val bulletPoints: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)
```

#### SummaryRequest
```kotlin
data class SummaryRequest(
    val text: String,
    val persona: SummaryPersona = SummaryPersona.GENERAL,
    val inputType: InputType,
    val options: SummaryOptions = SummaryOptions()
)

data class SummaryOptions(
    val maxLength: Int? = null,
    val bulletPoints: Boolean = false,
    val preserveFormatting: Boolean = false
)
```

#### PdfExtractionResult
```kotlin
data class PdfExtractionResult(
    val text: String,
    val pageCount: Int,
    val metadata: PdfMetadata,
    val extractionTime: Long
)

data class PdfMetadata(
    val title: String?,
    val author: String?,
    val subject: String?,
    val keywords: List<String>,
    val creationDate: Long?,
    val fileSize: Long
)
```

### Enums

#### SummaryPersona
```kotlin
enum class SummaryPersona {
    GENERAL,      // Balanced, general-purpose summary
    EDUCATIONAL,  // Academic, learning-focused
    BUSINESS,     // Professional, actionable insights
    TECHNICAL     // Technical details preserved
}
```

#### InputType
```kotlin
enum class InputType {
    TEXT,    // Manual text input
    PDF,     // PDF document
    OCR      // Camera/image text
}
```

#### ProcessingState
```kotlin
sealed class ProcessingState {
    object Idle : ProcessingState()
    object Validating : ProcessingState()
    object Extracting : ProcessingState()
    object Summarizing : ProcessingState()
    data class Success(val summary: Summary) : ProcessingState()
    data class Error(val error: AppError) : ProcessingState()
}
```

## ❌ Error Handling

### Error Types

```kotlin
sealed class AppError : Exception() {
    // Network errors
    data class NetworkError(override val message: String) : AppError()
    data class ApiError(val code: Int, override val message: String) : AppError()
    data class RateLimitError(val retryAfter: Long) : AppError()
    
    // Validation errors
    data class ValidationError(override val message: String) : AppError()
    data class TextTooShortError(val minLength: Int) : AppError()
    data class TextTooLongError(val maxLength: Int) : AppError()
    
    // File errors
    data class FileNotFoundError(val uri: Uri) : AppError()
    data class FileTooLargeError(val maxSize: Long) : AppError()
    data class UnsupportedFileTypeError(val mimeType: String) : AppError()
    
    // Processing errors
    data class PdfExtractionError(override val message: String) : AppError()
    data class OcrError(override val message: String) : AppError()
    
    // Generic errors
    data class UnknownError(override val message: String) : AppError()
}
```

### Error Response Format

```kotlin
data class ErrorResponse(
    val error: ErrorDetail
)

data class ErrorDetail(
    val code: Int,
    val message: String,
    val status: String,
    val details: List<ErrorInfo>? = null
)

data class ErrorInfo(
    val type: String,
    val reason: String,
    val domain: String,
    val metadata: Map<String, String>? = null
)
```

### Error Handling Example

```kotlin
class GeminiErrorHandler {
    fun handleError(throwable: Throwable): AppError {
        return when (throwable) {
            is HttpException -> handleHttpError(throwable)
            is IOException -> AppError.NetworkError("Network connection error")
            is JsonParseException -> AppError.ApiError(
                code = -1,
                message = "Invalid response format"
            )
            else -> AppError.UnknownError(
                throwable.message ?: "An unexpected error occurred"
            )
        }
    }
    
    private fun handleHttpError(exception: HttpException): AppError {
        return when (exception.code()) {
            401 -> AppError.ApiError(401, "Invalid API key")
            429 -> AppError.RateLimitError(
                retryAfter = exception.response()?.headers()
                    ?.get("Retry-After")?.toLongOrNull() ?: 60000
            )
            403 -> AppError.ApiError(403, "Access forbidden")
            404 -> AppError.ApiError(404, "Resource not found")
            500, 502, 503 -> AppError.ApiError(
                exception.code(),
                "Server error. Please try again later."
            )
            else -> AppError.ApiError(
                exception.code(),
                exception.message ?: "Unknown error"
            )
        }
    }
}
```

## 🔐 Security

### API Key Management

```kotlin
class ApiKeyManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {
    fun setApiKey(key: String) {
        encryptedPrefs.edit()
            .putString(API_KEY_PREF, key)
            .apply()
    }
    
    fun getApiKey(): String? {
        return encryptedPrefs.getString(API_KEY_PREF, null)
    }
    
    fun validateApiKey(key: String): Boolean {
        return key.matches(Regex("^[A-Za-z0-9_-]{39}$"))
    }
}
```

### Request Interceptor

```kotlin
class AuthInterceptor @Inject constructor(
    private val apiKeyManager: ApiKeyManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val apiKey = apiKeyManager.getApiKey()
        
        if (apiKey == null) {
            throw AppError.ApiError(401, "API key not configured")
        }
        
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()
        
        val request = originalRequest.newBuilder()
            .url(url)
            .build()
        
        return chain.proceed(request)
    }
}
```

---

*For implementation details, see [Technical Architecture](../architecture/02-technical-architecture.md)*
# TÀI LIỆU 3: THIẾT KẾ KIẾN TRÚC HỆ THỐNG
## SUMUP - ỨNG DỤNG TÓM TẮT VĂN BẢN THÔNG MINH

---

## 1. TÀI LIỆU KIẾN TRÚC TỔNG THỂ

### 1.1. Tổng quan Hệ thống

**SumUp** được xây dựng theo **Clean Architecture** với pattern **MVVM**, đảm bảo:
- ✅ Separation of Concerns (Tách biệt trách nhiệm)
- ✅ Testability (Dễ kiểm thử)
- ✅ Maintainability (Dễ bảo trì)
- ✅ Scalability (Dễ mở rộng)

**Technology Stack:**
```
Frontend:  Jetpack Compose + Material 3
Backend:   Kotlin Coroutines + Flow
Database:  Room 2.6.1
DI:        Hilt 2.51
Network:   Retrofit 2.11.0 + OkHttp 4.12.0
AI:        Google Gemini 1.5 Flash API
```

### 1.2. Kiến trúc 3 tầng (Clean Architecture)

```
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                      │
│  ┌────────────┐  ┌────────────┐  ┌──────────────────┐  │
│  │   Screens  │  │ ViewModels │  │   UI Components  │  │
│  │  (Compose) │  │ (StateFlow)│  │  (Material 3)    │  │
│  └────────────┘  └────────────┘  └──────────────────┘  │
│         ↓                ↓                    ↓          │
└─────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                         │
│  ┌────────────┐  ┌────────────┐  ┌──────────────────┐  │
│  │  Use Cases │  │   Models   │  │   Repositories   │  │
│  │            │  │  (Entities)│  │   (Interfaces)   │  │
│  └────────────┘  └────────────┘  └──────────────────┘  │
│         ↓                ↓                    ↓          │
└─────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│                     DATA LAYER                           │
│  ┌────────────┐  ┌────────────┐  ┌──────────────────┐  │
│  │  Room DB   │  │ Retrofit   │  │  Local Files     │  │
│  │  (Local)   │  │ (Remote)   │  │  (PDF/DOCX)      │  │
│  └────────────┘  └────────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 1.3. Component Diagram

```
┌──────────────────────────────────────────────────────┐
│                    SumUp Application                  │
│                                                       │
│  ┌─────────────────────────────────────────────┐    │
│  │  UI Layer (Jetpack Compose)                 │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐    │    │
│  │  │  Main    │ │  Result  │ │ History  │    │    │
│  │  │  Screen  │ │  Screen  │ │  Screen  │    │    │
│  │  └──────────┘ └──────────┘ └──────────┘    │    │
│  └─────────────────────────────────────────────┘    │
│                       ↓ ↑                             │
│  ┌─────────────────────────────────────────────┐    │
│  │  ViewModel Layer                            │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐    │    │
│  │  │   Main   │ │  Result  │ │ History  │    │    │
│  │  │ViewModel │ │ViewModel │ │ViewModel │    │    │
│  │  └──────────┘ └──────────┘ └──────────┘    │    │
│  └─────────────────────────────────────────────┘    │
│                       ↓ ↑                             │
│  ┌─────────────────────────────────────────────┐    │
│  │  Use Case Layer (Business Logic)            │    │
│  │  ┌─────────────────┐ ┌───────────────────┐ │    │
│  │  │ Summarize       │ │ Process           │ │    │
│  │  │ TextUseCase     │ │ DocumentUseCase   │ │    │
│  │  └─────────────────┘ └───────────────────┘ │    │
│  └─────────────────────────────────────────────┘    │
│                       ↓ ↑                             │
│  ┌─────────────────────────────────────────────┐    │
│  │  Repository Layer                           │    │
│  │  ┌─────────────┐ ┌────────────────────────┐│    │
│  │  │  Summary    │ │  Settings              ││    │
│  │  │  Repository │ │  Repository            ││    │
│  │  └─────────────┘ └────────────────────────┘│    │
│  └─────────────────────────────────────────────┘    │
│                       ↓ ↑                             │
│  ┌─────────────────────────────────────────────┐    │
│  │  Data Sources                               │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐    │    │
│  │  │  Room DB │ │ Gemini   │ │  Local   │    │    │
│  │  │          │ │   API    │ │  Files   │    │    │
│  │  └──────────┘ └──────────┘ └──────────┘    │    │
│  └─────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────┘
```

### 1.4. Module Structure

```
app/
├── src/main/java/com/example/sumup/
│   ├── data/
│   │   ├── local/
│   │   │   ├── dao/
│   │   │   │   └── SummaryDao.kt
│   │   │   ├── database/
│   │   │   │   └── SumUpDatabase.kt
│   │   │   ├── entity/
│   │   │   │   └── SummaryEntity.kt
│   │   │   └── converter/
│   │   │       └── Converters.kt
│   │   ├── remote/
│   │   │   ├── api/
│   │   │   │   ├── GeminiApiService.kt
│   │   │   │   └── EnhancedGeminiApiService.kt
│   │   │   ├── dto/
│   │   │   │   └── SummarizeDto.kt
│   │   │   ├── mock/
│   │   │   │   └── MockGeminiApiService.kt
│   │   │   └── security/
│   │   │       └── SecureApiKeyProvider.kt
│   │   └── repository/
│   │       ├── SummaryRepositoryImpl.kt
│   │       ├── SettingsRepositoryImpl.kt
│   │       └── PdfRepositoryImpl.kt
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Summary.kt
│   │   │   ├── AppError.kt
│   │   │   ├── ProcessingState.kt
│   │   │   └── SummaryMetrics.kt
│   │   ├── repository/
│   │   │   ├── SummaryRepository.kt
│   │   │   ├── SettingsRepository.kt
│   │   │   └── PdfRepository.kt
│   │   ├── usecase/
│   │   │   ├── SummarizeTextUseCase.kt
│   │   │   ├── ProcessDocumentUseCase.kt
│   │   │   ├── SmartSectioningUseCase.kt
│   │   │   └── DocumentProcessorFactory.kt
│   │   └── worker/
│   │       └── SummarizationWorker.kt
│   ├── presentation/
│   │   ├── screens/
│   │   │   ├── main/
│   │   │   │   ├── MainScreen.kt
│   │   │   │   ├── MainViewModel.kt
│   │   │   │   └── MainUiState.kt
│   │   │   ├── result/
│   │   │   ├── history/
│   │   │   ├── settings/
│   │   │   ├── ocr/
│   │   │   └── processing/
│   │   ├── navigation/
│   │   │   ├── SumUpNavigation.kt
│   │   │   └── TransitionNavigation.kt
│   │   ├── components/
│   │   └── theme/
│   ├── di/
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   ├── RepositoryModule.kt
│   │   └── UtilsModule.kt
│   └── utils/
│       ├── EnhancedApiKeyManager.kt
│       ├── InputValidator.kt
│       ├── ApiUsageTracker.kt
│       └── WorkManagerHelper.kt
```

---

## 2. MÔ TẢ CHI TIẾT CÁC THÀNH PHẦN

### 2.1. Presentation Layer

#### **ViewModels (7 total)**
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val summarizeTextUseCase: SummarizeTextUseCase,
    private val draftManager: DraftManager,
    private val apiKeyManager: EnhancedApiKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun summarize() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            summarizeTextUseCase(inputText, persona)
                .catch { error -> handleError(error) }
                .collect { result ->
                    _uiState.update { it.copy(summary = result) }
                }
        }
    }
}
```

**State Management:**
- Immutable UI States
- StateFlow cho reactive updates
- Error handling với sealed AppError class
- Haptic feedback integration

#### **Navigation**
```kotlin
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Ocr : Screen("ocr")
    object Processing : Screen("processing")
    object Result : Screen("result?summaryId={summaryId}")
    object Settings : Screen("settings")
    object History : Screen("history")
}

@Composable
fun SumUpNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(/*...*/)
        }
        // ... other screens
    }
}
```

### 2.2. Domain Layer

#### **Use Cases**
```kotlin
class SummarizeTextUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    suspend operator fun invoke(
        text: String,
        persona: SummaryPersona
    ): Flow<Result<Summary>> = flow {
        // Validation
        if (text.length < 10) {
            throw AppError.TextTooShortError
        }

        // Process
        val result = repository.summarizeText(text, persona)
        emit(Result.success(result))
    }
}

class ProcessDocumentUseCase @Inject constructor(
    private val processorFactory: DocumentProcessorFactory,
    private val summarizeUseCase: SummarizeTextUseCase
) {
    suspend operator fun invoke(document: Document): Flow<Summary> {
        val processor = processorFactory.getProcessor(document.type)
        val extractedText = processor.extractText(document)
        return summarizeUseCase(extractedText, persona)
    }
}
```

#### **Domain Models**
```kotlin
data class Summary(
    val id: String,
    val originalText: String,
    val summaryText: String,
    val metrics: SummaryMetrics,
    val persona: SummaryPersona,
    val timestamp: Long
)

sealed class AppError(val message: String) {
    object NetworkError : AppError("No internet")
    object ApiKeyError : AppError("API key required")
    data class RateLimitError(val resetTime: Long) : AppError("Rate limit")
}

data class SummaryMetrics(
    val originalWordCount: Int,
    val summaryWordCount: Int,
    val reductionPercentage: Float,
    val readingTimeSaved: Int
)
```

### 2.3. Data Layer

#### **Repository Implementation**
```kotlin
class SummaryRepositoryImpl @Inject constructor(
    private val api: GeminiApiService,
    private val dao: SummaryDao,
    private val mapper: SummaryMapper
) : SummaryRepository {

    override suspend fun summarizeText(
        text: String,
        persona: SummaryPersona
    ): Summary {
        // Call API
        val response = api.generateContent(
            buildRequest(text, persona)
        )

        // Map to domain
        val summary = mapper.toDomain(response)

        // Save locally
        dao.insert(mapper.toEntity(summary))

        return summary
    }

    override fun getSummaryHistory(): Flow<List<Summary>> {
        return dao.getAllSummaries()
            .map { entities -> entities.map(mapper::toDomain) }
    }
}
```

#### **Database Schema**
```kotlin
@Database(
    entities = [SummaryEntity::class, ApiKeyEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class, SummaryConverters::class)
abstract class SumUpDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
    abstract fun apiKeyDao(): ApiKeyDao
}

@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries ORDER BY timestamp DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(summary: SummaryEntity)

    @Query("SELECT * FROM summaries WHERE id = :summaryId")
    suspend fun getSummaryById(summaryId: String): SummaryEntity?

    @Delete
    suspend fun delete(summary: SummaryEntity)
}
```

---

## 3. SEQUENCE DIAGRAMS

### 3.1. Text Summarization Flow

```
User → MainScreen → MainViewModel → SummarizeTextUseCase → SummaryRepository
                                                              ↓
                                                    GeminiApiService
                                                              ↓
                                                         Gemini API
                                                              ↓
                                                    Response Processing
                                                              ↓
User ← ResultScreen ← StateFlow Update ← Repository ← Mapper ← API Response
```

**Chi tiết:**
```
┌────┐      ┌────────┐      ┌─────────┐      ┌─────────┐      ┌────────┐
│User│      │MainView│      │UseCase  │      │Repo     │      │API     │
└────┘      │Model   │      │         │      │         │      │        │
  │         └────────┘      └─────────┘      └─────────┘      └────────┘
  │
  │ tap "Summarize"
  │──────────────>
  │
  │         │ summarize()
  │         │──────────────>
  │
  │                        │ invoke(text, persona)
  │                        │──────────────────────>
  │
  │                                              │ summarizeText()
  │                                              │──────────────>
  │
  │                                                        │ API Call
  │                                                        │────────>
  │                                                        │
  │                                                        │<────────
  │                                              │<──────────────
  │                        │<──────────────────────
  │         │<──────────────
  │
  │         │ update UI State
  │<──────────────────────
  │
  │ Navigate to Result
  │──────────────>
```

### 3.2. PDF Processing Flow

```
User → FileUploadScreen → MainViewModel → ProcessDocumentUseCase
                                              ↓
                                    DocumentProcessorFactory
                                              ↓
                                       PdfDocumentProcessor
                                              ↓
                                    PDFBox Extraction (local)
                                              ↓
                                   SmartSectioningUseCase (if large)
                                              ↓
                                   ParallelSectionProcessor
                                              ↓
User ← ProcessingScreen ← Progress Updates ← Multiple API Calls → Gemini API
                                              ↓
User ← ResultScreen ← Combined Summary ← Merge Results
```

### 3.3. API Key Management Flow

```
User → SettingsScreen → SettingsViewModel → EnhancedApiKeyManager
                                                      ↓
                                             Validate Key Format
                                                      ↓
                                            (Optional) Test API Call
                                                      ↓
                                         Encrypt with Security Crypto
                                                      ↓
                                         Store in EncryptedSharedPrefs
                                                      ↓
User ← Success Message ← Update UI State ← Key Saved Successfully
```

---

## 4. CLASS DIAGRAMS

### 4.1. Core Domain Classes

```
┌─────────────────────────┐
│      Summary            │
├─────────────────────────┤
│ - id: String            │
│ - originalText: String  │
│ - summaryText: String   │
│ - metrics: Metrics      │
│ - persona: Persona      │
│ - timestamp: Long       │
├─────────────────────────┤
│ + calculateMetrics()    │
│ + toEntity()            │
└─────────────────────────┘
           ↑
           │ uses
           │
┌─────────────────────────┐
│   SummaryMetrics        │
├─────────────────────────┤
│ - originalWordCount: Int│
│ - summaryWordCount: Int │
│ - reductionPct: Float   │
│ - readingTimeSaved: Int │
├─────────────────────────┤
│ + calculate()           │
└─────────────────────────┘

┌─────────────────────────┐
│   SummaryPersona        │
│   (enum)                │
├─────────────────────────┤
│ GENERAL                 │
│ STUDENT                 │
│ PROFESSIONAL            │
│ ACADEMIC                │
│ CREATIVE                │
│ QUICK_BRIEF             │
└─────────────────────────┘
```

### 4.2. Repository Pattern

```
┌───────────────────────────┐
│  <<interface>>            │
│  SummaryRepository        │
├───────────────────────────┤
│ + summarizeText(): Summary│
│ + getHistory(): Flow<List>│
│ + deleteSummary()         │
│ + getSummaryById()        │
└───────────────────────────┘
            △
            │ implements
            │
┌───────────────────────────┐
│ SummaryRepositoryImpl     │
├───────────────────────────┤
│ - api: GeminiApiService   │
│ - dao: SummaryDao         │
│ - mapper: SummaryMapper   │
├───────────────────────────┤
│ + summarizeText()         │
│ + getHistory()            │
│ + deleteSummary()         │
└───────────────────────────┘
        │                │
        │ uses          │ uses
        ▼                ▼
┌─────────────┐   ┌──────────────┐
│GeminiApiServ│   │  SummaryDao  │
└─────────────┘   └──────────────┘
```

### 4.3. Use Case Pattern

```
┌────────────────────────────┐
│  SummarizeTextUseCase      │
├────────────────────────────┤
│ - repository: Repository   │
│ - validator: Validator     │
├────────────────────────────┤
│ + invoke(): Flow<Summary>  │
└────────────────────────────┘
            │ uses
            ▼
┌────────────────────────────┐
│  ProcessDocumentUseCase    │
├────────────────────────────┤
│ - factory: ProcessorFactory│
│ - summarizeUC: UseCase     │
├────────────────────────────┤
│ + invoke(): Flow<Summary>  │
└────────────────────────────┘
            │ uses
            ▼
┌────────────────────────────┐
│  DocumentProcessorFactory  │
├────────────────────────────┤
│ + getProcessor(): Processor│
└────────────────────────────┘
            │ creates
            ▼
┌────────────────────────────┐
│  <<interface>>             │
│  DocumentProcessor         │
├────────────────────────────┤
│ + extractText(): String    │
└────────────────────────────┘
       △          △          △
       │          │          │
   ┌───┴───┐  ┌──┴────┐  ┌──┴────┐
   │PdfProc│  │DocxProc│ │TxtProc│
   └───────┘  └────────┘  └───────┘
```

---

## 5. DEPENDENCY INJECTION (HILT)

### 5.1. Module Structure

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SumUpDatabase {
        return Room.databaseBuilder(
            context,
            SumUpDatabase::class.java,
            "sumup_database"
        ).build()
    }

    @Provides
    fun provideSummaryDao(database: SumUpDatabase): SummaryDao {
        return database.summaryDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(/* logging */)
            .addInterceptor(/* caching */)
            .certificatePinner(/* pinning for prod */)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApiService(
        retrofit: Retrofit,
        apiKeyManager: EnhancedApiKeyManager
    ): GeminiApiService {
        val activeKey = apiKeyManager.getActiveApiKey()
        return if (activeKey != null) {
            EnhancedGeminiApiService(retrofit, activeKey)
        } else {
            MockGeminiApiService()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSummaryRepository(
        impl: SummaryRepositoryImpl
    ): SummaryRepository

    @Binds
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
```

### 5.2. Dependency Graph

```
Application
    │
    ├── SingletonComponent
    │   ├── Database (Room)
    │   ├── Retrofit
    │   ├── OkHttpClient
    │   ├── Repositories
    │   ├── Use Cases
    │   ├── ApiKeyManager
    │   └── WorkManager
    │
    ├── ViewModelComponent (ViewModel scope)
    │   ├── ViewModels (injected via constructor)
    │   └── Use Cases (from Singleton)
    │
    └── ActivityComponent
        └── MainActivity (Single Activity)
```

---

## 6. API DOCUMENTATION

### 6.1. Gemini API Integration

**Base URL:** `https://generativelanguage.googleapis.com/`

**Endpoints:**
```
POST /v1/models/gemini-1.5-flash:generateContent
Authorization: x-goog-api-key: {API_KEY}
Content-Type: application/json

Request:
{
  "contents": [{
    "parts": [{"text": "Summarize: {text}"}]
  }],
  "generationConfig": {
    "temperature": 0.7,
    "maxOutputTokens": 1024,
    "topP": 0.8,
    "topK": 40
  }
}

Response:
{
  "candidates": [{
    "content": {
      "parts": [{"text": "Summary..."}],
      "role": "model"
    },
    "finishReason": "STOP"
  }],
  "usageMetadata": {
    "promptTokenCount": 250,
    "candidatesTokenCount": 150,
    "totalTokenCount": 400
  }
}
```

### 6.2. Internal API Interfaces

```kotlin
interface GeminiApiService {
    @POST("v1/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

class EnhancedGeminiApiService(
    private val retrofit: Retrofit,
    private val apiKey: String,
    private val apiKeyManager: EnhancedApiKeyManager,
    private val usageTracker: ApiUsageTracker
) : GeminiApiService {

    override suspend fun generateContent(
        apiKey: String,
        request: GenerateContentRequest
    ): GenerateContentResponse = withContext(Dispatchers.IO) {
        // Retry logic
        retry(times = 3, delayMillis = 1000) {
            // Track usage
            usageTracker.trackRequest()

            // Make API call
            val response = api.generateContent(activeKey, request)

            // Handle errors
            if (response.error != null) {
                handleApiError(response.error)
            }

            response
        }
    }
}
```

### 6.3. DTO Models

```kotlin
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val parts: List<Part>,
    val role: String = "user"
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val maxOutputTokens: Int = 1024,
    val topP: Float = 0.8f,
    val topK: Int = 40
)

data class GenerateContentResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata?,
    val error: ApiError?
)
```

---

## 7. BẢO MẬT & PERFORMANCE

### 7.1. Security Architecture

**API Key Security:**
```kotlin
class SecureApiKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiKey(key: String) {
        encryptedPrefs.edit()
            .putString("api_key", key)
            .apply()
    }

    fun getApiKey(): String? {
        return encryptedPrefs.getString("api_key", null)
    }
}
```

**Certificate Pinning (Production):**
```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("generativelanguage.googleapis.com", "sha256/hash1")
    .add("*.googleapis.com", "sha256/hash2")
    .build()

OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### 7.2. Performance Optimization

**Caching Strategy:**
```kotlin
// OkHttp Cache
val cacheSize = 10 * 1024 * 1024L // 10MB
val cache = Cache(context.cacheDir, cacheSize)

// Cache control
builder.addInterceptor { chain ->
    val response = chain.proceed(request)
    if (response.isSuccessful) {
        response.newBuilder()
            .header("Cache-Control", "public, max-age=300")
            .build()
    } else {
        response
    }
}

// Offline cache
builder.addInterceptor { chain ->
    var request = chain.request()
    if (!isNetworkAvailable()) {
        request = request.newBuilder()
            .header("Cache-Control", "public, only-if-cached, max-stale=604800")
            .build()
    }
    chain.proceed(request)
}
```

**Database Optimization:**
```kotlin
@Entity(
    tableName = "summaries",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["persona"]),
        Index(value = ["isFavorite"])
    ]
)
data class SummaryEntity(...)

// Pagination
@Query("SELECT * FROM summaries ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
suspend fun getSummariesPaged(limit: Int, offset: Int): List<SummaryEntity>
```

---

## PHỤ LỤC

### A. Technology Decision Records (TDR)

**TDR-001: Clean Architecture**
- Decision: Sử dụng Clean Architecture với 3 layers
- Rationale: Separation of concerns, testability, maintainability
- Alternatives: MVC, MVP
- Status: Approved

**TDR-002: Jetpack Compose**
- Decision: Compose thay vì XML Views
- Rationale: Modern declarative UI, less boilerplate
- Alternatives: XML Views, Flutter
- Status: Approved

**TDR-003: Room Database**
- Decision: Room cho local storage
- Rationale: Type-safe, LiveData/Flow support
- Alternatives: SQLite, Realm
- Status: Approved

### B. Architectural Constraints

1. **Minimum SDK 24**: Android 7.0+
2. **Single Activity**: Compose Navigation only
3. **No Firebase for dev/staging**: Only prod flavor
4. **Encrypted API keys**: Security Crypto mandatory
5. **Offline-first**: Local cache + Room database

### C. Future Architecture Plans

**Phase 2:**
- Multi-module architecture
- Feature modules (`:feature:summarize`, `:feature:history`)
- Shared modules (`:core:network`, `:core:database`)

**Phase 3:**
- Cloud sync với backend server
- GraphQL API layer
- Real-time collaboration

---

**Ngày tạo**: Ngày hiện tại
**Phiên bản**: 1.0
**Architects**: Team SumUp Backend & System Design
**Status**: ✅ Production-ready Architecture

# Technical Architecture - SumUp Android

## 🏗️ Architecture Overview

### MVVM + Clean Architecture
```
┌─────────────────────────────────────────┐
│               Presentation              │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │   Compose   │  │   ViewModels    │   │
│  │   Screens   │◄─┤   (State Mgmt)  │   │
│  └─────────────┘  └─────────────────┘   │
└─────────────────────────────────────────┘
                      │
┌─────────────────────────────────────────┐
│                Domain                   │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │ Use Cases   │  │   Repositories  │   │
│  │ (Business)  │◄─┤  (Interfaces)   │   │
│  └─────────────┘  └─────────────────┘   │
└─────────────────────────────────────────┘
                      │
┌─────────────────────────────────────────┐
│                 Data                    │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │  Local DB   │  │   Remote API    │   │
│  │  (Room)     │  │   (Retrofit)    │   │
│  └─────────────┘  └─────────────────┘   │
└─────────────────────────────────────────┘
```

## 📦 Module Structure
```
app/
├── src/main/java/com/sumup/
│   ├── data/
│   │   ├── local/
│   │   │   ├── database/
│   │   │   │   ├── SummaryDao.kt
│   │   │   │   ├── SummaryDatabase.kt
│   │   │   │   └── entities/
│   │   │   └── preferences/
│   │   │       └── SettingsDataStore.kt
│   │   ├── remote/
│   │   │   ├── api/
│   │   │   │   ├── SummarizationApi.kt
│   │   │   │   └── dto/
│   │   │   └── ocr/
│   │   │       └── TextRecognizer.kt
│   │   └── repository/
│   │       ├── SummaryRepositoryImpl.kt
│   │       └── SettingsRepositoryImpl.kt
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Summary.kt
│   │   │   ├── ProcessingState.kt
│   │   │   └── SummaryPersona.kt
│   │   ├── repository/
│   │   │   ├── SummaryRepository.kt
│   │   │   └── SettingsRepository.kt
│   │   └── usecase/
│   │       ├── CreateSummaryUseCase.kt
│   │       ├── GetSummaryHistoryUseCase.kt
│   │       └── ProcessTextWithOCRUseCase.kt
│   ├── presentation/
│   │   ├── ui/
│   │   │   ├── home/
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   ├── camera/
│   │   │   │   ├── CameraScreen.kt
│   │   │   │   └── CameraViewModel.kt
│   │   │   ├── processing/
│   │   │   │   ├── ProcessingScreen.kt
│   │   │   │   └── ProcessingViewModel.kt
│   │   │   ├── result/
│   │   │   │   ├── ResultScreen.kt
│   │   │   │   └── ResultViewModel.kt
│   │   │   ├── history/
│   │   │   │   ├── HistoryScreen.kt
│   │   │   │   └── HistoryViewModel.kt
│   │   │   └── settings/
│   │   │       ├── SettingsScreen.kt
│   │   │       └── SettingsViewModel.kt
│   │   ├── navigation/
│   │   │   ├── SumUpNavigation.kt
│   │   │   └── Screen.kt
│   │   ├── components/
│   │   │   ├── SumUpTextField.kt
│   │   │   ├── ActionBar.kt
│   │   │   ├── PersonaChips.kt
│   │   │   └── ErrorDialog.kt
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   ├── di/
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   ├── RepositoryModule.kt
│   │   └── UseCaseModule.kt
│   └── util/
│       ├── Extensions.kt
│       ├── Constants.kt
│       └── Analytics.kt
```

## 🗄️ Data Layer

### Local Database (Room)
```kotlin
@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val summaryText: String,
    val title: String,
    val persona: String,
    val originalWordCount: Int,
    val summaryWordCount: Int,
    val timestamp: Long,
    val isFavorite: Boolean = false
)

@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries ORDER BY timestamp DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE id = :id")
    suspend fun getSummaryById(id: String): SummaryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity)
    
    @Delete
    suspend fun deleteSummary(summary: SummaryEntity)
    
    @Query("DELETE FROM summaries")
    suspend fun clearAllSummaries()
    
    @Query("SELECT COUNT(*) FROM summaries")
    suspend fun getSummaryCount(): Int
}

@Database(
    entities = [SummaryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SumUpDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
}
```

### Repository Implementation
```kotlin
class SummaryRepositoryImpl @Inject constructor(
    private val summaryDao: SummaryDao,
    private val summarizationApi: SummarizationApi,
    private val textRecognizer: TextRecognizer
) : SummaryRepository {
    
    override fun getAllSummaries(): Flow<List<Summary>> {
        return summaryDao.getAllSummaries()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun createSummary(
        text: String,
        persona: SummaryPersona
    ): Result<Summary> {
        return try {
            val response = summarizationApi.summarize(
                SummarizationRequest(
                    text = text,
                    style = persona.apiStyle,
                    length = "medium"
                )
            )
            
            val summary = Summary(
                originalText = text,
                summaryText = response.summary,
                title = response.title ?: text.take(50),
                persona = persona,
                originalWordCount = text.split(" ").size,
                summaryWordCount = response.summary.split(" ").size,
                timestamp = System.currentTimeMillis()
            )
            
            summaryDao.insertSummary(summary.toEntity())
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recognizeTextFromImage(
        imageUri: Uri
    ): Result<String> {
        return textRecognizer.recognizeText(imageUri)
    }
}
```

## 🧠 Domain Layer

### Use Cases
```kotlin
class CreateSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(
        text: String,
        persona: SummaryPersona = SummaryPersona.GENERAL
    ): Flow<SummaryCreationState> = flow {
        emit(SummaryCreationState.Processing(0.1f, "Reading text..."))
        
        // Validate input
        if (text.trim().length < 50) {
            emit(SummaryCreationState.Error("Text too short. Need at least 50 characters."))
            return@flow
        }
        
        emit(SummaryCreationState.Processing(0.3f, "Understanding context..."))
        
        try {
            val result = summaryRepository.createSummary(text, persona)
            
            if (result.isSuccess) {
                emit(SummaryCreationState.Processing(0.9f, "Finalizing..."))
                delay(500) // Smooth completion
                emit(SummaryCreationState.Success(result.getOrThrow()))
            } else {
                emit(SummaryCreationState.Error(result.exceptionOrNull()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(SummaryCreationState.Error(e.message ?: "Failed to create summary"))
        }
    }
}

sealed class SummaryCreationState {
    data class Processing(val progress: Float, val message: String) : SummaryCreationState()
    data class Success(val summary: Summary) : SummaryCreationState()
    data class Error(val message: String) : SummaryCreationState()
}
```

## 🎨 Presentation Layer

### ViewModel Pattern
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createSummaryUseCase: CreateSummaryUseCase,
    private val processTextWithOCRUseCase: ProcessTextWithOCRUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun onTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
    
    fun onSummarizeClicked() {
        val currentText = _uiState.value.inputText
        if (currentText.trim().length < 50) {
            _uiState.update { 
                it.copy(error = "Text too short. Add ${50 - currentText.trim().length} more characters.")
            }
            return
        }
        
        viewModelScope.launch {
            createSummaryUseCase(currentText)
                .collect { state ->
                    when (state) {
                        is SummaryCreationState.Processing -> {
                            _uiState.update { 
                                it.copy(
                                    isProcessing = true,
                                    processingProgress = state.progress,
                                    processingMessage = state.message
                                )
                            }
                        }
                        is SummaryCreationState.Success -> {
                            _uiState.update { 
                                it.copy(
                                    isProcessing = false,
                                    summary = state.summary
                                )
                            }
                            // Navigate to result screen
                        }
                        is SummaryCreationState.Error -> {
                            _uiState.update { 
                                it.copy(
                                    isProcessing = false,
                                    error = state.message
                                )
                            }
                        }
                    }
                }
        }
    }
}

data class HomeUiState(
    val inputText: String = "",
    val isProcessing: Boolean = false,
    val processingProgress: Float = 0f,
    val processingMessage: String = "",
    val summary: Summary? = null,
    val error: String? = null
)
```

## 🔧 Dependency Injection (Hilt)

### Modules
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
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.gemini.google.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSummarizationApi(retrofit: Retrofit): SummarizationApi {
        return retrofit.create(SummarizationApi::class.java)
    }
}
```

## 📊 Performance Considerations

### Memory Management
```kotlin
// Use Paging for large lists
@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries ORDER BY timestamp DESC")
    fun getAllSummariesPaged(): PagingSource<Int, SummaryEntity>
}

// ViewModel with proper cleanup
class HistoryViewModel : ViewModel() {
    private var searchJob: Job? = null
    
    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            performSearch(query)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
```

### Image Processing Optimization
```kotlin
class TextRecognizer {
    private val detector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    suspend fun recognizeText(imageUri: Uri): Result<String> {
        return try {
            // Optimize image size for OCR
            val optimizedImage = optimizeImageForOCR(imageUri)
            val inputImage = InputImage.fromBitmap(optimizedImage, 0)
            
            val result = suspendCancellableCoroutine<Text> { continuation ->
                detector.process(inputImage)
                    .addOnSuccessListener { text -> continuation.resume(text) }
                    .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
            }
            
            Result.success(result.text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun optimizeImageForOCR(uri: Uri): Bitmap {
        // Resize to optimal OCR dimensions (max 1080x1920)
        // Increase contrast for better recognition
        // Apply noise reduction if needed
    }
}
```

## 🧪 Testing Strategy

### Unit Tests
```kotlin
@Test
fun `createSummary should return success when API succeeds`() = runTest {
    // Given
    val inputText = "This is a long text that needs summarizing..."
    val mockResponse = SummarizationResponse(
        summary = "Short summary",
        title = "Test Summary"
    )
    coEvery { summarizationApi.summarize(any()) } returns mockResponse
    coEvery { summaryDao.insertSummary(any()) } returns Unit
    
    // When
    val result = summaryRepository.createSummary(inputText, SummaryPersona.GENERAL)
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals("Short summary", result.getOrThrow().summaryText)
}
```

### UI Tests
```kotlin
@Test
fun homeScreen_enterTextAndSummarize_navigatesToResult() {
    composeTestRule.setContent {
        HomeScreen(
            onNavigateToResult = { summaryId ->
                // Verify navigation
                assertEquals(expectedSummaryId, summaryId)
            }
        )
    }
    
    // Enter text
    composeTestRule.onNodeWithTag("input_field")
        .performTextInput("This is a test text for summarization...")
    
    // Tap summarize
    composeTestRule.onNodeWithTag("summarize_button")
        .performClick()
    
    // Verify processing state
    composeTestRule.onNodeWithText("Processing...")
        .assertIsDisplayed()
}
```

---

**Critical Implementation Notes**:
- Use StateFlow for UI state management
- Implement proper error handling at all layers
- Add comprehensive logging for debugging
- Use coroutines for async operations
- Implement proper dependency injection
- Add pagination for large data sets
- Optimize images before OCR processing
- Implement proper caching strategies

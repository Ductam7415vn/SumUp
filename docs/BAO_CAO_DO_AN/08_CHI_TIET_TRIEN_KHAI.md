# CHƯƠNG 8: CHI TIẾT TRIỂN KHAI

## 8.1. Triển khai màn hình chính (Main Screen)

### 8.1.1. Cấu trúc UI Components

**MainScreen Composable Structure:**
```kotlin
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { MainTopBar() },
        floatingActionButton = { SummarizeFAB() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Character counter
            CharacterCounter(
                current = uiState.characterCount,
                max = Constants.MAX_CHAR_LIMIT
            )
            
            // Text input section
            TextInputSection(
                text = uiState.inputText,
                onTextChange = viewModel::updateText,
                placeholder = "Nhập hoặc dán văn bản cần tóm tắt..."
            )
            
            // Action buttons
            ActionButtonsRow(
                onPdfClick = viewModel::selectPdf,
                onCameraClick = viewModel::openCamera,
                onClearClick = viewModel::clearText
            )
            
            // Persona selector
            PersonaSelector(
                selectedPersona = uiState.selectedPersona,
                onPersonaChange = viewModel::updatePersona
            )
        }
    }
}
```

### 8.1.2. State Management Implementation

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val summarizeUseCase: SummarizeTextUseCase,
    private val draftManager: DraftManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        // Restore draft if exists
        viewModelScope.launch {
            draftManager.getCurrentDraft()?.let { draft ->
                _uiState.update { it.copy(inputText = draft.content) }
            }
        }
    }
    
    fun updateText(text: String) {
        if (text.length <= Constants.MAX_CHAR_LIMIT) {
            _uiState.update { 
                it.copy(
                    inputText = text,
                    characterCount = text.length
                )
            }
            // Auto-save draft
            draftManager.saveDraft(text)
        }
    }
    
    fun summarize() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            summarizeUseCase(
                text = uiState.value.inputText,
                persona = uiState.value.selectedPersona
            ).collect { result ->
                when (result) {
                    is Result.Success -> {
                        navigateToResult(result.data)
                    }
                    is Result.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = result.error
                            )
                        }
                    }
                    is Result.Loading -> {
                        // Already handled
                    }
                }
            }
        }
    }
}
```

### 8.1.3. PDF Processing Implementation

```kotlin
class ProcessPdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository,
    private val summarizeUseCase: SummarizeTextUseCase
) {
    suspend operator fun invoke(
        uri: Uri,
        persona: Persona
    ): Flow<Result<Summary>> = flow {
        emit(Result.Loading)
        
        try {
            // Extract text from PDF
            val extractedText = pdfRepository.extractTextFromPdf(uri)
            
            // Check text length
            if (extractedText.length > Constants.MAX_CHAR_LIMIT) {
                emit(Result.Error(
                    AppError.ValidationError(
                        field = "pdf",
                        message = "PDF quá dài. Giới hạn ${Constants.MAX_CHAR_LIMIT} ký tự"
                    )
                ))
                return@flow
            }
            
            // Summarize extracted text
            summarizeUseCase(extractedText, persona).collect { result ->
                emit(result)
            }
            
        } catch (e: Exception) {
            emit(Result.Error(
                AppError.PdfError(
                    message = "Không thể đọc file PDF: ${e.message}"
                )
            ))
        }
    }
}
```

### 8.1.4. OCR Implementation với ML Kit

```kotlin
class OcrViewModel @Inject constructor(
    private val textRecognizer: TextRecognizer
) : ViewModel() {
    
    private val _recognizedText = MutableStateFlow<String>("")
    val recognizedText = _recognizedText.asStateFlow()
    
    fun processImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val inputImage = InputImage.fromFilePath(context, imageUri)
                
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        _recognizedText.value = visionText.text
                    }
                    .addOnFailureListener { e ->
                        _uiState.update {
                            it.copy(error = AppError.OcrError(e.message ?: "OCR failed"))
                        }
                    }
            } catch (e: IOException) {
                handleError(e)
            }
        }
    }
}
```

## 8.2. Triển khai xử lý văn bản với AI

### 8.2.1. Gemini API Integration

```kotlin
@Singleton
class GeminiApiService @Inject constructor(
    private val apiKey: String,
    private val httpClient: OkHttpClient
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val api = retrofit.create(GeminiApi::class.java)
    
    suspend fun generateContent(
        prompt: String,
        text: String
    ): SummaryResponse {
        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "$prompt\n\n$text")
                    )
                )
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7,
                topK = 40,
                topP = 0.95,
                maxOutputTokens = 1024
            )
        )
        
        return api.generateContent(
            apiKey = apiKey,
            model = "gemini-1.5-flash",
            request = request
        )
    }
}
```

### 8.2.2. Prompt Engineering cho các Persona

```kotlin
object GeminiPromptBuilder {
    fun buildPrompt(persona: Persona, language: Language): String {
        return when (persona) {
            Persona.DEFAULT -> when (language) {
                Language.VIETNAMESE -> """
                    Hãy tóm tắt văn bản sau một cách ngắn gọn và dễ hiểu.
                    Giữ lại các ý chính và thông tin quan trọng.
                    Độ dài khoảng 20-30% văn bản gốc.
                """.trimIndent()
                
                Language.ENGLISH -> """
                    Please summarize the following text concisely and clearly.
                    Keep the main ideas and important information.
                    Length should be about 20-30% of the original.
                """.trimIndent()
            }
            
            Persona.STUDENT -> when (language) {
                Language.VIETNAMESE -> """
                    Tóm tắt văn bản này cho sinh viên, tập trung vào:
                    - Các khái niệm chính
                    - Định nghĩa quan trọng
                    - Ví dụ minh họa
                    - Công thức hoặc quy tắc cần nhớ
                    Sử dụng bullet points cho dễ đọc.
                """.trimIndent()
                // ... other languages
            }
            
            Persona.PROFESSIONAL -> when (language) {
                Language.VIETNAMESE -> """
                    Tạo bản tóm tắt chuyên nghiệp với:
                    - Executive summary ở đầu
                    - Các điểm chính theo số thứ tự
                    - Kết luận và hành động tiếp theo
                    - Giọng điệu formal, súc tích
                """.trimIndent()
                // ... other languages
            }
            
            Persona.ACADEMIC -> when (language) {
                Language.VIETNAMESE -> """
                    Tóm tắt theo phong cách học thuật:
                    - Giữ nguyên thuật ngữ chuyên môn
                    - Trích dẫn số liệu quan trọng
                    - Nêu rõ phương pháp và kết quả
                    - Thảo luận và hạn chế (nếu có)
                """.trimIndent()
                // ... other languages
            }
            
            Persona.CREATIVE -> when (language) {
                Language.VIETNAMESE -> """
                    Tóm tắt sáng tạo và thu hút:
                    - Sử dụng ngôn ngữ sinh động
                    - Có thể dùng phép so sánh, ẩn dụ
                    - Làm nổi bật điểm thú vị
                    - Kết thúc với câu hỏi hoặc suy ngẫm
                """.trimIndent()
                // ... other languages
            }
        }
    }
}
```

### 8.2.3. Error Handling và Retry Logic

```kotlin
class EnhancedGeminiApiService @Inject constructor(
    private val baseService: GeminiApiService,
    private val connectivity: ConnectivityManager
) {
    suspend fun summarizeWithRetry(
        text: String,
        persona: Persona,
        maxRetries: Int = 3
    ): Result<Summary> {
        // Check connectivity first
        if (!isNetworkAvailable()) {
            return Result.Error(AppError.Network("Không có kết nối Internet"))
        }
        
        var lastError: AppError? = null
        
        repeat(maxRetries) { attempt ->
            try {
                val response = baseService.generateContent(
                    prompt = GeminiPromptBuilder.buildPrompt(persona, currentLanguage),
                    text = text
                )
                
                return Result.Success(
                    Summary(
                        originalText = text,
                        summarizedText = response.candidates.first().content.parts.first().text,
                        persona = persona,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: HttpException) {
                lastError = when (e.code()) {
                    429 -> AppError.RateLimitError("Quá giới hạn request. Vui lòng thử lại sau.")
                    401 -> AppError.AuthError("API key không hợp lệ")
                    else -> AppError.ApiError(e.code(), e.message())
                }
                
                if (e.code() != 429 || attempt == maxRetries - 1) {
                    break
                }
                
                // Exponential backoff for rate limit
                delay((2.0.pow(attempt) * 1000).toLong())
            } catch (e: Exception) {
                lastError = AppError.Unknown(e)
                break
            }
        }
        
        return Result.Error(lastError ?: AppError.Unknown(Exception("Unknown error")))
    }
}
```

## 8.3. Triển khai Local Database

### 8.3.1. Room Database Setup

```kotlin
@Database(
    entities = [SummaryEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SumUpDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
    
    companion object {
        @Volatile
        private var INSTANCE: SumUpDatabase? = null
        
        fun getInstance(context: Context): SumUpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SumUpDatabase::class.java,
                    "sumup_database"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE summaries ADD COLUMN input_type TEXT NOT NULL DEFAULT 'TEXT'")
        database.execSQL("ALTER TABLE summaries ADD COLUMN source_info TEXT")
    }
}
```

### 8.3.2. DAO Implementation

```kotlin
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
    suspend fun deleteAllSummaries()
    
    @Query("SELECT * FROM summaries WHERE " +
           "original_text LIKE '%' || :query || '%' OR " +
           "summarized_text LIKE '%' || :query || '%' " +
           "ORDER BY timestamp DESC")
    fun searchSummaries(query: String): Flow<List<SummaryEntity>>
    
    @Query("SELECT COUNT(*) FROM summaries")
    suspend fun getSummaryCount(): Int
    
    @Query("DELETE FROM summaries WHERE timestamp < :timestamp")
    suspend fun deleteOldSummaries(timestamp: Long)
}
```

### 8.3.3. Repository Implementation

```kotlin
@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val summaryDao: SummaryDao,
    private val geminiService: EnhancedGeminiApiService,
    private val mapper: SummaryMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SummaryRepository {
    
    override fun getAllSummaries(): Flow<List<Summary>> {
        return summaryDao.getAllSummaries()
            .map { entities ->
                entities.map { mapper.toDomain(it) }
            }
            .flowOn(ioDispatcher)
    }
    
    override suspend fun saveSummary(summary: Summary) {
        withContext(ioDispatcher) {
            summaryDao.insertSummary(mapper.toEntity(summary))
        }
    }
    
    override suspend fun summarizeText(
        text: String,
        persona: Persona
    ): Flow<Result<Summary>> = flow {
        emit(Result.Loading)
        
        val result = geminiService.summarizeWithRetry(text, persona)
        
        if (result is Result.Success) {
            // Save to database
            saveSummary(result.data)
        }
        
        emit(result)
    }.flowOn(ioDispatcher)
}
```

## 8.4. Triển khai UI Components

### 8.4.1. Adaptive Layout Implementation

```kotlin
@Composable
fun AdaptiveMainScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactMainScreen(navController, viewModel)
        }
        WindowWidthSizeClass.Medium -> {
            MediumMainScreen(navController, viewModel)
        }
        WindowWidthSizeClass.Expanded -> {
            ExpandedMainScreen(navController, viewModel)
        }
    }
}

@Composable
private fun ExpandedMainScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Input panel
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            MainScreenContent(viewModel)
        }
        
        // Preview panel
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            PreviewPanel(viewModel.uiState.collectAsStateWithLifecycle().value)
        }
    }
}
```

### 8.4.2. Custom Components

```kotlin
@Composable
fun CharacterCounter(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val progress = current.toFloat() / max
    val color = when {
        progress < 0.8f -> MaterialTheme.colorScheme.primary
        progress < 0.95f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$current ký tự",
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
            Text(
                text = "Tối đa $max",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### 8.4.3. Animation Implementation

```kotlin
@Composable
fun AnimatedSummaryCard(
    summary: Summary,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandTransition = updateTransition(
        targetState = isExpanded,
        label = "expand_transition"
    )
    
    val cardElevation by expandTransition.animateDp(
        label = "card_elevation"
    ) { expanded ->
        if (expanded) 8.dp else 2.dp
    }
    
    val contentAlpha by expandTransition.animateFloat(
        label = "content_alpha"
    ) { expanded ->
        if (expanded) 1f else 0.7f
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        onClick = onToggleExpand
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header always visible
            SummaryHeader(summary)
            
            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.alpha(contentAlpha)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = summary.summarizedText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ActionButtons(summary)
                }
            }
        }
    }
}
```

## 8.5. Triển khai Navigation

### 8.5.1. Navigation Graph

```kotlin
@Composable
fun SumUpNavigation(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    NavHost(
        navController = navController,
        startDestination = if (shouldShowOnboarding()) {
            Screen.Onboarding.route
        } else {
            Screen.Main.route
        }
    ) {
        // Onboarding
        composable(
            route = Screen.Onboarding.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } }
        ) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main screen
        composable(
            route = Screen.Main.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            AdaptiveMainScreen(windowSizeClass, navController)
        }
        
        // Processing screen
        composable(
            route = Screen.Processing.route,
            enterTransition = { slideInVertically { it } },
            exitTransition = { slideOutVertically { -it } }
        ) { backStackEntry ->
            val processingType = backStackEntry.arguments?.getString("type")
            ProcessingScreen(
                processingType = processingType,
                onComplete = { result ->
                    navController.navigate(
                        Screen.Result.createRoute(result.id)
                    )
                }
            )
        }
        
        // Result screen
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("summaryId") { type = NavType.StringType }
            ),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val summaryId = backStackEntry.arguments?.getString("summaryId") ?: ""
            AdaptiveResultScreen(
                summaryId = summaryId,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
        
        // History screen
        composable(
            route = Screen.History.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            HistoryScreen(
                onSummaryClick = { summary ->
                    navController.navigate(
                        Screen.Result.createRoute(summary.id)
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Settings screen
        composable(
            route = Screen.Settings.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```

## 8.6. Triển khai Performance Optimization

### 8.6.1. Lazy Loading và Pagination

```kotlin
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val lazyListState = rememberLazyListState()
    val summaries by viewModel.summaries.collectAsStateWithLifecycle()
    
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = summaries,
            key = { it.id }
        ) { summary ->
            SummaryHistoryItem(
                summary = summary,
                onClick = { /* Handle click */ },
                modifier = Modifier.animateItemPlacement()
            )
        }
        
        // Load more indicator
        if (viewModel.hasMore) {
            item {
                LaunchedEffect(Unit) {
                    viewModel.loadMore()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
```

### 8.6.2. Memory Management

```kotlin
class ImageCache @Singleton @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25) // 25% of available memory
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(50 * 1024 * 1024) // 50MB
                .build()
        }
        .respectCacheHeaders(false)
        .build()
    
    fun getImageLoader() = imageLoader
}
```

## 8.7. Tóm tắt chương

Chương này đã trình bày chi tiết việc triển khai các thành phần chính của ứng dụng SumUp:

1. **Main Screen Implementation**: State management, UI components, user interactions
2. **AI Integration**: Gemini API, prompt engineering, error handling
3. **Local Database**: Room setup, DAO, repository pattern
4. **UI Components**: Adaptive layouts, custom components, animations
5. **Navigation**: Navigation graph với transitions
6. **Performance**: Lazy loading, caching, memory management

Các implementation này đảm bảo ứng dụng:
- Responsive và smooth UX
- Reliable AI processing
- Efficient data management
- Beautiful và intuitive UI
- Optimized performance

Chapter tiếp theo sẽ đi sâu vào chi tiết tích hợp AI và xử lý dữ liệu.
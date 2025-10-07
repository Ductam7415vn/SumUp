# Implementation Guide - SumUp Android

## 🚀 Phase 1: Foundation Setup (Week 1)

### Step 1: Project Setup
```kotlin
// build.gradle.kts (Project level)
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

// build.gradle.kts (App level)
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Camera & OCR
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

### Step 2: Base Structure
Create the basic project structure:

1. **Create base Application class**:
```kotlin
@HiltAndroidApp
class SumUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize analytics, logging, etc.
    }
}
```

2. **Set up Navigation**:
```kotlin
@Composable
fun SumUpNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                onNavigateToProcessing = {
                    navController.navigate(Screen.Processing.route)
                }
            )
        }
        
        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Add other screens...
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Processing : Screen("processing")
    object Result : Screen("result/{summaryId}") {
        fun createRoute(summaryId: String) = "result/$summaryId"
    }
    object History : Screen("history")
    object Settings : Screen("settings")
}
```

## 📱 Phase 2: Core Screens Implementation (Week 2-3)

### Home/Input Screen Implementation
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCamera: () -> Unit,
    onNavigateToProcessing: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("SumUp") },
            actions = {
                IconButton(onClick = { /* Navigate to settings */ }) {
                    Icon(Icons.Default.Settings, "Settings")
                }
            }
        )
        
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Text Input Field
            SumUpTextField(
                value = uiState.inputText,
                onValueChange = viewModel::onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = "Paste or type your text here...\n\nTip: Works best with 100-2000 words",
                isError = uiState.error != null
            )
            
            // Character Counter & Helper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CharacterCounter(
                    current = uiState.inputText.length,
                    max = 5000
                )
                
                IconButton(
                    onClick = { /* Show help dialog */ },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Outlined.Help, "Help")
                }
            }
            
            // Error Message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        // Bottom Action Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Clear Button
                AnimatedVisibility(visible = uiState.inputText.isNotEmpty()) {
                    TextButton(onClick = viewModel::onClearText) {
                        Text("Clear All")
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Summarize Button
                Button(
                    onClick = {
                        viewModel.onSummarizeClicked()
                        onNavigateToProcessing()
                    },
                    enabled = uiState.inputText.trim().length >= 50 && !uiState.isProcessing
                ) {
                    if (uiState.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Summarize")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        
        // Bottom Navigation with FAB
        Box {
            BottomNavigation { /* Navigation items */ }
            
            FloatingActionButton(
                onClick = onNavigateToCamera,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = (-32).dp)
            ) {
                Icon(Icons.Default.CameraAlt, "Scan Text")
            }
        }
    }
}

@Composable
fun SumUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            if (newText.length <= 5000) {
                onValueChange(newText)
            }
        },
        modifier = modifier.heightIn(min = 120.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = true,
            keyboardType = KeyboardType.Text
        ),
        maxLines = Int.MAX_VALUE,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}
```

### Processing Screen Implementation
```kotlin
@Composable
fun ProcessingScreen(
    viewModel: ProcessingViewModel = hiltViewModel(),
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.result) {
        uiState.result?.let { summaryId ->
            onNavigateToResult(summaryId)
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Handle error - could stay on screen or navigate back
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Logo
                AnimatedGeminiLogo()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Status Message
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress Bar
                LinearProgressIndicator(
                    progress = uiState.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = getProgressColor(uiState.progress)
                )
                
                // Timeout Message
                if (uiState.showTimeoutMessage) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = uiState.timeoutMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Cancel Button
                TextButton(
                    onClick = {
                        viewModel.cancelProcessing()
                        onNavigateBack()
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun AnimatedGeminiLogo() {
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_gemini_logo),
            contentDescription = "Processing",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
```

## 🔧 Phase 3: Data Layer Implementation (Week 3-4)

### Database Setup
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
}

@Database(
    entities = [SummaryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SumUpDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
}
```

### Repository Implementation
```kotlin
@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val summaryDao: SummaryDao,
    private val summarizationApi: SummarizationApi
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
            // Call API
            val response = summarizationApi.summarize(
                SummarizationRequest(
                    text = text,
                    style = persona.apiStyle,
                    length = "medium"
                )
            )
            
            // Create summary object
            val summary = Summary(
                originalText = text,
                summaryText = response.summary,
                title = response.title ?: text.take(50),
                persona = persona,
                originalWordCount = text.split(" ").size,
                summaryWordCount = response.summary.split(" ").size,
                timestamp = System.currentTimeMillis()
            )
            
            // Save to database
            summaryDao.insertSummary(summary.toEntity())
            
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 📋 Phase 4: Testing Implementation (Week 4)

### Unit Tests
```kotlin
class SummaryRepositoryTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: SumUpDatabase
    private lateinit var summaryDao: SummaryDao
    private lateinit var repository: SummaryRepositoryImpl
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SumUpDatabase::class.java
        ).allowMainThreadQueries().build()
        
        summaryDao = database.summaryDao()
        repository = SummaryRepositoryImpl(summaryDao, mockApi)
    }
    
    @Test
    fun `getAllSummaries returns empty list initially`() = runTest {
        val summaries = repository.getAllSummaries().first()
        assertTrue(summaries.isEmpty())
    }
    
    @Test
    fun `createSummary saves to database`() = runTest {
        val text = "This is a test text for summarization"
        val result = repository.createSummary(text, SummaryPersona.GENERAL)
        
        assertTrue(result.isSuccess)
        val savedSummaries = repository.getAllSummaries().first()
        assertEquals(1, savedSummaries.size)
    }
}
```

### UI Tests
```kotlin
@HiltAndroidTest
class HomeScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun homeScreen_enterValidText_enablesSummarizeButton() {
        val testText = "This is a long enough text for summarization that should enable the button"
        
        composeTestRule.onNodeWithTag("input_field")
            .performTextInput(testText)
        
        composeTestRule.onNodeWithTag("summarize_button")
            .assertIsEnabled()
    }
    
    @Test
    fun homeScreen_enterShortText_showsError() {
        composeTestRule.onNodeWithTag("input_field")
            .performTextInput("Short")
        
        composeTestRule.onNodeWithTag("summarize_button")
            .performClick()
        
        composeTestRule.onNodeWithText("Text too short")
            .assertIsDisplayed()
    }
}
```

## 🚀 Phase 5: Polish & Launch Preparation (Week 5-6)

### Performance Optimization
1. **Add Proguard rules**
2. **Optimize images and resources**
3. **Add crash reporting (Firebase Crashlytics)**
4. **Implement analytics**
5. **Add performance monitoring**

### Analytics Implementation
```kotlin
class Analytics @Inject constructor() {
    fun track(event: String, parameters: Map<String, Any> = emptyMap()) {
        // Firebase Analytics or your preferred analytics service
        FirebaseAnalytics.getInstance(context).logEvent(event, Bundle().apply {
            parameters.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    is Long -> putLong(key, value)
                }
            }
        })
    }
}
```

### Release Configuration
```kotlin
android {
    compileSdk 34
    
    defaultConfig {
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}
```

## ⚠️ Critical Implementation Notes

### Must-Have Error Handling
```kotlin
// Network error handling
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val throwable: Throwable?) : NetworkResult<T>()
    data class Loading<T>(val message: String = "Loading...") : NetworkResult<T>()
}

// Global error handler
@Composable
fun ErrorHandler(
    error: String?,
    onDismiss: () -> Unit
) {
    error?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Error") },
            text = { Text(it) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}
```

### Performance Monitoring
```kotlin
// Monitor key metrics
class PerformanceMonitor {
    fun trackScreenLoad(screenName: String, duration: Long) {
        Analytics.track("screen_load_time", mapOf(
            "screen" to screenName,
            "duration_ms" to duration
        ))
    }
    
    fun trackApiCall(endpoint: String, duration: Long, success: Boolean) {
        Analytics.track("api_call", mapOf(
            "endpoint" to endpoint,
            "duration_ms" to duration,
            "success" to success
        ))
    }
}
```

---

**Deployment Checklist**:
- [ ] All screens implemented and tested
- [ ] Error handling comprehensive
- [ ] Performance targets met
- [ ] Analytics integrated
- [ ] Crash reporting active
- [ ] Release build optimized
- [ ] Store listing prepared
- [ ] Privacy policy ready

**Reality Check**: Focus on core functionality first. Polish can come in updates. A working app beats a perfect prototype.

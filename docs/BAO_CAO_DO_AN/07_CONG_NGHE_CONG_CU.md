# CHƯƠNG 7: CÔNG NGHỆ VÀ CÔNG CỤ

## 7.1. Tổng quan công nghệ sử dụng

### 7.1.1. Technology Stack Overview
SumUp được xây dựng trên nền tảng công nghệ hiện đại, tận dụng những framework và thư viện mới nhất trong hệ sinh thái Android:

```
┌─────────────────────────────────────────────────────┐
│                   Frontend Layer                     │
│  ┌─────────────────────────────────────────────┐   │
│  │  Jetpack Compose UI  │  Material Design 3   │   │
│  └─────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────┤
│                Architecture Layer                    │
│  ┌───────────┐  ┌──────────┐  ┌──────────────┐    │
│  │   MVVM    │  │   Hilt   │  │ Clean Arch   │    │
│  └───────────┘  └──────────┘  └──────────────┘    │
├─────────────────────────────────────────────────────┤
│                  Data Layer                          │
│  ┌───────────┐  ┌──────────┐  ┌──────────────┐    │
│  │   Room    │  │ Retrofit │  │ Coroutines   │    │
│  └───────────┘  └──────────┘  └──────────────┘    │
├─────────────────────────────────────────────────────┤
│                External Services                     │
│  ┌───────────┐  ┌──────────┐  ┌──────────────┐    │
│  │Gemini API │  │  ML Kit  │  │Play Services │    │
│  └───────────┘  └──────────┘  └──────────────┘    │
└─────────────────────────────────────────────────────┘
```

### 7.1.2. Lý do lựa chọn công nghệ

| Công nghệ | Lý do lựa chọn | Alternatives đã xem xét |
|-----------|----------------|-------------------------|
| Kotlin | Modern, null-safe, concise syntax, Google recommended | Java |
| Jetpack Compose | Declarative UI, faster development, better performance | XML layouts |
| Hilt | Official DI solution, compile-time verification | Koin, Dagger |
| Room | Type-safe, reactive, migration support | Realm, SQLite |
| Coroutines | Lightweight, structured concurrency | RxJava |
| Retrofit | Mature, flexible, great ecosystem | Ktor, Volley |

## 7.2. Ngôn ngữ lập trình

### 7.2.1. Kotlin
**Version**: 2.0.21

```kotlin
// Build configuration
kotlin {
    jvmToolchain(17)
    
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }
}
```

**Kotlin Features được sử dụng**:
- **Coroutines & Flow**: Asynchronous programming
- **Data Classes**: Immutable data models
- **Sealed Classes**: Type-safe state management
- **Extension Functions**: Code reusability
- **Null Safety**: Prevent NPE at compile time
- **Scope Functions**: Cleaner code với let, apply, also
- **Parcelize**: Easy parcelable implementation

### 7.2.2. Kotlin Multiplatform Ready
Cấu trúc code được thiết kế để dễ dàng migrate sang KMP trong tương lai:
- Domain layer thuần Kotlin
- Minimal Android dependencies
- Clear separation of concerns

## 7.3. Android Development Kit

### 7.3.1. Android SDK Configuration
```gradle
android {
    namespace = "com.example.sumup"
    compileSdk = 35  // Android 15
    
    defaultConfig {
        applicationId = "com.example.sumup"
        minSdk = 24      // Android 7.0 (Nougat)
        targetSdk = 35   // Android 15
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
```

### 7.3.2. Android API Level Strategy
- **Min SDK 24**: Cover 98.5% devices
- **Target SDK 35**: Latest features và security
- **Compile SDK 35**: Use newest APIs

**API Level Features Used**:
| API Level | Feature | Fallback Strategy |
|-----------|---------|-------------------|
| 26+ | Notification Channels | Basic notifications |
| 31+ | Dynamic Colors | Static theme colors |
| 33+ | Notification Permission | Request in-app |
| 34+ | Predictive Back | Standard back |

## 7.4. UI Framework - Jetpack Compose

### 7.4.1. Compose Configuration
```gradle
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Compose utilities
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}
```

### 7.4.2. Compose Best Practices Applied
1. **State Hoisting**: UI state managed in ViewModels
2. **Recomposition Optimization**: Stable classes, remember usage
3. **Preview Support**: Multiple preview configurations
4. **Theme System**: Dynamic theming với Material You
5. **Animation**: Smooth transitions với AnimatedContent

### 7.4.3. Material Design 3
```kotlin
@Composable
fun SumUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## 7.5. Architecture Components

### 7.5.1. Hilt - Dependency Injection
```gradle
// Hilt setup
dependencies {
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
}

// KSP configuration
ksp {
    arg("dagger.fastInit", "true")
    arg("dagger.experimentalDaggerErrorMessages", "enabled")
}
```

**Hilt Modules Structure**:
- `AppModule`: Singleton dependencies
- `NetworkModule`: API services, OkHttp
- `DatabaseModule`: Room database
- `RepositoryModule`: Repository bindings
- `UseCaseModule`: Business logic

### 7.5.2. ViewModel & LiveData
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val summarizeUseCase: SummarizeTextUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Lifecycle-aware data handling
    fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .flowOn(Dispatchers.IO)
                .catch { e -> handleError(e) }
                .collect { data -> updateState(data) }
        }
    }
}
```

### 7.5.3. Navigation Component
```gradle
implementation("androidx.navigation:navigation-compose:2.8.3")
```

**Type-safe Navigation**:
```kotlin
@Serializable
sealed class Screen {
    @Serializable
    data object Main : Screen()
    
    @Serializable
    data class Result(val summaryId: String) : Screen()
    
    @Serializable
    data object History : Screen()
}
```

## 7.6. Data Persistence

### 7.6.1. Room Database
```gradle
dependencies {
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
}
```

**Room Features Used**:
- Type Converters for complex types
- Migration support
- Full-text search with FTS5
- Coroutines support
- Multi-threading với different executors

### 7.6.2. DataStore
```gradle
implementation("androidx.datastore:datastore-preferences:1.1.1")
```

Used for:
- User preferences (theme, language)
- Onboarding state
- Simple key-value storage

## 7.7. Networking

### 7.7.1. Retrofit & OkHttp
```gradle
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
```

**Network Configuration**:
```kotlin
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) BODY else NONE
        })
        .addInterceptor(ErrorInterceptor())
        .build()
}
```

### 7.7.2. Gson for JSON
```gradle
implementation("com.google.code.gson:gson:2.11.0")
```

Custom adapters for:
- Date/Time serialization
- Enum handling
- Null safety

## 7.8. AI và Machine Learning

### 7.8.1. Google Gemini API
```gradle
// Manual integration (no official SDK yet)
implementation("com.squareup.retrofit2:retrofit:2.11.0")
```

**API Integration**:
- RESTful API calls
- Custom error handling
- Rate limiting management
- Token counting

### 7.8.2. ML Kit for OCR
```gradle
dependencies {
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
}
```

**ML Kit Features**:
- On-device text recognition
- Multi-language support
- Confidence scores
- Text block detection

## 7.9. Image và Camera

### 7.9.1. CameraX
```gradle
dependencies {
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
}
```

### 7.9.2. Coil for Image Loading
```gradle
implementation("io.coil-kt:coil-compose:2.7.0")
```

**Coil Configuration**:
```kotlin
@Provides
@Singleton
fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
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
}
```

## 7.10. PDF Processing

### 7.10.1. PdfBox Android
```gradle
implementation("com.tom-roush:pdfbox-android:2.0.27.0")
```

**Configuration cho memory efficiency**:
```kotlin
System.setProperty("org.apache.pdfbox.baseParser.pushBackSize", "2048576")
PDFBoxResourceLoader.init(context)
```

## 7.11. Development Tools

### 7.11.1. Build Tools
- **Gradle**: 8.9.2
- **Android Gradle Plugin**: 8.7.2
- **KSP**: 2.0.21-1.0.25

### 7.11.2. Code Quality Tools

**Kotlin Lint**:
```gradle
android {
    lint {
        warningsAsErrors = true
        abortOnError = true
        htmlReport = true
        htmlOutput = file("$buildDir/reports/lint/lint-report.html")
    }
}
```

**Detekt** for static analysis:
```gradle
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/config/detekt.yml")
}
```

### 7.11.3. Testing Tools
```gradle
dependencies {
    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.12")
    
    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Debug Tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

## 7.12. Performance Monitoring

### 7.12.1. LeakCanary
```gradle
debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
```

### 7.12.2. Chucker for Network Debugging
```gradle
debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")
```

### 7.12.3. Compose Metrics
```gradle
android {
    buildTypes {
        release {
            kotlinOptions {
                freeCompilerArgs += [
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$buildDir/compose_metrics"
                ]
            }
        }
    }
}
```

## 7.13. CI/CD Tools

### 7.13.1. Version Control
- **Git**: Source control
- **GitHub**: Repository hosting
- **Git Flow**: Branching strategy

### 7.13.2. Build Automation
```yaml
# GitHub Actions workflow
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Gradle
      run: ./gradlew build
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Generate APK
      run: ./gradlew assembleDebug
```

### 7.13.3. Release Management
```gradle
android {
    signingConfigs {
        release {
            storeFile file(RELEASE_STORE_FILE)
            storePassword RELEASE_STORE_PASSWORD
            keyAlias RELEASE_KEY_ALIAS
            keyPassword RELEASE_KEY_PASSWORD
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                         'proguard-rules.pro'
        }
    }
}
```

## 7.14. Documentation Tools

### 7.14.1. KDoc for Code Documentation
```kotlin
/**
 * Summarizes the provided text using AI.
 *
 * @param text The input text to summarize
 * @param persona The summarization style to use
 * @return Flow emitting the summarization result
 * @throws IllegalArgumentException if text is empty
 * @throws ApiException if AI service fails
 */
suspend fun summarize(
    text: String,
    persona: Persona = Persona.DEFAULT
): Flow<Result<Summary>>
```

### 7.14.2. Dokka for API Documentation
```gradle
plugins {
    id("org.jetbrains.dokka") version "1.9.20"
}

tasks.dokkaHtml {
    outputDirectory.set(file("$buildDir/documentation"))
    
    dokkaSourceSets {
        configureEach {
            includeNonPublic.set(false)
            skipEmptyPackages.set(true)
            reportUndocumented.set(true)
        }
    }
}
```

## 7.15. Third-party Libraries

### 7.15.1. UI Enhancement Libraries
```gradle
dependencies {
    // Swipe actions
    implementation("me.saket.swipe:swipe:1.2.0")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    
    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")
}
```

### 7.15.2. Utility Libraries
```gradle
dependencies {
    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    
    // Browser for opening links
    implementation("androidx.browser:browser:1.8.0")
    
    // Timber for logging (debug only)
    debugImplementation("com.jakewharton.timber:timber:5.0.1")
}
```

## 7.16. Development Environment

### 7.16.1. IDE và Plugins
**Android Studio** Ladybug | 2024.2.1
- Kotlin plugin
- Compose Preview
- Database Inspector
- Layout Inspector
- Network Profiler

**Recommended Plugins**:
- Rainbow Brackets
- Key Promoter X
- ADB Idea
- JSON To Kotlin Class

### 7.16.2. Hardware Requirements
**Minimum**:
- RAM: 8GB
- Storage: 4GB for Android Studio + 4GB for SDK
- OS: Windows 10/macOS 10.14/Ubuntu 18.04

**Recommended**:
- RAM: 16GB+
- Storage: SSD with 8GB+ free
- CPU: Multi-core processor
- GPU: For emulator acceleration

## 7.17. Tóm tắt chương

Chương này đã trình bày chi tiết về công nghệ và công cụ sử dụng trong dự án SumUp:

1. **Modern Tech Stack**: Kotlin, Compose, Coroutines
2. **Architecture Tools**: Hilt, Room, Navigation
3. **AI Integration**: Gemini API, ML Kit
4. **Development Tools**: Android Studio, Gradle, Git
5. **Quality Tools**: Lint, Detekt, Testing frameworks
6. **Performance Tools**: LeakCanary, Chucker, Metrics
7. **Third-party Libraries**: Carefully selected for specific needs

**Key Benefits**:
- **Modern**: Sử dụng công nghệ mới nhất
- **Efficient**: Tools tối ưu cho productivity
- **Maintainable**: Clear architecture và documentation
- **Scalable**: Ready cho future enhancements
- **Quality**: Comprehensive testing và monitoring

Stack công nghệ này đảm bảo SumUp được xây dựng trên nền tảng vững chắc, dễ maintain và scale.
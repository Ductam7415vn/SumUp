# SumUp - Complete Source Code Analysis

## Table of Contents
1. [Application Overview](#application-overview)
2. [Architecture Pattern](#architecture-pattern)
3. [Feature Flow Analysis](#feature-flow-analysis)
4. [ViewModels and State Management](#viewmodels-and-state-management)
5. [Use Cases](#use-cases)
6. [Repository Implementations](#repository-implementations)
7. [Navigation Architecture](#navigation-architecture)
8. [UI Components](#ui-components)
9. [Database Schema](#database-schema)
10. [API Structure](#api-structure)
11. [Advanced Implementations](#advanced-implementations)

## Application Overview

SumUp is a comprehensive Android text summarization application built with:
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture with MVVM
- **DI**: Hilt
- **Database**: Room
- **Network**: Retrofit + OkHttp
- **AI Integration**: Google Gemini API

## Architecture Pattern

### Clean Architecture Layers

1. **Presentation Layer** (`presentation/`)
   - ViewModels managing UI state
   - Compose UI screens and components
   - Navigation handling
   - Adaptive layouts for different screen sizes

2. **Domain Layer** (`domain/`)
   - Business logic encapsulated in Use Cases
   - Repository interfaces
   - Domain models independent of data sources

3. **Data Layer** (`data/`)
   - Repository implementations
   - Local database (Room)
   - Remote API services (Retrofit)
   - Data mappers and DTOs

## Feature Flow Analysis

### 1. Text Input Summarization Flow

```
User Input → MainViewModel → SummarizeTextUseCase → SummaryRepository 
→ GeminiApiService → API Response → Save to Database → Navigate to Result
```

**Detailed Steps:**
1. User enters text in MainScreen
2. MainViewModel validates input (min 50 chars)
3. Draft auto-saved via DraftManager
4. On summarize: MainViewModel calls SummarizeTextUseCase
5. Use case validates and sanitizes input
6. Repository sends request to Gemini API
7. Response parsed and saved to Room database
8. Navigation to ProcessingScreen then ResultScreen

### 2. PDF Processing Flow

```
PDF Selection → MainViewModel → ProcessPdfUseCase → PdfRepository 
→ Text Extraction → Text Summarization Flow
```

**Detailed Steps:**
1. User selects PDF via file picker
2. ProcessPdfUseCase validates file size (<10MB)
3. PDFBox extracts text with progress updates
4. Extracted text cleaned and validated
5. If valid, follows text summarization flow
6. FileUploadState tracks multi-stage progress

### 3. OCR (Camera) Flow

```
Camera Permission → CameraX Preview → ML Kit OCR → Text Detection 
→ Review Screen → Main Screen with Text
```

**Detailed Steps:**
1. OcrViewModel handles permission requests
2. CameraX provides live preview
3. ML Kit processes image for text
4. Detected text shown in review dialog
5. User confirms and text sent to MainScreen
6. Follows standard text summarization flow

## ViewModels and State Management

### MainViewModel
- **State**: `MainUiState`
- **Key Functions**:
  - `updateText()`: Updates input with character limit (5000)
  - `summarize()`: Initiates summarization based on input type
  - `selectPdf()`: Handles PDF selection
  - Draft recovery and auto-save management

### HistoryViewModel
- **State**: `HistoryUiState`
- **Key Functions**:
  - `loadSummaries()`: Loads grouped summaries by timeframe
  - `updateSearchQuery()`: Filters summaries
  - `deleteSummary()`: Removes individual items
  - Multi-selection mode for bulk operations

### ResultViewModel
- **State**: `ResultUiState`
- **Key Functions**:
  - `loadSummary()`: Loads latest summary from repository
  - `copySummary()`: Copies to clipboard
  - `toggleFavorite()`: Updates favorite status
  - Persona switching (UI ready, regeneration pending)

### OcrViewModel
- **State**: `OcrUiState`
- **Key Functions**:
  - `onPermissionGranted/Denied()`: Camera permission handling
  - `startTextRecognition()`: Initiates OCR processing
  - `onTextDetected()`: Handles detected text
  - Mock implementation for development

### SettingsViewModel
- **State**: `SettingsUiState`
- **Key Functions**:
  - `setThemeMode()`: Updates app theme
  - `setDynamicColorEnabled()`: Toggles Material You
  - `clearAllData()`: Removes all app data
  - Language and summary length preferences

## Use Cases

### Core Use Cases
1. **SummarizeTextUseCase**
   - Validates input via InputValidator
   - Calls repository for API summarization
   - Returns Result<Summary> with error handling

2. **ProcessPdfUseCase**
   - Validates PDF file
   - Extracts text with progress tracking
   - Cleans and prepares text for summarization

3. **ExtractPdfTextUseCase**
   - Uses PDFBox for text extraction
   - Handles various PDF formats
   - Returns PdfExtractionResult

4. **GetSummaryHistoryUseCase**
   - Retrieves summaries from database
   - Applies filtering and sorting
   - Groups by timeframe

5. **DeleteSummaryUseCases**
   - Single and bulk deletion operations
   - Updates database state

## Repository Implementations

### SummaryRepositoryImpl
- **Local**: Room database via SummaryDao
- **Remote**: Gemini API via GeminiApiService
- **Key Operations**:
  - `summarizeText()`: API call with metrics calculation
  - `getAllSummaries()`: Flow of database summaries
  - CRUD operations for summary management

### PdfRepositoryImpl
- **PDF Processing**: PDFBox Android
- **Operations**:
  - File validation (size, type)
  - Text extraction with metadata
  - Progress tracking

### SettingsRepositoryImpl
- **Storage**: DataStore Preferences
- **Settings**:
  - Theme mode (Light/Dark/System)
  - Dynamic colors
  - Summary preferences
  - Onboarding state

## Navigation Architecture

### Navigation Structure
```
MainActivity
├── Onboarding (first launch)
└── Main Flow
    ├── MainScreen (with bottom nav/rail)
    ├── OcrScreen
    ├── ProcessingScreen
    ├── ResultScreen
    ├── HistoryScreen
    └── SettingsScreen
```

### Adaptive Navigation
- **Phone**: Bottom Navigation Bar
- **Tablet**: Navigation Rail with FAB
- **Transitions**: Fade + slide animations
- **State Preservation**: SavedStateHandle

## UI Components

### Core Components
1. **AdaptiveMainScreen**: Responsive layout for different screen sizes
2. **ProcessingScreen**: Multi-stage progress with animations
3. **SwipeableHistoryItem**: Swipe-to-delete with haptic feedback
4. **PersonaSelector**: Chip-based persona selection
5. **ErrorDialog/Snackbar**: Context-aware error display

### Advanced Components
1. **AutoSaveTextField**: Debounced draft saving
2. **ShimmerLoading**: Skeleton loading states
3. **AnimatedProcessingIcon**: Stage-based animations
4. **PdfPreviewDialog**: PDF content preview
5. **AdaptiveLayout**: Responsive grid/list layouts

## Database Schema

### SummaryEntity
```kotlin
@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey val id: String,
    val originalText: String,
    val summaryText: String,
    val bullets: List<String>, // TypeConverter
    val persona: String,
    val originalWordCount: Int,
    val summaryWordCount: Int,
    val originalReadingTime: Int,
    val summaryReadingTime: Int,
    val reductionPercent: Int,
    val confidence: Float,
    val createdAt: Long,
    val isFavorite: Boolean
)
```

### Indices
- createdAt (for sorting)
- isFavorite (for filtering)
- persona (for grouping)

## API Structure

### Gemini API Integration

#### Request Structure
```kotlin
GeminiRequest(
    contents: List<GeminiContent>,
    generationConfig: GeminiGenerationConfig
)
```

#### Response Handling
- Real API: Parses Gemini response with confidence scoring
- Mock API: Returns persona-specific bullet points
- Automatic fallback on API key issues

#### Error Handling
- Rate limiting detection
- Network error recovery
- Model overload handling
- Graceful fallback responses

## Advanced Implementations

### 1. Draft Management System
- **Auto-save**: 2-second debounce
- **Recovery**: On app restart
- **Multi-type**: Text, PDF, OCR support
- **Preferences**: Saves persona and style choices

### 2. Error Handling Architecture
- **Sealed AppError Hierarchy**:
  - NetworkError
  - ServerError
  - ValidationError
  - OCRFailedError
  - TextTooShortError
- **Smart Display**: Dialog vs Snackbar based on context

### 3. Processing State Management
```kotlin
sealed class ProcessingStage {
    READING_FILE
    EXTRACTING_TEXT
    CLEANING_TEXT
    PREPARING_SUMMARY
}
```

### 4. Haptic Feedback Integration
- Error states
- Swipe actions
- Long press operations
- Success confirmations

### 5. Adaptive UI System
- WindowSizeClass detection
- Responsive layouts
- Dynamic navigation (bottom bar vs rail)
- Tablet-optimized screens

### 6. Performance Optimizations
- Lazy loading for history
- Debounced search
- Efficient Room queries with limits
- Coroutine cancellation for jobs

## Special Patterns

### 1. Mock/Real Service Switching
```kotlin
// Automatic based on API key validity
if (apiKey.isValid()) RealGeminiApiService() 
else MockGeminiApiService()
```

### 2. Multi-stage Progress Tracking
- File reading → Text extraction → Cleaning → Summarization
- Real-time UI updates via Flow

### 3. Persona-based Summarization
- GENERAL, EDUCATIONAL, ACTIONABLE, PRECISE
- Different prompt engineering per persona
- Stored with each summary

### 4. Time-based Grouping
- Today, Yesterday, This Week, This Month
- Dynamic section headers in history

### 5. Predictive Back Gesture
- Custom handling per screen
- Save prompts for unsaved changes

## Testing Architecture
- Unit tests for Use Cases
- Integration tests for Repositories
- Performance benchmarks for critical paths
- Mock implementations for development

## Configuration
- BuildConfig for API keys
- ProGuard rules for release
- Gradle version catalogs
- Multi-module considerations ready
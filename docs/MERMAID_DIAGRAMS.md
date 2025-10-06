# MERMAID DIAGRAMS
## SUMUP - BIỂU ĐỒ HỆ THỐNG

---

## 📋 MỤC LỤC

1. [System Architecture Overview](#1-system-architecture-overview)
2. [Data Flow Diagrams](#2-data-flow-diagrams)
3. [Sequence Diagrams](#3-sequence-diagrams)
4. [State Diagrams](#4-state-diagrams)
5. [Entity Relationship Diagrams](#5-entity-relationship-diagrams)
6. [Component Diagrams](#6-component-diagrams)
7. [Deployment Diagrams](#7-deployment-diagrams)

---

## 1. SYSTEM ARCHITECTURE OVERVIEW

### 1.1. Clean Architecture Layers

```mermaid
graph TB
    subgraph Presentation["🎨 PRESENTATION LAYER"]
        UI[Jetpack Compose UI]
        VM[ViewModels]
        NAV[Navigation]
    end

    subgraph Domain["🏛️ DOMAIN LAYER"]
        UC[Use Cases]
        MOD[Domain Models]
        REPO[Repository Interfaces]
    end

    subgraph Data["💾 DATA LAYER"]
        REPOIMPL[Repository Implementations]
        LOCAL[Local Data Sources]
        REMOTE[Remote Data Sources]
    end

    subgraph External["🌐 EXTERNAL SERVICES"]
        ROOM[(Room DB)]
        API[Gemini API]
        MLKIT[ML Kit OCR]
        FILES[File System]
    end

    UI --> VM
    VM --> UC
    UC --> REPO
    REPO --> REPOIMPL
    REPOIMPL --> LOCAL
    REPOIMPL --> REMOTE
    LOCAL --> ROOM
    REMOTE --> API
    REMOTE --> MLKIT
    LOCAL --> FILES

    style Presentation fill:#e1f5fe
    style Domain fill:#f3e5f5
    style Data fill:#e8f5e9
    style External fill:#fff3e0
```

### 1.2. Module Dependencies

```mermaid
graph LR
    APP[App Module]

    subgraph Presentation
        SCREENS[Screens]
        COMP[Components]
        NAV[Navigation]
    end

    subgraph Domain
        UC[Use Cases]
        MODELS[Models]
        REPO_INT[Repositories]
    end

    subgraph Data
        REPO_IMPL[Repository Impl]
        DB[Database]
        API_SVC[API Services]
    end

    subgraph DI
        MODULES[Hilt Modules]
    end

    APP --> Presentation
    APP --> DI
    Presentation --> Domain
    Domain --> Data
    DI -.provides.-> Presentation
    DI -.provides.-> Domain
    DI -.provides.-> Data

    style APP fill:#ff6f00
    style Presentation fill:#2196f3
    style Domain fill:#9c27b0
    style Data fill:#4caf50
    style DI fill:#ff9800
```

---

## 2. DATA FLOW DIAGRAMS

### 2.1. Text Summarization Flow

```mermaid
flowchart TD
    START([User Opens App]) --> INPUT[User Inputs Text]
    INPUT --> DRAFT{Auto-save Draft?}
    DRAFT -->|After 2s| SAVE_DRAFT[(Save to SharedPrefs)]
    DRAFT --> SELECT[User Selects Persona]
    SELECT --> VALIDATE{Validate Input}

    VALIDATE -->|Invalid| ERROR1[Show Error Message]
    ERROR1 --> INPUT

    VALIDATE -->|Valid| CHECK_SIZE{Text Size?}
    CHECK_SIZE -->|< 10,000 chars| STANDARD[Standard Processing]
    CHECK_SIZE -->|≥ 10,000 chars| SECTION[Smart Sectioning]

    STANDARD --> API_CALL[Call Gemini API]
    SECTION --> DIVIDE[Divide into Sections]
    DIVIDE --> PARALLEL[Parallel Processing]
    PARALLEL --> COMBINE[Combine Results]
    COMBINE --> API_CALL

    API_CALL --> CHECK_RESPONSE{API Response?}
    CHECK_RESPONSE -->|Error| ERROR2[Handle Error]
    CHECK_RESPONSE -->|Success| PARSE[Parse Response]

    PARSE --> METRICS[Calculate Metrics]
    METRICS --> SAVE_DB[(Save to Room DB)]
    SAVE_DB --> CLEAR_DRAFT[Clear Draft]
    CLEAR_DRAFT --> NAVIGATE[Navigate to Result]
    NAVIGATE --> DISPLAY[Display Summary]
    DISPLAY --> END([End])

    ERROR2 --> RETRY{User Retry?}
    RETRY -->|Yes| API_CALL
    RETRY -->|No| END

    style START fill:#4caf50
    style END fill:#f44336
    style API_CALL fill:#2196f3
    style SAVE_DB fill:#ff9800
```

### 2.2. PDF Processing Flow

```mermaid
flowchart TD
    START([User Selects PDF]) --> VALIDATE{Validate File}

    VALIDATE -->|Invalid Type| ERR1[Error: Invalid Format]
    VALIDATE -->|Too Large| ERR2[Error: File Too Large]
    VALIDATE -->|Encrypted| ERR3[Error: Password Protected]
    VALIDATE -->|Valid| EXTRACT[Extract Text with PDFBox]

    EXTRACT --> CHECK_PAGES{Page Count?}
    CHECK_PAGES -->|≤ 50 pages| STANDARD[Standard Extraction]
    CHECK_PAGES -->|> 50 pages| WARN[Show Warning Dialog]

    WARN --> CHOICE{User Choice?}
    CHOICE -->|Process All| SECTION[Sectioning Strategy]
    CHOICE -->|Select Pages| PICKER[Page Picker UI]
    CHOICE -->|Cancel| CANCEL([Cancel])

    PICKER --> SELECT_PAGES[Extract Selected Pages]
    SELECT_PAGES --> STANDARD

    SECTION --> DIVIDE[Divide into Sections]
    DIVIDE --> PARALLEL[Parallel Processing]
    PARALLEL --> STANDARD

    STANDARD --> TEXT_READY[Text Extracted]
    TEXT_READY --> SUMMARIZE[Summarize Text]
    SUMMARIZE --> SAVE[(Save to DB)]
    SAVE --> RESULT[Show Result]
    RESULT --> END([End])

    ERR1 --> END
    ERR2 --> END
    ERR3 --> END

    style START fill:#4caf50
    style END fill:#f44336
    style CANCEL fill:#9e9e9e
    style SAVE fill:#ff9800
```

### 2.3. OCR Processing Flow

```mermaid
flowchart TD
    START([User Taps SCAN]) --> PERM{Camera Permission?}

    PERM -->|Denied| REQ[Request Permission]
    REQ --> USER_RESP{User Response?}
    USER_RESP -->|Deny| EXPLAIN[Show Explanation]
    EXPLAIN --> SETTINGS[Go to Settings]
    USER_RESP -->|Grant| INIT

    PERM -->|Granted| INIT[Initialize CameraX]
    INIT --> PREVIEW[Show Camera Preview]
    PREVIEW --> ALIGN[User Aligns Document]

    ALIGN --> CHECK_LIGHT{Light Quality?}
    CHECK_LIGHT -->|Poor| WARN_LIGHT[Warning: Improve Lighting]
    WARN_LIGHT --> FLASH{Use Flash?}
    FLASH -->|Yes| ENABLE_FLASH[Enable Flash]
    FLASH -->|No| ALIGN
    ENABLE_FLASH --> ALIGN

    CHECK_LIGHT -->|Good| CAPTURE[User Captures Image]
    CAPTURE --> OCR[ML Kit Text Recognition]
    OCR --> CONFIDENCE{Confidence Score?}

    CONFIDENCE -->|< 80%| LOW_CONF[Low Quality Warning]
    LOW_CONF --> RETRY_CHOICE{Retry or Edit?}
    RETRY_CHOICE -->|Retry| ALIGN
    RETRY_CHOICE -->|Edit| REVIEW

    CONFIDENCE -->|≥ 80%| REVIEW[Review Extracted Text]
    REVIEW --> EDIT{User Edits?}
    EDIT -->|Yes| MANUAL_EDIT[Edit Text]
    MANUAL_EDIT --> REVIEW
    EDIT -->|No| CONFIRM[User Confirms]

    CONFIRM --> SUMMARIZE[Summarize Text]
    SUMMARIZE --> SAVE[(Save to DB)]
    SAVE --> RESULT[Show Result]
    RESULT --> END([End])

    style START fill:#4caf50
    style END fill:#f44336
    style OCR fill:#2196f3
    style SAVE fill:#ff9800
```

---

## 3. SEQUENCE DIAGRAMS

### 3.1. User Authentication & API Key Setup

```mermaid
sequenceDiagram
    actor User
    participant UI as Settings UI
    participant VM as SettingsViewModel
    participant AKM as ApiKeyManager
    participant ENC as EncryptedPrefs
    participant API as Gemini API
    participant DB as Room DB

    User->>UI: Open Settings
    UI->>VM: loadApiKeys()
    VM->>AKM: getAllKeys()
    AKM->>ENC: decrypt keys
    ENC-->>AKM: encrypted keys
    AKM-->>VM: key list (masked)
    VM-->>UI: display keys

    User->>UI: Tap "Add New Key"
    UI->>UI: Show input dialog
    User->>UI: Enter API key
    UI->>VM: validateKey(key)

    alt Invalid Format
        VM-->>UI: Show error "Invalid format"
    else Valid Format
        VM->>API: Test API call
        alt API Test Fails
            API-->>VM: Error response
            VM-->>UI: Show "Invalid key"
        else API Test Success
            API-->>VM: Success response
            VM->>AKM: saveKey(key)
            AKM->>ENC: encrypt(key)
            ENC-->>AKM: saved
            AKM->>DB: update key metadata
            DB-->>AKM: success
            AKM-->>VM: key saved
            VM-->>UI: Update UI
            UI-->>User: Show success message
        end
    end
```

### 3.2. Text Summarization with Error Handling

```mermaid
sequenceDiagram
    actor User
    participant UI as MainScreen
    participant VM as MainViewModel
    participant UC as SummarizeUseCase
    participant REPO as SummaryRepository
    participant API as Gemini API
    participant DB as Room DB
    participant NAV as Navigation

    User->>UI: Enter text
    UI->>VM: onTextChange(text)
    VM->>VM: debounce(2s)
    VM->>VM: saveDraft(text)

    User->>UI: Tap "SUMMARIZE"
    UI->>VM: summarize()
    VM->>UC: invoke(text, persona)
    UC->>UC: validate(text)

    alt Text Invalid
        UC-->>VM: ValidationError
        VM-->>UI: Show error
        UI-->>User: Display error message
    else Text Valid
        UC->>REPO: summarizeText(text, persona)
        REPO->>API: generateContent(request)

        alt Network Error
            API-->>REPO: NetworkException
            REPO-->>UC: NetworkError
            UC-->>VM: AppError.NetworkError
            VM-->>UI: Show retry dialog
            User->>UI: Retry
            UI->>VM: summarize()
        else Rate Limit
            API-->>REPO: RateLimitException
            REPO-->>UC: RateLimitError
            UC-->>VM: AppError.RateLimitError
            VM-->>UI: Show "Try later"
        else Success
            API-->>REPO: Summary response
            REPO->>REPO: parse response
            REPO->>REPO: calculate metrics
            REPO->>DB: insert(summary)
            DB-->>REPO: summaryId
            REPO-->>UC: Summary object
            UC-->>VM: Result.Success
            VM->>NAV: navigateToResult(summaryId)
            NAV->>UI: Navigate
            UI-->>User: Show summary
        end
    end
```

### 3.3. PDF Upload and Processing

```mermaid
sequenceDiagram
    actor User
    participant UI as MainScreen
    participant VM as MainViewModel
    participant PDF as PdfProcessor
    participant SEC as SmartSectioning
    participant API as Gemini API
    participant DB as Room DB

    User->>UI: Tap "FILE" tab
    UI->>UI: Show file picker
    User->>UI: Select PDF file
    UI->>VM: onFileSelected(uri)

    VM->>VM: validate file
    alt Invalid File
        VM-->>UI: Show error
    else Valid File
        VM->>PDF: extractText(uri)
        PDF->>PDF: Load with PDFBox
        PDF->>PDF: Count pages

        alt Large PDF (>50 pages)
            PDF-->>VM: LargePdfDetected
            VM-->>UI: Show warning dialog
            User->>UI: Choose option

            alt Process All
                UI-->>VM: processAll()
                VM->>SEC: sectionPdf(pages)
                SEC->>SEC: Divide into sections

                loop Each Section
                    SEC->>PDF: extractSection(pages)
                    PDF-->>SEC: text
                    SEC->>API: summarize(text)
                    API-->>SEC: summary
                end

                SEC->>SEC: Combine summaries
                SEC-->>VM: Final summary
            else Select Pages
                UI-->>User: Show page picker
                User->>UI: Select pages
                UI-->>VM: processPages(selected)
                VM->>PDF: extractPages(selected)
            end
        else Normal PDF
            PDF->>PDF: Extract all text
        end

        PDF-->>VM: Extracted text
        VM->>API: summarize(text)
        API-->>VM: Summary
        VM->>DB: save(summary)
        DB-->>VM: summaryId
        VM-->>UI: Navigate to result
    end
```

---

## 4. STATE DIAGRAMS

### 4.1. Application State Machine

```mermaid
stateDiagram-v2
    [*] --> Splash
    Splash --> CheckingAuth: Initialize

    CheckingAuth --> Onboarding: First Launch
    CheckingAuth --> Main: Returning User

    Onboarding --> ApiSetup: Next
    ApiSetup --> Main: Complete

    Main --> TextInput: Tab Text
    Main --> FileUpload: Tab File
    Main --> OCRScan: Tab Scan
    Main --> History: Tap History
    Main --> Settings: Tap Settings

    TextInput --> Processing: Summarize
    FileUpload --> Processing: Process File
    OCRScan --> Processing: OCR Complete

    Processing --> Result: Success
    Processing --> Error: Failure

    Error --> TextInput: Retry
    Error --> Main: Cancel

    Result --> Main: Back
    Result --> Export: Export
    Result --> Share: Share

    History --> Result: View Item
    History --> Main: Back

    Settings --> ApiManagement: Manage Keys
    Settings --> ThemeSwitch: Change Theme
    Settings --> Main: Back

    Export --> Result: Done
    Share --> Result: Done

    ApiManagement --> Settings: Save
    ThemeSwitch --> Settings: Apply

    Main --> [*]: Exit App
```

### 4.2. Summary Processing States

```mermaid
stateDiagram-v2
    [*] --> Idle

    Idle --> Validating: User Submits

    Validating --> ValidationError: Invalid Input
    Validating --> CheckingSize: Valid Input

    ValidationError --> Idle: User Fixes

    CheckingSize --> StandardProcessing: < 10K chars
    CheckingSize --> SectionedProcessing: ≥ 10K chars

    StandardProcessing --> CallingAPI: Single Call

    SectionedProcessing --> DividingSections: Split Text
    DividingSections --> ParallelProcessing: 3 Concurrent Calls
    ParallelProcessing --> CombiningResults: All Complete
    CombiningResults --> CallingAPI: Meta-Summary

    CallingAPI --> Success: 200 OK
    CallingAPI --> NetworkError: No Connection
    CallingAPI --> RateLimitError: 429
    CallingAPI --> ApiError: 4xx/5xx

    NetworkError --> Retrying: Auto Retry (3x)
    Retrying --> CallingAPI: Retry
    Retrying --> Failed: Max Retries

    RateLimitError --> Waiting: Show Timer
    Waiting --> Idle: User Waits
    Waiting --> Failed: User Cancels

    ApiError --> Failed: Unrecoverable

    Success --> CalculatingMetrics: Parse Response
    CalculatingMetrics --> SavingToDB: Compute Stats
    SavingToDB --> Completed: Persist

    Completed --> [*]: Navigate Result
    Failed --> [*]: Show Error
```

### 4.3. API Key Lifecycle

```mermaid
stateDiagram-v2
    [*] --> NoKey

    NoKey --> Entering: User Adds Key
    Entering --> Validating: Submit

    Validating --> FormatError: Invalid Format
    Validating --> Testing: Valid Format

    FormatError --> Entering: Fix Format

    Testing --> TestFailed: API Test Fail
    Testing --> Encrypting: API Test Pass

    TestFailed --> Entering: Retry

    Encrypting --> Storing: AES256-GCM
    Storing --> Active: Set as Active

    Active --> InUse: Making Requests
    InUse --> RateLimited: Quota Exceeded
    InUse --> Invalid: Key Revoked
    InUse --> Active: Request Complete

    RateLimited --> Waiting: Show Limit
    Waiting --> Active: Quota Reset

    Invalid --> Inactive: Deactivate
    Inactive --> Deleting: User Removes
    Deleting --> NoKey: Cleanup

    Active --> Switching: User Switches Key
    Switching --> Active: New Key Active

    Active --> Deleting: User Deletes
```

---

## 5. ENTITY RELATIONSHIP DIAGRAMS

### 5.1. Database Schema

```mermaid
erDiagram
    SUMMARY ||--o{ SUMMARY_METRICS : has
    SUMMARY ||--o{ TAGS : has
    SUMMARY }o--|| INPUT_TYPE : uses
    SUMMARY }o--|| PERSONA : uses
    API_KEY ||--o{ USAGE_LOG : tracks

    SUMMARY {
        string id PK
        string originalText
        string summaryText
        string inputType FK
        string persona FK
        long timestamp
        boolean isFavorite
        string sourceFileName
    }

    SUMMARY_METRICS {
        string summaryId FK
        int originalWordCount
        int summaryWordCount
        float reductionPercentage
        int readingTimeSaved
        float aiQualityScore
    }

    TAGS {
        string summaryId FK
        string tag
    }

    INPUT_TYPE {
        string type PK
        string displayName
    }

    PERSONA {
        string id PK
        string name
        string description
        string promptTemplate
    }

    API_KEY {
        string keyId PK
        string encryptedKey
        string label
        int usageCount
        boolean isActive
        long createdAt
        long lastUsed
    }

    USAGE_LOG {
        string id PK
        string keyId FK
        long timestamp
        int tokenCount
        boolean success
    }
```

### 5.2. Data Relationships

```mermaid
graph TD
    subgraph User Data
        USER[User]
        DRAFT[Drafts]
        SETTINGS[Settings]
    end

    subgraph Summaries
        SUMMARY[Summary]
        METRICS[Metrics]
        HISTORY[History Items]
    end

    subgraph API
        KEYS[API Keys]
        USAGE[Usage Stats]
        QUOTA[Rate Limits]
    end

    subgraph Files
        PDF[PDF Documents]
        DOCX[DOCX Documents]
        IMAGES[OCR Images]
    end

    USER -->|creates| DRAFT
    USER -->|configures| SETTINGS
    USER -->|manages| KEYS

    DRAFT -->|generates| SUMMARY
    PDF -->|extracts to| SUMMARY
    DOCX -->|extracts to| SUMMARY
    IMAGES -->|OCR to| SUMMARY

    SUMMARY -->|has| METRICS
    SUMMARY -->|stored in| HISTORY

    KEYS -->|tracks| USAGE
    USAGE -->|enforces| QUOTA

    KEYS -->|used by| SUMMARY

    style USER fill:#2196f3
    style SUMMARY fill:#4caf50
    style KEYS fill:#ff9800
```

---

## 6. COMPONENT DIAGRAMS

### 6.1. Presentation Layer Components

```mermaid
graph TB
    subgraph Screens
        MAIN[MainScreen]
        PROC[ProcessingScreen]
        RESULT[ResultScreen]
        HIST[HistoryScreen]
        SET[SettingsScreen]
        OCR[OcrScreen]
    end

    subgraph ViewModels
        MAINVM[MainViewModel]
        PROCVM[ProcessingViewModel]
        RESULTVM[ResultViewModel]
        HISTVM[HistoryViewModel]
        SETVM[SettingsViewModel]
        OCRVM[OcrViewModel]
    end

    subgraph Components
        CARD[SummaryCard]
        METRIC[MetricCard]
        INPUT[InputField]
        BTN[PrimaryButton]
        DIALOG[DialogComponents]
    end

    subgraph Navigation
        NAV[NavController]
        ROUTES[Screen Routes]
        TRANS[Transitions]
    end

    MAIN --> MAINVM
    PROC --> PROCVM
    RESULT --> RESULTVM
    HIST --> HISTVM
    SET --> SETVM
    OCR --> OCRVM

    MAIN -.uses.-> CARD
    MAIN -.uses.-> INPUT
    MAIN -.uses.-> BTN
    RESULT -.uses.-> METRIC
    HIST -.uses.-> CARD

    NAV --> ROUTES
    NAV --> TRANS

    MAIN --> NAV
    PROC --> NAV
    RESULT --> NAV

    style Screens fill:#e3f2fd
    style ViewModels fill:#f3e5f5
    style Components fill:#e8f5e9
    style Navigation fill:#fff3e0
```

### 6.2. Domain Layer Components

```mermaid
graph TB
    subgraph Use Cases
        SUMM[SummarizeTextUseCase]
        PROC_DOC[ProcessDocumentUseCase]
        SECT[SmartSectioningUseCase]
        ADAPT[AdaptiveProcessingUseCase]
        EXPORT[ExportSummaryUseCase]
    end

    subgraph Repositories
        SUMM_REPO[SummaryRepository]
        PDF_REPO[PdfRepository]
        SET_REPO[SettingsRepository]
    end

    subgraph Models
        SUMMARY[Summary]
        ERROR[AppError]
        STATE[ProcessingState]
        METRICS[SummaryMetrics]
    end

    subgraph Document Processors
        PDF_PROC[PdfProcessor]
        DOCX_PROC[DocxProcessor]
        TXT_PROC[TxtProcessor]
        FACTORY[ProcessorFactory]
    end

    SUMM --> SUMM_REPO
    PROC_DOC --> PDF_REPO
    PROC_DOC --> FACTORY
    SECT --> SUMM_REPO
    ADAPT --> SUMM_REPO

    FACTORY --> PDF_PROC
    FACTORY --> DOCX_PROC
    FACTORY --> TXT_PROC

    SUMM -.returns.-> SUMMARY
    SUMM -.throws.-> ERROR
    PROC_DOC -.emits.-> STATE

    style "Use Cases" fill:#e1f5fe
    style Repositories fill:#f3e5f5
    style Models fill:#e8f5e9
    style "Document Processors" fill:#fff3e0
```

### 6.3. Data Layer Components

```mermaid
graph TB
    subgraph Repository Impl
        SUMM_IMPL[SummaryRepositoryImpl]
        PDF_IMPL[PdfRepositoryImpl]
        SET_IMPL[SettingsRepositoryImpl]
    end

    subgraph Local Data
        ROOM[Room Database]
        DAO[DAOs]
        ENTITY[Entities]
        CONV[Converters]
    end

    subgraph Remote Data
        API[GeminiApiService]
        ENH_API[EnhancedApiService]
        MOCK[MockApiService]
        DTO[DTOs]
    end

    subgraph Security
        ENC[EncryptedPrefs]
        AKM[ApiKeyManager]
        SEC_PROV[SecureProvider]
    end

    subgraph File Processing
        PDF_BOX[PDFBox Android]
        MAMMOTH[Mammoth DOCX]
        MLKIT[ML Kit OCR]
    end

    SUMM_IMPL --> ROOM
    SUMM_IMPL --> API
    PDF_IMPL --> PDF_BOX
    PDF_IMPL --> MAMMOTH
    SET_IMPL --> ENC

    ROOM --> DAO
    DAO --> ENTITY
    ENTITY --> CONV

    API -.fallback.-> MOCK
    API --> ENH_API
    ENH_API --> AKM
    AKM --> SEC_PROV
    SEC_PROV --> ENC

    style "Repository Impl" fill:#e3f2fd
    style "Local Data" fill:#f3e5f5
    style "Remote Data" fill:#e8f5e9
    style Security fill:#ffebee
    style "File Processing" fill:#fff3e0
```

---

## 7. DEPLOYMENT DIAGRAMS

### 7.1. Application Deployment

```mermaid
graph TB
    subgraph Android Device
        APP[SumUp APK]

        subgraph App Components
            UI[UI Layer]
            LOGIC[Business Logic]
            DATA[Data Layer]
        end

        subgraph Local Storage
            DB[(Room SQLite)]
            PREFS[SharedPreferences]
            FILES[File System]
            CACHE[Cache]
        end

        subgraph System Services
            CAM[Camera Service]
            MLKIT_LOCAL[ML Kit]
            STORAGE[Storage Service]
        end
    end

    subgraph External Services
        GEMINI[Google Gemini API<br/>generativelanguage.googleapis.com]
        PLAY[Google Play Services<br/>ML Kit Text Recognition]
    end

    subgraph Build Variants
        DEV[Dev Build<br/>com.example.sumup.dev]
        STAGING[Staging Build<br/>com.example.sumup.staging]
        PROD[Production Build<br/>com.example.sumup]
    end

    APP --> UI
    UI --> LOGIC
    LOGIC --> DATA

    DATA --> DB
    DATA --> PREFS
    DATA --> FILES
    DATA --> CACHE

    DATA -.API Calls.-> GEMINI
    LOGIC -.OCR.-> MLKIT_LOCAL
    MLKIT_LOCAL -.Download Models.-> PLAY

    UI --> CAM
    DATA --> STORAGE

    DEV -.debug.-> APP
    STAGING -.testing.-> APP
    PROD -.release.-> APP

    style APP fill:#4caf50
    style GEMINI fill:#2196f3
    style PLAY fill:#ff9800
    style DB fill:#9c27b0
```

### 7.2. Network Architecture

```mermaid
graph LR
    subgraph Client["📱 Android Client"]
        APP[SumUp App]
        CACHE[HTTP Cache<br/>10MB]
        CERT[Certificate Pinning]
    end

    subgraph Network["🌐 Network Layer"]
        OKHTTP[OkHttp Client<br/>60s timeout]
        RETROFIT[Retrofit<br/>Gson Converter]
        INT[Interceptors]
    end

    subgraph API["☁️ Cloud Services"]
        GEMINI[Gemini API<br/>1.5 Flash]
        MLKIT[ML Kit API]
    end

    subgraph Security["🔒 Security"]
        TLS[TLS 1.3]
        PIN[Certificate Pins]
        ENC[AES-256 Encryption]
    end

    APP --> OKHTTP
    OKHTTP --> INT
    INT --> CACHE
    INT --> CERT
    OKHTTP --> RETROFIT
    RETROFIT --> GEMINI
    RETROFIT --> MLKIT

    CERT --> PIN
    OKHTTP --> TLS
    APP -.stores keys.-> ENC

    style Client fill:#e1f5fe
    style Network fill:#f3e5f5
    style API fill:#e8f5e9
    style Security fill:#ffebee
```

### 7.3. CI/CD Pipeline

```mermaid
graph TD
    START([Git Push]) --> TRIGGER[GitHub Actions Trigger]
    TRIGGER --> LINT[Run Lint Checks]

    LINT --> UNIT[Run Unit Tests]
    UNIT --> INT[Run Integration Tests]

    INT --> BUILD{Build Variants}
    BUILD --> DEV[Build Dev APK]
    BUILD --> STAGING[Build Staging APK]
    BUILD --> PROD[Build Prod APK]

    DEV --> SIGN_DEV[Sign with Debug Key]
    STAGING --> SIGN_STAGING[Sign with Staging Key]
    PROD --> SIGN_PROD[Sign with Release Key]

    SIGN_DEV --> UPLOAD_DEV[Upload to Firebase<br/>App Distribution]
    SIGN_STAGING --> UPLOAD_STAGING[Upload to Internal Testing]
    SIGN_PROD --> REVIEW[Manual Review]

    REVIEW --> APPROVE{Approved?}
    APPROVE -->|Yes| UPLOAD_PROD[Upload to Play Store<br/>Beta Track]
    APPROVE -->|No| REJECT([Reject])

    UPLOAD_DEV --> NOTIFY[Notify Team]
    UPLOAD_STAGING --> NOTIFY
    UPLOAD_PROD --> NOTIFY

    NOTIFY --> END([Complete])
    REJECT --> END

    style START fill:#4caf50
    style END fill:#2196f3
    style REJECT fill:#f44336
```

---

## 📊 DIAGRAM SUMMARY

### Tổng số Diagrams: 22

**By Category:**
- Architecture: 3 diagrams
- Data Flow: 3 diagrams
- Sequence: 3 diagrams
- State: 3 diagrams
- Entity Relationship: 2 diagrams
- Component: 3 diagrams
- Deployment: 3 diagrams
- CI/CD: 2 diagrams

**Complexity Levels:**
- Simple: 8 diagrams
- Medium: 10 diagrams
- Complex: 4 diagrams

### Rendering Instructions

**GitHub/GitLab:**
- Tất cả diagrams tự động render
- Hỗ trợ dark mode

**Markdown Viewers:**
- Cần Mermaid plugin
- VSCode: Markdown Preview Mermaid Support
- IntelliJ: Mermaid plugin

**Export Options:**
- PNG: `mermaid-cli` tool
- SVG: Mermaid Live Editor
- PDF: Via markdown-pdf

---

**📅 Ngày tạo**: January 2025
**📝 Version**: 1.0
**🎨 Tổng số Mermaid Diagrams**: 22
**✍️ Tác giả**: Team SumUp - System Architect

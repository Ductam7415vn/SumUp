# TÀI LIỆU 2: THIẾT KẾ GIAO DIỆN NGƯỜI DÙNG (UI/UX DESIGN)
## SUMUP - ỨNG DỤNG TÓM TẮT VĂN BẢN THÔNG MINH

---

## 1. BẢNG THIẾT KẾ GIAO DIỆN NGƯỜI DÙNG

### 1.1. Design System - Material 3

**Công cụ sử dụng:**
- ✅ **Figma** - Primary design tool
- Adobe XD - Alternative (nếu cần)
- Balsamiq - Wireframing

**Design Tokens:**
```kotlin
// Color Palette
Primary: #6750A4 (Purple)
Secondary: #625B71
Tertiary: #7D5260
Background: #FFFBFE (Light) / #1C1B1F (Dark)
Surface: #FFFBFE (Light) / #1C1B1F (Dark)
Error: #B3261E

// Typography
Font Family: Roboto, Noto Sans (Vietnamese support)
Display Large: 57sp / Regular / -0.25
Headline Large: 32sp / Regular / 0
Title Large: 22sp / Regular / 0
Body Large: 16sp / Regular / 0.5
Label Large: 14sp / Medium / 0.1
```

### 1.2. Wireframes - 7 Màn hình Chính

#### **Màn 1: Main Screen (Màn hình Chính)**
```
┌────────────────────────────────┐
│  🏠 SumUp        ⚙️ 📚 👤      │
├────────────────────────────────┤
│                                │
│  💡 Welcome Card               │
│  "Get started with SumUp"      │
│                                │
├────────────────────────────────┤
│  Input Methods:                │
│  ┌──────┐ ┌──────┐ ┌──────┐   │
│  │ 📝   │ │ 📄   │ │ 📷   │   │
│  │ TEXT │ │ FILE │ │ SCAN │   │
│  └──────┘ └──────┘ └──────┘   │
├────────────────────────────────┤
│  ┌──────────────────────────┐ │
│  │ Enter text to summarize  │ │
│  │                          │ │
│  │ (Multi-line text field)  │ │
│  │                          │ │
│  └──────────────────────────┘ │
│                                │
│  AI Persona: [General ▼]       │
│                                │
│  [     SUMMARIZE     ]         │
│                                │
│  ℹ️ Draft auto-saved           │
└────────────────────────────────┘
```

**Interactive Elements:**
- Tab bar navigation (Text/File/Camera)
- Expandable text field (max 5,000 chars)
- Persona dropdown with 6 options
- Draft recovery dialog (nếu có draft)
- Character counter with warning at limit
- Floating Action Button (FAB) để quick summarize

#### **Màn 2: File Upload Screen**
```
┌────────────────────────────────┐
│  ← Upload Document             │
├────────────────────────────────┤
│                                │
│     ┌──────────────────┐       │
│     │   📁             │       │
│     │                  │       │
│     │  Drop file here  │       │
│     │  or tap to browse│       │
│     │                  │       │
│     └──────────────────┘       │
│                                │
│  Supported formats:            │
│  • PDF (max 50 pages)          │
│  • DOCX, DOC                   │
│  • TXT, RTF                    │
│                                │
│  ┌──────────────────────────┐ │
│  │ 📄 document.pdf          │ │
│  │ 2.5 MB • 15 pages        │ │
│  │ [Processing...] 45%      │ │
│  └──────────────────────────┘ │
│                                │
│  [    PROCESS DOCUMENT    ]    │
└────────────────────────────────┘
```

**Features:**
- Drag & drop file upload
- File validation (size, type, page count)
- Progress indicator
- Large PDF warning dialog (>50 pages)
- Processing strategy selection (STANDARD/SECTIONED/PARALLEL)

#### **Màn 3: OCR Camera Screen**
```
┌────────────────────────────────┐
│  ← Scan Document         [?]   │
├────────────────────────────────┤
│                                │
│  ┌──────────────────────────┐ │
│  │                          │ │
│  │   Camera Preview         │ │
│  │                          │ │
│  │   ┌────────────┐         │ │
│  │   │  Document  │         │ │
│  │   │   Frame    │         │ │
│  │   └────────────┘         │ │
│  │                          │ │
│  └──────────────────────────┘ │
│                                │
│  📸 Tips: Align document       │
│      within the frame          │
│                                │
│  [Auto]  [Flash]  [Gallery]    │
│                                │
│       [ 📷 CAPTURE ]           │
└────────────────────────────────┘
```

**Features:**
- Real-time camera preview
- Document boundary detection
- Auto-capture when aligned
- Flash toggle
- Gallery picker fallback
- Permission request dialogs
- ML Kit text recognition feedback

#### **Màn 4: Processing Screen**
```
┌────────────────────────────────┐
│  Processing...                 │
├────────────────────────────────┤
│                                │
│                                │
│      ┌────────────┐            │
│      │  ⟳ 🤖     │            │
│      │  Loading   │            │
│      └────────────┘            │
│                                │
│   AI is analyzing your text    │
│                                │
│  ▓▓▓▓▓▓▓▓▓▓░░░░░░  65%        │
│                                │
│  Status: Processing section 3  │
│          of 5...               │
│                                │
│  Estimated time: 2s            │
│                                │
│       [   CANCEL   ]           │
│                                │
└────────────────────────────────┘
```

**Features:**
- Animated loading indicator (Lottie or Compose animation)
- Real-time progress bar
- Stage-by-stage status updates
- Estimated time remaining
- Cancel button với confirmation dialog
- Shimmer effects

#### **Màn 5: Result Screen**
```
┌────────────────────────────────┐
│  ← Summary  ⭐ 📤 ⋮           │
├────────────────────────────────┤
│  ┌──────────────────────────┐ │
│  │ 📊 Metrics               │ │
│  │ Original: 1,250 words    │ │
│  │ Summary: 250 words       │ │
│  │ Reduction: 80%           │ │
│  │ Time saved: 8 min        │ │
│  └──────────────────────────┘ │
│                                │
│  Summary (General)             │
│  ┌──────────────────────────┐ │
│  │ The main points are:     │ │
│  │                          │ │
│  │ • Key insight 1          │ │
│  │ • Key insight 2          │ │
│  │ • Key insight 3          │ │
│  │                          │ │
│  │ Conclusion: ...          │ │
│  └──────────────────────────┘ │
│                                │
│  [Copy]  [Export]  [Retry]     │
└────────────────────────────────┘
```

**Features:**
- KPI cards với metrics visualization
- Collapsible original text section
- Bullet-point formatting
- Copy to clipboard với haptic feedback
- Export options: Text, Markdown, PDF
- Share functionality
- Favorite toggle
- Persona badge display
- AI quality score indicator

#### **Màn 6: History Screen**
```
┌────────────────────────────────┐
│  History  🔍 ⚙️               │
├────────────────────────────────┤
│  ┌──────────────────────────┐ │
│  │  🔎 Search summaries...  │ │
│  └──────────────────────────┘ │
│                                │
│  Filters: [All ▼] [⭐ Favorites]│
│                                │
│  Today                         │
│  ┌──────────────────────────┐ │
│  │ 📝 Project Summary       │ │
│  │ 250 words • 10:30 AM     │ │
│  │ ⭐ General persona       │ │
│  └──────────────────────────┘ │
│  ┌──────────────────────────┐ │
│  │ 📄 Research Paper        │ │
│  │ 180 words • 09:15 AM     │ │
│  │ Academic persona         │ │
│  └──────────────────────────┘ │
│                                │
│  Yesterday                     │
│  ...                           │
└────────────────────────────────┘
```

**Features:**
- Real-time search với fuzzy matching
- Filter by: Date, Persona, Input type, Favorites
- Section headers (Today, Yesterday, This week, etc.)
- Swipe actions: Delete, Favorite, Share
- Empty state illustration
- Pull-to-refresh
- Infinite scroll/pagination
- Shimmer loading states

#### **Màn 7: Settings Screen**
```
┌────────────────────────────────┐
│  ← Settings                    │
├────────────────────────────────┤
│                                │
│  API Configuration             │
│  ┌──────────────────────────┐ │
│  │ 🔑 Gemini API Key        │ │
│  │ AIza****3f2 (Active)     │ │
│  │ Usage: 45/60 per min     │ │
│  └──────────────────────────┘ │
│  [+ Add New Key]               │
│                                │
│  Appearance                    │
│  • Theme:  [Auto ▼]            │
│  • Language: [English ▼]       │
│                                │
│  Advanced                      │
│  • Max characters: 5000        │
│  • Auto-save drafts: ON        │
│  • Background processing: ON   │
│                                │
│  About                         │
│  • Version 1.0.3               │
│  • Privacy Policy              │
│  • Terms of Service            │
│                                │
└────────────────────────────────┘
```

**Features:**
- API key management (add/edit/delete) với encryption
- Usage statistics visualization
- Theme selector với live preview
- Language switcher (EN/VI)
- Advanced settings với explanations
- Version info với update checker
- About section with team credits

---

## 2. MOCKUPS & PROTOTYPES

### 2.1. Figma Design Links

**🎨 Design System:**
```
Figma Project: SumUp Design System
├── 📁 Components Library
│   ├── Buttons (Primary, Secondary, Text, Icon)
│   ├── Cards (Summary, Metric, History Item)
│   ├── Input Fields (Text, File, Search)
│   ├── Dialogs (Confirmation, Error, Info)
│   └── Navigation (Top bar, Bottom nav, Drawer)
├── 📁 Screens (7 màn hình)
│   ├── Main Screen (variants: Empty, With draft, With error)
│   ├── File Upload (variants: Empty, Uploading, Success, Error)
│   ├── Camera OCR (variants: Permission, Scanning, Processing)
│   ├── Processing (variants: Analyzing, Sectioning, Streaming)
│   ├── Result (variants: Success, With metrics, Export options)
│   ├── History (variants: Empty, Loaded, Searching, Filtered)
│   └── Settings (variants: Default, API config, Theme selection)
├── 📁 Prototypes
│   ├── User Flow 1: Text Input → Summary
│   ├── User Flow 2: PDF Upload → Large file handling
│   ├── User Flow 3: Camera scan → OCR → Summary
│   ├── User Flow 4: History search → View details
│   └── User Flow 5: Settings → API key management
└── 📁 Style Guide
    ├── Colors (Light/Dark theme)
    ├── Typography (Material 3)
    ├── Spacing & Layout grid
    └── Icons & Illustrations
```

**Interactive Prototype Features:**
- Click-through navigation
- Animated transitions
- State changes (loading, success, error)
- Form validation feedback
- Responsive layouts (phone/tablet)

### 2.2. Style Guides

**Typography Scale:**
```
Display Large:   57sp / Roboto Regular / -0.25
Display Medium:  45sp / Roboto Regular / 0
Display Small:   36sp / Roboto Regular / 0
Headline Large:  32sp / Roboto Regular / 0
Headline Medium: 28sp / Roboto Regular / 0
Headline Small:  24sp / Roboto Regular / 0
Title Large:     22sp / Roboto Medium / 0
Title Medium:    16sp / Roboto Medium / 0.15
Title Small:     14sp / Roboto Medium / 0.1
Body Large:      16sp / Roboto Regular / 0.5
Body Medium:     14sp / Roboto Regular / 0.25
Body Small:      12sp / Roboto Regular / 0.4
Label Large:     14sp / Roboto Medium / 0.1
Label Medium:    12sp / Roboto Medium / 0.5
Label Small:     11sp / Roboto Medium / 0.5
```

**Component Specs:**
```kotlin
// Button Styles
PrimaryButton:
  - Height: 40dp
  - Padding: 24dp horizontal, 10dp vertical
  - Corner radius: 20dp (Full rounded)
  - Elevation: 2dp

TextInputField:
  - Height: 56dp (single line) / auto (multiline)
  - Padding: 16dp
  - Corner radius: 4dp (top)
  - Border: 1dp (outline style)

Card:
  - Elevation: 1dp
  - Corner radius: 12dp
  - Padding: 16dp
  - Background: Surface color
```

### 2.3. User Flows

#### **Flow 1: Text Summarization (Happy Path)**
```
1. User opens app → Main Screen
2. Sees draft recovery dialog → Restores/Dismisses
3. Enters text in text field (auto-save after 2s)
4. Selects AI persona (default: General)
5. Taps "SUMMARIZE" button
6. → Navigate to Processing Screen
7. Shows animated loading (3-5s)
8. → Auto-navigate to Result Screen
9. Views summary with metrics
10. Taps "Copy" → Haptic feedback + Snackbar confirmation
```

#### **Flow 2: PDF Document Processing**
```
1. User taps "FILE" tab → Main Screen switches
2. Taps upload area → File picker opens
3. Selects PDF file (e.g., 30 pages, 2.5MB)
4. → File validation
5. Shows upload progress bar
6. File validated → Processing Screen
7. Shows sectioning status (5 sections)
8. Parallel processing with progress
9. → Result Screen with combined summary
10. User can expand to see section summaries
```

#### **Flow 3: Error Handling**
```
1. User uploads large PDF (65 pages)
2. → Dialog: "Large PDF Warning"
   Options: [Process All] [Select Pages] [Cancel]
3. User selects "Process All"
4. → Processing Screen
5. Network error occurs
6. → Error dialog with retry option
7. User taps "Retry"
8. → Back to processing
9. Success → Result Screen
```

---

## 3. THIẾT KẾ API (UI-Related)

### 3.1. Tài liệu Mô tả API (cho UI)

**API Endpoints sử dụng:**
```
POST /v1/models/gemini-1.5-flash:generateContent
Headers:
  - x-goog-api-key: {API_KEY}
  - Content-Type: application/json

Request Body:
{
  "contents": [{
    "parts": [{
      "text": "Summarize this: {user_text}"
    }]
  }],
  "generationConfig": {
    "temperature": 0.7,
    "maxOutputTokens": 1024,
    "topP": 0.8
  }
}

Response:
{
  "candidates": [{
    "content": {
      "parts": [{
        "text": "Summary result..."
      }]
    }
  }],
  "usageMetadata": {
    "promptTokenCount": 250,
    "candidatesTokenCount": 150,
    "totalTokenCount": 400
  }
}
```

### 3.2. Tham số & Kiểu dữ liệu

**Request Models:**
```kotlin
data class SummarizeRequest(
    val text: String,
    val persona: SummaryPersona,
    val maxLength: Int = 1024
)

enum class SummaryPersona {
    GENERAL,
    STUDENT,
    PROFESSIONAL,
    ACADEMIC,
    CREATIVE,
    QUICK_BRIEF
}
```

**Response Models:**
```kotlin
data class SummaryResponse(
    val summary: String,
    val metrics: SummaryMetrics,
    val aiQualityScore: Float
)

data class SummaryMetrics(
    val originalWordCount: Int,
    val summaryWordCount: Int,
    val reductionPercentage: Float,
    val readingTimeSaved: Int // minutes
)
```

### 3.3. Kiểu dữ liệu & Cấu hình

**UI State Models:**
```kotlin
data class MainUiState(
    val inputText: String = "",
    val inputType: InputType = InputType.TEXT,
    val selectedPersona: SummaryPersona = SummaryPersona.GENERAL,
    val isProcessing: Boolean = false,
    val error: AppError? = null,
    val hasActiveDraft: Boolean = false,
    val fileUploadState: FileUploadState? = null
)

sealed class FileUploadState {
    data class Uploading(val progress: Float) : FileUploadState()
    data class Success(val document: Document) : FileUploadState()
    data class Error(val message: String) : FileUploadState()
}
```

**API Display Metadata:**
```kotlin
data class ApiKeyInfo(
    val keyId: String,
    val label: String,
    val maskedKey: String, // "AIza****3f2"
    val isActive: Boolean,
    val usageCount: Int,
    val rateLimit: RateLimit
)

data class RateLimit(
    val current: Int,    // 45
    val max: Int,        // 60
    val window: String   // "per minute"
)
```

---

## 4. HƯỚNG DẪN SỬ DỤNG

### 4.1. User Manual (Người dùng cuối)

**Bước 1: Cài đặt & Khởi động**
1. Tải APK từ [Google Drive](https://drive.google.com/drive/folders/14ZlVof3C42ugtQ4hR_T7P_rpb-MKZaLD)
2. Cài đặt (cho phép "Install from unknown sources" nếu cần)
3. Mở ứng dụng → Màn hình Onboarding (lần đầu)
4. Điền API key Gemini (hoặc dùng mock để demo)

**Bước 2: Tóm tắt văn bản**
1. Chọn phương thức input:
   - 📝 TEXT: Nhập/paste văn bản
   - 📄 FILE: Upload PDF/DOCX
   - 📷 SCAN: Chụp ảnh/scan tài liệu
2. Chọn AI Persona phù hợp
3. Nhấn "SUMMARIZE"
4. Đợi 3-5 giây
5. Xem kết quả với metrics

**Bước 3: Quản lý kết quả**
1. Vào tab "History" (📚)
2. Search/filter summaries
3. Swipe để xóa hoặc favorite
4. Tap để xem chi tiết
5. Export: Text, Markdown, PDF

**Bước 4: Cài đặt**
1. Vào Settings (⚙️)
2. Quản lý API keys
3. Đổi theme (Dark/Light/Auto)
4. Chọn ngôn ngữ (EN/VI)

### 4.2. Cấu hình & Cài đặt (cho Developers)

**Development Setup:**
```bash
# Clone repository
git clone https://github.com/Ductam7415vn/SumUp.git
cd SumUp

# Figma Design Import
# 1. Export components từ Figma
# 2. Import vào Android Studio với Relay plugin
# 3. Generate Compose code từ Figma components

# Build flavors
./gradlew assembleDevDebug      # Development
./gradlew assembleStagingDebug  # Staging
./gradlew assembleProdRelease   # Production

# Run UI tests
./gradlew connectedAndroidTest
```

**Figma to Code Workflow:**
```
1. Design trong Figma với naming convention:
   - Screens: MainScreen, ResultScreen, etc.
   - Components: SummaryCard, MetricCard, etc.

2. Export với Figma Relay plugin:
   - Select component → Export to Android
   - Auto-generate Compose code

3. Customize trong code:
   - Add ViewModel logic
   - Wire up navigation
   - Add interactions
```

---

## 5. QUẢN LÝ THAY ĐỔI

### 5.1. Change Log UI/UX

**Version 1.0.3 (Current)**
- ✅ Material 3 full migration
- ✅ Adaptive layouts cho tablet
- ✅ Dark theme support
- ✅ Vietnamese language support
- ✅ Figma design system complete

**Version 1.0.2**
- ✅ Added streaming summaries
- ✅ Improved processing animations
- ✅ Enhanced error displays

**Version 1.0.1**
- ✅ Initial UI implementation
- ✅ Basic Material Design

### 5.2. Yêu cầu thay đổi

**Pending Changes:**
- [ ] Animations polish (Lottie integration)
- [ ] Accessibility improvements (TalkBack support)
- [ ] Tablet optimization (multi-pane layouts)
- [ ] Widget support (Home screen widget)

**Future Enhancements:**
- Voice input UI
- Collaborative features
- Cloud sync UI
- Advanced export options UI

### 5.3. Tính trạng thay đổi

| Change ID | Description | Status | Priority | Assignee |
|-----------|-------------|--------|----------|----------|
| UI-001 | Lottie loading animation | Planned | Medium | UI/UX Designer |
| UI-002 | TalkBack accessibility | In Progress | High | Front-end Dev |
| UI-003 | Tablet multi-pane | Planned | Low | Front-end Dev |
| UI-004 | Home screen widget | Backlog | Low | Front-end Dev |

---

## PHỤ LỤC

### A. Figma Component Library

**Base Components:**
- ✅ Material 3 Button variations (40+ variants)
- ✅ Text Fields (Single, Multi-line, Search)
- ✅ Cards (Elevated, Filled, Outlined)
- ✅ Dialogs (Alert, Full-screen, Bottom sheet)
- ✅ Navigation (Top bar, Bottom bar, Rail, Drawer)
- ✅ Lists (Single line, Two line, Three line)
- ✅ Chips (Filter, Input, Suggestion)

**Custom Components:**
- ✅ SummaryCard với swipe actions
- ✅ MetricCard với animated counters
- ✅ ProcessingIndicator với shimmer
- ✅ PersonaSelector dropdown
- ✅ ApiKeyCard với secure display
- ✅ FileUploadZone với drag-drop

### B. Design Tokens Export

```json
{
  "colors": {
    "primary": "#6750A4",
    "onPrimary": "#FFFFFF",
    "primaryContainer": "#EADDFF",
    "onPrimaryContainer": "#21005D"
  },
  "typography": {
    "displayLarge": {
      "fontSize": 57,
      "lineHeight": 64,
      "fontWeight": 400
    }
  },
  "spacing": {
    "xs": 4,
    "sm": 8,
    "md": 16,
    "lg": 24,
    "xl": 32
  }
}
```

### C. Responsive Breakpoints

```kotlin
// Window Size Classes
Compact:  width < 600dp  (Phone portrait)
Medium:   600dp ≤ width < 840dp  (Tablet portrait, Phone landscape)
Expanded: width ≥ 840dp  (Tablet landscape)

// Adaptive Layouts
CompactScreen:
  - Single column
  - Bottom navigation
  - Modal dialogs

MediumScreen:
  - Two columns (optional)
  - Navigation rail
  - Side sheets

ExpandedScreen:
  - Three columns
  - Persistent drawer
  - Multi-pane layouts
```

---

**Ngày tạo**: Ngày hiện tại
**Phiên bản**: 1.0
**Designer**: Team SumUp UI/UX
**Tools**: Figma (Primary), Adobe XD (Secondary)
**Status**: ✅ Complete & Production-ready

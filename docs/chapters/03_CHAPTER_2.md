## 2.1. Kiến trúc Thông tin (Information Architecture)

### 2.1.1. Nguyên tắc thiết kế kiến trúc thông tin

**Mục đích của Information Architecture (IA):**

Information Architecture là nghệ thuật tổ chức và cấu trúc nội dung một cách có hệ thống, giúp người dùng dễ dàng tìm thấy thông tin và hoàn thành nhiệm vụ. IA tốt là **không nhìn thấy** - người dùng điều hướng tự nhiên mà không phải suy nghĩ.

**Nguyên tắc IA áp dụng cho SumUp:**

**1. CLARITY (Rõ ràng):**

✅ **Áp dụng:**
- Tên màn hình và labels phải rõ ràng, không mơ hồ
- Main Screen có 3 tabs: "Text", "Document", "OCR" (không dùng từ phức tạp)
- Navigation items dùng Icons + Labels để tăng khả năng nhận diện

**2. SIMPLICITY (Đơn giản):**

✅ **Áp dụng:**
- Flat hierarchy: Tối đa 2-3 levels (Main → Feature → Detail)
- Tuân thủ "3-click rule": Mọi tính năng đến được trong ≤3 taps
- Hide complexity: Advanced features ẩn trong menus/settings

**3. CONSISTENCY (Nhất quán):**

✅ **Áp dụng:**
- Navigation pattern nhất quán trên toàn app
- Iconography theo Material Design guidelines
- Terminology thống nhất (VD: "Summary" không lẫn với "Tóm tắt" trong cùng ngôn ngữ)

**4. FINDABILITY (Dễ tìm thấy):**

✅ **Áp dụng:**
- History có Search bar ở vị trí prominent
- Filter options dễ access (Filter icon ở TopAppBar)
- Settings có cấu trúc sections rõ ràng

**5. SCALABILITY (Mở rộng được):**

✅ **Áp dụng:**
- Architecture cho phép thêm input types mới (future: Voice, URL)
- Persona system mở rộng được (hiện tại 6, có thể thêm custom personas)
- Export formats có thể scale (hiện tại 3, dễ thêm mới)

**6. HIERARCHY (Phân cấp hợp lý):**

✅ **Áp dụng:**
- Primary navigation: Bottom Navigation / Navigation Rail (Main, History, Settings)
- Secondary navigation: Tabs trong Main Screen (Text, Document, OCR)
- Tertiary: Menus, Dialogs, Bottom Sheets cho advanced options

---

### 2.1.2. Sơ đồ phân cấp màn hình và tính năng

**Hình 2.1: Sơ đồ kiến trúc thông tin tổng thể của SumUp**

```
                           ┌─────────────────────┐
                           │   SumUp App Root    │
                           └──────────┬──────────┘
                                      │
                ┌─────────────────────┼─────────────────────┐
                │                     │                     │
                ▼                     ▼                     ▼
        ┌───────────────┐     ┌───────────────┐    ┌───────────────┐
        │  Main Screen  │     │ History Screen│    │ Settings Screen│
        │   (Primary)   │     │   (Primary)   │    │   (Primary)   │
        └───────┬───────┘     └───────┬───────┘    └───────┬───────┘
                │                     │                     │
        ┌───────┼───────┐             │              ┌──────┴──────┐
        │       │       │             │              │             │
        ▼       ▼       ▼             ▼              ▼             ▼
    ┌──────┬──────┬──────┐    ┌──────────────┐  ┌─────────┐  ┌─────────┐
    │ Text │ Doc  │ OCR  │    │ Search/Filter│  │API Keys │  │ Themes  │
    │ Tab  │ Tab  │ Tab  │    │  Interface   │  │ Mgmt    │  │ & Lang  │
    └──┬───┴──┬───┴──┬───┘    └──────┬───────┘  └─────────┘  └─────────┘
       │      │      │               │
       │      │      │               ▼
       │      │      │        ┌──────────────┐
       │      │      │        │ Summary Item │
       │      │      │        │   Details    │
       │      │      │        └──────┬───────┘
       │      │      │               │
       └──────┴──────┴───────────────┘
                │
                ▼
        ┌───────────────┐
        │   Processing  │
        │     Screen    │
        └───────┬───────┘
                │
                ▼
        ┌───────────────┐
        │ Result Screen │──┐
        │   (Adaptive)  │  │
        └───────────────┘  │
                │          │
        ┌───────┼──────────┘
        │       │
        ▼       ▼
    ┌─────┐ ┌─────────┐
    │Share│ │ Export  │
    └─────┘ └─────────┘
```

**Bảng 2.1: Danh sách các màn hình chính và chức năng**

| **Màn hình** | **Vai trò** | **Tính năng chính** | **Navigation Access** |
|--------------|-------------|---------------------|-----------------------|
| **Main Screen** | Entry point, input hub | - 3 tabs (Text, Document, OCR)<br>- Persona selector<br>- Draft auto-save<br>- Summarize CTA | Bottom Nav / Nav Rail |
| **OCR Screen** | Camera scanner | - Camera preview<br>- Capture button<br>- Text preview dialog<br>- Scan another / Proceed | From Main → OCR Tab |
| **Processing Screen** | Loading state | - Progress indicator<br>- Animated icon<br>- Status text<br>- Cancel option | Auto-navigate from Main |
| **Result Screen** | Summary display | - Summary content<br>- KPI metrics cards<br>- Persona selector<br>- FAB menu (Share, Export, Copy) | Auto-navigate from Processing |
| **History Screen** | Past summaries | - Search bar<br>- Filter bottom sheet<br>- Summary list (grouped by date)<br>- Swipe actions | Bottom Nav / Nav Rail |
| **Settings Screen** | Configuration | - API Key management<br>- Theme & Language<br>- About & Help | Bottom Nav / Nav Rail |
| **Onboarding** | First-time UX | - Welcome screens<br>- Feature highlights<br>- Permissions request | First launch only |

**Tổng cộng: 7 màn hình chính**

---

### 2.1.3. Hệ thống điều hướng (Navigation System)

**Hình 2.2: Sơ đồ hệ thống điều hướng chính**

**Navigation Strategy cho SumUp:**

Dựa trên Material Design 3 guidelines và adaptive layout requirements, SumUp sử dụng **Adaptive Navigation** với 3 patterns tùy theo screen size:

**1. COMPACT (360dp - 599dp) - Điện thoại:**
```
┌──────────────────────┐
│   TopAppBar          │
│   Title + Actions    │
├──────────────────────┤
│                      │
│   Screen Content     │
│                      │
│                      │
├──────────────────────┤
│  Bottom Navigation   │
│  [Main][History][⚙] │
└──────────────────────┘
```

**Bottom Navigation Bar với 3 items:**
- 🏠 Main (Home icon)
- 📝 History (History icon)
- ⚙️ Settings (Settings icon)

**2. MEDIUM (600dp - 839dp) - Tablets:**
```
┌──────────────────────────┐
│    TopAppBar             │
├──────────────────────────┤
│                          │
│    Screen Content        │
│    (wider layout)        │
│                          │
├──────────────────────────┤
│   Bottom Navigation      │
│   [Main] [History] [⚙]  │
└──────────────────────────┘
```

**Giữ nguyên Bottom Navigation** nhưng wider spacing

**3. EXPANDED (840dp+) - Foldables, Desktop:**
```
┌─────┬────────────────────┐
│     │   TopAppBar        │
│     ├────────────────────┤
│ Nav │                    │
│Rail │  Screen Content    │
│     │  (2-column layout) │
│ [⚙] │                    │
│[📝] │                    │
│[🏠] │                    │
└─────┴────────────────────┘
```

**NavigationRail** (sidebar) thay cho Bottom Navigation

**Bảng 2.2: Ma trận điều hướng giữa các màn hình**

| **From \ To** | **Main** | **OCR** | **Processing** | **Result** | **History** | **Settings** |
|---------------|----------|---------|----------------|------------|-------------|--------------|
| **Main** | - | Tab switch | "Summarize" button | - | Nav Bar | Nav Bar |
| **OCR** | Tab switch | - | "Summarize" button | - | Nav Bar | Nav Bar |
| **Processing** | Back (cancel) | - | - | Auto (on success) | - | - |
| **Result** | Back button | - | - | - | Nav Bar | Nav Bar |
| **History** | Nav Bar | - | - | Click item | - | Nav Bar |
| **Settings** | Nav Bar | - | - | - | Nav Bar | - |

**Navigation Principles:**

✅ **Persistent Navigation:** Bottom Nav / Nav Rail luôn hiển thị (trừ fullscreen states)

✅ **Clear Back Stack:**
- Main, History, Settings là top-level destinations (không có back button)
- Processing, Result, OCR có back button để return

✅ **Contextual Actions:**
- TopAppBar actions thay đổi theo context (VD: History có Search icon)
- FAB chỉ xuất hiện khi phù hợp (VD: Result screen có FAB menu)

✅ **Gesture Navigation:**
- Swipe back để return (Android native gesture)
- Swipe actions trong History list items

---

<div style="page-break-after: always;"></div>

## 2.2. Sơ đồ Luồng người dùng (User Flow Diagrams)

User Flow Diagrams visualize các bước người dùng thực hiện để hoàn thành một nhiệm vụ. Diagrams này giúp identify friction points và optimize UX.

**Ký hiệu sử dụng:**

```
┌─────────┐   Màn hình / State
│         │
└─────────┘

    ▼         Luồng tiến (forward flow)

◇ Decision  ◇ Decision point (if/else)

[Action]     User action

《System》    System action (auto)
```

---

### 2.2.1. Luồng chính: Tóm tắt văn bản từ Text Input

**Hình 2.3: User Flow - Tóm tắt văn bản từ Text Input**

**Scenario:** Minh (sinh viên) cần tóm tắt 1 đoạn text đã copy từ paper.

```
         START
           │
           ▼
    ┌─────────────┐
    │   Launch    │
    │   App       │
    └──────┬──────┘
           │
           ▼
    《Check Draft》
           │
      ┌────┴────┐
      │         │
    YES        NO
      │         │
      ▼         ▼
┌──────────┐ ┌──────────┐
│ Show     │ │ Empty    │
│ Draft    │ │ Text     │
│ Dialog   │ │ Input    │
└────┬─────┘ └────┬─────┘
     │            │
 [Recover]    [Dismiss]
     │            │
     └────┬───────┘
          │
          ▼
   ┌──────────────┐
   │ Main Screen  │
   │  Text Tab    │
   └──────┬───────┘
          │
     [Paste Text]
          │
          ▼
   《Auto-save Draft》
   《Update char count》
          │
          ▼
     ◇ Text Valid? ◇
          │
    ┌─────┴─────┐
   NO          YES
    │            │
    ▼            │
【Show Error】    │
 "Min 50 chars" │
    │            │
    └────┬───────┘
         │
    [Select Persona]
         │
         ▼
  《Remember preference》
         │
         ▼
    [Tap "Summarize"]
         │
         ▼
    《Validate API key》
         │
    ◇ Has API key? ◇
         │
    ┌────┴────┐
   NO        YES
    │         │
    ▼         │
【Navigate to】 │
【Settings  】 │
    │         │
    └────┬────┘
         │
         ▼
   ┌──────────────┐
   │  Processing  │
   │    Screen    │
   └──────┬───────┘
          │
   《Call Gemini API》
   《Show progress》
          │
     ◇ Success? ◇
          │
    ┌─────┴─────┐
   NO          YES
    │            │
    ▼            │
【Show Error】    │
【Snackbar  】    │
【+ Retry   】    │
    │            │
    └────┬───────┘
         │
         ▼
   《Clear Draft》
   《Save to History》
         │
         ▼
   ┌──────────────┐
   │   Result     │
   │   Screen     │
   └──────┬───────┘
          │
     [Read Summary]
          │
     ◇ Satisfied? ◇
          │
    ┌─────┴─────┐
   NO          YES
    │            │
    ▼            ▼
[Change Persona] [Copy/Share]
[Regenerate]          │
    │                 │
    │                 ▼
    │              END
    │
    └──> [Back to Main]
              │
              ▼
            END
```

**Bảng 2.3: Các bước trong User Flow tóm tắt văn bản**

| **Step** | **Actor** | **Action** | **System Response** | **Screen** |
|----------|-----------|------------|---------------------|------------|
| 1 | User | Launch app | Check for existing draft | Main |
| 2 | System | Detect draft | Show recovery dialog (if exists) | Main |
| 3 | User | Paste/type text | Auto-save every 2s, update char count | Main |
| 4 | User | Select persona (optional) | Remember preference | Main |
| 5 | User | Tap "Summarize" | Validate input & API key | Main |
| 6 | System | Navigate | Show processing animation | Processing |
| 7 | System | Call API | Display progress (0-100%) | Processing |
| 8 | System | Success | Clear draft, save to history, navigate | Processing |
| 9 | User | View result | Display summary + metrics | Result |
| 10 | User | Copy/Share/Export | Execute action | Result |

**Design Decisions từ Flow này:**

✅ **Draft Recovery Dialog:**
- Xuất hiện khi có draft <24h
- 2 options: "Recover" (default) và "Discard"
- Ngăn người dùng mất công paste lại

✅ **Real-time Validation:**
- Character count live update
- Error message inline nếu <50 chars
- "Summarize" button disabled khi invalid

✅ **Smart API Key Check:**
- Validate trước khi navigate to Processing
- Nếu không có key → Dialog suggest đến Settings
- Không waste user's time với "Processing" rồi mới báo lỗi

✅ **Progress Transparency:**
- Show progress % trong Processing screen
- Cancel option nếu user đổi ý
- Estimated time remaining

✅ **Error Recovery:**
- Network error → Snackbar với "Retry" action
- API error → Dialog với error details + "Try Again"
- Không navigate back, giữ context

---

### 2.2.2. Luồng phụ: Upload và xử lý tài liệu (PDF/DOCX)

**Hình 2.4: User Flow - Upload và xử lý tài liệu PDF/DOCX**

**Scenario:** Dr. Hoàng cần tóm tắt 1 paper PDF 15 trang.

```
         START
           │
           ▼
    ┌─────────────┐
    │ Main Screen │
    │ Document Tab│
    └──────┬──────┘
           │
    [Tap "Upload"]
           │
           ▼
    《Open File Picker》
           │
           ▼
    [Select File]
           │
           ▼
    《Validate File》
           │
     ◇ Valid? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【Error Dialog】   │
- Wrong format    │
- Too large       │
- Corrupted       │
    │              │
    └──────┬───────┘
           │
           ▼
    《Extract metadata》
    《Show file info》
           │
           ▼
   ┌──────────────┐
   │  File Card   │
   │  Preview     │
   └──────┬───────┘
           │
   ◇ Large file? ◇
   (>50 pages)
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    │              ▼
    │      【Show Warning】
    │      "Large PDF"
    │              │
    │       ┌──────┴──────┐
    │       │             │
    │   [Full]      [Selective]
    │    Process      Process
    │       │             │
    │       └──────┬──────┘
    │              │
    └──────┬───────┘
           │
    [Select Persona]
           │
    [Tap "Summarize"]
           │
           ▼
   ┌──────────────┐
   │  Processing  │
   │   Screen     │
   └──────┬───────┘
           │
   《Extract Text》
           │
     ◇ Success? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【Error Handling】 │
- OCR fallback    │
- Manual text     │
    │              │
    └──────┬───────┘
           │
   《Call Gemini API》
   《Multi-stage if large》
           │
           ▼
   《Save to History》
   《Attach file metadata》
           │
           ▼
   ┌──────────────┐
   │   Result     │
   │   Screen     │
   └──────┬───────┘
           │
    [View Summary]
           │
    [Export/Share]
           │
           ▼
          END
```

**Bảng 2.4: Các bước trong User Flow upload tài liệu**

| **Step** | **Actor** | **Action** | **System Response** | **Validation** |
|----------|-----------|------------|---------------------|----------------|
| 1 | User | Navigate to Document tab | Show upload interface | - |
| 2 | User | Tap "Upload Document" | Open file picker (PDF/DOCX/TXT/RTF) | - |
| 3 | User | Select file | Validate format & size | ≤10MB, valid format |
| 4 | System | Extract metadata | Show file info card (name, size, pages) | - |
| 5 | System | Check file size | If >50 pages → Show warning dialog | Large file handling |
| 6 | User | Choose processing option (if large) | Remember preference | Full vs Selective |
| 7 | User | Select persona | Update UI | - |
| 8 | User | Tap "Summarize" | Navigate to Processing | - |
| 9 | System | Extract text | PDFBox for PDF, Mammoth for DOCX | Handle errors |
| 10 | System | Summarize | Call API (multi-stage if large) | Monitor progress |
| 11 | System | Save | Store summary + file metadata | Include file info |
| 12 | User | View result | Display summary | - |

**Design Decisions từ Flow này:**

✅ **File Validation Upfront:**
- Check format, size, corruption ngay khi select
- Clear error messages: "File too large (15MB > 10MB limit)"
- Suggest solutions: "Try uploading first 50 pages only"

✅ **File Preview Card:**
- Show file name, size, page count
- Small thumbnail icon (PDF/DOCX/TXT icon)
- "Remove" button để chọn lại file

✅ **Large File Warning:**
- Trigger khi >50 pages (configurable)
- Dialog explain: "Large files may take 30-60s to process"
- Options:
  - "Process Full Document" (default)
  - "Select Pages" (advanced)
  - "Cancel"

✅ **Progressive Processing:**
- Large files → Show detailed progress: "Extracting text... 45%"
- Multi-stage: Extract → Section → Summarize each → Combine
- Allow cancel during extraction

✅ **Metadata Preservation:**
- Save file name, type, size with summary
- Display in Result: "Summarized from: paper.pdf (12 pages)"
- Helpful for History search later

---

### 2.2.3. Luồng OCR: Quét và tóm tắt từ hình ảnh

**Hình 2.5: User Flow - OCR quét và tóm tắt từ hình ảnh**

**Scenario:** Minh cần tóm tắt 2 trang sách in không có PDF.

```
         START
           │
           ▼
    ┌─────────────┐
    │ Main Screen │
    │   OCR Tab   │
    └──────┬──────┘
           │
   《Request Camera》
   《Permission》
           │
     ◇ Granted? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【Permission】     │
【Rationale】      │
   Dialog          │
    │              │
    └──────┬───────┘
           │
           ▼
   ┌──────────────┐
   │ Camera View  │
   │   Preview    │
   └──────┬───────┘
           │
   [Adjust Angle]
   [Good Lighting]
           │
    [Tap Capture]
           │
           ▼
   《Capture Image》
   《Freeze Preview》
           │
           ▼
   《ML Kit OCR》
   《Process Image》
           │
     ◇ Text Found? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【No Text Error】  │
["No text detected" │
 "Try better light"]│
    │              │
   [Retry]         │
    │              │
    └──────┬───────┘
           │
           ▼
   ┌──────────────┐
   │ Text Preview │
   │   Dialog     │
   └──────┬───────┘
           │
   《Show extracted》
   《text for review》
           │
     ◇ Correct? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
 [Edit Text]       │
 [Scan Again]      │
    │              │
    └──────┬───────┘
           │
    [Tap "Summarize"]
           │
           ▼
   《Navigate to Main》
   《with OCR text》
           │
           ▼
   ┌──────────────┐
   │ Main Screen  │
   │  Text Tab    │
   │ (OCR filled) │
   └──────┬───────┘
           │
    [Select Persona]
           │
    [Tap "Summarize"]
           │
           ▼
       (Continue như
        Text Input flow)
           │
           ▼
          END
```

**Bảng 2.5: Các bước trong User Flow OCR**

| **Step** | **Actor** | **Action** | **System Response** | **Notes** |
|----------|-----------|------------|---------------------|-----------|
| 1 | User | Navigate to OCR tab | Request camera permission | First time only |
| 2 | System | Check permission | If denied → Show rationale dialog | Explain why needed |
| 3 | User | Grant permission | Show camera preview | - |
| 4 | User | Position camera, tap capture | Freeze frame, show loading | - |
| 5 | System | ML Kit processing | Extract text from image (2-5s) | Show progress |
| 6 | System | Validate result | If no text → Error dialog | Suggest retry |
| 7 | System | Show preview | Display extracted text in dialog | Allow review |
| 8 | User | Review text | Edit if needed (optional) | Inline editing |
| 9 | User | Confirm "Use This Text" | Navigate to Main with text filled | - |
| 10 | User | Continue flow | Same as Text Input from here | Select persona, summarize |

**Design Decisions từ Flow này:**

✅ **Permission Rationale:**
- Show clear explanation: "Camera needed to scan text from images"
- "Settings" button nếu user deny permanently
- Graceful degradation: Suggest upload image instead

✅ **Camera UX Best Practices:**
- Grid overlay để guide framing
- Tips: "Ensure good lighting", "Hold steady"
- Auto-focus indicator
- Flash toggle nếu lighting kém

✅ **OCR Feedback:**
- Show processing animation: "Scanning..."
- Confidence indicator nếu possible
- Highlight low-confidence words để user review

✅ **Text Preview & Edit:**
- Dialog với text scrollable
- Allow inline editing nếu OCR sai
- "Scan Another" button để capture lại
- "Use This Text" CTA button

✅ **Seamless Handoff:**
- Auto-fill text vào Main Screen Text Tab
- User có thể edit thêm trước khi summarize
- Maintain context: "Scanned from image"

✅ **Multi-page Support:**
- "Scan Another Page" button sau capture thành công
- Append text vào existing (với separator)
- Useful cho books, documents nhiều trang

---

### 2.2.4. Luồng quản lý: Lịch sử và cài đặt

**Hình 2.6: User Flow - Quản lý lịch sử và cài đặt**

**Scenario A: Tìm kiếm summary đã tạo trong History**

```
         START
           │
           ▼
    ┌─────────────┐
    │  History    │
    │   Screen    │
    └──────┬──────┘
           │
     ◇ Has Content? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【Empty State】    │
 "No summaries    │
  yet"            │
 [Start Summarize]│
    │              │
    │              ▼
    │       ┌──────────────┐
    │       │ Summary List │
    │       │  (Grouped)   │
    │       └──────┬───────┘
    │              │
    │       [Tap Search Icon]
    │              │
    │              ▼
    │       《Show Search Bar》
    │              │
    │       [Type Query]
    │              │
    │       《Filter in Real-time》
    │              │
    │         ◇ Found? ◇
    │              │
    │       ┌──────┴──────┐
    │      NO            YES
    │       │              │
    │       ▼              │
    │  【No Results】     │
    │   Empty State       │
    │   "Try different    │
    │    keywords"        │
    │       │              │
    │       └──────┬───────┘
    │              │
    │       [Clear Search]
    │       [Apply Filters]
    │              │
    │              ▼
    │       ┌──────────────┐
    │       │ Filtered     │
    │       │ Results      │
    │       └──────┬───────┘
    │              │
    │       [Click Item]
    │              │
    │              ▼
    │       ┌──────────────┐
    │       │   Result     │
    │       │   Screen     │
    │       └──────┬───────┘
    │              │
    │       [Read Summary]
    │              │
    └──────────────┴───────
                   │
                   ▼
                  END
```

**Scenario B: Quản lý API Keys trong Settings**

```
         START
           │
           ▼
    ┌─────────────┐
    │  Settings   │
    │   Screen    │
    └──────┬──────┘
           │
   [Tap "API Keys"]
           │
           ▼
   ┌──────────────┐
   │  API Key     │
   │ Management   │
   └──────┬───────┘
           │
     ◇ Has Keys? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【Empty State】    │
 "Add API key     │
  to use SumUp"   │
    │              │
    └──────┬───────┘
           │
    [Tap "Add Key"]
           │
           ▼
   ┌──────────────┐
   │  Add Key     │
   │   Dialog     │
   └──────┬───────┘
           │
   [Paste API Key]
           │
   《Validate Format》
           │
     ◇ Valid Format? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【Format Error】   │
 "Invalid key     │
  format"         │
    │              │
    └──────┬───────┘
           │
   《Test API Key》
   《Call Gemini》
           │
     ◇ API Works? ◇
           │
    ┌──────┴──────┐
   NO            YES
    │              │
    ▼              │
【API Error】      │
 "Key invalid or  │
  quota exceeded" │
    │              │
    └──────┬───────┘
           │
   《Encrypt & Save》
   《Set as Active》
           │
           ▼
   ┌──────────────┐
   │  Key List    │
   │  Updated     │
   └──────┬───────┘
           │
   [Manage Keys:]
   - Set Active
   - View Usage
   - Delete
           │
           ▼
          END
```

**Bảng 2.6: Feature Matrix - History & Settings Flows**

| **Feature** | **Entry Point** | **Key Actions** | **Feedback** |
|-------------|----------------|-----------------|--------------|
| **Search History** | History screen → Search icon | Type query → Real-time filter | Show result count, "No results" state |
| **Filter History** | History screen → Filter icon | Open bottom sheet → Select filters | Show active filters as chips, "X results" |
| **View Summary** | History → Click item | Navigate to Result | Load from database |
| **Delete Summary** | History → Swipe left | Confirm dialog | Snackbar với "Undo" |
| **Favorite** | History → Swipe right / Star icon | Toggle state | Visual feedback (filled star) |
| **Add API Key** | Settings → API Keys → Add | Paste, validate, test, save | Success snackbar, error dialog |
| **Switch Active Key** | Settings → API Keys → Click | Set as active | Update indicator |
| **View Usage** | Settings → API Keys → Click | Show stats dialog | Requests count, quota |
| **Change Theme** | Settings → Appearance | Select Light/Dark/Auto | Immediate apply |
| **Change Language** | Settings → Language | Select VI/EN | Restart required dialog |

**Design Decisions:**

✅ **Smart Search:**
- Debounce 300ms để không spam queries
- Search across: Summary content, original text preview, date
- Highlight matches trong results

✅ **Advanced Filters:**
- Bottom Sheet UI (không take over full screen)
- Multiple filters combinable: Date + Persona + Input Type + Favorites
- "Clear All" button để reset nhanh
- Active filters shown as removable chips

✅ **Swipe Actions:**
- Swipe left → Delete (red background)
- Swipe right → Favorite (yellow/gold background)
- Haptic feedback on action
- Undo snackbar sau delete

✅ **API Key Security:**
- Input field obscured (password field style)
- "Show/Hide" eye icon
- Validation trước khi test
- Test call để verify key works
- Encrypted storage (EncryptedSharedPreferences)

✅ **Usage Tracking:**
- Show per-key statistics
- Quota warnings: "80% quota used"
- Suggestions: "Add backup key"

---

<div style="page-break-after: always;"></div>

## 2.3. Phác thảo Giao diện (Wireframing)

Wireframes là bản phác thảo low-fidelity của giao diện, tập trung vào **cấu trúc, layout và chức năng** thay vì visual design. Wireframes giúp:

- Visualize ideas nhanh chóng
- Test layout và flow trước khi đầu tư vào visual design
- Communicate với stakeholders
- Iterate dễ dàng (không mất time vào colors, fonts)

---

### 2.3.1. Nguyên tắc thiết kế wireframe

**Mục đích của wireframing:**

Wireframe là **blueprint** của giao diện - tập trung vào "cái gì ở đâu" và "hoạt động như thế nào", không phải "trông như thế nào".

**Các thành phần trong wireframe:**

| Thành phần | Mô tả | Ký hiệu |
|------------|-------|---------|
| **Layout boxes** | Các containers chính | `┌─────┐` |
| **Text labels** | Tiêu đề, labels | `[Label]` |
| **Buttons** | CTAs, actions | `[ Button ]` |
| **Input fields** | Text inputs | `┌─────────┐`<br>`│ Input..  │` |
| **Icons** | Placeholder cho icons | `[≡]` `[⚙]` |
| **Images** | Placeholder cho images | `[IMG]` |
| **Lists** | Danh sách items | `• Item 1`<br>`• Item 2` |

**Design Principles cho Wireframes:**

✅ **Keep it simple:** Grayscale, no colors, no fancy styling

✅ **Annotate:** Thêm notes giải thích interactions

✅ **Consistent:** Dùng cùng một style cho cùng element type

✅ **Realistic content:** Dùng real-ish labels, không dùng "Lorem ipsum"

✅ **Show states:** Normal, hover, active, disabled, error, loading

---

### 2.3.2. Wireframe cho màn hình chính

**Hình 2.7: Wireframe - Main Screen (Text Input Mode)**

```
┌─────────────────────────────────┐
│ ☰  SumUp            [⚙] [👤]   │ TopAppBar
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │ [Text] [Document] [OCR]   │ │ Tabs
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │ Type or paste text here.. │ │ Text Input
│  │                           │ │ (expandable)
│  │                           │ │
│  │                           │ │
│  │                           │ │
│  │                           │ │
│  │                           │ │
│  └───────────────────────────┘ │
│                                 │
│  📊 0/5,000 characters          │ Character count
│                                 │
│  ┌───────────────────────────┐ │
│  │ Persona: [General ▾]      │ │ Persona Selector
│  └───────────────────────────┘ │
│                                 │
│         [ Summarize ]           │ CTA Button
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │ Bottom Nav
└─────────────────────────────────┘
```

**Annotations - Main Screen (Text Tab):**

**TopAppBar:**
- Hamburger menu (☰): Open navigation drawer (future)
- Title: "SumUp"
- Settings icon (⚙): Navigate to Settings
- Profile icon (👤): User profile (future feature)

**Tabs:**
- 3 tabs: Text (active), Document, OCR
- Underline indicator shows active tab
- Tap to switch

**Text Input Area:**
- Multi-line text field
- Placeholder: "Type or paste text here..."
- Auto-expands as user types (up to 60% screen height)
- Scroll when overflow

**Character Count:**
- Live update as user types
- Format: "X/5,000 characters"
- Color changes: Green (<4,000), Yellow (4,000-4,900), Red (>4,900)
- Error message if >5,000: "Text too long. Please reduce to 5,000 characters"

**Persona Selector:**
- Dropdown/Bottom Sheet
- Options: General, Student, Professional, Academic, Creative, Quick Brief
- Remember last selection
- Icon + Label for each persona

**Summarize Button:**
- Large, prominent CTA
- Disabled state when:
  - Text <50 characters
  - Text >5,000 characters
  - No API key
- Ripple effect on tap

**Bottom Navigation:**
- 3 destinations: Main (Home), History, Settings
- Current destination highlighted
- Labels always visible

---

**Hình 2.8: Wireframe - Main Screen (Document Upload Mode)**

```
┌─────────────────────────────────┐
│ ☰  SumUp            [⚙] [👤]   │
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │ [Text] [Document] [OCR]   │ │
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │      📄                   │ │
│  │  Drag & Drop File         │ │
│  │        or                 │ │
│  │  [ Upload Document ]      │ │ Upload Button
│  │                           │ │
│  │  Supported: PDF, DOCX,    │ │
│  │  TXT, RTF (max 10MB)      │ │
│  └───────────────────────────┘ │
│                                 │
│ (If file selected:)             │
│  ┌───────────────────────────┐ │
│  │ [PDF] research_paper.pdf  │ │ File Card
│  │ 2.3 MB • 15 pages    [×]  │ │
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │ Persona: [Academic ▾]     │ │
│  └───────────────────────────┘ │
│                                 │
│         [ Summarize ]           │
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**Annotations - Document Tab:**

**Upload Area:**
- Drag & drop zone (desktop/tablet)
- "Upload Document" button
- Supported formats shown clearly
- Size limit displayed: "max 10MB"

**File Card (after upload):**
- File type icon (PDF/DOCX/TXT)
- File name (truncate if long)
- File size and page count
- Remove button (×) to upload different file

**Large File Warning:**
- If >50 pages → Show alert dialog
- Options:
  - "Process Full Document" (may take 30-60s)
  - "Select Pages" (advanced)
  - "Cancel"

---

### 2.3.3. Wireframe cho màn hình OCR

**Hình 2.9: Wireframe - OCR Scanner Screen**

```
┌─────────────────────────────────┐
│ ☰  Scan Text            [⚙]    │
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │ [Text] [Document] [OCR]   │ │
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │   ┌─────────────────┐     │ │
│  │   │                 │     │ │
│  │   │   Camera        │     │ │
│  │   │   Preview       │     │ │ Camera View
│  │   │                 │     │ │
│  │   │     [Grid]      │     │ │ Guide overlay
│  │   │                 │     │ │
│  │   └─────────────────┘     │ │
│  │                           │ │
│  │   💡 Tips:                │ │
│  │   • Good lighting         │ │
│  │   • Hold steady           │ │
│  │   • Avoid shadows         │ │
│  └───────────────────────────┘ │
│                                 │
│  [Flash: Off ▾]    [Switch 🔄] │ Controls
│                                 │
│           [ 📷 Capture ]        │ Capture button
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**Annotations - OCR Screen:**

**Camera Preview:**
- Full-screen camera feed (within safe area)
- Grid overlay (rule of thirds) để guide framing
- Auto-focus indicator
- Tap to focus

**Controls:**
- Flash toggle: Off / On / Auto
- Camera switch: Front / Rear
- Both với icons rõ ràng

**Tips Section:**
- Contextual tips để improve OCR accuracy
- Short, actionable
- Can be dismissed (future)

**Capture Button:**
- Large, circular button
- Center-bottom position (thumb-friendly)
- Haptic feedback on tap

---

### 2.3.4. Wireframe cho màn hình xử lý

**Hình 2.10: Wireframe - Processing Screen**

```
┌─────────────────────────────────┐
│  Processing                [×]  │ TopAppBar
├─────────────────────────────────┤
│                                 │
│                                 │
│         ┌──────────┐            │
│         │          │            │
│         │   ⟳     │            │ Animated
│         │          │            │ spinner
│         └──────────┘            │
│                                 │
│      Summarizing...             │ Status text
│                                 │
│  ┌───────────────────────────┐ │
│  │▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░│ │ Progress bar
│  └───────────────────────────┘ │
│                                 │
│        45% complete             │ Percentage
│                                 │
│    Estimated: 8 seconds         │ Time estimate
│                                 │
│                                 │
│                                 │
│         [ Cancel ]              │ Cancel button
│                                 │
│                                 │
│                                 │
└─────────────────────────────────┘
```

**Annotations - Processing Screen:**

**Animated Icon:**
- Lottie animation or Material spinner
- Rotation animation
- App logo or loading icon

**Status Text:**
- Dynamic updates:
  - "Analyzing text..."
  - "Generating summary..."
  - "Almost done..."

**Progress Indicator:**
- LinearProgressIndicator (Material 3)
- 0-100% with smooth animation
- Color: Primary color

**Percentage & Time:**
- Update in real-time
- Time estimate based on text length
- Countdown effect

**Cancel Button:**
- Allow user to abort
- Confirmation dialog: "Cancel summarization?"
- Return to previous screen

**States to Show:**

1. **Initial (0-10%):** "Analyzing text..."
2. **Mid (11-50%):** "Generating summary..."
3. **Final (51-99%):** "Almost done..."
4. **Complete (100%):** Auto-navigate to Result

---

### 2.3.5. Wireframe cho màn hình kết quả

**Hình 2.11: Wireframe - Result Screen**

```
┌─────────────────────────────────┐
│ ← Summary           [⭐] [⋮]    │ TopAppBar
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │ 📊 Original: 1,245 words  │ │
│  │ 📝 Summary: 248 words     │ │ KPI Cards
│  │ ⏱️ Saved: 4 min read      │ │
│  │ 📉 Reduction: 80%         │ │
│  └───────────────────────────┘ │
│                                 │
│  Persona: [Student ▾]           │ Persona
│                                 │
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │ • Key point 1             │ │
│  │                           │ │
│  │ • Key point 2             │ │
│  │                           │ │ Summary
│  │ • Key point 3             │ │ Content
│  │                           │ │ (scrollable)
│  │ Lorem ipsum dolor sit     │ │
│  │ amet, consectetur...      │ │
│  │                           │ │
│  │                           │ │
│  └───────────────────────────┘ │
│                                 │
│               ┌───┐             │
│               │ + │             │ FAB Menu
│               └───┘             │
│           (tap to expand:)      │
│           • Share               │
│           • Copy                │
│           • Export              │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**Annotations - Result Screen:**

**TopAppBar:**
- Back button: Return to Main
- Star icon: Toggle favorite
- Menu (⋮): Regenerate, Delete, Report issue

**KPI Metrics Cards:**
- 4 key metrics in grid (2x2)
- Icons + Numbers
- Color-coded for quick scan

**Persona Selector:**
- Dropdown to change persona
- On change: Regenerate summary immediately
- Show loading overlay during regeneration

**Summary Content:**
- Scrollable vertical
- Support both Bullet Points & Paragraphs
- Selectable text (for copying)
- Typography hierarchy:
  - Headings: Bold, larger
  - Bullets: Proper indentation
  - Paragraphs: Comfortable line height

**FAB (Floating Action Button):**
- Speed dial menu
- Expands on tap:
  - Share: Android share sheet
  - Copy: Copy to clipboard + snackbar
  - Export: Open export dialog (Text/MD/PDF)

---

### 2.3.6. Wireframe cho màn hình lịch sử

**Hình 2.12: Wireframe - History Screen**

```
┌─────────────────────────────────┐
│ ☰  History       [🔍] [⚙]      │ TopAppBar
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │ 🔍 Search summaries...    │ │ Search Bar
│  └───────────────────────────┘ │ (expandable)
│                                 │
│  [Date ▾] [Persona ▾] [Type ▾] │ Filter Chips
│                                 │
│  📊 12 results                  │ Result count
│                                 │
│  ── Today ──                    │ Section Header
│                                 │
│  ┌───────────────────────────┐ │
│  │ [⭐] Meeting Notes        │ │
│  │ Student • 10:30 AM        │ │ Summary Item
│  │ From: document.pdf        │ │
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │ [ ] Research Paper        │ │
│  │ Academic • 9:15 AM        │ │ Summary Item
│  │ From: text input          │ │
│  └───────────────────────────┘ │
│                                 │
│  ── Yesterday ──                │
│                                 │
│  ┌───────────────────────────┐ │
│  │ [⭐] Project Proposal     │ │
│  │ Professional • 3:45 PM    │ │
│  │ From: proposal.docx       │ │
│  └───────────────────────────┘ │
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**Annotations - History Screen:**

**Search & Filter:**
- Search bar (collapsible)
- 3 filter chips: Date, Persona, Type
- Real-time results with debounce

**Section Headers:**
- Grouped by time: Today, Yesterday, This Week, etc.
- Sticky headers when scrolling

**List Items:**
- Star icon for favorites
- Title, metadata (persona, time)
- Source indicator
- Swipe actions (delete, favorite)

**Empty States:**
- No summaries: Show illustration + CTA
- No search results: Clear search suggestion

---

### 2.3.7. Wireframe cho màn hình cài đặt

**Hình 2.13: Wireframe - Settings Screen**

```
┌─────────────────────────────────┐
│ ☰  Settings                     │
├─────────────────────────────────┤
│                                 │
│  ── API Keys ──                 │
│  ┌───────────────────────────┐ │
│  │ 🔑 API Key Management     │ │
│  │ 2 keys • 1 active    [>] │ │
│  └───────────────────────────┘ │
│                                 │
│  ── Appearance ──               │
│  Theme                          │
│  [Light] [Dark] [Auto]    ●    │
│                                 │
│  Language                       │
│  ○ English  ● Tiếng Việt        │
│                                 │
│  ── Preferences ──              │
│  Default Persona                │
│  [General ▾]                    │
│                                 │
│  Auto-save drafts               │
│  [ON ━━━●]                      │
│                                 │
│  ── Data ──                     │
│  ┌───────────────────────────┐ │
│  │ Clear History             │ │
│  │ 45 summaries        [>]   │ │
│  └───────────────────────────┘ │
│                                 │
│  ── About ──                    │
│  Version 1.0.3                  │
│  Help & Support          [>]   │
│  Privacy Policy          [>]   │
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**Annotations:**

**Sections:**
1. API Keys (most important)
2. Appearance (theme, language)
3. Preferences (defaults, auto-save)
4. Data management
5. About & help

**Controls:**
- Segmented buttons (theme)
- Radio buttons (language)
- Toggles (auto-save)
- Dropdowns (persona)
- Navigation cards (API keys, clear data)

---

**Tổng kết phần 2.3 - Wireframing:**

✅ Đã tạo wireframes cho **7 màn hình chính** với annotations đầy đủ

✅ Cover tất cả states: Normal, loading, error, empty

✅ Adaptive layouts cho mobile và tablet

✅ Interactive elements được document rõ ràng

---

<div style="page-break-after: always;"></div>

**Kết luận Chương 2:**

Chương 2 đã hoàn thành việc thiết kế **cấu trúc và luồng tương tác** cho ứng dụng SumUp:

✅ **Information Architecture (2.1):**
- 6 nguyên tắc IA
- Sơ đồ phân cấp 7 màn hình
- Hệ thống điều hướng adaptive
- Ma trận điều hướng

✅ **User Flow Diagrams (2.2):**
- 4 luồng chính với 40+ bước
- Decision points và system actions
- Error handling và recovery flows

✅ **Wireframes (2.3):**
- 13 wireframes cho tất cả screens
- Annotations chi tiết
- All states documented

Với foundation này, **Chương 3** sẽ biến wireframes thành **high-fidelity mockups** với Design System hoàn chỉnh.

---

<div style="page-break-after: always;"></div>

**--- KẾT THÚC CHƯƠNG 2 ---**

---


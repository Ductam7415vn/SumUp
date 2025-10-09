## 2.1. Kiến trúc Thông tin (Information Architecture)

### 2.1.1. Nguyên tắc thiết kế kiến trúc thông tin

**Mục đích của Information Architecture (IA):**

Information Architecture là nghệ thuật tổ chức và cấu trúc nội dung một cách có hệ thống, giúp người dùng dễ dàng tìm thấy thông tin và hoàn thành nhiệm vụ.
IA tốt là **không nhìn thấy**- người dùng điều hướng tự nhiên mà không phải suy nghĩ.

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

### 2.3.8. Wireframe cho màn hình Onboarding

**Hình 2.14: Wireframe - Onboarding Screen (3 slides)**

**Slide 1: Welcome**

```
┌─────────────────────────────────┐
│                                 │
│                                 │
│         ┌───────────┐           │
│         │           │           │
│         │   [IMG]   │           │ Logo/Illustration
│         │  SumUp    │           │
│         │           │           │
│         └───────────┘           │
│                                 │
│    Welcome to SumUp!            │ Heading
│                                 │
│  Your AI-powered assistant      │
│  for instant text               │ Subheading
│  summarization                  │
│                                 │
│                                 │
│    ○  ○  ○                      │ Page indicators
│                                 │
│                                 │
│         [ Get Started ]         │ CTA Button
│                                 │
│         Skip →                  │ Skip link
│                                 │
└─────────────────────────────────┘
```

**Slide 2: Features**

```
┌─────────────────────────────────┐
│                                 │
│         ┌───────────┐           │
│         │  [📝]     │           │ Icon
│         └───────────┘           │
│                                 │
│    Multiple Input Types         │ Heading
│                                 │
│  • Text input                   │
│  • PDF & DOCX upload            │ Feature list
│  • Camera OCR scan              │
│  • 5 formats supported          │
│                                 │
│                                 │
│    ○  ●  ○                      │ Page 2 active
│                                 │
│  [ Previous ]  [ Next ]         │ Navigation
│                                 │
│         Skip →                  │
│                                 │
└─────────────────────────────────┘
```

**Slide 3: Permissions**

```
┌─────────────────────────────────┐
│                                 │
│         ┌───────────┐           │
│         │  [🔐]     │           │ Security icon
│         └───────────┘           │
│                                 │
│    Setup Required               │ Heading
│                                 │
│  We need a few permissions      │
│  to work properly:              │ Explanation
│                                 │
│  📷 Camera (for OCR)            │
│  📁 Storage (for PDFs)          │ Permission list
│  🔔 Notifications (optional)   │
│                                 │
│  🔑 API Key Required            │ Key requirement
│    Add your Gemini API key      │
│    in Settings to start         │
│                                 │
│    ○  ○  ●                      │ Page 3
│                                 │
│  [ Previous ]  [ Finish ]       │ Complete
│                                 │
└─────────────────────────────────┘
```

**Annotations - Onboarding:**

**Navigation Pattern:**
- Swipe left/right để chuyển slides
- Dots indicator show current position
- "Skip" button ở mọi slide → Navigate to Main
- "Finish" ở slide cuối → Navigate to Settings (API key setup)

**Design Principles:**
- Keep it brief: Chỉ 3 slides, mỗi slide 1 concept
- Visual-heavy: Large illustrations/icons
- Clear value props: Features người dùng quan tâm
- Action-oriented: CTA buttons rõ ràng

**Content Strategy:**
1. Slide 1: Welcome + Value proposition
2. Slide 2: Key features highlight
3. Slide 3: Required permissions + API key nudge

**Show Once:**
- Chỉ hiển thị lần đầu tiên mở app
- Lưu flag "onboarding_completed" vào SharedPreferences
- Có option "Show onboarding again" trong Settings

---

### 2.3.9. Wireframes cho Dialogs và Modals

#### **Hình 2.15: Draft Recovery Dialog**

```
┌─────────────────────────────────┐
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │   📝 Draft Found          │ │ Title
│  │                           │ │
│  │   You have an unsaved     │ │
│  │   draft from 2 hours ago: │ │ Message
│  │                           │ │
│  │   ┌───────────────────┐   │ │
│  │   │ The meeting notes │   │ │
│  │   │ from yesterday... │   │ │ Preview
│  │   │ (124 characters)  │   │ │
│  │   └───────────────────┘   │ │
│  │                           │ │
│  │   Created: 10:30 AM       │ │ Metadata
│  │   Age: 2 hours ago        │ │
│  │                           │ │
│  │   [ Discard ]  [ Recover ]│ │ Actions
│  │                           │ │
│  └───────────────────────────┘ │
└─────────────────────────────────┘
```

**Annotations:**
- Auto-show khi launch app và có draft <24h
- "Recover" là Primary action (filled button)
- "Discard" là Secondary (text button)
- Preview text truncated sau 50 chars
- Dismiss on tap outside (Android standard)

---

#### **Hình 2.16: API Key Add/Edit Dialog**

```
┌─────────────────────────────────┐
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │   🔑 Add API Key          │ │ Title
│  │                           │ │
│  │   Enter your Gemini API   │ │
│  │   key to enable           │ │ Description
│  │   summarization           │ │
│  │                           │ │
│  │   ┌───────────────────┐   │ │
│  │   │ AIzaSy...     [👁]│   │ │ Password field
│  │   └───────────────────┘   │ │ (obscured)
│  │                           │ │
│  │   ✅ Valid format         │ │ Validation
│  │                           │ │
│  │   [ Get API Key ]         │ │ Helper link
│  │                           │ │
│  │   ⚠️ Note: Your key will  │ │
│  │   be stored securely with │ │ Security note
│  │   AES-256 encryption      │ │
│  │                           │ │
│  │   [ Cancel ]    [ Save ]  │ │ Actions
│  │                           │ │
│  └───────────────────────────┘ │
└─────────────────────────────────┘
```

**Annotations:**
- Password field với eye icon để toggle visibility
- Real-time validation (format check)
- "Get API Key" link → Open browser to Google AI Studio
- "Save" button disabled nếu invalid format
- On save → Test API call → Show loading → Success/Error feedback

**Validation States:**
- ❌ "Invalid format (must start with AIza...)"
- ⚠️ "Testing API key..."
- ✅ "API key verified"
- ❌ "API key invalid or quota exceeded"

---

#### **Hình 2.17: Large PDF Warning Dialog**

```
┌─────────────────────────────────┐
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │   ⚠️ Large Document       │ │ Title
│  │                           │ │
│  │   research_paper.pdf      │ │ File name
│  │   12.5 MB • 78 pages      │ │ File info
│  │                           │ │
│  │   This is a large file    │ │
│  │   that may take 45-60     │ │ Warning
│  │   seconds to process.     │ │
│  │                           │ │
│  │   How would you like to   │ │
│  │   proceed?                │ │ Question
│  │                           │ │
│  │   ○ Process All Pages     │ │
│  │     (slower, complete)    │ │ Option 1
│  │                           │ │
│  │   ○ Select Pages          │ │
│  │     (faster, custom)      │ │ Option 2
│  │                           │ │
│  │   [ Cancel ]              │ │
│  │   [ Continue ]            │ │ Actions
│  │                           │ │
│  └───────────────────────────┘ │
└─────────────────────────────────┘
```

**Annotations:**
- Trigger: File >50 pages hoặc >10MB
- Radio buttons cho 2 options
- "Process All Pages" selected by default
- "Select Pages" → Navigate to page selector screen
- Show estimated time based on page count
- "Cancel" → Return to Document tab

---

#### **Hình 2.18: Export Options Dialog**

```
┌─────────────────────────────────┐
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │   📤 Export Summary       │ │ Title
│  │                           │ │
│  │   Choose format:          │ │ Label
│  │                           │ │
│  │   ┌───────────────────┐   │ │
│  │   │ ● Plain Text      │   │ │ Option 1
│  │   │   (.txt)          │   │ │
│  │   └───────────────────┘   │ │
│  │                           │ │
│  │   ┌───────────────────┐   │ │
│  │   │ ○ Markdown        │   │ │ Option 2
│  │   │   (.md)           │   │ │
│  │   └───────────────────┘   │ │
│  │                           │ │
│  │   ┌───────────────────┐   │ │
│  │   │ ○ PDF Document    │   │ │ Option 3
│  │   │   (.pdf)          │   │ │
│  │   └───────────────────┘   │ │
│  │                           │ │
│  │   ✅ Include metrics      │ │ Checkbox
│  │   ✅ Include timestamp    │ │ options
│  │                           │ │
│  │   [ Cancel ]   [ Export ] │ │ Actions
│  │                           │ │
│  └───────────────────────────┘ │
└─────────────────────────────────┘
```

**Annotations:**
- Radio group cho format selection
- Checkboxes cho optional metadata
- "Export" → Open Android share sheet with generated file
- Default: Plain Text, include both metadata
- Preview button (future): Show export preview before saving

---

### 2.3.10. Error State Variations

#### **Hình 2.19: Main Screen - Input Validation Error**

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
│  │ This is a short text      │ │ Text input
│  │                           │ │ (ERROR STATE)
│  └───────────────────────────┘ │
│  ⚠️ Text too short (min 50    │ │ Error message
│     characters)                │ │ (Red color)
│  📊 35/5,000 characters        │ │ Count (orange)
│                                 │
│  ┌───────────────────────────┐ │
│  │ Persona: [General ▾]      │ │
│  └───────────────────────────┘ │
│                                 │
│      [ Summarize ]              │ DISABLED
│      (grayed out)               │ state
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**Error States for Text Input:**
1. **Too Short (<50 chars):**
   - Message: "Text too short (min 50 characters)"
   - Count color: Orange
   - Button: Disabled

2. **Too Long (>5,000 chars):**
   - Message: "Text too long. Please reduce to 5,000 characters"
   - Count color: Red
   - Button: Disabled

3. **No API Key:**
   - Message: "API key required. Add one in Settings"
   - Action link: "Go to Settings →"
   - Button: Disabled

---

#### **Hình 2.20: Processing Screen - Network Error**

```
┌─────────────────────────────────┐
│  Processing                [×]  │
├─────────────────────────────────┤
│                                 │
│         ┌──────────┐            │
│         │    ❌    │            │ Error icon
│         └──────────┘            │
│                                 │
│      Connection Failed          │ Error title
│                                 │
│  ┌───────────────────────────┐ │
│  │ Unable to reach the AI    │ │
│  │ service. Please check     │ │ Error message
│  │ your internet connection. │ │
│  └───────────────────────────┘ │
│                                 │
│         [ Try Again ]           │ Primary action
│                                 │
│         [ Cancel ]              │ Secondary
│                                 │
└─────────────────────────────────┘
```

**Error Scenarios:**
1. **Network Error:**
   - Icon: ❌ or 📡
   - Message: "Unable to reach the AI service..."
   - Action: "Try Again" (retry same request)

2. **API Rate Limit:**
   - Icon: ⏱️
   - Message: "Daily limit reached. Quota resets at 12:00 AM"
   - Action: "Add Another Key" or "Cancel"

3. **Invalid API Key:**
   - Icon: 🔑
   - Message: "API key is invalid or expired"
   - Action: "Update Key" → Navigate to Settings

---

#### **Hình 2.21: OCR Screen - No Text Detected Error**

```
┌─────────────────────────────────┐
│ ☰  Scan Text            [⚙]    │
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │      ┌─────────┐          │ │
│  │      │   📷    │          │ │
│  │      │   ❌    │          │ │ Error overlay
│  │      └─────────┘          │ │
│  │                           │ │
│  │   No Text Detected        │ │ Error title
│  │                           │ │
│  │   • Ensure good lighting  │ │
│  │   • Hold camera steady    │ │ Suggestions
│  │   • Avoid shadows/glare   │ │
│  │                           │ │
│  │   [ Try Again ]           │ │ Retry button
│  │                           │ │
│  └───────────────────────────┘ │
│                                 │
├─────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]        │
└─────────────────────────────────┘
```

**OCR Error States:**
1. **No Text Found:**
   - Overlay trên camera preview
   - Actionable tips
   - "Try Again" → Back to camera

2. **Low Confidence:**
   - Show warning: "Text quality may be poor"
   - Allow edit before proceed
   - Highlight low-confidence words

3. **Permission Denied:**
   - Icon: 🚫
   - Message: "Camera permission required for OCR"
   - Action: "Open Settings" → App settings

---

### 2.3.11. Component Specifications

**Bảng 2.7: Component Sizing & Spacing Specifications**

#### **Main Screen Components:**

| Component | Type | Size/Dimensions | Spacing | States |
|-----------|------|-----------------|---------|--------|
| **TopAppBar** | Material 3 | Height: 64dp | Padding: 16dp H | Default, Scrolled |
| **Tab Row** | TabRow | Height: 48dp | Item spacing: 24dp | Inactive, Active, Pressed |
| **Text Input** | OutlinedTextField | Min: 120dp<br>Max: 60% screen | Padding: 16dp all | Normal, Focus, Error, Disabled |
| **Character Count** | BodySmall Text | Font: 12sp | Top margin: 8dp | Default, Warning (orange), Error (red) |
| **Persona Selector** | DropdownMenu | Height: 56dp | Margin: 16dp V | Collapsed, Expanded |
| **Summarize Button** | FilledButton | Height: 48dp<br>Width: Match parent | Margin: 16dp all | Enabled, Disabled, Pressed, Loading |
| **Bottom Navigation** | NavigationBar | Height: 80dp | Item min width: 80dp | Selected, Unselected, Pressed |

#### **Dialog Components:**

| Component | Type | Size/Dimensions | Spacing | Notes |
|-----------|------|-----------------|---------|-------|
| **Dialog Container** | Surface | Max width: 320dp<br>Min height: 180dp | Padding: 24dp | Corner radius: 28dp (M3) |
| **Dialog Title** | HeadlineSmall | Font: 24sp | Bottom: 16dp | Bold weight |
| **Dialog Body** | BodyMedium | Font: 14sp | Vertical: 16dp | Line height: 20sp |
| **Dialog Actions** | Row | Height: 48dp | Top: 24dp<br>Between: 8dp | Right-aligned |
| **Text Button** | TextButton | Min height: 40dp | Horizontal: 12dp | Ripple effect |
| **Filled Button** | Button | Min height: 40dp | Horizontal: 24dp | Elevation: 1dp |

#### **Processing Screen Components:**

| Component | Type | Size/Dimensions | Spacing | Animation |
|-----------|------|-----------------|---------|-----------|
| **Loading Icon** | CircularProgress | Diameter: 64dp | Center-aligned | 360° rotation, 1.5s duration |
| **Status Text** | TitleMedium | Font: 16sp | Top: 24dp | Fade transition 300ms |
| **Progress Bar** | LinearProgress | Height: 4dp<br>Width: 80% screen | Vertical: 16dp | Smooth fill animation |
| **Percentage Text** | BodyLarge | Font: 16sp | Top: 8dp | Update every 100ms |
| **Time Estimate** | BodySmall | Font: 12sp | Top: 4dp | Countdown effect |

#### **Result Screen Components:**

| Component | Type | Size/Dimensions | Spacing | Notes |
|-----------|------|-----------------|---------|-------|
| **KPI Card** | ElevatedCard | Height: 80dp<br>Width: 45% parent | Gap: 12dp | 2x2 grid |
| **Icon (in card)** | Icon | Size: 24dp | Start: 16dp | Material Icons Extended |
| **Metric Value** | TitleLarge | Font: 22sp | Top: 8dp | Bold, Primary color |
| **Metric Label** | BodySmall | Font: 12sp | Top: 4dp | Medium opacity |
| **Summary Content** | OutlinedCard | Min height: 200dp | Padding: 16dp | Scrollable vertical |
| **FAB** | ExtendedFAB | Size: 56dp (collapsed)<br>Width: auto (expanded) | Bottom: 16dp<br>End: 16dp | Speed dial menu |

---

### 2.3.12. Adaptive Layout Wireframes

#### **Hình 2.22: Main Screen - Tablet Layout (Medium, 600-839dp)**

```
┌───────────────────────────────────────────────────────┐
│ ☰  SumUp                            [🔍] [⚙] [👤]     │ TopAppBar (wider)
├───────────────────────────────────────────────────────┤
│                                                       │
│  ┌─────────────────────────────────────────────────┐ │
│  │    [Text]       [Document]       [OCR]          │ │ Tabs (wider spacing)
│  └─────────────────────────────────────────────────┘ │
│                                                       │
│  ┌───────────────────────────┬─────────────────────┐ │
│  │                           │                     │ │
│  │ Type or paste text here.. │  📊 0/5,000         │ │ 2-column layout
│  │                           │                     │ │ Input | Metadata
│  │                           │  ┌───────────────┐  │ │
│  │                           │  │ Persona:      │  │ │
│  │                           │  │ [General ▾]   │  │ │
│  │                           │  └───────────────┘  │ │
│  │                           │                     │ │
│  │                           │  [ Summarize ]      │ │
│  │                           │                     │ │
│  └───────────────────────────┴─────────────────────┘ │
│                                                       │
├───────────────────────────────────────────────────────┤
│        [🏠]           [📝]           [⚙️]             │ Bottom Nav (wider)
└───────────────────────────────────────────────────────┘
```

**Tablet Adaptations (600-839dp):**
- **TopAppBar:** Wider, more spacing between icons
- **Content:** Max width 840dp, centered if screen wider
- **Tabs:** Equal width distribution, more padding
- **Input Area:** 2-column layout:
  - Left: Text input (65% width)
  - Right: Metadata panel (35% width) - Character count, Persona, Button
- **Bottom Nav:** Items evenly distributed, labels always visible
- **Padding:** Increased from 16dp → 24dp horizontal

---

#### **Hình 2.23: Result Screen - Desktop Layout (Expanded, 840dp+)**

```
┌─────┬─────────────────────────────────────────────────────┐
│     │ ← Summary                        [⭐] [⋮]           │
│ [⚙]├─────────────────────────────────────────────────────┤
│     │                                                     │
│[📝] │  ┌───────────────────┬───────────────────────────┐ │
│     │  │ 📊 Original: 1,245│ 📝 Summary: 248 words    │ │
│[🏠] │  │    words          │                          │ │
│     │  └───────────────────┴───────────────────────────┘ │
│  N  │  ┌───────────────────┬───────────────────────────┐ │
│  a  │  │ ⏱️ Saved: 4 min   │ 📉 Reduction: 80%        │ │
│  v  │  │    read           │                          │ │
│     │  └───────────────────┴───────────────────────────┘ │
│  R  │                                                     │
│  a  │  Persona: [Student ▾]          [ Regenerate ]     │
│  i  │                                                     │
│  l  │  ┌─────────────────────────────────────────────┐  │
│     │  │                                             │  │
│  80 │  │  • Key point 1                              │  │
│  dp │  │                                             │  │
│     │  │  • Key point 2                              │  │
│     │  │                                             │  │
│     │  │  • Key point 3                              │  │
│     │  │                                             │  │
│     │  │  Lorem ipsum dolor sit amet...              │  │
│     │  │                                             │  │
│     │  └─────────────────────────────────────────────┘  │
│     │                                                     │
│     │           ┌───┐                                    │
│     │           │ + │  FAB Menu                          │
│     │           └───┘                                    │
└─────┴─────────────────────────────────────────────────────┘
```

**Desktop Adaptations (840dp+):**
- **Navigation:** Bottom Nav → NavigationRail (80dp width, left side)
  - Icons only (no labels) - Vertical stacking
  - Active indicator: Filled container
  - Tooltips on hover
- **Content:** Max width 1200dp, margins auto
- **KPI Cards:** 4 cards in 2x2 grid → 4 cards in 1 row (horizontal)
- **Actions:** More prominent
  - "Regenerate" button visible (not in menu)
  - FAB stays but with more options
- **Typography:** Slightly larger for readability at distance
- **Mouse Interactions:**
  - Hover states for all interactive elements
  - Context menus on right-click
  - Keyboard shortcuts displayed

---

**Bảng 2.8: Adaptive Layout Breakpoints Summary**

| Breakpoint | Width | Navigation | Content Layout | Typography | Padding |
|------------|-------|------------|----------------|------------|---------|
| **Compact** (Mobile) | 360-599dp | Bottom Navigation Bar | Single column | Default (14-16sp) | 16dp H |
| **Medium** (Tablet) | 600-839dp | Bottom Navigation Bar (wider) | 2-column (65/35) | Default (14-16sp) | 24dp H |
| **Expanded** (Desktop) | 840dp+ | NavigationRail (80dp) | Multi-column + Sidebars | Larger (+2sp) | 32dp H |

---

**Tổng kết phần 2.3 - Wireframing (Updated):**

✅ Đã tạo wireframes cho **7 màn hình chính** với annotations đầy đủ

✅ **NEW:** Wireframe cho Onboarding (3 slides)

✅ **NEW:** 4 Dialog wireframes (Draft Recovery, API Key, Large PDF, Export)

✅ **NEW:** 3 Error state variations (Input, Processing, OCR)

✅ **NEW:** Component specifications table với sizing & spacing chi tiết

✅ **NEW:** Adaptive layout wireframes cho Tablet và Desktop

✅ Cover tất cả states: Normal, loading, error, empty, disabled

✅ Responsive designs cho 3 breakpoints (Compact, Medium, Expanded)

✅ Interactive elements được document rõ ràng với specifications

**Tổng cộng: 20+ wireframes** bao gồm:
- 7 main screens
- 1 onboarding (3 slides)
- 4 dialogs/modals
- 3 error states
- 2 adaptive layouts
- 1 component spec table
- 1 breakpoint summary table

---

<div style="page-break-after: always;"></div>

**Kết luận Chương 2:**

Chương 2 đã hoàn thành việc thiết kế **cấu trúc và luồng tương tác** cho ứng dụng SumUp với độ chi tiết cao:

✅ **Information Architecture (2.1):**
- 6 nguyên tắc IA (Clarity, Simplicity, Consistency, Findability, Scalability, Hierarchy)
- Sơ đồ phân cấp 7 màn hình chính
- Hệ thống điều hướng adaptive (Bottom Nav → NavigationRail)
- Ma trận điều hướng hoàn chỉnh giữa các màn hình

✅ **User Flow Diagrams (2.2):**
- 4 luồng chính với 40+ bước chi tiết
- Decision points và system actions rõ ràng
- Error handling và recovery flows đầy đủ
- Bảng flow steps với actor, action, system response

✅ **Wireframes (2.3):**
- **20+ wireframes** covering all screens, dialogs, and states:
  - 7 màn hình chính (Main, OCR, Processing, Result, History, Settings, Onboarding)
  - 4 dialogs/modals quan trọng (Draft Recovery, API Key, Large PDF Warning, Export Options)
  - 3 error state variations (Input validation, Network error, OCR error)
  - 2 adaptive layouts (Tablet, Desktop)
- **Component Specifications:** Sizing & spacing tables với measurements chi tiết
- **Annotations:** Đầy đủ cho mọi element với states, behaviors, interactions
- **Adaptive Design:** Breakpoints cho Compact (360-599dp), Medium (600-839dp), Expanded (840dp+)

**Metrics:**
- **Total wireframes:** 20+ (7 main + 1 onboarding + 4 dialogs + 3 errors + 2 adaptive + 3 spec tables)
- **Total annotations:** 100+ component descriptions
- **States documented:** Normal, Focus, Disabled, Error, Loading, Hover, Pressed
- **Responsive breakpoints:** 3 (Mobile, Tablet, Desktop)

Với foundation chi tiết này, **Chương 3** sẽ biến wireframes thành **high-fidelity mockups** với Design System hoàn chỉnh, colors, typography, và interactive prototypes.

---

<div style="page-break-after: always;"></div>

**--- KẾT THÚC CHƯƠNG 2 ---**

---


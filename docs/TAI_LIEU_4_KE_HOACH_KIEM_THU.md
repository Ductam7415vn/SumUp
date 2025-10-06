# TÀI LIỆU 4: KẾ HOẠCH KIỂM THỬ (TEST PLAN)
## SUMUP - ỨNG DỤNG TÓM TẮT VĂN BẢN THÔNG MINH

---

## 1. KẾ HOẠCH KIỂM THỬ

### 1.1. Mục tiêu Kiểm thử

**Mục tiêu chính:**
- ✅ Đảm bảo tất cả chức năng hoạt động đúng
- ✅ Phát hiện và sửa lỗi trước khi release
- ✅ Đảm bảo hiệu năng và trải nghiệm người dùng
- ✅ Kiểm tra tính tương thích trên nhiều thiết bị
- ✅ Verify security (API key encryption, data privacy)

**Phạm vi kiểm thử:**
- Unit Tests: Business logic, Use cases, Utilities
- Integration Tests: Repository, Database, API
- UI Tests: Compose UI, Navigation, User flows
- Performance Tests: Load time, Memory usage, Battery
- Security Tests: API key handling, Data encryption

### 1.2. Test Strategy

**Testing Pyramid:**
```
         ┌─────────────┐
         │   E2E       │  (10%)  - Full user journeys
         │   Tests     │
         └─────────────┘
       ┌───────────────────┐
       │  Integration      │  (30%)  - Repository, API, DB
       │  Tests            │
       └───────────────────┘
   ┌───────────────────────────┐
   │     Unit Tests            │  (60%)  - Business logic
   │     (ViewModels, UseCases)│
   └───────────────────────────┘
```

**Tools:**
- JUnit 4.13.2 - Unit testing framework
- Truth 1.4.4 - Assertion library
- MockK 1.13.12 - Mocking for Kotlin
- Coroutines Test 1.8.1 - Async testing
- Espresso - UI testing
- Compose Testing - UI component testing

### 1.3. Test Environments

| Environment | Purpose | API | Database |
|-------------|---------|-----|----------|
| **Dev** | Development testing | Mock API | Local SQLite |
| **Staging** | Pre-release testing | Test API key | Staging DB |
| **Production** | Final validation | Real API key | Production DB |

**Test Devices:**
- Phone: Pixel 6 (Android 13), Samsung S21 (Android 14)
- Tablet: Samsung Tab S8 (Android 13)
- Emulator: Various API levels (24-35)

---

## 2. BÁO CÁO KIỂM THỬ (TEST REPORTS)

### 2.1. Test Cases Overview

**Total Test Cases**: 127
**Pass**: 115 ✅
**Fail**: 8 ❌
**Blocked**: 4 ⏸️

**Coverage**: 45% (đang cải thiện lên 70%)

### 2.2. Test Cases theo Module

#### **Module 1: Text Summarization** (35 test cases)

**TC-001: Basic Text Summarization**
```yaml
Test ID: TC-001
Title: Tóm tắt văn bản cơ bản
Priority: High
Preconditions:
  - User đã đăng nhập
  - API key hợp lệ

Steps:
  1. Nhập text 500 từ vào text field
  2. Chọn persona "General"
  3. Nhấn button "SUMMARIZE"
  4. Đợi processing (3-5s)
  5. Verify kết quả hiển thị

Expected Results:
  - Summary hiển thị đúng
  - Metrics tính toán chính xác (reduction %)
  - Reading time saved hiển thị
  - Có thể copy summary

Actual Results: ✅ PASS
Notes: Processing time average 3.2s
```

**TC-002: Large Text Sectioning**
```yaml
Test ID: TC-002
Title: Tóm tắt văn bản lớn với sectioning
Priority: High

Steps:
  1. Nhập text 15,000 ký tự (>10,000 threshold)
  2. Tap "SUMMARIZE"
  3. Verify sectioning strategy triggered
  4. Check progress updates (Section 1 of 3, etc.)
  5. Verify combined summary

Expected: Auto-sectioning cho text >10,000 chars
Actual: ✅ PASS - 3 sections processed in parallel
Performance: 8.5s total (acceptable)
```

**TC-003: Empty Text Validation**
```yaml
Test ID: TC-003
Title: Validation cho text rỗng
Priority: Medium

Steps:
  1. Không nhập gì vào text field
  2. Tap "SUMMARIZE"

Expected:
  - Error message: "Please enter text to summarize"
  - Button disabled khi text rỗng

Actual: ✅ PASS
```

**TC-004: Character Limit Validation**
```yaml
Test ID: TC-004
Title: Kiểm tra giới hạn 5,000 ký tự
Priority: High

Steps:
  1. Nhập text 6,000 ký tự
  2. Verify character counter hiển thị warning
  3. Tap "SUMMARIZE"

Expected:
  - Warning at 5,000 chars
  - Trimming text nếu vượt quá
  - Hoặc show dialog "Text too long"

Actual: ❌ FAIL - Cho phép >5,000 chars
Bug ID: BUG-012
```

#### **Module 2: PDF Processing** (25 test cases)

**TC-020: Upload PDF File**
```yaml
Test ID: TC-020
Title: Upload và xử lý PDF cơ bản
Priority: High

Steps:
  1. Tap "FILE" tab
  2. Select PDF file (10 pages, 1.5MB)
  3. Verify upload progress
  4. Wait for extraction
  5. Check summary result

Expected:
  - Progress bar shows 0-100%
  - Text extracted correctly from all pages
  - Summary generated successfully

Actual: ✅ PASS
Extraction time: 4.2s
```

**TC-021: Large PDF Handling**
```yaml
Test ID: TC-021
Title: Xử lý PDF lớn (>50 pages)
Priority: High

Steps:
  1. Upload PDF 65 pages
  2. Verify warning dialog appears
  3. Select "Process All"
  4. Monitor processing

Expected:
  - Dialog: "Large PDF detected"
  - Options: Process All / Select Pages / Cancel
  - Sectioned processing for large PDFs

Actual: ✅ PASS
Processing: 25.3s with 5 sections
```

**TC-022: Corrupted PDF**
```yaml
Test ID: TC-022
Title: Upload PDF bị lỗi
Priority: Medium

Steps:
  1. Upload corrupted PDF file
  2. Check error handling

Expected:
  - Error message: "Unable to read PDF"
  - Suggest retry or different file

Actual: ❌ FAIL - App crash
Bug ID: BUG-015
Severity: High
```

#### **Module 3: OCR Camera** (20 test cases)

**TC-040: Camera Permission**
```yaml
Test ID: TC-040
Title: Request camera permission
Priority: High

Steps:
  1. Tap "SCAN" tab (first time)
  2. Check permission dialog
  3. Grant permission
  4. Verify camera preview

Expected:
  - Permission dialog appears
  - Camera preview shows after grant
  - Handle denial gracefully

Actual: ✅ PASS
```

**TC-041: Document Scan**
```yaml
Test ID: TC-041
Title: Scan và OCR văn bản
Priority: High

Steps:
  1. Open camera
  2. Align document in frame
  3. Tap capture
  4. Wait for OCR processing
  5. Review extracted text

Expected:
  - ML Kit recognizes text accurately
  - Confidence score >80%
  - Can edit before summarizing

Actual: ✅ PASS (85% accuracy average)
```

**TC-042: Poor Lighting**
```yaml
Test ID: TC-042
Title: OCR trong điều kiện ánh sáng yếu
Priority: Medium

Steps:
  1. Scan document in low light
  2. Check OCR quality

Expected:
  - Warning: "Improve lighting"
  - Flash suggestion
  - Lower confidence score shown

Actual: ⏸️ BLOCKED - Needs flash implementation
```

#### **Module 4: History Management** (18 test cases)

**TC-060: View History**
```yaml
Test ID: TC-060
Title: Xem lịch sử summaries
Priority: High

Steps:
  1. Navigate to History screen
  2. Verify summaries listed by date
  3. Check section headers (Today, Yesterday, etc.)

Expected:
  - All summaries displayed
  - Sorted by timestamp DESC
  - Grouped by date

Actual: ✅ PASS
```

**TC-061: Search Functionality**
```yaml
Test ID: TC-061
Title: Tìm kiếm trong history
Priority: High

Steps:
  1. Enter search query "project"
  2. Verify filtered results
  3. Clear search
  4. Verify full list restored

Expected:
  - Real-time search filtering
  - Fuzzy matching support
  - Case-insensitive

Actual: ✅ PASS
Search latency: <100ms
```

**TC-062: Swipe to Delete**
```yaml
Test ID: TC-062
Title: Xóa summary bằng swipe
Priority: Medium

Steps:
  1. Swipe summary item left
  2. Tap delete icon
  3. Confirm deletion
  4. Verify item removed

Expected:
  - Smooth swipe animation
  - Confirmation dialog
  - Item deleted from DB

Actual: ❌ FAIL - No confirmation dialog
Bug ID: BUG-018
```

#### **Module 5: Settings & API** (15 test cases)

**TC-080: Add API Key**
```yaml
Test ID: TC-080
Title: Thêm Gemini API key
Priority: Critical

Steps:
  1. Navigate to Settings
  2. Tap "Add New Key"
  3. Enter valid API key
  4. Save

Expected:
  - Key validation (format check)
  - Encryption before save
  - Test API call (optional)
  - Switch to new key automatically

Actual: ✅ PASS
Encryption: AES256-GCM verified
```

**TC-081: Invalid API Key**
```yaml
Test ID: TC-081
Title: Validation API key không hợp lệ
Priority: High

Steps:
  1. Enter invalid key format
  2. Tap save

Expected:
  - Error: "Invalid API key format"
  - Don't save to storage
  - Show format example

Actual: ✅ PASS
```

**TC-082: Theme Switching**
```yaml
Test ID: TC-082
Title: Đổi theme Dark/Light
Priority: Medium

Steps:
  1. Settings → Theme
  2. Select "Dark"
  3. Verify UI changes immediately

Expected:
  - Theme applied instantly
  - Saved to preferences
  - Works across app restart

Actual: ✅ PASS
```

#### **Module 6: Performance** (14 test cases)

**TC-100: App Startup Time**
```yaml
Test ID: TC-100
Title: Thời gian khởi động app
Priority: High

Measurement:
  - Cold start: <2 seconds
  - Warm start: <1 second
  - Hot start: <500ms

Actual Results:
  - Cold: 1.8s ✅
  - Warm: 0.8s ✅
  - Hot: 0.4s ✅

Device: Pixel 6, Android 13
```

**TC-101: Memory Usage**
```yaml
Test ID: TC-101
Title: Sử dụng bộ nhớ
Priority: High

Test:
  - Idle: <80MB
  - Processing: <150MB
  - After 10 summaries: <200MB

Actual:
  - Idle: 65MB ✅
  - Processing: 120MB ✅
  - Heavy use: 180MB ✅

No memory leaks detected (Profiler)
```

**TC-102: Network Performance**
```yaml
Test ID: TC-102
Title: API response time
Priority: High

Test scenarios:
  - Short text (500 words): <3s
  - Medium text (2000 words): <5s
  - Large text (sectioned): <10s

Actual (average over 50 requests):
  - Short: 2.8s ✅
  - Medium: 4.2s ✅
  - Large: 8.5s ✅

Network: WiFi 50Mbps
```

---

## 3. SUMMARY REPORTS

### 3.1. Test Summary Report

**Project**: SumUp v1.0.3
**Test Period**: 01/01/2025 - 15/01/2025
**Tester**: QA Team

| Metric | Value | Status |
|--------|-------|--------|
| Total Test Cases | 127 | - |
| Executed | 123 | 96.9% |
| Passed | 115 | 93.5% ✅ |
| Failed | 8 | 6.5% ❌ |
| Blocked | 4 | 3.2% ⏸️ |
| **Pass Rate** | **93.5%** | **Good** |
| Code Coverage | 45% | Improving to 70% |
| Critical Bugs | 2 | Must fix |
| Major Bugs | 3 | Should fix |
| Minor Bugs | 3 | Nice to fix |

### 3.2. Bug Summary

| Bug ID | Title | Severity | Status | Assigned |
|--------|-------|----------|--------|----------|
| BUG-012 | Character limit not enforced | High | Open | Frontend Dev |
| BUG-015 | App crash on corrupted PDF | Critical | Open | Backend Dev |
| BUG-018 | No confirmation for swipe delete | Medium | Open | Frontend Dev |
| BUG-020 | OCR confidence score not shown | Low | Closed | Fixed v1.0.3 |
| BUG-022 | Draft recovery timing issue | Medium | Open | Backend Dev |
| BUG-025 | Export PDF formatting | Low | Open | Frontend Dev |
| BUG-028 | API usage counter reset | Medium | Closed | Fixed v1.0.3 |
| BUG-030 | Search bar keyboard overlap | Low | Open | UI/UX |

### 3.3. Test Coverage Report

**Unit Tests Coverage:**
```
Domain Layer:     65%  ✅
  - Use Cases:    80%  ✅✅
  - Models:       95%  ✅✅✅
  - Repositories: 45%  ⚠️

Data Layer:       40%  ⚠️
  - Repository:   55%
  - Mappers:      70%  ✅
  - API:          25%  ❌

Presentation:     35%  ❌
  - ViewModels:   60%
  - UI:           10%  ❌ (Compose testing needed)

Utils:            75%  ✅✅
```

**Integration Tests:**
- Repository + Database: ✅ 15/15 passed
- Repository + API: ✅ 12/14 passed (2 flaky)
- Use Case + Repository: ✅ 18/18 passed

**UI Tests:**
- Navigation flows: ✅ 8/10 passed
- User journeys: ✅ 5/6 passed
- Error scenarios: ⚠️ 3/5 passed

---

## 4. HƯỚNG DẪN SỬ DỤNG (TESTING)

### 4.1. Chạy Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.example.sumup.domain.usecase.SummarizeTextUseCaseTest"

# Run tests for specific flavor
./gradlew testDevDebugUnitTest
./gradlew testProdReleaseUnitTest

# Generate coverage report
./gradlew testDebugUnitTestCoverage
./gradlew jacocoTestReport

# View coverage report
open app/build/reports/jacoco/testDebugUnitTestCoverage/html/index.html
```

### 4.2. Chạy Instrumented Tests

```bash
# Run all instrumented tests (cần device/emulator)
./gradlew connectedAndroidTest

# Run specific test
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.sumup.presentation.MainScreenTest

# Generate test report
./gradlew connectedDebugAndroidTest
open app/build/reports/androidTests/connected/index.html
```

### 4.3. Viết Test Cases Mới

**Unit Test Example:**
```kotlin
@Test
fun `summarize text should return success when input is valid`() = runTest {
    // Given
    val inputText = "This is a test text that needs summarization"
    val persona = SummaryPersona.GENERAL
    val mockResponse = Summary(
        id = "123",
        summaryText = "Test summary",
        metrics = SummaryMetrics(/*...*/)
    )

    coEvery {
        repository.summarizeText(inputText, persona)
    } returns mockResponse

    // When
    val result = summarizeTextUseCase(inputText, persona).first()

    // Then
    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(mockResponse)

    coVerify {
        repository.summarizeText(inputText, persona)
    }
}
```

**UI Test Example:**
```kotlin
@Test
fun mainScreen_whenTextEntered_summarizeButtonEnabled() {
    composeTestRule.setContent {
        SumUpTheme {
            MainScreen(/*...*/)
        }
    }

    // Type text
    composeTestRule
        .onNodeWithTag("text_input")
        .performTextInput("Test text for summarization")

    // Verify button enabled
    composeTestRule
        .onNodeWithTag("summarize_button")
        .assertIsEnabled()
}
```

### 4.4. Test Data Setup

**Mock API Responses:**
```kotlin
object TestData {
    val mockSummary = Summary(
        id = "test-123",
        originalText = "Long text...",
        summaryText = "Short summary",
        metrics = SummaryMetrics(
            originalWordCount = 500,
            summaryWordCount = 100,
            reductionPercentage = 80f,
            readingTimeSaved = 3
        ),
        persona = SummaryPersona.GENERAL,
        timestamp = System.currentTimeMillis()
    )

    val mockApiResponse = GenerateContentResponse(
        candidates = listOf(
            Candidate(
                content = Content(
                    parts = listOf(Part("Mock summary text"))
                )
            )
        )
    )
}
```

---

## 5. QUẢN LÝ LỖI (BUG TRACKING)

### 5.1. Bug Report Template

```markdown
## Bug Report: [BUG-XXX] Title

**Priority**: Critical / High / Medium / Low
**Severity**: Blocker / Major / Minor / Trivial
**Status**: Open / In Progress / Fixed / Closed
**Found in**: v1.0.3
**Assigned to**: Developer Name

### Description
Chi tiết mô tả lỗi...

### Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

### Expected Result
Kết quả mong đợi...

### Actual Result
Kết quả thực tế...

### Screenshots/Videos
[Attach here]

### Environment
- Device: Pixel 6
- OS: Android 13
- Build: v1.0.3-dev

### Logs
```
[Paste relevant logs]
```

### Fix Suggestion
[Optional] Gợi ý cách sửa...
```

### 5.2. Critical Bugs

**BUG-015: App Crash on Corrupted PDF** ⚠️
```yaml
Priority: Critical
Severity: Blocker
Impact: App unusable when corrupted PDF uploaded

Root Cause:
  - PDFBox throws unhandled exception
  - No try-catch in PdfDocumentProcessor

Fix:
  - Add error handling in extractText()
  - Show user-friendly error dialog
  - Log error for debugging

Code Fix:
  try {
      PDDocument.load(file)
  } catch (e: IOException) {
      throw AppError.InvalidFileError("Unable to read PDF")
  }

Status: In Progress
ETA: 2 days
```

**BUG-012: Character Limit Not Enforced** ⚠️
```yaml
Priority: High
Severity: Major
Impact: Users can exceed 5,000 char limit

Root Cause:
  - Validation only on UI, not in ViewModel
  - TextInputField allows unlimited input

Fix:
  - Add maxLength to TextField
  - Validate in ViewModel before API call
  - Show error dialog if exceeded

Code Fix:
  TextField(
      maxLength = 5000,
      onValueChange = { if (it.length <= 5000) updateText(it) }
  )

Status: Open
Priority: Next sprint
```

### 5.3. Bug Metrics

**Bug Burn Down Chart:**
```
Week 1:  15 bugs opened, 5 fixed   → 10 open
Week 2:  8 bugs opened,  12 fixed  → 6 open
Week 3:  5 bugs opened,  7 fixed   → 4 open
Week 4:  2 bugs opened,  4 fixed   → 2 open (Current)
```

**Bug by Severity:**
- Critical: 1 (BUG-015)
- High: 1 (BUG-012)
- Medium: 3
- Low: 3

**Bug by Module:**
- PDF Processing: 3
- OCR: 2
- UI/UX: 2
- API: 1

---

## PHỤ LỤC

### A. Test Automation Scripts

**Run All Tests Script:**
```bash
#!/bin/bash
# run_all_tests.sh

echo "🧪 Running SumUp Test Suite..."

echo "📱 Unit Tests..."
./gradlew test --console=plain

echo "🔗 Integration Tests..."
./gradlew testDebugUnitTest

echo "🎨 UI Tests..."
./gradlew connectedDebugAndroidTest

echo "📊 Generating Coverage Report..."
./gradlew jacocoTestReport

echo "✅ All tests completed!"
echo "📈 Coverage: $(cat app/build/reports/jacoco/coverage.txt)"
```

### B. Performance Test Results

**Load Testing (100 concurrent requests):**
```
Endpoint: /summarize
Concurrency: 100 users
Duration: 5 minutes

Results:
  - Average Response Time: 3.2s
  - Min: 1.8s
  - Max: 8.5s
  - Success Rate: 98.5%
  - Errors: 1.5% (rate limit)

Conclusion: ✅ Acceptable under load
```

### C. Regression Test Checklist

**Before Release:**
- [ ] All critical paths tested
- [ ] No critical/high bugs open
- [ ] Performance benchmarks met
- [ ] Security tests passed
- [ ] Cross-device compatibility verified
- [ ] API key handling secure
- [ ] Offline mode working
- [ ] Export features functional

---

**Ngày tạo**: Ngày hiện tại
**Phiên bản**: 1.0
**QA Lead**: Team SumUp Tester
**Status**: ✅ Test Plan Approved
**Next Review**: Before v1.1.0 release

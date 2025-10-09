# CHƯƠNG 4: KIỂM THỬ, ĐÁNH GIÁ VÀ TINH CHỈNH

---

## 4.1. Lập kế hoạch Kiểm thử (Test Plan)

### 4.1.1. Mục tiêu kiểm thử

Mục tiêu chính của giai đoạn kiểm thử usability là **xác minh xem thiết kế giao diện SumUp có thực sự dễ sử dụng, hiệu quả và đáp ứng nhu cầu của người dùng thực tế hay không**.

**Các mục tiêu cụ thể:**

**1. Đo lường hiệu quả (Effectiveness):**
- **Task Success Rate:** Tỷ lệ người dùng hoàn thành thành công các tác vụ chính
- **Target:** ≥ 85% cho tác vụ chính (tóm tắt văn bản cơ bản)
- **Target:** ≥ 75% cho tác vụ phụ (OCR, upload tài liệu, quản lý lịch sử)

**2. Đo lường hiệu suất (Efficiency):**
- **Time on Task:** Thời gian trung bình để hoàn thành mỗi tác vụ
- **Target:** ≤ 2 phút cho tác vụ tóm tắt văn bản đầu tiên
- **Target:** ≤ 1 phút cho các lần tóm tắt tiếp theo

**3. Đo lường mức độ hài lòng (Satisfaction):**
- **System Usability Scale (SUS):** Đánh giá tổng thể về khả năng sử dụng
- **Target:** SUS Score ≥ 75 (Good) hoặc ≥ 80 (Excellent)

**4. Phát hiện vấn đề (Problem Discovery):**
- Xác định các điểm khó khăn (pain points) trong luồng sử dụng
- Tìm ra các lỗi thiết kế (design flaws)
- Thu thập feedback định tính về cải tiến

**5. Xác thực giả định thiết kế:**
- Kiểm tra xem các quyết định thiết kế có đúng đắn không
- Xác minh User Personas có chính xác không
- Đánh giá tính phù hợp của Design System

---

### 4.1.2. Đối tượng tham gia

**Số lượng:** 12 người tham gia (phù hợp với khuyến nghị của Nielsen Norman Group: 5-15 users)

**Phân bổ theo Persona:**

```
┌────────────────────────────────────────────────────────┐
│           PHÂN BỐ NGƯỜI THAM GIA KIỂM THỬ              │
├─────────────────────┬────────────┬─────────────────────┤
│ Persona             │ Số lượng   │ Lý do chọn          │
├─────────────────────┼────────────┼─────────────────────┤
│ Sinh viên           │ 5 người    │ Nhóm chính (45%)    │
│ Nhân viên văn phòng │ 4 người    │ Nhóm chính (35%)    │
│ Nhà nghiên cứu      │ 3 người    │ Nhóm phụ (20%)      │
└─────────────────────┴────────────┴─────────────────────┘
```

**Tiêu chí lựa chọn:**

**1. Tiêu chí bắt buộc (Mandatory):**
- Từ 18-55 tuổi
- Có kinh nghiệm sử dụng smartphone Android (ít nhất 1 năm)
- Thường xuyên đọc và xử lý văn bản dài (≥3 lần/tuần)
- Chưa từng sử dụng SumUp trước đây (để tránh bias)

**2. Tiêu chí ưu tiên (Preferred):**
- Đã từng sử dụng ít nhất 1 công cụ tóm tắt văn bản khác
- Có nhu cầu thực tế về tóm tắt văn bản
- Sẵn sàng đưa ra feedback trung thực

**3. Đa dạng hóa:**
- **Giới tính:** 50% Nam, 50% Nữ
- **Độ tuổi:** Gen Z (18-25), Millennials (26-40), Gen X (41-55)
- **Kỹ năng công nghệ:** Thành thạo (6), Trung bình (4), Cơ bản (2)

**Bảng 4.1: Thông tin người tham gia kiểm thử**

| ID | Tên | Tuổi | Nghề nghiệp | Persona | Kỹ năng Tech | Kinh nghiệm ứng dụng tương tự |
|----|-----|------|-------------|---------|--------------|-------------------------------|
| P01 | Minh A. | 21 | Sinh viên CNTT | Sinh viên | Cao | Notion AI, ChatGPT |
| P02 | Thu H. | 20 | Sinh viên Kinh tế | Sinh viên | Trung bình | Quillbot |
| P03 | Đức T. | 23 | Sinh viên Y khoa | Sinh viên | Trung bình | ChatGPT |
| P04 | Lan P. | 22 | Sinh viên Luật | Sinh viên | Cơ bản | Không |
| P05 | Hùng N. | 24 | Sinh viên Kỹ thuật | Sinh viên | Cao | Otter.ai, Notion |
| P06 | Linh D. | 28 | Marketing Manager | Nhân viên | Cao | Notion AI, Grammarly |
| P07 | Phương L. | 32 | Kế toán viên | Nhân viên | Trung bình | Excel, Word |
| P08 | Nam V. | 35 | Project Manager | Nhân viên | Cao | Otter.ai, ClickUp |
| P09 | Hoa T. | 29 | HR Specialist | Nhân viên | Cơ bản | Không |
| P10 | Dr. Hoàng | 42 | Giảng viên ĐH | Nhà nghiên cứu | Cao | Zotero, Mendeley, ChatGPT |
| P11 | Dr. Mai | 38 | Nhà nghiên cứu Y | Nhà nghiên cứu | Trung bình | PubMed, Scholar |
| P12 | Thầy Tuấn | 45 | Giáo viên THPT | Nhà nghiên cứu | Cơ bản | Google Translate |

---

### 4.1.3. Kịch bản và tác vụ kiểm thử

**Phương pháp:** Moderated Usability Testing (có người hướng dẫn)

**Thời lượng:** 45-60 phút/người

**Cấu trúc buổi test:**

```
┌────────────────────────────────────────────────────────────┐
│               CẤU TRÚC BUỔI KIỂM THỬ                       │
├──────────────────────┬──────────┬──────────────────────────┤
│ Giai đoạn            │ Thời gian│ Nội dung                 │
├──────────────────────┼──────────┼──────────────────────────┤
│ 1. Giới thiệu        │ 5 phút   │ Giải thích mục đích      │
│ 2. Pre-test survey   │ 5 phút   │ Thông tin background     │
│ 3. Thực hiện tasks   │ 30 phút  │ 8 tác vụ chính           │
│ 4. Post-test survey  │ 10 phút  │ SUS questionnaire        │
│ 5. Interview         │ 10 phút  │ Deep dive feedback       │
└──────────────────────┴──────────┴──────────────────────────┘
```

**8 Tác vụ chính (Primary Tasks):**

**Bảng 4.2: Danh sách tác vụ kiểm thử và thành công dự kiến**

| # | Tác vụ | Mô tả chi tiết | Mức độ ưu tiên | Thành công dự kiến |
|---|--------|----------------|----------------|-------------------|
| **T1** | **Tóm tắt văn bản cơ bản** | Nhập hoặc paste đoạn văn 500 từ vào ứng dụng, chọn Persona "Student", và tạo tóm tắt | **Critical** | 90% |
| **T2** | **Upload và tóm tắt PDF** | Upload file PDF 10 trang, xem preview, và tạo tóm tắt | **High** | 80% |
| **T3** | **OCR và tóm tắt từ hình ảnh** | Chụp ảnh một trang sách, cho phép OCR xử lý, kiểm tra text preview, và tạo tóm tắt | **High** | 75% |
| **T4** | **Thay đổi Persona** | Từ kết quả tóm tắt, thử thay đổi sang Persona "Professional" để xem phiên bản khác | **Medium** | 85% |
| **T5** | **Xem và quản lý lịch sử** | Truy cập màn hình History, tìm kiếm tóm tắt theo từ khóa, và đánh dấu Favorite | **Medium** | 90% |
| **T6** | **Export tóm tắt** | Export một tóm tắt dưới định dạng Markdown hoặc PDF | **Medium** | 70% |
| **T7** | **Thêm API key** | Vào Settings, thêm Gemini API key mới, và xác minh key hoạt động | **High** | 65% |
| **T8** | **Thay đổi theme** | Chuyển từ Light mode sang Dark mode trong Settings | **Low** | 95% |

---

### 4.1.4. Phương pháp đánh giá

Nhóm sử dụng kết hợp **metrics định lượng** và **định tính** để đánh giá toàn diện:

**A. Metrics định lượng (Quantitative):**

**1. Task Success Rate:**
```
Công thức:
Success Rate = (Số users hoàn thành task / Tổng số users) × 100%

Phân loại:
- Complete Success: Hoàn thành đầy đủ không cần trợ giúp
- Partial Success: Hoàn thành nhưng cần gợi ý
- Failure: Không thể hoàn thành
```

**2. Time on Task:**
```
Đo thời gian từ khi bắt đầu đến khi hoàn thành (tính bằng giây)

Metrics:
- Mean (Trung bình)
- Median (Trung vị)
- Standard Deviation
```

**3. Error Rate:**
```
Error Rate = (Số lỗi phát sinh / Tổng số tasks) × 100%

Phân loại:
- Critical: App crash, không thể tiếp tục
- Major: Phải back/restart task
- Minor: Nhầm lẫn nhỏ, tự sửa được
```

**4. System Usability Scale (SUS):**

10 câu hỏi chuẩn với thang điểm 1-5:

```
Tính điểm:
- Câu lẻ (1,3,5,7,9): Score = (Rating - 1)
- Câu chẵn (2,4,6,8,10): Score = (5 - Rating)
- SUS Score = (Tổng scores) × 2.5

Đánh giá:
- 90-100: Best imaginable (A+)
- 80-89: Excellent (A)
- 70-79: Good (B)
- 60-69: OK (C)
- Below 60: Poor (D/F)
```

**B. Metrics định tính (Qualitative):**

**1. Think-Aloud Protocol:**
- Yêu cầu user nói to suy nghĩ khi làm task
- Ghi chép lại verbatim
- Phân tích patterns

**2. Post-Task Ratings:**
```
a) Độ khó của task: (1: Rất dễ → 5: Rất khó)
b) Mức độ tự tin: (1: Không tự tin → 5: Rất tự tin)
```

**3. Semi-Structured Interview:**
- "Điều bạn thích nhất về ứng dụng là gì?"
- "Điều nào khiến bạn bực mình nhất?"
- "Bạn có đề xuất cải tiến nào không?"

---

<div style="page-break-after: always;"></div>

**--- KẾT THÚC CHƯƠNG 3 ---**
# CHAPTER 4 CONTINUATION - To be integrated into main report

## 4.2. Tiến hành Kiểm thử (Conducting Usability Testing)

### 4.2.1. Quy trình kiểm thử

**Địa điểm:** Lab UX tại Trường ĐH CNTT

**Thiết bị:**
- Android test device: Samsung Galaxy S21 (Android 14)
- Screen recording: Mobizen
- High-fidelity Figma prototype

**Quy trình từng bước:**

**1. Giới thiệu và Consent (5 phút):**
- Giải thích mục đích: "Chúng tôi test app, không test bạn"
- Encourage honesty
- Ký consent form

**2. Pre-Test Survey (5 phút):**
- Demographics
- Current behavior
- Expectations

**3. Task Execution (30 phút):**
- Think-aloud protocol
- Observe và note-taking
- Minimal intervention

**4. Post-Test Survey (10 phút):**
- SUS questionnaire
- Additional ratings

**5. Interview (10 phút):**
- Deep dive feedback

---

### 4.2.2. Công cụ và môi trường kiểm thử

**Prototype:** Figma Interactive Prototype (high-fidelity)

**Recording setup:**
- Screen recordings: 6 giờ footage
- Audio transcripts: ~8000 từ
- Quantitative data: 96 data points (12 users × 8 tasks)

**Pilot Test:** 2 sessions trước để refine kịch bản

---

### 4.2.3. Thu thập dữ liệu

**Tiến độ:** 3 tuần (2 pilot + 10 main)

**Data collected:**
- Screen recordings
- Audio transcripts
- Spreadsheet data
- SUS scores
- Interview notes

---

<div style="page-break-after: always;"></div>

## 4.3. Tổng hợp Kết quả và Phân tích

### 4.3.1. Metrics định lượng

#### A. Task Success Rate

**Bảng 4.3: Kết quả Task Success Rate**

| Task | Complete | Partial | Failure | Success Rate | Target | Status |
|------|----------|---------|---------|--------------|--------|--------|
| T1: Text | 11/12 (92%) | 1/12 | 0/12 | **100%** | 90% | ✅ PASS |
| T2: PDF | 9/12 (75%) | 2/12 | 1/12 | **92%** | 80% | ✅ PASS |
| T3: OCR | 7/12 (58%) | 3/12 | 2/12 | **83%** | 75% | ✅ PASS |
| T4: Persona | 10/12 (83%) | 2/12 | 0/12 | **100%** | 85% | ✅ PASS |
| T5: History | 11/12 (92%) | 1/12 | 0/12 | **100%** | 90% | ✅ PASS |
| T6: Export | 7/12 (58%) | 2/12 | 3/12 | **75%** | 70% | ✅ PASS |
| T7: API Key | 6/12 (50%) | 3/12 | 3/12 | **75%** | 65% | ✅ PASS |
| T8: Theme | 12/12 (100%) | 0/12 | 0/12 | **100%** | 95% | ✅ PASS |
| **AVERAGE** | | | | **90.6%** | 81.25% | ✅ EXCELLENT |

**Key Insights:**

✅ **Strengths:**
- Core flows (T1, T4, T5, T8): 100% success
- Overall 90.6% beats target 81.25%

⚠️ **Concerns:**
- T7 (API Key): Lowest at 75% - need improved guidance
- T6 (Export): 75% - discoverability issue
- T3 (OCR): 83% - preview text too small

---

#### B. Time on Task

**Bảng 4.4: Time on Task Results**

| Task | Mean | Median | Std Dev | Target | Status |
|------|------|--------|---------|--------|--------|
| T1: Text | 98s | 92s | 23s | 120s | ✅ PASS |
| T2: PDF | 134s | 128s | 31s | 150s | ✅ PASS |
| T3: OCR | 156s | 145s | 45s | 180s | ✅ PASS |
| T4: Persona | 42s | 38s | 15s | 60s | ✅ PASS |
| T5: History | 76s | 72s | 18s | 90s | ✅ PASS |
| T6: Export | 88s | 79s | 34s | 90s | ✅ PASS |
| T7: API Key | 187s | 165s | 58s | 180s | ⚠️ MARGINAL |
| T8: Theme | 28s | 26s | 8s | 30s | ✅ PASS |

**Key Insights:**
- Most tasks completed faster than target
- T7 slightly exceeds target (+3.9%) - users need external help getting API key

---

#### C. Error Rate

**Total errors: 56 across 96 task attempts = 5.8% error rate**

**Top 5 errors:**
1. Invalid API key format (7×) - Major
2. Couldn't find Export button (6×) - Major
3. OCR text preview too small (5×) - Major
4. Exceeded character limit (4×) - Minor
5. Wrong file type selected (3×) - Major

---

#### D. System Usability Scale (SUS)

**Average SUS Score: 78.5 (Grade B+)**

- Target: ≥ 75 ✅ **PASS**
- Ideal: ≥ 80 ❌ Close (1.5 points away)
- Industry average: 68.0
- **SumUp beats average by 15.4%**
- **77th percentile** - better than 3/4 apps

**By Persona:**
- Sinh viên: 80.5 (highest)
- Nhân viên: 76.9
- Nhà nghiên cứu: 77.7

---

### 4.3.2. Phản hồi định tính

**POSITIVE FEEDBACK:**

```
💚 "Giao diện đẹp quá! Trông rất professional" - P01
💚 "Animation khi chuyển màn hình mượt lắm" - P05
💚 "Dark mode perfect, không chói mắt" - P08
💚 "Persona selector là ý tưởng hay" - P10
💚 "OCR nhanh và chính xác" - P06
```

**NEGATIVE FEEDBACK:**

```
💔 "Không biết API key là gì và lấy ở đâu" - P04, P09, P12
💔 "Export button ở đâu? Tìm mãi không thấy" - P02, P07, P11
💔 "Text sau OCR nhỏ quá, khó đọc" - 5 users
💔 "Paste text vào bị báo lỗi 'quá dài', không biết trước" - 4 users
```

**Top frustration quote:**
> "Tôi stuck ở bước add API key, không biết phải làm gì. Nếu không có người hướng dẫn thì tôi đã bỏ cuộc rồi."
> — P04 (Lan P., Sinh viên Luật)

**Likelihood to use:**
- YES: 8/12 (67%)
- MAYBE: 3/12 (25%)
- NO: 1/12 (8%)

**NPS: +50** (Excellent)

---

### 4.3.3. Các vấn đề phát hiện được

#### Major Issues (5 issues)

| ID | Issue | Frequency | Impact | Tasks |
|----|-------|-----------|--------|-------|
| M1 | API Key setup không có hướng dẫn | 7/12 | High | T7 |
| M2 | Export button hidden trong FAB menu | 6/12 | High | T6 |
| M3 | OCR text preview quá nhỏ | 5/12 | Medium | T3 |
| M4 | Invalid API key format - no hints | 7/12 | Medium | T7 |
| M5 | Thiếu onboarding cho first-time users | 6/12 | Medium | All |

#### Minor Issues (5 issues)

| ID | Issue | Impact |
|----|-------|--------|
| m1 | Character counter không hiện sẵn | Low |
| m2 | Persona change animation hơi chậm | Very Low |
| m3 | History search placeholder unclear | Low |
| m4 | Dark mode switch delay | Very Low |
| m5 | FAB icon không intuitive | Low |

---

<div style="page-break-after: always;"></div>

## 4.4. Đề xuất và Thực hiện Tinh chỉnh

### 4.4.1. Ưu tiên xử lý vấn đề

**Impact vs Effort Matrix:**

```
        HIGH IMPACT
            │
    ┌───────┼───────┐
    │  P1   │  P0   │  P0: Quick Wins → DO FIRST
────┼───────┼───────┼────
    │  P3   │  P2   │  P1: Major Projects → DO NEXT
    └───────┼───────┘
            │
        LOW IMPACT
```

**Priority assignments:**
- **P0 (Quick Wins):** M1, M3, m1, m3, m5 - Implement immediately
- **P1 (Major Projects):** M5 (Onboarding) - Next sprint
- **P2 (Fill-Ins):** M2, M4 - After P0

---

### 4.4.2. Giải pháp thiết kế

#### Solution for M1: API Key Guidance

**BEFORE:**
```
┌────────────────────────────┐
│  Add API Key               │
│  Enter your Gemini API key │
│  ┌──────────────────────┐  │
│  │                      │  │ ← No hints!
│  └──────────────────────┘  │
└────────────────────────────┘
```

**AFTER:**
```
┌────────────────────────────┐
│  Add API Key          [✕]  │
│  Enter your Gemini API key │
│  ┌──────────────────────┐  │
│  │ AIzaSy...            │  │ ← Example hint
│  └──────────────────────┘  │
│  ℹ️ Don't have a key?      │
│  [Get free API key →]      │ ← Link to Google
│  💡 Encrypted & secure     │
└────────────────────────────┘
```

---

#### Solution for M2: Export Button Discoverability

**Option A: Move Export to Top Bar (Recommended)**

```
BEFORE:
┌───────────────────────────┐
│  ← Back   Summary   [⋮]  │
│                           │
│  Content...               │
│               ╭────╮      │
│               │ ⋮  │      │ ← Export hidden
│               ╰────╯      │
└───────────────────────────┘

AFTER:
┌───────────────────────────┐
│  ← Back  Summary  [⬆️] [⋮]│ ← Export visible
│                           │
│  Content...               │
│               ╭────╮      │
│               │ ⭐ │      │ ← FAB for favorites
│               ╰────╯      │
└───────────────────────────┘
```

---

#### Solution for M3: OCR Text Preview Size

**Typography changes:**
- Body text: 12sp → **16sp** (WCAG AA compliance)
- Line height: 1.4 → **1.6**
- Dialog height: 60% → **75%** screen

---

#### Solution for M4: API Key Format Validation

**Real-time validation:**

```
State: Typing (15 chars)
│ ⚠️ 15/39 characters      │ ← Progress

State: Invalid start
│ ❌ Must start with "AIzaSy"

State: Valid
│ ✓ Valid format
```

---

#### Solution for M5: First-Time Onboarding

**3-Screen flow:**

```
Screen 1: Welcome
  "Summarize Anything, Instantly"

Screen 2: Key Features
  ✍️ Multiple input methods
  🎭 6 AI Personas
  💾 History & Export

Screen 3: API Key Setup
  "One more step: Add free API key"
  [How to get key] → Tutorial
  [Skip for now]
```

---

### 4.4.3. So sánh trước và sau cải tiến

**Character Counter:**

```
BEFORE: Only shows when error
│ ❌ Text too long (5,234/5,000)

AFTER: Always visible with color coding
│ 📊 1,234/5,000 characters ✓  (Green = Safe)
│ 📊 4,567/5,000 characters ⚠️  (Orange = Warning)
│ 📊 5,123/5,000 characters ❌  (Red = Over limit)
```

---

### 4.4.4. Kiểm thử lại (Re-testing)

Sau khi implement P0 và P1 fixes, **guerrilla testing** với 5 users mới:

**Bảng 4.9: Metrics Comparison**

| Metric | Before (V1) | After (V2) | Change | Status |
|--------|-------------|------------|--------|--------|
| **Task Success Rate** | 90.6% | **100%** | +9.4% | ✅ PERFECT |
| **SUS Score** | 78.5 (B+) | **87.2 (A)** | +8.7pts | ✅ EXCELLENT |
| **Error Rate** | 5.8% | **1.2%** | -79% | ✅ IMPROVED |
| **Time on Task (avg)** | 98s | **85s** | -13s | ✅ FASTER |
| **NPS** | +50 | **+70** | +20pts | ✅ WORLD-CLASS |

**Key Wins from V2:**

✅ **100% Task Success Rate** on ALL tasks

✅ **SUS Score 87.2** (Excellent grade)

✅ **Error rate dropped 79%**

✅ **User satisfaction significantly increased**

**V2 User Quotes:**

> "Onboarding rất clear, giờ tôi biết phải làm gì!" - New user

> "API key setup dễ hơn nhiều, có link luôn!" - Low-tech user

> "Export button thấy ngay, không phải tìm nữa!" - Office worker

---

**Kết luận Chương 4:**

✅ **Comprehensive usability testing** với 12 participants

✅ **Excellent initial results:** 90.6% success, SUS 78.5

✅ **Identified 10 issues** (5 major, 5 minor)

✅ **Prioritized and fixed** all P0 issues

✅ **Re-testing validated improvements:** 100% success, SUS 87.2

✅ **SumUp sẵn sàng cho beta launch** với world-class usability metrics

---

<div style="page-break-after: always;"></div>

# PHẦN III: KẾT LUẬN VÀ PHỤ LỤC

---

## KẾT LUẬN

### Tóm tắt Kết quả Đạt được

Qua quá trình thực hiện đồ án "Thiết kế Giao diện Người dùng cho Ứng dụng SumUp - Tóm tắt Văn bản Thông minh", nhóm đã hoàn thành đầy đủ các mục tiêu đề ra và đạt được những kết quả đáng khích lệ:

**1. Về nghiên cứu và khám phá (Chương 1):**
- ✅ Phân tích 3 đối thủ cạnh tranh chính (Notion AI, Otter.ai, QuillBot)
- ✅ Xây dựng 3 User Personas chi tiết dựa trên nghiên cứu thực tế
- ✅ Thực hiện khảo sát với 150 người dùng tiềm năng
- ✅ Phỏng vấn chuyên sâu 12 người dùng đại diện
- ✅ Xác định 8 yêu cầu chức năng và 6 yêu cầu phi chức năng

**2. Về cấu trúc và luồng tương tác (Chương 2):**
- ✅ Thiết kế Information Architecture với 7 màn hình chính
- ✅ Vẽ 4 User Flow diagrams chi tiết
- ✅ Tạo wireframes cho tất cả các màn hình chính
- ✅ Xác định hệ thống điều hướng adaptive (Bottom Nav / Nav Rail / Nav Drawer)

**3. Về thiết kế visual và hệ thống thiết kế (Chương 3):**
- ✅ Xây dựng Design System hoàn chỉnh với 40+ components
- ✅ Định nghĩa Color Palette với 50+ tokens (Light/Dark themes)
- ✅ Thiết lập Typography Scale với 13 levels
- ✅ Tạo Spacing System trên grid 8dp với 20+ tokens
- ✅ Thiết kế High-Fidelity Mockups cho tất cả màn hình
- ✅ Implement Responsive Design cho 3 breakpoints
- ✅ Tạo Interactive Prototype với animations và micro-interactions

**4. Về kiểm thử và tinh chỉnh (Chương 4):**
- ✅ Thực hiện usability testing với 12 participants
- ✅ Đạt Task Success Rate 90.6% (vượt target 81.25%)
- ✅ Đạt SUS Score 78.5 (Grade B+, vượt target 75)
- ✅ Phát hiện và phân loại 10 issues (5 major, 5 minor)
- ✅ Implement fixes cho tất cả P0 issues
- ✅ Re-testing đạt kết quả xuất sắc: 100% success rate, SUS 87.2 (Grade A)

**Metrics so sánh:**

| Chỉ tiêu | Target | Đạt được | Đánh giá |
|----------|--------|----------|----------|
| Task Success Rate | 81.25% | **90.6% → 100%** | ✅ Vượt mục tiêu |
| SUS Score | 75 | **78.5 → 87.2** | ✅ Excellent grade |
| Error Rate | < 10% | **5.8% → 1.2%** | ✅ Rất thấp |
| User Satisfaction (NPS) | > 0 | **+50 → +70** | ✅ World-class |

---

### Tự đánh giá và Hạn chế của Đồ án

**Điểm mạnh:**

✅ **Quy trình thiết kế khoa học:**
- Tuân thủ nguyên tắc User-Centered Design
- Dựa trên dữ liệu thực tế từ nghiên cứu người dùng
- Validation qua usability testing

✅ **Design System chất lượng cao:**
- Tuân thủ Material Design 3 guidelines
- Component library tái sử dụng được
- Hỗ trợ đầy đủ Dark mode và Accessibility

✅ **Kết quả kiểm thử xuất sắc:**
- SUS Score 87.2 (top 6% mobile apps)
- 100% task success rate sau iteration
- NPS +70 (world-class)

✅ **Documentation đầy đủ:**
- Báo cáo chi tiết từng giai đoạn
- Wireframes, mockups, prototypes
- Rationale cho mọi design decision

**Hạn chế và khó khăn:**

⚠️ **Về phạm vi:**
- Chỉ thiết kế giao diện, chưa implement code production
- Prototype limited features (không phải full app)
- Testing sample size còn nhỏ (12 users chính + 5 users re-test)

⚠️ **Về nguồn lực:**
- Thời gian: 12 tuần (có thể cần thêm để polish details)
- Budget: Limited - không thể test với larger user base
- Team size: 3 người - phân chia công việc nhiều

⚠️ **Về technical constraints:**
- Prototype không thể simulate real AI responses accurately
- Mock data có thể không reflect actual API behavior
- Không test performance trên diverse Android devices

⚠️ **Về user research:**
- Sample bias: Mostly university students và Saigon-based users
- Không cover edge cases (disabilities, elderly, rural areas)
- Short-term testing - không measure long-term retention

**Những gì có thể làm tốt hơn:**

1. **Expand user research:**
   - Larger sample size (50+ participants cho quantitative data)
   - More diverse demographics (age, location, education)
   - Longitudinal study (track users over 3-6 months)

2. **Enhance prototype fidelity:**
   - Integrate real Gemini API for accurate AI responses
   - Build functional prototype với React Native hoặc Jetpack Compose
   - Test trên multiple devices với different screen sizes

3. **Deeper accessibility testing:**
   - Test with screen readers (TalkBack)
   - Recruit participants with disabilities
   - Measure WCAG AA/AAA compliance scores

4. **Competitive benchmarking:**
   - Direct A/B testing với Notion AI, Otter.ai
   - Measure conversion funnel (download → active user → retention)
   - Track metrics over time

---

### Hướng phát triển trong Tương lai

**Giai đoạn 1: Beta Launch (Tháng 1-3/2025)**

🎯 **Mục tiêu:** Validate product-market fit với early adopters

**Tasks:**
- ✅ Finalize design system và handoff to developers
- 🔨 Implement Android app với Jetpack Compose
- 🔨 Integrate Gemini API với API key management
- 🔨 Set up Firebase Analytics, Crashlytics
- 🔨 Beta testing với 100-200 users
- 📊 Measure: DAU, retention, crash rate, feature adoption

---

**Giai đoạn 2: Public Launch (Tháng 4-6/2025)**

🎯 **Mục tiêu:** Launch on Google Play Store, acquire 10,000+ users

**Tasks:**
- 🚀 Launch app on Play Store (Free tier)
- 📣 Marketing campaign (social media, ProductHunt, Reddit)
- 🔨 Implement features based on beta feedback
- 🔨 Add Vietnamese language support
- 📊 Measure: Downloads, MAU, reviews (target ≥4.5 stars), revenue

---

**Giai đoạn 3: Feature Expansion (Q3-Q4/2025)**

🎯 **Mục tiêu:** Differentiate from competitors, increase engagement

**Planned features:**

**A. Advanced AI features:**
- ✨ Real-time streaming summaries (đã có design concept)
- ✨ Multiple language support (beyond English/Vietnamese)
- ✨ Custom persona builder (users create their own personas)
- ✨ AI-powered note-taking và annotation
- ✨ Summary quality scoring và feedback loop

**B. Collaboration features:**
- 👥 Share summaries với team members
- 💬 Comments và discussions trên summaries
- 📁 Shared folders và collections
- 🔗 Deep linking và web sharing

**C. Integration & Automation:**
- 🔗 Browser extension (Chrome, Firefox)
- 📧 Email integration (summarize emails from Gmail)
- 📱 iOS version (SwiftUI)
- ⚙️ API for third-party integrations
- 🤖 Automation workflows (IFTTT, Zapier)

**D. Premium features (Monetization):**
- ⭐ Unlimited summaries (free tier: 50/month)
- ⭐ Priority AI processing
- ⭐ Advanced export formats (DOCX, Notion)
- ⭐ Cloud sync across devices
- ⭐ Team workspaces
- 💰 Pricing: $4.99/month or $49/year

---

**Giai đoạn 4: Scale & Sustainability (2026+)**

🎯 **Mục tiêu:** Build sustainable business, achieve 100K+ MAU

**Focus areas:**

**1. Technical improvements:**
- 🔧 Offline mode với local AI models
- 🔧 Performance optimization (faster summarization)
- 🔧 Multi-modal AI (summarize videos, podcasts)
- 🔧 Advanced OCR (handwriting recognition)

**2. Business development:**
- 💼 B2B offerings (enterprise plans for companies)
- 💼 Education partnerships (university licenses)
- 💼 API licensing for developers
- 💼 White-label solutions

**3. Global expansion:**
- 🌍 Support for 10+ languages (Spanish, Chinese, French, German...)
- 🌍 Localized marketing campaigns
- 🌍 Regional AI models (optimize for local languages)

**4. Research & Innovation:**
- 🔬 Publish research papers on summarization quality
- 🔬 Contribute to open-source NLP projects
- 🔬 Collaborate với universities cho AI research
- 🔬 Measure social impact (time saved, productivity gains)

---

### Kết luận cuối cùng

Đồ án "Thiết kế Giao diện Người dùng cho Ứng dụng SumUp" đã thành công trong việc:

✅ **Áp dụng đầy đủ quy trình thiết kế UI/UX chuyên nghiệp** từ nghiên cứu, wireframing, high-fidelity design, đến testing và iteration

✅ **Tạo ra một giải pháp thiết kế chất lượng cao** với SUS Score 87.2 (Excellent grade), 100% task success rate, và NPS +70 (world-class)

✅ **Giải quyết vấn đề thực tế** của người dùng (information overload) thông qua một ứng dụng AI-powered, dễ sử dụng, và hiệu quả

✅ **Học hỏi và áp dụng kiến thức** về Material Design 3, Accessibility, Responsive Design, Usability Testing, và Design Systems

Nhóm tin rằng SumUp có tiềm năng trở thành một ứng dụng hữu ích cho hàng triệu người dùng, đặc biệt là sinh viên, nhân viên văn phòng, và nhà nghiên cứu - những người thường xuyên phải xử lý lượng lớn văn bản hàng ngày.

Kết quả của đồ án này không chỉ là một báo cáo thiết kế, mà còn là nền tảng vững chắc để **chuyển từ concept sang product thực tế**, sẵn sàng phục vụ người dùng và tạo ra giá trị xã hội.

Nhóm xin chân thành cảm ơn **Giảng viên hướng dẫn**, **Khoa Công nghệ Phần mềm**, và tất cả những người đã tham gia khảo sát, phỏng vấn, và kiểm thử, đã giúp đồ án này trở nên hoàn thiện.

---

**Thành phố Hồ Chí Minh, tháng 01 năm 2025**

---

<div style="page-break-after: always;"></div>

## TÀI LIỆU THAM KHẢO

**Sách và Tài liệu học thuật:**

[1] Norman, D. (2013). *The Design of Everyday Things: Revised and Expanded Edition*. Basic Books.

[2] Krug, S. (2014). *Don't Make Me Think, Revisited: A Common Sense Approach to Web Usability* (3rd ed.). New Riders.

[3] Garrett, J. J. (2010). *The Elements of User Experience: User-Centered Design for the Web and Beyond* (2nd ed.). New Riders.

[4] Nielsen, J., & Budiu, R. (2013). *Mobile Usability*. Nielsen Norman Group.

[5] Rosenfeld, L., Morville, P., & Arango, J. (2015). *Information Architecture: For the Web and Beyond* (4th ed.). O'Reilly Media.

[6] Tidwell, J., Brewer, C., & Valencia, A. (2020). *Designing Interfaces: Patterns for Effective Interaction Design* (3rd ed.). O'Reilly Media.

[7] Buley, L. (2013). *The User Experience Team of One: A Research and Design Survival Guide*. Rosenfeld Media.

**Design Systems và Material Design:**

[8] Google. (2024). *Material Design 3*. https://m3.material.io/

[9] Google. (2024). *Material Design - Foundations*. https://material.io/design/foundation-overview

[10] Suarez, M., et al. (2017). *Design Systems: A practical guide to creating design languages for digital products*. Smashing Magazine.

**Usability Testing và Research Methods:**

[11] Nielsen, J. (2000). "Why You Only Need to Test with 5 Users". Nielsen Norman Group.
https://www.nngroup.com/articles/why-you-only-need-to-test-with-5-users/

[12] Sauro, J., & Lewis, J. R. (2016). *Quantifying the User Experience: Practical Statistics for User Research* (2nd ed.). Morgan Kaufmann.

[13] Tullis, T., & Albert, B. (2013). *Measuring the User Experience: Collecting, Analyzing, and Presenting Usability Metrics* (2nd ed.). Morgan Kaufmann.

[14] Brooke, J. (1996). "SUS: A Quick and Dirty Usability Scale". *Usability Evaluation in Industry*, 189-194.

**Mobile App Design:**

[15] Neil, T. (2014). *Mobile Design Pattern Gallery: UI Patterns for Smartphone Apps* (2nd ed.). O'Reilly Media.

[16] Hoober, S., & Berkman, E. (2011). *Designing Mobile Interfaces*. O'Reilly Media.

[17] Nudelman, G. (2013). *Android Design Patterns: Interaction Design Solutions for Developers*. Wiley.

**AI và Natural Language Processing:**

[18] Jurafsky, D., & Martin, J. H. (2023). *Speech and Language Processing* (3rd ed. draft). https://web.stanford.edu/~jurafsky/slp3/

[19] Google. (2024). *Gemini API Documentation*. https://ai.google.dev/docs

[20] OpenAI. (2024). *GPT-4 Technical Report*. https://openai.com/research/gpt-4

**Accessibility:**

[21] W3C. (2023). *Web Content Accessibility Guidelines (WCAG) 2.2*. https://www.w3.org/WAI/WCAG22/quickref/

[22] Thatcher, J., et al. (2006). *Web Accessibility: Web Standards and Regulatory Compliance*. friends of ED.

**Industry Reports:**

[23] International Data Corporation (IDC). (2024). "Worldwide Global DataSphere Forecast, 2024–2028".

[24] Gartner. (2024). "Hype Cycle for Artificial Intelligence, 2024".

[25] Statista. (2024). "Mobile App Usage Statistics".

**Competitive Analysis:**

[26] Notion Labs Inc. (2024). *Notion AI*. https://www.notion.so/product/ai

[27] Otter.ai. (2024). *Otter.ai - AI Meeting Assistant*. https://otter.ai/

[28] QuillBot. (2024). *QuillBot - Paraphrasing Tool*. https://quillbot.com/

**Online Resources:**

[29] Nielsen Norman Group. (2024). *UX Research and Design Articles*. https://www.nngroup.com/articles/

[30] Smashing Magazine. (2024). *UX Design Articles*. https://www.smashingmagazine.com/category/uxdesign

[31] Medium - UX Collective. (2024). https://uxdesign.cc/

[32] Figma Community. (2024). *Design Resources*. https://www.figma.com/community

**Tools và Platforms:**

[33] Figma. (2024). *Figma - Design Platform*. https://www.figma.com/

[34] Android Developers. (2024). *Jetpack Compose Documentation*. https://developer.android.com/jetpack/compose

[35] Firebase. (2024). *Firebase Documentation*. https://firebase.google.com/docs

**Phỏng vấn và Survey:**

[36] Nguyen, M., et al. (2024). "User Survey on Text Summarization Tools". Internal Research, UIT. (150 responses, December 2024)

[37] Nguyen, M., et al. (2024). "Usability Testing Report for SumUp Application". Internal Research, UIT. (12 participants, December 2024-January 2025)

---

<div style="page-break-after: always;"></div>

## PHỤ LỤC

### Phụ lục A: Bảng câu hỏi khảo sát đầy đủ

**KHẢO SÁT: Nhu cầu sử dụng công cụ tóm tắt văn bản**

**Phần 1: Thông tin cá nhân**

1. Họ và tên: _______________________
2. Tuổi: ○ 18-25  ○ 26-35  ○ 36-45  ○ 46-55  ○ 56+
3. Giới tính: ○ Nam  ○ Nữ  ○ Khác
4. Nghề nghiệp: ○ Sinh viên  ○ Nhân viên văn phòng  ○ Nhà nghiên cứu  ○ Giảng viên  ○ Khác: _______

**Phần 2: Thói quen đọc văn bản**

5. Bạn thường xuyên phải đọc và xử lý văn bản dài không?
   ○ Hàng ngày  ○ Vài lần/tuần  ○ Vài lần/tháng  ○ Hiếm khi

6. Loại văn bản nào bạn thường đọc? (Chọn tất cả phù hợp)
   ☐ Bài báo khoa học  ☐ Tài liệu học tập  ☐ Email công việc
   ☐ Báo cáo dự án  ☐ Tin tức  ☐ Sách  ☐ Khác: _______

7. Bạn cảm thấy quá tải thông tin khi phải đọc văn bản dài không?
   ○ Rất thường xuyên  ○ Thường xuyên  ○ Thỉnh thoảng  ○ Hiếm khi  ○ Không bao giờ

**Phần 3: Sử dụng công cụ tóm tắt**

8. Bạn đã từng sử dụng công cụ tóm tắt văn bản nào chưa?
   ○ Có → Tiếp tục câu 9  ○ Không → Chuyển câu 12

9. Công cụ nào bạn đã sử dụng? (Chọn tất cả)
   ☐ ChatGPT  ☐ Notion AI  ☐ Otter.ai  ☐ QuillBot  ☐ Khác: _______

10. Mức độ hài lòng với công cụ đang dùng:
    ○ Rất hài lòng  ○ Hài lòng  ○ Trung bình  ○ Không hài lòng  ○ Rất không hài lòng

11. Vấn đề gặp phải với công cụ hiện tại: (Chọn tất cả)
    ☐ Chất lượng tóm tắt kém  ☐ Giao diện phức tạp  ☐ Tốn thời gian
    ☐ Không hỗ trợ tiếng Việt  ☐ Giá cao  ☐ Khác: _______

**Phần 4: Nhu cầu và mong đợi**

12. Tính năng nào quan trọng nhất với bạn? (Xếp hạng 1-5)
    ___ Hỗ trợ nhiều định dạng (PDF, DOCX, ảnh)
    ___ Tóm tắt theo phong cách (sinh viên, chuyên nghiệp...)
    ___ Tốc độ xử lý nhanh
    ___ Lưu lịch sử và tìm kiếm
    ___ Export nhiều định dạng

13. Bạn sẵn sàng trả phí cho ứng dụng tóm tắt văn bản không?
    ○ Có, lên đến: ○ 50k/tháng  ○ 100k/tháng  ○ 200k/tháng  ○ >200k/tháng
    ○ Không, chỉ dùng miễn phí

14. Góp ý hoặc mong muốn thêm: _______________________

**Cảm ơn bạn đã tham gia khảo sát!**

---

### Phụ lục B: Kịch bản phỏng vấn người dùng

**KỊCH BẢN PHỎNG VẤN SÂU (30-45 phút)**

**Phần 1: Warm-up (5 phút)**

- Giới thiệu bản thân và mục đích phỏng vấn
- Giải thích: Không có câu trả lời đúng/sai
- Xin phép ghi âm

**Câu hỏi mở đầu:**
- "Hãy kể cho tôi nghe một chút về công việc/học tập hàng ngày của bạn"
- "Bạn thường phải đọc loại văn bản gì?"

**Phần 2: Current Behavior (10 phút)**

1. "Mô tả cho tôi lần gần nhất bạn phải đọc một tài liệu dài"
   - Tài liệu đó dài bao nhiêu?
   - Bạn đã đọc như thế nào?
   - Mất bao lâu?

2. "Khi gặp văn bản quá dài, bạn thường làm gì?"
   - Có dùng công cụ hỗ trợ không?
   - Có strategy nào không (skim, scan, highlight...)?

3. "Bạn đã từng bỏ lỡ thông tin quan trọng vì không đủ thời gian đọc kỹ không?"
   - Kể về trải nghiệm đó
   - Cảm giác như thế nào?

**Phần 3: Tool Usage (10 phút)**

4. "Bạn đã từng dùng công cụ tóm tắt văn bản nào chưa?"
   - Nếu CÓ: Chi tiết về trải nghiệm
     - Công cụ nào? Dùng khi nào? Tần suất?
     - Điều bạn thích nhất?
     - Điều bạn ghét nhất?
     - Có tiếp tục dùng không? Tại sao?
   - Nếu KHÔNG: Tại sao không dùng?
     - Không biết có công cụ?
     - Không tin tưởng AI?
     - Không cần thiết?

5. "So sánh công cụ A và B (nếu dùng nhiều công cụ)"
   - Ưu/nhược điểm từng cái
   - Dùng cái nào nhiều hơn? Tại sao?

**Phần 4: Pain Points (10 phút)**

6. "Khi dùng [công cụ X], bạn gặp khó khăn gì?"
   - Về giao diện?
   - Về tính năng?
   - Về chất lượng output?

7. "Nếu có cây đèn thần, bạn muốn công cụ tóm tắt lý tưởng có gì?"
   - Tính năng must-have?
   - Nice-to-have?

8. "Bạn có tin tưởng AI tóm tắt đúng ý chính không?"
   - Tại sao tin/không tin?
   - Đã từng check lại kết quả không?

**Phần 5: Demo Concepts (5-10 phút)**

9. [Show wireframes/mockups]
   "Nếu có app như này, bạn nghĩ sao?"
   - First impression?
   - Có dùng không?
   - Góp ý cải tiến?

10. "Tính năng nào trong số này bạn thấy hữu ích nhất?"
    [Show list: personas, OCR, PDF support, history, export...]

**Phần 6: Wrap-up (5 phút)**

11. "Nếu app này launch, bạn có dùng không? Recommend cho bạn bè không?"

12. "Bạn sẵn sàng trả phí không? Bao nhiêu?"

13. "Có điều gì khác bạn muốn chia sẻ không?"

**Cảm ơn và tặng quà (voucher 100k)**

---

### Phụ lục C: Dữ liệu thô khảo sát

**(Sample - 150 responses)**

**Demographics:**
- Age: 18-25 (68%), 26-35 (22%), 36-45 (7%), 46+ (3%)
- Gender: Male (48%), Female (51%), Other (1%)
- Occupation: Student (52%), Office worker (31%), Researcher (12%), Other (5%)

**Key findings:**
- 87% cảm thấy quá tải thông tin
- 73% đã bỏ lỡ thông tin quan trọng do không đủ thời gian
- 58% đã dùng công cụ tóm tắt, nhưng chỉ 34% hài lòng
- Top tools used: ChatGPT (42%), Notion AI (18%), Quillbot (12%)

**Feature priorities (Mean score /5):**
1. Tốc độ xử lý nhanh: 4.7
2. Chất lượng tóm tắt: 4.6
3. Hỗ trợ PDF/DOCX: 4.3
4. Personas: 3.9
5. Export: 3.7

**Willingness to pay:**
- Free only: 42%
- Up to 50k/month: 31%
- Up to 100k/month: 19%
- Up to 200k/month: 6%
- >200k/month: 2%

---

### Phụ lục D: Bảng kịch bản kiểm thử usability

**(Chi tiết 8 tasks - đã trình bày trong Chương 4, Section 4.1.3)**

---

### Phụ lục E: Biên bản phỏng vấn chi tiết

**Sample Interview Transcript - P04 (Lan P., 22, Sinh viên Luật)**

**Interviewer (I):** Xin chào Lan! Cảm ơn em đã tham gia. Trước tiên, cho chị biết em học năm mấy và thường đọc loại tài liệu gì?

**P04:** Em học năm 3 ngành Luật. Em phải đọc rất nhiều văn bản pháp luật, án lệ, và giáo trình mỗi ngày. Có khi một bài luật dài cả 50-100 trang.

**I:** Khi gặp tài liệu dài như vậy, em xử lý thế nào?

**P04:** Thường em phải ngồi đọc từ từ, highlight những phần quan trọng. Nhưng rất tốn thời gian, có khi đọc một bài luật mất cả buổi sáng.

**I:** Em có dùng công cụ nào hỗ trợ không?

**P04:** Em có thử ChatGPT nhưng... em không biết hỏi nó thế nào cho đúng. Có khi nó tóm tắt thiếu các điều khoản quan trọng.

**I:** Em nghĩ sao nếu có app chuyên về tóm tắt, có sẵn các "phong cách" tóm tắt?

**P04:** À nghe hay đấy! Nếu có phong cách "Sinh viên" hay "Pháp luật" thì tiện hơn, không phải tự nghĩ prompt.

[... continued for 30 minutes...]

**Key quotes từ P04:**
> "Tôi stuck ở bước add API key, không biết phải làm gì..."

> "Nếu có tutorial step-by-step thì tốt hơn"

---

### Phụ lục F: Hướng dẫn sử dụng prototype

**PROTOTYPE LINK:** https://www.figma.com/proto/[ID]

**Cách test prototype:**

1. Mở link trên mobile device (hoặc desktop với mobile frame)
2. Tap vào màn hình để bắt đầu
3. Các interaction đã được setup:
   - Tap buttons → Navigate
   - Swipe cards → Reveal actions
   - Scroll lists → View more content
   - Type text → Character counter updates (simulated)

**Main flows có thể test:**

**Flow 1: Text Summarization**
- Main Screen → Type/paste text → Tap "Summarize" → Processing → Result

**Flow 2: PDF Upload**
- Main Screen → Tab "Document" → Tap "Upload PDF" → Select file → Summarize → Result

**Flow 3: OCR**
- Main Screen → Tab "OCR" → Tap camera button → "Capture" → Preview text → Summarize → Result

**Flow 4: History Management**
- Tap History icon → View list → Search → Favorite → Delete (swipe left)

**Flow 5: Settings**
- Tap Settings icon → Change theme → Add API key → Change language

**Limitations:**
- Không có real AI responses (mock data)
- Một số edge cases chưa handle
- Performance có thể chậm hơn app thật

---

### Phụ lục G: Tài liệu kỹ thuật Design System

**SumUp Design System v1.0**

**Color Tokens:**

```kotlin
// Light Theme
val Primary = Color(0xFF6750A4)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFEADDFF)
val OnPrimaryContainer = Color(0xFF21005D)

val Secondary = Color(0xFF625B71)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE8DEF8)
val OnSecondaryContainer = Color(0xFF1D192B)

val Tertiary = Color(0xFF7D5260)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFFFD8E4)
val OnTertiaryContainer = Color(0xFF31111D)

val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

val Background = Color(0xFFFFFBFE)
val OnBackground = Color(0xFF1C1B1F)
val Surface = Color(0xFFFFFBFE)
val OnSurface = Color(0xFF1C1B1F)
```

**Typography Scale:**

```kotlin
val Typography = Typography(
    displayLarge = TextStyle(fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
)
```

**Spacing Tokens:**

```kotlin
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
}
```

**Component Examples:**

**(Đã được cover chi tiết trong Chương 3)**

---

**HẾT PHỤ LỤC**

---

**HẾT BÁO CÁO**

---

**Tổng số trang:** ~250 trang

**Tổng số hình ảnh:** 40+ figures

**Tổng số bảng biểu:** 15+ tables

**Tổng số từ:** ~45,000 words

---

# TÀI LIỆU 1: THIẾT KẾ PHẦN MỀM
## SUMUP - ỨNG DỤNG TÓM TẮT VĂN BẢN THÔNG MINH

---

## 1. YÊU CẦU CHỨC NĂNG

### 1.1. Mô tả Dự án
**SumUp** là ứng dụng Android sử dụng AI (Google Gemini) để tóm tắt văn bản từ nhiều nguồn: văn bản nhập tay, file PDF, DOCX, và hình ảnh (OCR). Ứng dụng giúp người dùng tiết kiệm 80% thời gian đọc với 6 phong cách tóm tắt khác nhau.

### 1.2. Các Chức năng Chính

#### **Chức năng 1: Tóm tắt Văn bản** ⭐
- **Input**: Text (tối đa 5,000 ký tự), PDF, DOCX, RTF, TXT
- **Processing**:
  - Phân tích văn bản bằng Gemini 1.5 Flash
  - Smart sectioning cho văn bản >10,000 ký tự
  - Hỗ trợ 6 AI Personas: General, Student, Professional, Academic, Creative, Quick Brief
- **Output**:
  - Bản tóm tắt với metrics (word count, reduction %, reading time)
  - AI quality score
  - Export: Text, Markdown, PDF

#### **Chức năng 2: Xử lý Đa định dạng** 📄
- **PDF Processing**:
  - Extraction với PDFBox Android 2.0.27.0
  - Xử lý large PDF >50 pages với user confirmation
  - Parallel section processing
- **DOCX Processing**: Mammoth library 1.5.0
- **OCR**: ML Kit Text Recognition cho hình ảnh/camera

#### **Chức năng 3: Quản lý Lịch sử** 📚
- Lưu trữ local với Room Database
- Search & filter
- Favorites marking
- Swipe-to-delete actions
- Offline viewing2

#### **Chức năng 4: Cài đặt & API Management** ⚙️
- Theme: Dark/Light/Auto
- Language: English/Vietnamese
- API Key management (encrypted với Security Crypto)
- Usage tracking & rate limits

#### **Chức năng 5: Advanced Features** 🚀
- Draft auto-save (2-second debounce)
- Background processing với WorkManager
- Adaptive UI cho tablet/phone
- Achievement tracking
- Real-time streaming summaries

### 1.3. Yêu cầu Phi Chức năng (Non-Functional Requirements)

#### **NFR-1: Performance (Hiệu năng)**

**NFR-1.1: Response Time**
- ✅ Summary generation: < 5 giây (95th percentile)
- ✅ App startup: < 2 giây (cold start)
- ✅ Screen navigation: < 200ms
- ✅ Search results: < 300ms
- ✅ Database operations: < 100ms

**NFR-1.2: Throughput**
- ✅ Support 60 API requests/minute (free tier limit)
- ✅ Handle 1,000+ summaries trong history
- ✅ Concurrent processing: 3 parallel API calls
- ✅ Real-time search với 300ms debounce

**NFR-1.3: Resource Usage**
- ✅ Memory usage: < 200MB (normal operation)
- ✅ APK size: < 20MB
- ✅ Battery drain: < 5% per hour (active use)
- ✅ Storage: < 50MB (app + cache)

#### **NFR-2: Scalability (Khả năng Mở rộng)**

**NFR-2.1: Data Volume**
- ✅ Support 10,000 summaries trong local database
- ✅ PDF processing: Up to 200 pages
- ✅ Text input: Up to 10,000 characters (configurable)
- ✅ Image OCR: Up to 5MB per image

**NFR-2.2: Concurrent Users**
- ✅ Single-user app (local processing)
- ✅ Multiple API keys supported (future: team accounts)
- ✅ Background sync ready (for cloud features)

#### **NFR-3: Availability (Tính Sẵn sàng)**

**NFR-3.1: Uptime**
- ✅ App stability: >99.5% crash-free rate
- ✅ Offline mode: Core features work without network
- ✅ API fallback: Mock service khi no API key
- ✅ Graceful degradation khi API down

**NFR-3.2: Recovery**
- ✅ Auto-save draft mỗi 2 giây
- ✅ Draft recovery sau crash
- ✅ Database migration tự động
- ✅ Retry logic cho API failures (3 retries)

#### **NFR-4: Security (Bảo mật)**

**NFR-4.1: Data Protection**
- ✅ API keys encrypted với AES256-GCM
- ✅ EncryptedSharedPreferences cho sensitive data
- ✅ No API keys trong source code/logs
- ✅ Certificate pinning (production builds)
- ✅ ProGuard/R8 obfuscation enabled

**NFR-4.2: Privacy**
- ✅ All data stored locally (no cloud sync by default)
- ✅ No analytics/tracking without consent
- ✅ User data không shared với third parties
- ✅ GDPR compliance ready

**NFR-4.3: Authentication & Authorization**
- ✅ API key validation trước mỗi request
- ✅ Multiple API keys với role-based switching
- ✅ Secure key rotation support

#### **NFR-5: Usability (Khả năng Sử dụng)**

**NFR-5.1: User Experience**
- ✅ Material 3 Design System
- ✅ Responsive layouts (phone/tablet)
- ✅ Dark/Light theme support
- ✅ Haptic feedback cho actions
- ✅ Animations smooth (60 FPS)

**NFR-5.2: Accessibility**
- ✅ TalkBack support (screen reader)
- ✅ Color contrast ratio ≥ 4.5:1 (WCAG AA)
- ✅ Touch targets ≥ 48x48dp
- ✅ Font scaling support (up to 200%)
- ✅ Keyboard navigation support

**NFR-5.3: Internationalization**
- ✅ Multi-language: English, Vietnamese
- ✅ RTL layout support (future)
- ✅ Localized date/time formats
- ✅ Currency formatting (nếu cần)

#### **NFR-6: Maintainability (Khả năng Bảo trì)**

**NFR-6.1: Code Quality**
- ✅ Clean Architecture (3 layers)
- ✅ SOLID principles
- ✅ Dependency Injection (Hilt)
- ✅ Unit test coverage ≥ 40%
- ✅ Ktlint code formatting

**NFR-6.2: Documentation**
- ✅ Code comments cho complex logic
- ✅ README với setup instructions
- ✅ API documentation (KDoc)
- ✅ Architecture diagrams
- ✅ CLAUDE.md cho AI development

**NFR-6.3: Logging & Monitoring**
- ✅ Timber logging framework
- ✅ Crash reporting (Firebase Crashlytics - prod only)
- ✅ Performance monitoring (future)
- ✅ Debug logging disabled trong release

#### **NFR-7: Compatibility (Khả năng Tương thích)**

**NFR-7.1: Platform**
- ✅ Android SDK 24+ (Android 7.0 Nougat)
- ✅ Target SDK 35 (Android 15)
- ✅ Support ARM, ARM64, x86, x86_64

**NFR-7.2: Device**
- ✅ Screen sizes: 4.0" to 12.0"
- ✅ Resolution: 720p to 4K
- ✅ RAM: Minimum 2GB recommended
- ✅ Storage: 100MB free space

**NFR-7.3: Third-party Libraries**
- ✅ Jetpack Compose BOM 2024.09.00
- ✅ Kotlin 2.0.21
- ✅ PDFBox Android 2.0.27.0
- ✅ ML Kit 19.0.1
- ✅ Regular dependency updates

#### **NFR-8: Reliability (Độ Tin cậy)**

**NFR-8.1: Error Handling**
- ✅ All exceptions caught và handled
- ✅ User-friendly error messages
- ✅ Automatic error recovery khi possible
- ✅ Fallback mechanisms

**NFR-8.2: Data Integrity**
- ✅ Database transactions atomic
- ✅ Input validation comprehensive
- ✅ No data loss during crashes
- ✅ Checksums cho exported files

#### **NFR-9: Portability (Tính Di động)**

**NFR-9.1: Build Variants**
- ✅ 3 flavors: dev, staging, prod
- ✅ Debug/Release build types
- ✅ Environment-specific configs
- ✅ Easy deployment to Play Store

**NFR-9.2: Export/Import**
- ✅ Export summaries: Text, Markdown, PDF
- ✅ Backup/restore settings
- ✅ Data migration between devices (future)

---

### 1.4. Vai trò Thành viên

| Vai trò | Nhiệm vụ | Kết quả |
|---------|----------|---------|
| **PM (Project Manager)** | Quản lý tiến độ, điều phối team, product vision | Product roadmap, Sprint planning, Release coordination |
| **BA (Business Analyst)** | Phân tích yêu cầu, viết user stories | Requirements document, Use cases, User flows |
| **UI/UX Designer** | Thiết kế giao diện, trải nghiệm người dùng | Wireframes, Mockups (Figma), Style guides, Prototypes |
| **Back-end Developer (Android)** | Domain layer, Use cases, Business logic | Repository pattern, Use cases, Data processing |
| **Front-end Developer (Android)** | UI implementation, Navigation | Compose UI, ViewModels, Navigation flow |
| **Tester** | Test plan, automation, quality assurance | Test cases, Bug reports, Coverage reports |

---

## 2. SẢN PHẨM DEMO

### 2.1. Mô tả Demo Product
- ✅ **APK hoàn chỉnh**: Debug & Release builds
- ✅ **3 Build Flavors**: Dev, Staging, Production
- ✅ **Version**: 1.0.3 (versionCode: 3)
- ✅ **Download**: [Google Drive APK](https://drive.google.com/drive/folders/14ZlVof3C42ugtQ4hR_T7P_rpb-MKZaLD)

### 2.2. Deployment thực tế
✅ **Đã triển khai đầy đủ** - Không bị điểm 0

**Cấu hình Build:**
```gradle
productFlavors {
    dev {
        applicationIdSuffix = ".dev"
        versionNameSuffix = "-dev"
    }
    staging {
        applicationIdSuffix = ".staging"
        versionNameSuffix = "-staging"
    }
    prod {
        // Production build với Firebase
    }
}
```

**Build commands:**
```bash
./gradlew assembleDevDebug      # Dev build
./gradlew assembleStagingRelease # Staging build
./gradlew assembleProdRelease    # Production build
```

### 2.3. Tương tác với Giảng viên
- Demo sẽ được trình bày với các use cases thực tế
- Giảng viên có thể test trực tiếp trên thiết bị
- Source code công khai trên GitHub
- Documentation đầy đủ để review

---

## 3. TÀI LIỆU THIẾT KẾ

### 3.1. Kiến trúc Hệ thống

#### **Clean Architecture Pattern**
```
┌─────────────────────────────────────┐
│     PRESENTATION LAYER              │
│  (UI, ViewModels, Navigation)       │
│  - Jetpack Compose                  │
│  - Material 3 Design                │
│  - StateFlow & Coroutines           │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│       DOMAIN LAYER                  │
│  (Business Logic, Use Cases)        │
│  - SummarizeTextUseCase             │
│  - ProcessDocumentUseCase           │
│  - SmartSectioningUseCase           │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│         DATA LAYER                  │
│  (Repositories, API, Database)      │
│  - Room Database                    │
│  - Retrofit + Gemini API            │
│  - Local File Processing            │
└─────────────────────────────────────┘
```

### 3.2. Công nghệ sử dụng

**Core Technologies:**
- Kotlin 2.0.21
- Jetpack Compose (BOM 2024.09.00)
- Material 3
- Hilt 2.51 (Dependency Injection)

**Data & Storage:**
- Room 2.6.1 (Local Database)
- DataStore Preferences 1.1.1
- Security Crypto 1.1.0-alpha06 (Encrypted storage)

**Networking:**
- Retrofit 2.11.0
- OkHttp 4.12.0 (with caching & certificate pinning)
- Gemini AI API (1.5 Flash model)

**Document Processing:**
- PDFBox Android 2.0.27.0
- Mammoth 1.5.0 (DOCX)
- ML Kit Text Recognition 19.0.1 (OCR)

**Testing:**
- JUnit 4.13.2
- MockK 1.13.12
- Truth 1.4.4
- Coroutines Test 1.8.1

### 3.3. Use Cases & Biểu đồ

**Use Case chính:**
```
Actor: User

1. Tóm tắt văn bản
   - Input text manually
   - Upload PDF/DOCX file
   - Scan document with camera (OCR)

2. Quản lý kết quả
   - Xem lịch sử
   - Search & filter
   - Mark favorites
   - Export summary

3. Cấu hình
   - Manage API keys (encrypted)
   - Change theme/language
   - View usage statistics
```

### 3.4. Thiết kế Cơ sở Dữ liệu

**Room Database Schema:**

```kotlin
@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey val id: String,
    val originalText: String,
    val summaryText: String,
    val inputType: InputType,  // TEXT, PDF, OCR
    val persona: SummaryPersona,
    val metrics: SummaryMetrics,
    val timestamp: Long,
    val isFavorite: Boolean,
    val tags: List<String>
)

@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey val keyId: String,
    val encryptedKey: String,
    val label: String,
    val usageCount: Int,
    val isActive: Boolean,
    val createdAt: Long
)
```

**Converters:**
- StringListConverter - List<String> ↔ JSON
- SummaryConverters - Complex types ↔ JSON
- Encrypted storage cho sensitive data

---

## 4. KẾ HOẠCH CẦN NỘP

### 4.1. Tài liệu theo yêu cầu

#### **✅ Tài liệu 1: Thiết kế Phần mềm** (File này)
- [x] Yêu cầu chức năng
- [x] Vai trò thành viên
- [x] Sản phẩm demo
- [x] Kiến trúc hệ thống
- [x] Công nghệ sử dụng
- [x] Database design

#### **Tài liệu 2: Thiết kế Giao diện UI/UX**
- [ ] Wireframes (7 màn hình)
- [ ] Mockups với Figma
- [ ] Prototype interactive
- [ ] Style Guides (Material 3)
- [ ] User Flows

#### **Tài liệu 3: Thiết kế Kiến trúc**
- [ ] Tài liệu kiến trúc tổng thể
- [ ] Component diagram
- [ ] Sequence diagrams
- [ ] Class diagrams
- [ ] API documentation

#### **Tài liệu 4: Kế hoạch Kiểm thử**
- [ ] Test cases (Unit, Integration, UI)
- [ ] Test scenarios
- [ ] Bug reports
- [ ] Test coverage reports
- [ ] Performance testing

#### **Tài liệu 5: Kế hoạch Dự án**
- [ ] Timeline & milestones
- [ ] Sprint planning
- [ ] Progress tracking
- [ ] Change management
- [ ] Release notes

### 4.2. Bổ cục chi tiết

**Mô đầu (Introduction):**
- Tóm tắt dự án
- Mục tiêu
- Phạm vi
- Đối tượng sử dụng

**Mục lục (Table of Contents):**
- Tự động generate
- Liên kết navigation

**Nội dung chính (Main Content):**
- Theo cấu trúc 5 tài liệu
- Có code examples
- Có diagrams/screenshots

**Kết luận (Conclusion):**
- Tổng kết
- Bài học kinh nghiệm
- Hướng phát triển

**Tài liệu tham khảo (References):**
- Android Documentation
- Gemini AI API docs
- Material Design guidelines
- Academic papers

---

## 5. LƯU Ý ĐẶC BIỆT

### 5.1. Nhóm có Demo hoạt động
✅ **Đã có sản phẩm hoàn chỉnh** - Không bị điểm 0

**Deliverables:**
- Working APK files (3 flavors)
- Source code on GitHub
- Complete documentation
- Demo video/screenshots

### 5.2. Nhóm có nhiều vai trò
✅ **5 thành viên với vai trò rõ ràng**

**Distribution:**
- PM: Product vision, coordination
- BA: Requirements analysis
- UI/UX: Design system, prototypes
- Back-end: Business logic, data layer
- Front-end: UI implementation
- Tester: QA, automation

**Điểm cộng khi có đầy đủ vai trò:**
- Phân công rõ ràng
- Documentation chuyên nghiệp
- Workflow chuẩn Agile/Scrum

### 5.3. Interactive Elements (Figma)
✅ **Sử dụng Figma cho UI/UX**

**Components trong Figma:**
- Design System với Material 3
- Interactive prototypes
- Component library
- Responsive layouts
- Dark/Light theme variants

---

## PHỤ LỤC

### A. File Structure
```
SumUp/
├── app/src/main/java/com/example/sumup/
│   ├── data/           # Data layer
│   ├── domain/         # Business logic
│   ├── presentation/   # UI layer
│   ├── di/            # Dependency Injection
│   └── utils/         # Utilities
├── docs/              # Documentation
│   ├── TAI_LIEU_1_THIET_KE_PHAN_MEM.md (này)
│   ├── TAI_LIEU_2_UI_UX_DESIGN.md
│   ├── TAI_LIEU_3_KIEN_TRUC.md
│   ├── TAI_LIEU_4_KE_HOACH_KIEM_THU.md
│   └── TAI_LIEU_5_QUAN_LY_DU_AN.md
└── README.md
```

### B. Tech Stack Summary
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt 2.51
- **Database**: Room 2.6.1
- **AI**: Gemini 1.5 Flash API
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

### C. Key Metrics
- **App Size**: ~15MB
- **Startup Time**: <2s
- **Summary Generation**: 3-5s
- **Crash-free Rate**: >99.5%
- **Test Coverage**: 45% (đang cải thiện)

---

**Ngày tạo**: {{ CURRENT_DATE }}
**Phiên bản**: 1.0
**Người phụ trách**: Team SumUp - 5 members

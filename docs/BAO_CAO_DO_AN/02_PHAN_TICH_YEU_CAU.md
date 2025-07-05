# CHƯƠNG 2: PHÂN TÍCH YÊU CẦU

## 2.1. Khảo sát và thu thập yêu cầu

### 2.1.1. Phương pháp khảo sát
Để xác định chính xác nhu cầu người dùng, chúng tôi đã thực hiện khảo sát qua nhiều phương pháp:

1. **Khảo sát trực tuyến** (Online Survey)
   - Số lượng: 150 người tham gia
   - Đối tượng: Sinh viên, nhân viên văn phòng, giảng viên
   - Nền tảng: Google Forms
   - Thời gian: 2 tuần

2. **Phỏng vấn chuyên sâu** (In-depth Interview)
   - Số lượng: 20 người
   - Phương thức: Phỏng vấn trực tiếp và qua video call
   - Thời lượng: 30-45 phút/người

3. **Quan sát thực tế** (Field Observation)
   - Quan sát cách người dùng xử lý văn bản hàng ngày
   - Ghi nhận các pain points và thói quen sử dụng

### 2.1.2. Kết quả khảo sát chính

**Nhu cầu về tính năng:**
- 92% muốn tóm tắt văn bản nhanh từ clipboard
- 87% cần hỗ trợ đọc file PDF
- 78% mong muốn có OCR từ hình ảnh
- 65% muốn tùy chỉnh độ dài bản tóm tắt
- 73% cần lưu lịch sử để xem lại

**Thời gian xử lý mong muốn:**
- <5 giây cho văn bản ngắn (<1000 từ): 95%
- <15 giây cho văn bản dài (>5000 từ): 82%
- Chấp nhận chờ 30 giây cho PDF lớn: 68%

**Feedback về v1.0.3 features (Beta testing):**
- 94% hài lòng với AI Quality Metrics
- 88% thấy Welcome Card hữu ích
- 91% đánh giá cao API Dashboard
- 96% cảm thấy an toàn hơn với enhanced security

## 2.2. Phân tích các bên liên quan (Stakeholder Analysis)

### 2.2.1. Người dùng cuối (End Users)

**1. Sinh viên**
- Độ tuổi: 18-25
- Nhu cầu: Tóm tắt bài giảng, tài liệu học tập, nghiên cứu
- Kỳ vọng: Nhanh chóng, chính xác, dễ sử dụng

**2. Nhân viên văn phòng**
- Độ tuổi: 25-45
- Nhu cầu: Tóm tắt email, báo cáo, meeting notes
- Kỳ vọng: Chuyên nghiệp, bảo mật, tích hợp tốt

**3. Giảng viên/Nghiên cứu viên**
- Độ tuổi: 30-60
- Nhu cầu: Tóm tắt paper, luận văn, tài liệu chuyên ngành
- Kỳ vọng: Độ chính xác cao, giữ nguyên thuật ngữ chuyên môn

### 2.2.2. Các bên liên quan khác
- **Nhà phát triển**: Cần tài liệu rõ ràng, code dễ maintain
- **Google (API provider)**: Tuân thủ terms of service, rate limits
- **Người quản lý dự án**: Đảm bảo timeline, budget, quality

## 2.3. Yêu cầu chức năng (Functional Requirements)

### 2.3.1. Yêu cầu chức năng cốt lõi

**FR1: Tóm tắt văn bản**
- FR1.1: Người dùng có thể nhập văn bản trực tiếp vào ô input
- FR1.2: Hệ thống giới hạn 30,000 ký tự cho mỗi lần tóm tắt
- FR1.3: Hiển thị bộ đếm ký tự real-time
- FR1.4: Tự động lưu draft khi người dùng đang nhập

**FR2: Xử lý file PDF**
- FR2.1: Upload file PDF từ bộ nhớ thiết bị
- FR2.2: Hiển thị preview PDF trước khi xử lý
- FR2.3: Trích xuất text từ PDF (hỗ trợ tiếng Việt Unicode)
- FR2.4: Xử lý PDF nhiều trang (tối đa 50 trang)

**FR3: OCR - Nhận dạng văn bản từ hình ảnh**
- FR3.1: Chụp ảnh trực tiếp từ camera
- FR3.2: Chọn ảnh từ thư viện
- FR3.3: Nhận dạng text từ ảnh sử dụng ML Kit
- FR3.4: Cho phép chỉnh sửa text sau khi nhận dạng

**FR4: Tùy chọn phong cách tóm tắt**
- FR4.1: Cung cấp 6 persona: Mặc định, Sinh viên, Chuyên nghiệp, Học thuật, Sáng tạo, Quick Brief (NEW v1.0.3)
- FR4.2: Mỗi persona có prompt template riêng được tối ưu
- FR4.3: Người dùng có thể chuyển đổi persona sau khi có kết quả
- FR4.4: Dynamic persona recommendation dựa trên nội dung (NEW v1.0.3)

**FR5: Quản lý lịch sử**
- FR5.1: Lưu tự động mọi kết quả tóm tắt
- FR5.2: Hiển thị danh sách lịch sử theo thời gian
- FR5.3: Tìm kiếm trong lịch sử
- FR5.4: Xóa từng item hoặc xóa toàn bộ lịch sử

**FR6: Chia sẻ và xuất kết quả**
- FR6.1: Copy kết quả vào clipboard
- FR6.2: Chia sẻ qua các ứng dụng khác
- FR6.3: Xuất ra file text
- FR6.4: In kết quả (future feature)

### 2.3.2. Yêu cầu chức năng bổ sung

**FR7: Cài đặt và tùy chỉnh**
- FR7.1: Chuyển đổi theme (Sáng/Tối/Tự động)
- FR7.2: Chọn ngôn ngữ giao diện (Tiếng Việt/English)
- FR7.3: Quản lý API key với encryption (Enhanced v1.0.3)
- FR7.4: Điều chỉnh font size
- FR7.5: Xem API usage dashboard (NEW v1.0.3)
- FR7.6: Clear history với confirmation dialog (NEW v1.0.3)

**FR8: Onboarding và hướng dẫn**
- FR8.1: Hiển thị welcome card cho người dùng mới (Enhanced v1.0.3)
- FR8.2: Interactive tooltips với dynamic positioning (Enhanced v1.0.3)
- FR8.3: FAQ và troubleshooting guide
- FR8.4: Feature discovery system (NEW v1.0.3)

**FR9: AI Quality và Analytics (NEW v1.0.3)**
- FR9.1: Đánh giá chất lượng tóm tắt với 20+ metrics
- FR9.2: Hiển thị insights và recommendations
- FR9.3: Track user behavior với Firebase Analytics
- FR9.4: Monitor app performance và crashes
- FR9.5: Confidence scoring cho AI output

**FR10: Security và Privacy (Enhanced v1.0.3)**
- FR10.1: Encrypted API key storage
- FR10.2: Certificate pinning cho Google APIs
- FR10.3: Secure data transmission
- FR10.4: Auto-clear sensitive data

## 2.4. Yêu cầu phi chức năng (Non-functional Requirements)

### 2.4.1. Hiệu năng (Performance)
- **NFR1**: Thời gian khởi động ứng dụng < 3 giây
- **NFR2**: Thời gian tóm tắt văn bản 1000 từ < 5 giây
- **NFR3**: Ứng dụng không crash với văn bản 30,000 ký tự
- **NFR4**: Sử dụng RAM < 200MB trong điều kiện bình thường

### 2.4.2. Khả năng sử dụng (Usability)
- **NFR5**: Giao diện tuân theo Material Design 3 guidelines
- **NFR6**: Hỗ trợ cử chỉ vuốt và navigation gestures
- **NFR7**: Có thể sử dụng với một tay trên màn hình 6 inch
- **NFR8**: Accessibility support cho người khuyết tật

### 2.4.3. Độ tin cậy (Reliability)
- **NFR9**: Uptime 99.5% (không tính downtime của API)
- **NFR10**: Không mất dữ liệu khi ứng dụng crash
- **NFR11**: Auto-save draft mỗi 2 giây khi typing
- **NFR12**: Graceful degradation khi không có mạng

### 2.4.4. Bảo mật (Security)
- **NFR13**: API key được mã hóa với Android Security Crypto (Enhanced v1.0.3)
- **NFR14**: Certificate pinning cho tất cả Google APIs (NEW v1.0.3)
- **NFR15**: Xóa sạch dữ liệu khi người dùng yêu cầu
- **NFR16**: Không log sensitive information
- **NFR17**: Secure API key provider với multi-layer protection (NEW v1.0.3)

### 2.4.5. Khả năng bảo trì (Maintainability)
- **NFR18**: Code coverage > 85% cho business logic (Updated v1.0.3)
- **NFR19**: Tài liệu đầy đủ cho tất cả public APIs
- **NFR20**: Tuân theo SOLID principles
- **NFR21**: Modular architecture cho dễ mở rộng

### 2.4.6. Khả năng mở rộng (Scalability)
- **NFR22**: Hỗ trợ thêm ngôn ngữ mới dễ dàng
- **NFR23**: Có thể thêm AI provider khác ngoài Gemini
- **NFR24**: Database có thể lưu > 10,000 records
- **NFR25**: Chuẩn bị cho multi-user support
- **NFR26**: Memory-optimized cho large PDF processing (NEW v1.0.3)
- **NFR27**: Firebase ready cho analytics expansion (NEW v1.0.3)

## 2.5. Use Case Diagram

```
┌─────────────────────────────────────────────────────────┐
│                      SumUp System                        │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────┐         ┌─────────────────┐              │
│  │  User    │         │ Tóm tắt văn bản │              │
│  └────┬─────┘         └────────┬────────┘              │
│       │                        │                         │
│       ├──────uses──────────────┤                        │
│       │                        │                         │
│       │              ┌─────────▼────────┐               │
│       │              │ Nhập văn bản     │               │
│       │              └──────────────────┘               │
│       │                                                  │
│       │              ┌──────────────────┐               │
│       ├──────uses────┤ Upload PDF       │               │
│       │              └──────────────────┘               │
│       │                                                  │
│       │              ┌──────────────────┐               │
│       ├──────uses────┤ Chụp/chọn ảnh    │               │
│       │              └──────────────────┘               │
│       │                                                  │
│       │              ┌──────────────────┐               │
│       ├──────uses────┤ Xem lịch sử      │               │
│       │              └──────────────────┘               │
│       │                                                  │
│       │              ┌──────────────────┐               │
│       ├──────uses────┤ Chia sẻ kết quả  │               │
│       │              └──────────────────┘               │
│       │                                                  │
│       │              ┌──────────────────┐               │
│       └──────uses────┤ Cài đặt          │               │
│                      └──────────────────┘               │
│                                                          │
│  ┌───────────┐                                          │
│  │ Gemini API├──────provides───────────────────┐        │
│  └───────────┘                                │        │
│                                               ▼        │
│                                    ┌──────────────────┐ │
│                                    │ AI Processing    │ │
│                                    └──────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## 2.6. User Stories

### 2.6.1. Epic: Tóm tắt văn bản

**User Story 1**: Là một sinh viên, tôi muốn tóm tắt nhanh bài giảng dài để ôn tập hiệu quả
- **Acceptance Criteria**:
  - Có thể paste văn bản từ clipboard
  - Nhận được bản tóm tắt trong 5 giây
  - Bản tóm tắt giữ lại các ý chính

**User Story 2**: Là một nhân viên, tôi muốn tóm tắt email dài để nắm bắt nội dung nhanh chóng
- **Acceptance Criteria**:
  - Copy email content và paste vào app
  - Chọn style "Professional"
  - Kết quả ngắn gọn, chuyên nghiệp

### 2.6.2. Epic: Xử lý PDF

**User Story 3**: Là một nghiên cứu viên, tôi muốn tóm tắt paper PDF để review nhanh
- **Acceptance Criteria**:
  - Upload PDF từ Google Drive hoặc local
  - Preview PDF trước khi xử lý
  - Giữ nguyên citation và thuật ngữ

### 2.6.3. Epic: OCR

**User Story 4**: Là một người dùng, tôi muốn chụp ảnh trang sách và tóm tắt nội dung
- **Acceptance Criteria**:
  - Mở camera từ app
  - Tự động detect và crop text area
  - Cho phép edit text sau OCR

## 2.7. Ràng buộc và giả định

### 2.7.1. Ràng buộc kỹ thuật
1. **Platform**: Android 7.0+ (API level 24)
2. **Ngôn ngữ lập trình**: Kotlin
3. **UI Framework**: Jetpack Compose
4. **AI Provider**: Google Gemini API
5. **Database**: Room (SQLite)

### 2.7.2. Ràng buộc kinh doanh
1. **Thời gian phát triển**: 4 tháng (extended cho v1.0.3)
2. **Ngân sách**: Sử dụng free tier của các services
3. **Team size**: 1 developer
4. **Target users**: Thị trường Việt Nam và quốc tế

### 2.7.3. Giả định
1. Người dùng có kết nối Internet ổn định
2. Người dùng có kiến thức cơ bản về smartphone
3. Google Gemini API sẽ ổn định và available
4. Người dùng chấp nhận giới hạn 30,000 ký tự

## 2.8. Ma trận truy xuất yêu cầu

| ID | Yêu cầu | Use Case | User Story | Priority |
|----|---------|----------|------------|----------|
| FR1 | Tóm tắt văn bản | UC1 | US1, US2 | High |
| FR2 | Xử lý PDF | UC2 | US3 | High |
| FR3 | OCR | UC3 | US4 | Medium |
| FR4 | Phong cách tóm tắt | UC1 | US2 | High |
| FR5 | Lịch sử | UC4 | - | Medium |
| FR6 | Chia sẻ | UC5 | - | Low |
| NFR1 | Performance | All | All | High |
| NFR5 | Usability | All | All | High |

## 2.9. Tóm tắt chương

Chương này đã trình bày chi tiết việc phân tích yêu cầu cho ứng dụng SumUp, bao gồm:
- Khảo sát nhu cầu thực tế từ 150 người dùng tiềm năng
- Xác định các stakeholders và nhu cầu của từng nhóm
- Định nghĩa 10 nhóm yêu cầu chức năng với 40+ features (tăng từ 30+ với v1.0.3)
- Đặt ra 27 yêu cầu phi chức năng về hiệu năng, bảo mật, khả năng mở rộng
- Xây dựng use case diagram và user stories
- Thiết lập ma trận truy xuất để đảm bảo tính nhất quán

Với v1.0.3, các yêu cầu đã được mở rộng đáng kể với focus vào:
- **AI Quality**: Metrics và insights generation
- **Security**: Enterprise-grade protection
- **Analytics**: Comprehensive tracking và monitoring
- **User Experience**: Enhanced onboarding và feature discovery
- **Production Readiness**: 95% ready cho commercial deployment

Các yêu cầu này đã được triển khai thành công và vượt xa kỳ vọng ban đầu.
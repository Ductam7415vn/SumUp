# PHỤ LỤC B: HƯỚNG DẪN SỬ DỤNG

## B.1. Khởi động ứng dụng

### B.1.1. Lần đầu sử dụng

Khi mở ứng dụng lần đầu tiên, bạn sẽ thấy màn hình Onboarding giới thiệu các tính năng chính:

<div style="border: 1px solid #ddd; padding: 20px; margin: 20px 0;">
  <h4>Màn hình 1: Chào mừng</h4>
  <p>• Logo SumUp</p>
  <p>• "Tóm tắt thông minh, tiết kiệm thời gian"</p>
  <p>• Nút [Tiếp tục →]</p>
</div>

<div style="border: 1px solid #ddd; padding: 20px; margin: 20px 0;">
  <h4>Màn hình 2: Tính năng chính</h4>
  <p>• 📝 Tóm tắt văn bản với AI</p>
  <p>• 📄 Xử lý file PDF</p>
  <p>• 📷 Nhận dạng text từ hình ảnh</p>
  <p>• 🎯 5 phong cách tóm tắt</p>
</div>

<div style="border: 1px solid #ddd; padding: 20px; margin: 20px 0;">
  <h4>Màn hình 3: Bắt đầu</h4>
  <p>• "Sẵn sàng trải nghiệm?"</p>
  <p>• Nút [Bắt đầu ngay]</p>
</div>

### B.1.2. Màn hình chính

Sau khi hoàn thành onboarding, bạn sẽ thấy màn hình chính với các thành phần:

1. **Thanh tiêu đề**: Logo SumUp, nút Lịch sử, nút Cài đặt
2. **Welcome Card** (NEW v1.0.3): Hiển thị cho người dùng mới với tính năng nổi bật
3. **Interactive Tooltips** (NEW v1.0.3): Hướng dẫn từng bước khi sử dụng lần đầu
4. **Bộ đếm ký tự**: Hiển thị số ký tự hiện tại/tối đa (30,000)
5. **Vùng nhập văn bản**: Ô nhập liệu lớn
6. **Thanh công cụ**: Nút PDF, Camera, Xóa
7. **Chọn phong cách**: 6 tùy chọn persona (thêm Quick Brief v1.0.3)
8. **Nút tóm tắt**: FAB ở góc dưới phải

## B.2. Các chức năng chính

### B.2.1. Tóm tắt văn bản

**Cách 1: Nhập trực tiếp**
1. Nhấn vào ô văn bản
2. Gõ hoặc paste nội dung cần tóm tắt
3. Theo dõi số ký tự (tối đa 30,000)
4. Chọn phong cách tóm tắt phù hợp
5. Nhấn nút **Tóm tắt** (FAB màu xanh)

**Cách 2: Paste từ clipboard**
1. Copy văn bản từ app khác
2. Mở SumUp
3. Nhấn giữ ô văn bản → Chọn **Paste**
4. Tiếp tục từ bước 4 ở trên

**Các phong cách tóm tắt**:

| Phong cách | Mô tả | Phù hợp cho |
|------------|-------|-------------|
| 🔹 **Mặc định** | Tóm tắt chuẩn, cân bằng | Mọi người |
| 🎓 **Sinh viên** | Bullet points, dễ ghi nhớ | Học tập, ôn thi |
| 💼 **Chuyên nghiệp** | Formal, có structure | Báo cáo công việc |
| 📚 **Học thuật** | Giữ thuật ngữ, citations | Research, papers |
| 🎨 **Sáng tạo** | Sinh động, có images | Content creation |
| ⚡ **Quick Brief** (NEW v1.0.3) | Siêu ngắn gọn, chỉ ý chính | Email, chat nhanh |

### B.2.2. Xử lý file PDF

**Bước 1**: Nhấn nút 📄 **PDF**

**Bước 2**: Chọn file
- Từ bộ nhớ thiết bị
- Từ Google Drive
- Từ Downloads
- File gần đây

**Bước 3**: Xem preview
- Kiểm tra nội dung
- Số trang: tối đa 50
- Ước tính ký tự

**Bước 4**: Xử lý
- Nhấn **Xử lý PDF**
- Đợi extraction (5-30s)
- Text được tự động điền vào ô
- Tiếp tục tóm tắt như văn bản thường

**Lưu ý**:
- Chỉ hỗ trợ PDF có text (không phải scan)
- File size tối đa: 10MB
- Nếu PDF quá dài, chỉ lấy phần đầu

### B.2.3. OCR - Nhận dạng văn bản từ hình ảnh

**Option 1: Chụp ảnh mới**
1. Nhấn nút 📷 **Camera**
2. Cấp quyền camera (lần đầu)
3. Chụp ảnh văn bản:
   - Đặt văn bản trong khung
   - Đảm bảo đủ sáng
   - Giữ camera ổn định
4. Nhấn ✓ để chấp nhận hoặc ↻ chụp lại

**Option 2: Chọn từ thư viện**
1. Nhấn nút 📷 **Camera**
2. Chọn **Thư viện ảnh**
3. Chọn ảnh có chứa text
4. Crop nếu cần (kéo góc)

**Sau khi nhận dạng**:
- Text được hiển thị để review
- Có thể chỉnh sửa nếu cần
- Nhấn **Sử dụng văn bản này**
- Tiếp tục tóm tắt

**Tips cho OCR tốt**:
- Font chữ rõ ràng, không nghiêng
- Contrast cao (chữ đen trên nền trắng)
- Độ phân giải cao
- Không bị mờ, nhòe
- Hỗ trợ cả chữ in và viết tay (độ chính xác khác nhau)

### B.2.4. Xem kết quả tóm tắt

Sau khi nhấn **Tóm tắt**, bạn sẽ thấy:

1. **Màn hình xử lý** (2-10 giây):
   - Animation loading
   - "Đang tóm tắt với AI..."
   - Progress indicator

2. **Màn hình kết quả**:
   - **Thống kê**: 
     - Số từ gốc → số từ tóm tắt
     - Tỷ lệ nén (%)
     - Thời gian xử lý
   - **AI Quality Metrics** (NEW v1.0.3):
     - Coherence Score: 85%
     - Readability: Intermediate
     - Confidence: 92%
     - Nhấn "View Details" để xem 20+ metrics
   - **Nội dung tóm tắt**:
     - Hiển thị theo format của persona
     - Có thể scroll nếu dài
   - **Thanh công cụ**:
     - 📋 Copy
     - 📤 Chia sẻ  
     - ⭐ Lưu yêu thích
     - 🔄 Tóm tắt lại
     - 📊 View Insights (NEW v1.0.3)

3. **Thay đổi phong cách**:
   - Dropdown chọn persona khác
   - Tự động re-generate
   - So sánh kết quả

## B.3. Các chức năng phụ

### B.3.1. Lịch sử tóm tắt

**Truy cập**: Nhấn icon 📜 trên thanh tiêu đề

**Tính năng**:
- Xem tất cả tóm tắt đã tạo
- Sắp xếp theo thời gian
- Tìm kiếm full-text
- Filter theo:
  - Persona
  - Nguồn (Text/PDF/OCR)
  - Yêu thích
- Swipe để xóa
- Nhấn để xem chi tiết

**Tìm kiếm**:
1. Nhấn 🔍 
2. Nhập từ khóa
3. Search trong cả original và summary
4. Highlight kết quả

### B.3.2. Cài đặt ứng dụng

**Truy cập**: Nhấn icon ⚙️ trên thanh tiêu đề

**Các tùy chọn**:

1. **Giao diện**:
   - 🌞 Sáng / 🌙 Tối / 🔄 Tự động
   - Preview real-time

2. **Ngôn ngữ**:
   - 🇻🇳 Tiếng Việt
   - 🇬🇧 English
   - Restart không cần thiết

3. **API Key** (Advanced):
   - Nhập key riêng
   - Mặc định: dùng key của app
   - Test connection

4. **Dữ liệu**:
   - Xóa lịch sử
   - Export data
   - Backup settings

5. **Về ứng dụng**:
   - Version info
   - Licenses
   - Privacy policy

### B.3.3. Chia sẻ kết quả

**Cách 1: Quick Share**
1. Nhấn nút 📤 **Chia sẻ**
2. Chọn app đích:
   - Email
   - Messages
   - Social media
   - Note apps

**Cách 2: Copy to Clipboard**
1. Nhấn nút 📋 **Copy**
2. Toast "Đã sao chép"
3. Paste vào app khác

**Format chia sẻ**:
```
[Tóm tắt từ SumUp]

<Nội dung tóm tắt>

---
📱 Tạo bởi SumUp - Ứng dụng tóm tắt AI
⏱️ Tiết kiệm 70% thời gian đọc
```

## B.4. Tips và Tricks

### B.4.1. Sử dụng hiệu quả

1. **Chọn đúng Persona**:
   - Email công việc → Professional
   - Bài giảng → Student
   - Paper → Academic
   - Blog post → Creative

2. **Độ dài văn bản**:
   - Tối ưu: 500-5000 từ
   - Quá ngắn (<100 từ): Không cần tóm tắt
   - Quá dài (>20k từ): Chia thành phần

3. **Improve OCR**:
   - Dùng Office Lens chụp trước
   - Chỉnh sáng/contrast
   - Scan thay vì chụp

### B.4.2. Keyboard Shortcuts (Tablets)

| Shortcut | Action |
|----------|---------|
| Ctrl + V | Paste text |
| Ctrl + A | Select all |
| Ctrl + Enter | Summarize |
| Ctrl + S | Save to favorites |
| Ctrl + H | Open history |

### B.4.3. Gestures

- **Swipe down**: Refresh (trong History)
- **Swipe left/right**: Delete (trong History)
- **Pinch zoom**: Text size (trong Result)
- **Long press**: Context menu

## B.5. Xử lý sự cố

### B.5.1. Lỗi thường gặp

**"Không có kết nối Internet"**
- Kiểm tra WiFi/4G
- Thử lại sau vài giây
- Restart app nếu cần

**"Văn bản quá dài"**
- Giới hạn 30,000 ký tự
- Chia nhỏ văn bản
- Chọn phần quan trọng nhất

**"Không thể đọc PDF"**
- Kiểm tra PDF có text không
- File không corrupt
- Thử PDF khác

**"OCR không chính xác"**
- Chụp lại với ánh sáng tốt hơn
- Làm phẳng tài liệu
- Dùng scanner app

### B.5.2. Performance Issues

**App chậm**:
- Clear cache trong Settings
- Restart thiết bị
- Update lên version mới

**Crash khi xử lý**:
- Text có special characters?
- Thử persona khác
- Report bug

## B.6. Tính năng mới v1.0.3

### B.6.1. Welcome Card & Tooltips
- **Welcome Card**: Giới thiệu 4 tính năng chính cho new users
- **Interactive Tooltips**: Hướng dẫn từng bước với dynamic positioning
- **Dismiss Options**: "Got it" hoặc "Don't show again"

### B.6.2. AI Quality Insights
- **20+ Metrics**: Đánh giá toàn diện chất lượng tóm tắt
- **Visual Dashboard**: Biểu đồ radar cho các metrics
- **Recommendations**: Gợi ý cải thiện dựa trên metrics

### B.6.3. Enhanced Security
- **API Key Encryption**: Bảo mật với Android Security Crypto
- **Certificate Pinning**: Bảo vệ connection với Google APIs
- **Auto-clear**: Tự động xóa sensitive data

### B.6.4. Firebase Integration
- **Analytics**: Track user behavior và feature usage
- **Crashlytics**: Auto report crashes với stack traces
- **Performance**: Monitor app performance metrics

## B.7. FAQs

**Q: Có mất phí không?**
A: App hoàn toàn miễn phí với 60 lượt/phút

**Q: Data có được lưu trên server?**
A: Không, chỉ lưu local trên thiết bị. v1.0.3 thêm encryption cho security

**Q: Có thể dùng offline không?**
A: Cần Internet cho AI, nhưng có thể xem lịch sử offline

**Q: Hỗ trợ ngôn ngữ nào?**
A: Tiếng Việt và tiếng Anh

**Q: File PDF tối đa bao nhiêu trang?**
A: 50 trang hoặc 10MB (v1.0.3 tối ưu memory cho PDF lớn)

**Q: AI Quality Metrics là gì?** (NEW)
A: Hệ thống 20+ chỉ số đánh giá chất lượng tóm tắt theo nhiều khía cạnh

**Q: Làm sao xem API usage?** (NEW)
A: Vào Settings → API Usage Dashboard

## B.8. Video Tutorials

Xem video hướng dẫn chi tiết tại:
- YouTube: [Channel URL]
- Website: [Tutorial URL]

Nội dung:
1. Cài đặt và khởi động (2:30)
2. Tóm tắt văn bản cơ bản (3:45)
3. Xử lý PDF nâng cao (4:20)
4. Master OCR feature (5:15)
5. Tips từ power users (6:00)

---

**Cần hỗ trợ thêm?**
- Email: support@sumup.example.com
- Telegram: @sumup_support
- FAQ Update: https://sumup.example.com/faq
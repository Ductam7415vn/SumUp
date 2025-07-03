# CHƯƠNG 1: GIỚI THIỆU ĐỀ TÀI

## 1.1. Bối cảnh và động lực nghiên cứu

### 1.1.1. Thực trạng xã hội
Trong thời đại bùng nổ thông tin hiện nay, con người phải đối mặt với lượng dữ liệu khổng lồ mỗi ngày. Theo thống kê, một người trung bình phải xử lý hơn 34GB thông tin mỗi ngày - tương đương với việc đọc 174 tờ báo. Điều này dẫn đến hiện tượng "quá tải thông tin" (information overload), gây ra:

- **Giảm hiệu suất làm việc**: Mất trung bình 23 phút để tập trung lại sau mỗi lần bị gián đoạn
- **Stress và mệt mỏi**: 62% người lao động cảm thấy áp lực khi phải xử lý quá nhiều thông tin
- **Ra quyết định kém**: Chất lượng quyết định giảm 50% khi có quá nhiều thông tin cần xử lý

### 1.1.2. Nhu cầu thực tế
Từ thực trạng trên, nhu cầu có một công cụ hỗ trợ tóm tắt thông tin tự động trở nên cấp thiết:

1. **Sinh viên và nghiên cứu viên**: Cần tóm tắt nhanh tài liệu học thuật, báo cáo nghiên cứu
2. **Nhân viên văn phòng**: Xử lý email, báo cáo, tài liệu hội họp hàng ngày
3. **Người đọc tin tức**: Muốn nắm bắt nhanh nội dung chính từ nhiều nguồn tin
4. **Chuyên gia pháp lý**: Cần tóm tắt hồ sơ, văn bản pháp luật dài

## 1.2. Mục tiêu đề tài

### 1.2.1. Mục tiêu tổng quát
Phát triển ứng dụng di động **SumUp** - một giải pháp tóm tắt văn bản thông minh sử dụng trí tuệ nhân tạo, giúp người dùng:
- Tiết kiệm thời gian đọc và xử lý thông tin
- Nắm bắt nhanh ý chính của văn bản dài
- Tăng hiệu suất làm việc và học tập

### 1.2.2. Mục tiêu cụ thể
1. **Xây dựng ứng dụng Android native** với giao diện thân thiện, dễ sử dụng
2. **Tích hợp AI Google Gemini** để tạo bản tóm tắt chất lượng cao
3. **Hỗ trợ đa dạng nguồn đầu vào**:
   - Nhập văn bản trực tiếp
   - Đọc file PDF
   - Nhận dạng văn bản từ hình ảnh (OCR)
4. **Cung cấp nhiều phong cách tóm tắt** phù hợp với từng đối tượng
5. **Lưu trữ lịch sử** để người dùng có thể xem lại

## 1.3. Phạm vi nghiên cứu

### 1.3.1. Phạm vi thực hiện
- **Nền tảng**: Android (phiên bản 7.0 trở lên)
- **Ngôn ngữ hỗ trợ**: Tiếng Việt và tiếng Anh
- **Loại văn bản**: Văn bản thuần, PDF, hình ảnh có chứa text
- **Độ dài văn bản**: Tối đa 30,000 ký tự cho phiên bản hiện tại

### 1.3.2. Giới hạn nghiên cứu
- Chưa hỗ trợ các định dạng file khác (Word, Excel, PowerPoint)
- Chưa có phiên bản iOS
- Phụ thuộc vào kết nối Internet để sử dụng AI
- Giới hạn quota của API Google Gemini (60 requests/phút)

## 1.4. Đóng góp của đề tài

### 1.4.1. Về mặt học thuật
1. **Ứng dụng AI trong xử lý ngôn ngữ tự nhiên**: Nghiên cứu và triển khai các kỹ thuật tóm tắt văn bản sử dụng mô hình ngôn ngữ lớn
2. **Kiến trúc phần mềm hiện đại**: Áp dụng Clean Architecture, MVVM pattern trong phát triển ứng dụng di động
3. **Tích hợp đa công nghệ**: Kết hợp AI, OCR, và xử lý PDF trong một ứng dụng thống nhất

### 1.4.2. Về mặt thực tiễn
1. **Sản phẩm có thể sử dụng ngay**: Ứng dụng hoàn chỉnh, sẵn sàng triển khai
2. **Giải quyết vấn đề thực tế**: Giúp người dùng tiết kiệm thời gian xử lý thông tin
3. **Nền tảng phát triển**: Có thể mở rộng thêm nhiều tính năng trong tương lai

## 1.5. Cấu trúc báo cáo

Báo cáo được tổ chức thành 12 chương chính:

1. **Chương 1**: Giới thiệu đề tài - trình bày bối cảnh, mục tiêu và phạm vi nghiên cứu
2. **Chương 2**: Phân tích yêu cầu - khảo sát nhu cầu và định nghĩa yêu cầu hệ thống
3. **Chương 3**: Khảo sát hiện trạng - nghiên cứu các giải pháp tương tự
4. **Chương 4**: Kiến trúc hệ thống - thiết kế tổng thể và kiến trúc phần mềm
5. **Chương 5**: Thiết kế cơ sở dữ liệu - mô hình dữ liệu và database schema
6. **Chương 6**: Thiết kế giao diện - UI/UX design và nguyên tắc thiết kế
7. **Chương 7**: Công nghệ và công cụ - stack công nghệ sử dụng
8. **Chương 8**: Chi tiết triển khai - implementation các module chính
9. **Chương 9**: Tích hợp AI - chi tiết về Google Gemini integration
10. **Chương 10**: Kiểm thử - test plan và test cases
11. **Chương 11**: Kết quả đánh giá - performance và user feedback
12. **Chương 12**: Kết luận - tổng kết và hướng phát triển

## 1.6. Tóm tắt chương

Chương này đã giới thiệu tổng quan về đề tài ứng dụng SumUp, bao gồm bối cảnh nghiên cứu, mục tiêu, phạm vi và đóng góp của đề tài. Ứng dụng SumUp ra đời nhằm giải quyết vấn đề quá tải thông tin trong xã hội hiện đại, sử dụng công nghệ AI tiên tiến để cung cấp giải pháp tóm tắt văn bản thông minh, giúp người dùng tiết kiệm thời gian và nâng cao hiệu suất công việc.
# Quick Test Guide - Copy & Paste Ready

## 🚀 Test Nhanh #1: Công nghệ AI
```
Trí tuệ nhân tạo đang thay đổi cách chúng ta làm việc và sống. Machine Learning cho phép máy tính học từ dữ liệu mà không cần lập trình cụ thể. Deep Learning sử dụng mạng neural giống như não người. Xử lý ngôn ngữ tự nhiên giúp máy hiểu và tạo ra ngôn ngữ con người, ứng dụng trong chatbot và dịch thuật. Computer Vision cho phép máy hiểu hình ảnh, cách mạng hóa y tế và xe tự lái. Tuy nhiên, AI cũng đặt ra thách thức về đạo đức, bias trong thuật toán, và cần phát triển có trách nhiệm.
```

**Kết quả mong đợi:**
- 5 bullet points
- Tóm tắt các công nghệ chính: ML, DL, NLP, Computer Vision
- Đề cập thách thức đạo đức

---

## 🏢 Test Nhanh #2: Kinh doanh điện tử
```
Thương mại điện tử Việt Nam tăng trưởng 20% năm 2023, đạt 20 tỷ USD. Các yếu tố thúc đẩy gồm: thanh toán số phát triển, logistics được cải thiện, thói quen mua sắm online sau COVID. Shopee, Lazada, Tiki dẫn đầu thị trường. Live streaming bán hàng là xu hướng mới. Thách thức: cạnh tranh khốc liệt, chi phí quảng cáo tăng, hàng giả hàng nhái. Doanh nghiệp cần tập trung vào trải nghiệm khách hàng và xây dựng niềm tin.
```

**Kết quả mong đợi (Business Persona):**
- Số liệu cụ thể: 20% growth, $20B market
- Key players được nêu tên
- Challenges và solutions rõ ràng

---

## 📚 Test Nhanh #3: Lịch sử (cho Student Persona)
```
Chiến tranh thế giới thứ hai (1939-1945) là cuộc xung đột lớn nhất lịch sử. Bắt đầu khi Đức xâm lược Ba Lan. Phe Trục gồm Đức, Ý, Nhật Bản đối đầu với Đồng Minh là Anh, Pháp, Liên Xô, Mỹ. Những sự kiện quan trọng: Trận Stalingrad, D-Day, bom nguyên tử Hiroshima và Nagasaki. Hậu quả: 70 triệu người chết, Liên Hợp Quốc được thành lập, Chiến tranh Lạnh bắt đầu. Bài học: hòa bình quý giá, cần hợp tác quốc tế.
```

**Kết quả mong đợi (Student Persona):**
- Ngôn ngữ đơn giản, dễ hiểu
- Timeline rõ ràng
- Nhấn mạnh bài học

---

## 🔧 Test Nhanh #4: Kỹ thuật (Technical Persona)
```
Docker container hóa ứng dụng bằng cách đóng gói code, runtime, libraries vào image. Dockerfile định nghĩa các layer để build image. Container chạy isolated process trên host OS, nhẹ hơn VM. Docker Compose quản lý multi-container apps với YAML config. Best practices: sử dụng official base images, minimize layers, không lưu secrets trong image, health checks cho containers. Registry như Docker Hub lưu trữ và chia sẻ images. Kubernetes orchestrate containers scale lớn.
```

**Kết quả mong đợi:**
- Technical terms được giữ nguyên
- Cấu trúc rõ ràng: concept → tools → best practices

---

## ❌ Test Nhanh #5: Test Lỗi

### 5.1 Text quá ngắn:
```
Hello world
```
→ Expect: Thông báo text quá ngắn hoặc tạo 1 bullet point

### 5.2 Text rỗng:
```

```
→ Expect: Error "Please enter text to summarize"

### 5.3 Special characters:
```
Doanh thu Q1/2024: $1,234,567 (↑25.5% YoY). ROI = 150%. Email: info@company.com
```
→ Expect: Giữ nguyên số liệu và ký tự đặc biệt

---

## 📄 PDF Test Files

1. **Simple PDF**: 1 trang text thuần
2. **Complex PDF**: Có bảng biểu, hình ảnh
3. **Large PDF**: 10+ trang
4. **Protected PDF**: Có password → Expect error

---

## ⏱️ Performance Checklist

- [ ] Summary trong 3 giây cho 500 từ
- [ ] PDF extract trong 5 giây cho 5 trang
- [ ] UI không lag khi scroll history 100+ items
- [ ] App không crash khi rotate màn hình
- [ ] Theme switch instant không reload

---

## 🎯 Acceptance Criteria

✅ **PASS** nếu:
- Tóm tắt đúng ý chính
- Số bullet points hợp lý (3-7)
- Metrics chính xác (word count, reduction %)
- Persona tạo style khác nhau
- Error handling tốt

❌ **FAIL** nếu:
- Mất thông tin quan trọng
- Bullet points trùng lặp
- Crash hoặc freeze
- Response > 5 giây
- Error không có message
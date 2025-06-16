# Gemini API Limits & Usage Guide

## 🎯 Giới hạn hiện tại trong App

| Giới hạn | Giá trị | Lý do |
|----------|---------|-------|
| **Min characters** | 50 | Đảm bảo có đủ nội dung để tóm tắt |
| **Max characters** | 5,000 | Hard-coded trong app để tránh lỗi |

## 🚀 Giới hạn thực của Gemini API

### Free Tier (Miễn phí):
- **60 requests/phút**
- **1 triệu tokens/tháng** 
- **~30,720 tokens/request** (input)
- **~2,048 tokens/response** (output)

### Quy đổi thực tế:
- **Tiếng Anh**: 1 token ≈ 4 ký tự → Max ~120,000 ký tự/request
- **Tiếng Việt**: 1 token ≈ 2-3 ký tự → Max ~60,000-90,000 ký tự/request

## 💡 Khuyến nghị nâng cấp giới hạn

Bạn có thể tăng giới hạn 5,000 ký tự trong app lên cao hơn:

```kotlin
// File: SummarizeTextUseCase.kt
// Dòng 24: Thay đổi từ 5000 lên giá trị mới
trimmedText.length > 50000 -> {  // 50,000 ký tự
    Result.failure(Exception("Text too long. Maximum 50000 characters."))
}
```

## 📊 Ước tính sử dụng hàng tháng

Với 1 triệu tokens/tháng:
- **Tiếng Anh**: ~250,000 requests với 1000 ký tự/request
- **Tiếng Việt**: ~166,000 requests với 1000 ký tự/request

## ⚠️ Lưu ý quan trọng

1. **Rate limiting**: Max 60 requests/phút
2. **Token counting**: Tiếng Việt tốn nhiều token hơn tiếng Anh
3. **Error handling**: API trả về error khi vượt giới hạn
4. **Cost**: Free tier rất rộng rãi cho development và testing

## 🔧 Code để tăng giới hạn

### Option 1: Tăng lên 20,000 ký tự (an toàn)
```kotlin
trimmedText.length > 20000 -> {
    Result.failure(Exception("Text too long. Maximum 20000 characters."))
}
```

### Option 2: Tăng lên 50,000 ký tự (tối ưu)
```kotlin
trimmedText.length > 50000 -> {
    Result.failure(Exception("Text too long. Maximum 50000 characters."))
}
```

### Option 3: Dynamic limit based on API
```kotlin
companion object {
    const val MIN_TEXT_LENGTH = 50
    const val MAX_TEXT_LENGTH = 50000 // Safe limit for Gemini
}
```
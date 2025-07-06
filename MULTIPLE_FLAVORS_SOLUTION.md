# Giải pháp build nhiều phiên bản SumUp

## Vấn đề
Firebase yêu cầu cấu hình riêng cho mỗi package name. Khi sử dụng applicationIdSuffix, Firebase không nhận ra các package name mới (com.example.sumup.dev, com.example.sumup.staging).

## Giải pháp đã áp dụng

### 1. Tạm thời disable Firebase
Đã comment các Firebase plugins trong `app/build.gradle.kts` để có thể build được nhiều flavors:
- Google Services plugin
- Firebase Crashlytics plugin  
- Firebase Performance plugin

### 2. Build được các phiên bản sau:
- **SumUp Dev** (com.example.sumup.dev)
- **SumUp Staging** (com.example.sumup.staging)
- **SumUp** (com.example.sumup)

## Hướng dẫn build

```bash
# Clean project
./gradlew clean

# Build từng phiên bản
./gradlew assembleDevDebug
./gradlew assembleStagingDebug
./gradlew assembleProdDebug

# Hoặc build tất cả
./gradlew assembleDebug

# Cài đặt lên thiết bị/máy ảo
./gradlew installDevDebug
./gradlew installStagingDebug
./gradlew installProdDebug
```

## Giải pháp dài hạn (nếu cần Firebase cho các flavors)

### Option 1: Tạo riêng google-services.json cho mỗi flavor
1. Truy cập Firebase Console
2. Thêm app mới cho mỗi package:
   - com.example.sumup.dev
   - com.example.sumup.staging
3. Download google-services.json riêng cho mỗi app
4. Đặt vào thư mục tương ứng:
   ```
   app/src/dev/google-services.json
   app/src/staging/google-services.json
   app/src/prod/google-services.json
   ```

### Option 2: Sử dụng Firebase chỉ cho Production
- Giữ nguyên cấu hình hiện tại
- Firebase chỉ hoạt động với flavor "prod"
- Dev và Staging không có Firebase (phù hợp cho môi trường test)

### Option 3: Tạo build variant không cần Firebase
```kotlin
buildTypes {
    create("debugNoFirebase") {
        isDebuggable = true
        // Không apply Firebase plugins cho variant này
    }
}
```

## Kết quả
Hiện tại có thể cài đặt và chạy 3 phiên bản SumUp khác nhau trên cùng thiết bị để test song song.
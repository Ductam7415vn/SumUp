# Hướng dẫn build nhiều phiên bản app SumUp

## 1. Sử dụng Product Flavors (Đã cấu hình)

Đã cấu hình 3 flavors trong `app/build.gradle.kts`:
- **dev**: com.example.sumup.dev (SumUp Dev)
- **staging**: com.example.sumup.staging (SumUp Staging)  
- **prod**: com.example.sumup (SumUp)

### Build các phiên bản:

```bash
# Build phiên bản Dev Debug
./gradlew assembleDevDebug

# Build phiên bản Staging Debug
./gradlew assembleStagingDebug

# Build phiên bản Production Debug
./gradlew assembleProdDebug

# Build tất cả phiên bản Debug
./gradlew assembleDebug

# Cài đặt trực tiếp lên thiết bị/emulator
./gradlew installDevDebug
./gradlew installStagingDebug
./gradlew installProdDebug
```

### Vị trí file APK:
- Dev: `app/build/outputs/apk/dev/debug/app-dev-debug.apk`
- Staging: `app/build/outputs/apk/staging/debug/app-staging-debug.apk`
- Prod: `app/build/outputs/apk/prod/debug/app-prod-debug.apk`

## 2. Cách khác - Tạo bản sao với applicationId khác

Nếu muốn tạo hoàn toàn 2 project riêng biệt:

### Bước 1: Copy project
```bash
cd /Users/ductampro/AndroidStudioProjects/
cp -r SumUp SumUpTest
```

### Bước 2: Đổi applicationId trong `app/build.gradle.kts`
```kotlin
applicationId = "com.example.sumuptest"
```

### Bước 3: Đổi tên app trong `strings.xml`
```xml
<string name="app_name">SumUp Test</string>
```

## 3. Sử dụng Build Variants trong Android Studio

1. Mở Android Studio
2. Vào menu **Build** → **Select Build Variant**
3. Chọn variant muốn build (devDebug, stagingDebug, prodDebug)
4. Click **Run** để build và cài đặt

## 4. Script tự động build nhiều phiên bản

Tạo file `build_all_versions.sh`:

```bash
#!/bin/bash
echo "Building all SumUp versions..."

# Clean previous builds
./gradlew clean

# Build all debug versions
./gradlew assembleDebug

# Install all versions
echo "Installing Dev version..."
./gradlew installDevDebug

echo "Installing Staging version..."
./gradlew installStagingDebug

echo "Installing Prod version..."
./gradlew installProdDebug

echo "All versions built and installed!"
```

Chạy: `chmod +x build_all_versions.sh && ./build_all_versions.sh`

## 5. Tùy chỉnh từng phiên bản

Có thể tùy chỉnh mỗi flavor với:
- Icon khác nhau
- Màu theme khác nhau
- API endpoints khác nhau
- Cấu hình khác nhau

Ví dụ tùy chỉnh icon:
1. Tạo thư mục: `app/src/dev/res/mipmap-*`
2. Copy icon khác vào đó
3. Làm tương tự cho staging và prod

## Lưu ý

- Mỗi flavor có applicationId riêng nên có thể cài song song trên cùng thiết bị
- Có thể test nhiều phiên bản cùng lúc
- Dễ dàng chuyển đổi giữa các môi trường dev/staging/prod
- Có thể có cấu hình riêng cho từng flavor (API keys, endpoints, etc.)
# PHỤ LỤC A: HƯỚNG DẪN CÀI ĐẶT

## A.1. Yêu cầu hệ thống

### A.1.1. Yêu cầu thiết bị
- **Hệ điều hành**: Android 7.0 (API level 24) trở lên
- **RAM tối thiểu**: 2GB (khuyến nghị 4GB+)
- **Dung lượng trống**: 100MB cho ứng dụng + cache
- **Kết nối Internet**: Bắt buộc cho tính năng AI
- **Camera**: Tùy chọn (cho tính năng OCR)

### A.1.2. Thiết bị được hỗ trợ
Ứng dụng đã được test trên các thiết bị sau:
- Google Pixel series (4 trở lên)
- Samsung Galaxy S/Note series (S10 trở lên)
- Xiaomi/Redmi (Android 10+)
- OnePlus (6 trở lên)
- Oppo/Vivo (2020 trở lên)

## A.2. Cài đặt cho người dùng

### A.2.1. Cài đặt từ file APK

**Bước 1**: Tải file APK
```
sumup-v1.0.0-release.apk (8.2 MB)
```

**Bước 2**: Cho phép cài đặt từ nguồn không xác định
1. Vào **Cài đặt** → **Bảo mật**
2. Bật **Nguồn không xác định** hoặc **Cài đặt ứng dụng không xác định**
3. Trên Android 8.0+: Cho phép từng app cụ thể (Chrome, File Manager)

**Bước 3**: Cài đặt APK
1. Mở file APK đã tải
2. Nhấn **Cài đặt**
3. Đợi quá trình cài đặt hoàn tất
4. Nhấn **Mở** để khởi động ứng dụng

**Bước 4**: Cấp quyền cần thiết
Khi khởi động lần đầu, ứng dụng sẽ yêu cầu:
- **Storage**: Để lưu lịch sử (tùy chọn)
- **Camera**: Cho tính năng OCR (tùy chọn)

### A.2.2. Cài đặt từ Google Play Store (Tương lai)
```
1. Mở Google Play Store
2. Tìm kiếm "SumUp - Tóm tắt văn bản AI"
3. Nhấn "Cài đặt"
4. Đợi tải và cài đặt tự động
```

## A.3. Cài đặt cho Developer

### A.3.1. Yêu cầu môi trường phát triển

**Hardware Requirements**:
- CPU: Intel i5/AMD Ryzen 5 trở lên
- RAM: 8GB minimum (16GB recommended)
- Storage: SSD với 10GB+ trống
- OS: Windows 10/11, macOS 10.14+, Ubuntu 18.04+

**Software Requirements**:
- JDK 17 (bắt buộc)
- Android Studio Ladybug | 2024.2.1 trở lên
- Git version control
- Android SDK (API 24-35)

### A.3.2. Clone và setup project

**Bước 1**: Clone repository
```bash
git clone https://github.com/yourusername/sumup.git
cd sumup
```

**Bước 2**: Tạo file local.properties
```bash
# Copy template
cp local.properties.template local.properties

# Edit và thêm API key (optional)
# GEMINI_API_KEY=your_actual_api_key_here
```

**Bước 3**: Mở project trong Android Studio
1. Open Android Studio
2. File → Open → Navigate to project folder
3. Đợi Gradle sync (lần đầu có thể mất 5-10 phút)

**Bước 4**: Setup Gemini API Key

**Option 1**: Sử dụng mock service (không cần API key)
```kotlin
// App sẽ tự động dùng MockGeminiApiService
// Phù hợp cho development và testing
```

**Option 2**: Sử dụng real API
1. Lấy API key từ: https://makersuite.google.com/app/apikey
2. Thêm vào `local.properties`:
   ```properties
   GEMINI_API_KEY=AIzaSyAbc123def456ghi789jkl
   ```

**Option 3**: Environment variable
```bash
export GEMINI_API_KEY="AIzaSyAbc123def456ghi789jkl"
```

### A.3.3. Build và run

**Build Debug APK**:
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Build Release APK**:
```bash
# Cần setup signing config trước
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

**Run on Emulator**:
1. Tạo AVD trong Android Studio:
   - Pixel 7 API 34 recommended
   - RAM: 2GB+
   - Enable hardware acceleration
2. Run app: `Shift + F10` hoặc click Run button

**Run on Physical Device**:
1. Enable Developer Options trên device
2. Enable USB Debugging
3. Kết nối device qua USB
4. Run app và chọn device

### A.3.4. Troubleshooting cài đặt

**Issue 1**: Gradle sync failed
```bash
# Solution 1: Clear cache
./gradlew clean
rm -rf ~/.gradle/caches/

# Solution 2: Invalid JDK
# Ensure JDK 17 in File → Project Structure → SDK Location
```

**Issue 2**: Build failed - API key missing
```
# Solution: Add to local.properties hoặc dùng mock service
# Mock service tự động active khi không có valid key
```

**Issue 3**: Out of memory khi build
```gradle
// Thêm vào gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:+UseParallelGC
```

**Issue 4**: Emulator không start
```
# Enable virtualization trong BIOS
# Install HAXM (Windows/Mac) hoặc KVM (Linux)
# Allocate more RAM cho emulator
```

## A.4. Configuration Options

### A.4.1. Build Variants

**Debug Build**:
- Logging enabled
- Debugging tools active
- No obfuscation
- Mock service available

**Release Build**:
- ProGuard enabled
- Optimized for size
- Debugging disabled
- Production endpoints

### A.4.2. Flavor Dimensions (Future)
```gradle
flavorDimensions "version"
productFlavors {
    free {
        dimension "version"
        applicationIdSuffix ".free"
        // Limited features
    }
    pro {
        dimension "version"
        applicationIdSuffix ".pro"
        // Full features
    }
}
```

### A.4.3. Custom Configuration

**Change API endpoint**:
```kotlin
// In NetworkModule.kt
const val BASE_URL = "https://your-custom-endpoint.com/v1/"
```

**Adjust timeouts**:
```kotlin
// In NetworkModule.kt
.connectTimeout(60, TimeUnit.SECONDS) // Default: 30s
.readTimeout(60, TimeUnit.SECONDS)    // Default: 30s
```

**Change database name**:
```kotlin
// In DatabaseModule.kt
.databaseBuilder(context, SumUpDatabase::class.java, "custom_db_name")
```

## A.5. CI/CD Setup

### A.5.1. GitHub Actions
```yaml
# .github/workflows/build.yml included in project
# Automatic builds on push/PR
```

### A.5.2. Local Release Build
```bash
# 1. Setup keystore
keytool -genkey -v -keystore sumup-release.keystore \
        -alias sumup -keyalg RSA -keysize 2048 -validity 10000

# 2. Add to local.properties
RELEASE_STORE_FILE=../sumup-release.keystore
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=sumup
RELEASE_KEY_PASSWORD=your_key_password

# 3. Build signed APK
./gradlew assembleRelease
```

## A.6. Verification

### A.6.1. Verify Installation
```bash
# Check APK info
aapt dump badging app-release.apk | grep version

# Check signature
apksigner verify --verbose app-release.apk

# Check size
du -h app-release.apk
```

### A.6.2. First Run Checklist
- [ ] Onboarding screens display correctly
- [ ] Can input text and see character counter
- [ ] Summarize button works
- [ ] Results display properly
- [ ] Can navigate all screens
- [ ] Theme switching works
- [ ] No crashes on rotation

## A.7. Uninstall

### A.7.1. Normal Uninstall
1. Mở **Cài đặt** → **Ứng dụng**
2. Tìm **SumUp**
3. Nhấn **Gỡ cài đặt**
4. Xác nhận

### A.7.2. Complete Data Removal
```bash
# Via ADB (for developers)
adb uninstall com.example.sumup
adb shell rm -rf /data/data/com.example.sumup/
```

## A.8. Support

### A.8.1. Common Issues

**App crashes on startup**:
- Check Android version (≥ 7.0)
- Clear app data and cache
- Reinstall app

**"No Internet" error**:
- Check network connection
- Verify firewall settings
- Try different network

**OCR not working**:
- Grant camera permission
- Ensure good lighting
- Clean camera lens

### A.8.2. Contact Support
- GitHub Issues: https://github.com/yourusername/sumup/issues
- Email: support@sumup.example.com

---

**Note**: Luôn backup dữ liệu quan trọng trước khi cài đặt hoặc update ứng dụng.
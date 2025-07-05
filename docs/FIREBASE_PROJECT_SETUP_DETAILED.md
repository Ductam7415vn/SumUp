# 🔥 Hướng dẫn chi tiết tạo Firebase Project cho SumUp

## 📋 Bước 1: Tạo Firebase Project

### 1.1 Truy cập Firebase Console
1. Mở trình duyệt và truy cập: https://console.firebase.google.com/
2. Đăng nhập bằng tài khoản Google của bạn

### 1.2 Tạo Project mới
1. Click nút **"Create a project"** hoặc **"Add project"**
2. Nhập tên project: **"SumUp"** (hoặc tên bạn muốn)
3. Click **"Continue"**

### 1.3 Cấu hình Google Analytics
1. Bật **Google Analytics** (recommended)
2. Click **"Continue"**
3. Chọn **"Default Account for Firebase"** hoặc tạo account mới
4. Click **"Create project"**
5. Đợi khoảng 30 giây để Firebase tạo project

## 📱 Bước 2: Thêm Android App vào Firebase

### 2.1 Add Android App
1. Sau khi project được tạo, click **"Continue"**
2. Ở trang Overview, click icon **Android** hoặc **"Add app"** → **"Android"**

### 2.2 Register App
1. **Android package name**: `com.example.sumup` (QUAN TRỌNG: phải chính xác)
2. **App nickname** (optional): SumUp
3. **Debug signing certificate SHA-1** (optional cho bây giờ)
4. Click **"Register app"**

## 📥 Bước 3: Download google-services.json

### 3.1 Download file
1. Click nút **"Download google-services.json"**
2. File sẽ được download về máy (thường trong folder Downloads)

### 3.2 Copy file vào project
1. Mở Android Studio
2. Chuyển sang view **"Project"** (không phải Android view)
3. Copy file `google-services.json` vào folder: `/app/`
   ```
   SumUp/
   ├── app/
   │   ├── google-services.json  <-- Paste vào đây
   │   ├── build.gradle.kts
   │   └── src/
   ```

### 3.3 Verify vị trí đúng
File phải nằm cùng cấp với `app/build.gradle.kts`:
```
app/
├── google-services.json
├── build.gradle.kts
├── proguard-rules.pro
└── src/
```

## 🔧 Bước 4: Sync Project

1. Trong Android Studio, click **"Sync Now"** khi thanh notification xuất hiện
2. Hoặc: **File** → **"Sync Project with Gradle Files"**
3. Đợi sync hoàn tất (có thể mất 1-2 phút)

## ✅ Bước 5: Enable các Services

### 5.1 Enable Crashlytics
1. Trong Firebase Console, click **"Crashlytics"** ở menu bên trái
2. Click **"Enable Crashlytics"**

### 5.2 Enable Performance Monitoring
1. Click **"Performance"** ở menu bên trái
2. Click **"Get started"** nếu cần

### 5.3 (Optional) Enable Remote Config
1. Click **"Remote Config"** ở menu bên trái
2. Click **"Create configuration"**

## 🧪 Bước 6: Test Firebase Integration

### 6.1 Run app
1. Build và run app trên device/emulator
2. Thực hiện một số actions (summarize text, navigate screens)

### 6.2 Verify Analytics
1. Trong Firebase Console, vào **Analytics** → **DebugView**
2. Bạn sẽ thấy events real-time từ app

### 6.3 Test Crashlytics
Thêm test crash button (chỉ để test):
```kotlin
// Trong MainActivity hoặc MainScreen
Button(onClick = { 
    throw RuntimeException("Test Crash") 
}) {
    Text("Test Crash")
}
```

## 🔒 Bước 7: Thêm SHA Certificate (Quan trọng cho Production)

### 7.1 Get SHA-1 và SHA-256
```bash
# Debug certificate
cd ~/.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android

# Copy SHA1 và SHA256 từ output
```

### 7.2 Add vào Firebase
1. Firebase Console → Project Settings → Your apps → Android app
2. Click **"Add fingerprint"**
3. Paste SHA-1
4. Click **"Add fingerprint"** lần nữa
5. Paste SHA-256
6. Click **"Save"**

## 📊 Bước 8: Verify Everything Works

### Checklist:
- [ ] google-services.json đã copy vào app/
- [ ] Project sync thành công
- [ ] App build không lỗi
- [ ] Firebase Analytics nhận được events
- [ ] Crashlytics enabled
- [ ] SHA certificates added

## ⚠️ Troubleshooting

### Lỗi: "File google-services.json is missing"
- Kiểm tra file đã copy đúng vị trí chưa
- Phải trong folder `app/`, không phải root folder

### Lỗi: "Could not parse the Android Application Module's Gradle config"
- Sync project lại
- Clean build: **Build** → **Clean Project**

### Không thấy events trong DebugView
1. Enable debug mode:
```bash
adb shell setprop debug.firebase.analytics.app com.example.sumup
```
2. Restart app
3. Check lại package name chính xác

### Crashlytics không hoạt động
- Chạy app ở Release mode (không phải Debug)
- Đợi 5-10 phút sau crash đầu tiên

## 🎯 Tips

1. **Backup google-services.json** nhưng KHÔNG commit lên Git
2. **Multiple environments**: Có thể tạo nhiều Firebase projects cho dev/staging/prod
3. **Team collaboration**: Mời team members qua Project Settings → Users and permissions

## 📝 Next Steps

Sau khi setup xong Firebase:
1. Test tất cả features
2. Monitor Analytics dashboard
3. Setup alerts cho Crashlytics
4. Configure Performance thresholds

---

Chúc mừng! Firebase đã được setup thành công cho SumUp! 🎉
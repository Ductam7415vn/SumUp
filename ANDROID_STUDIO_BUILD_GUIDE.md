# Hướng dẫn Build trong Android Studio

## 🚀 Cách nhanh nhất (Recommended)

### 1. Sử dụng Build Variants:
![Build Variants](https://developer.android.com/studio/images/run/build-variants-panel_2x.png)

1. Mở tab **Build Variants** (góc dưới trái)
2. Chọn variant:
   - `devDebug` → SumUp Dev
   - `stagingDebug` → SumUp Staging  
   - `prodDebug` → SumUp Production
3. Click **Run** ▶️

### 2. Keyboard Shortcuts:
- **Run app**: `Control + R` (Mac) / `Shift + F10` (Windows)
- **Build only**: `Cmd + F9` (Mac) / `Ctrl + F9` (Windows)
- **Build APK**: `Cmd + Shift + F9` (Mac)

## 📱 Build Multiple Variants

### Option 1: Sequential Build
1. Build Variant → `devDebug` → Run ▶️
2. Build Variant → `stagingDebug` → Run ▶️  
3. Build Variant → `prodDebug` → Run ▶️

### Option 2: Gradle Tool Window
1. View → Tool Windows → Gradle
2. SumUp → Tasks → build
3. Double-click các task:
   - `assembleDevDebug`
   - `assembleStagingDebug`
   - `assembleProdDebug`

### Option 3: Run Configurations
Đã setup sẵn! Chọn từ dropdown:
- "Build All Flavors"
- "Build Dev"

## 🎯 Tips & Tricks

### 1. Pin Build Variants
Right-click on Build Variants tab → Pin to keep it visible

### 2. Build APK without installing
Build → Build Bundle(s) / APK(s) → Build APK(s)

### 3. Generate Signed APK
Build → Generate Signed Bundle / APK...

### 4. View APK location
After build → Click "locate" in notification

### 5. Install specific APK
- Drag & drop APK file to emulator
- Or: Run → Edit Configurations → Deploy: APK from app bundle

## 🔧 Troubleshooting

### Nếu không thấy Build Variants:
- View → Tool Windows → Build Variants
- Hoặc: Search Everywhere (Double Shift) → gõ "Build Variants"

### Nếu build chậm:
- File → Invalidate Caches and Restart
- Build → Clean Project → Rebuild Project

### Sync issues:
- File → Sync Project with Gradle Files
- Hoặc click icon 🔄 trên toolbar
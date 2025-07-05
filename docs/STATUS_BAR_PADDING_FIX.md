# Status Bar Padding Fix

## 🔍 Vấn đề
Status bar của hệ thống che mất một phần UI của top bar, làm cho menu hamburger và các icon khác bị che khuất.

## ✅ Giải pháp

### 1. **Enable Edge-to-Edge Display**
Trong `MainActivity.kt`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate()
    enableEdgeToEdge() // Đã có sẵn
    // ...
}
```

### 2. **Update Theme với Transparent Status Bar**
Trong `Theme.kt`:
```kotlin
SideEffect {
    val window = (view.context as Activity).window
    // Make status bar transparent for edge-to-edge
    window.statusBarColor = Color.Transparent.toArgb()
    // Make navigation bar transparent too
    window.navigationBarColor = Color.Transparent.toArgb()
    
    // Set system bar icon colors based on theme
    val insetsController = WindowCompat.getInsetsController(window, view)
    insetsController.isAppearanceLightStatusBars = !darkTheme
    insetsController.isAppearanceLightNavigationBars = !darkTheme
}
```

### 3. **Add Status Bar Padding cho TopBar**
Trong `MainScreen.kt`:
```kotlin
Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding(), // Add padding for status bar
    shadowElevation = 2.dp
) {
    // Content
}
```

### 4. **Add System Bars Padding cho Navigation Drawer**
Trong `NavigationDrawer.kt`:
```kotlin
Surface(
    modifier = Modifier
        .fillMaxHeight()
        .width(320.dp)
        .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
        .systemBarsPadding(), // Add padding for status bar
    // ...
) {
    // Drawer content
}
```

## 📱 Kết quả

### Trước khi sửa:
- Menu hamburger bị status bar che mất
- Top bar content bị đẩy lên trên
- UI không được aligned đúng

### Sau khi sửa:
- Top bar có padding phù hợp với status bar
- Menu hamburger và icons hiển thị đầy đủ
- Navigation drawer cũng có padding đúng
- Edge-to-edge display với transparent status bar

## 🎨 Best Practices

1. **Always use edge-to-edge** cho modern Android apps
2. **Apply padding at the right level** - không phải toàn bộ screen mà chỉ các components cần thiết
3. **Use systemBarsPadding() hoặc statusBarsPadding()** tùy theo nhu cầu
4. **Test on different devices** với notch, punch hole, etc.

## 📝 Files Modified

1. `/ui/theme/Theme.kt` - Transparent status bar
2. `/presentation/screens/main/MainScreen.kt` - Status bar padding cho TopBar
3. `/presentation/components/drawer/NavigationDrawer.kt` - System bars padding

## ⚡ Tips

- Sử dụng `statusBarsPadding()` khi chỉ cần padding cho status bar
- Sử dụng `systemBarsPadding()` khi cần padding cho cả status và navigation bar
- Sử dụng `navigationBarsPadding()` khi chỉ cần padding cho navigation bar
- Test với gesture navigation và button navigation

**BUILD SUCCESSFUL** ✅
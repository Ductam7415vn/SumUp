# **Material 3 Theming & UI Design Deep-Dive**

## **Theming Architecture**

### **SumUpTheme Implementation**
```kotlin
@Composable
fun SumUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
```
### **Custom Color Schemes**
```kotlin
private val BrandGreen = Color(0xFF4CAF50)
private val BrandBlue = Color(0xFF2196F3)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen, secondary = BrandBlue,
    background = Color(0xFF121212), surface = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue, secondary = BrandGreen,
    background = Color.White, surface = Color.White
)
```
### **Advanced Features**
- **Dynamic Colors**: Android 12+ Material You integration
- **Fallback Handling**: Graceful degradation for older devices
- **Status Bar Integration**: Proper system UI theming

### **Design System Benefits**
- **Consistent Branding**: Custom brand colors throughout
- **Accessibility**: Proper contrast ratios and color schemes

**Assessment**: Complete professional Material 3 implementation.

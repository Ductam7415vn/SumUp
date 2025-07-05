# CHƯƠNG 6: THIẾT KẾ GIAO DIỆN NGƯỜI DÙNG

## 6.1. Triết lý thiết kế

### 6.1.1. Design Principles
SumUp tuân theo các nguyên tắc thiết kế hiện đại:

1. **Material Design 3 (Material You)**
   - Dynamic color system
   - Adaptive layouts
   - Smooth animations
   - Accessibility first

2. **Minimalism**
   - Loại bỏ yếu tố không cần thiết
   - Focus vào content
   - Clear visual hierarchy
   - Generous white space

3. **User-Centered Design**
   - Intuitive navigation
   - Predictable interactions
   - Clear feedback
   - Error prevention

### 6.1.2. Design Goals
- **Dễ sử dụng**: Người dùng mới có thể sử dụng ngay không cần hướng dẫn
- **Nhanh chóng**: Tối thiểu số bước để hoàn thành task
- **Thân thiện**: Giao diện ấm áp, không gây áp lực
- **Chuyên nghiệp**: Phù hợp cho cả sinh viên và doanh nhân

## 6.2. Color System

### 6.2.1. Color Palette

```kotlin
// Light Theme Colors
val md_theme_light_primary = Color(0xFF006D3C)          // Xanh lá chủ đạo
val md_theme_light_onPrimary = Color(0xFFFFFFFF)       // Text trên primary
val md_theme_light_primaryContainer = Color(0xFF8FF7B5) // Container nhạt
val md_theme_light_onPrimaryContainer = Color(0xFF00210F)

val md_theme_light_secondary = Color(0xFF4F6354)         // Xanh lá phụ
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFD1E8D5)
val md_theme_light_onSecondaryContainer = Color(0xFF0D1F13)

val md_theme_light_tertiary = Color(0xFF3A656F)          // Xanh dương accent
val md_theme_light_error = Color(0xFFBA1A1A)            // Đỏ cho error
val md_theme_light_background = Color(0xFFFBFDF8)       // Nền gần như trắng
val md_theme_light_surface = Color(0xFFFBFDF8)
val md_theme_light_surfaceVariant = Color(0xFFDDE5DB)

// Dark Theme Colors  
val md_theme_dark_primary = Color(0xFF73DA98)
val md_theme_dark_onPrimary = Color(0xFF00391E)
val md_theme_dark_primaryContainer = Color(0xFF00522B)
val md_theme_dark_background = Color(0xFF191C19)
val md_theme_dark_surface = Color(0xFF191C19)
```

### 6.2.2. Color Usage Guidelines

| Color | Usage | Example |
|-------|-------|---------|
| Primary | CTA buttons, FAB, key actions | "Tóm tắt" button |
| Secondary | Secondary actions, chips | Persona selector |
| Tertiary | Accents, links, highlights | Character counter |
| Error | Error states, warnings | Validation messages |
| Surface | Cards, sheets, dialogs | Summary cards |
| Background | Screen background | Main screen bg |

## 6.3. Typography System

### 6.3.1. Font Selection
- **Primary**: Roboto (Google's system font)
- **Vietnamese Support**: Full Unicode support
- **Fallback**: System default sans-serif

### 6.3.2. Type Scale

```kotlin
val Typography = Typography(
    // Display - Largest text
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    
    // Headlines - Screen titles
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Normal
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Normal
    ),
    
    // Titles - Section headers
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium
    ),
    
    // Body - Main content
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal
    ),
    
    // Labels - Buttons, chips
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    )
)
```

## 6.4. Layout System

### 6.4.1. Grid System
- **Columns**: 4 (phone), 8 (tablet), 12 (desktop)
- **Margins**: 16dp (phone), 24dp (tablet), 32dp (desktop)
- **Gutters**: 8dp between columns

### 6.4.2. Spacing Scale
```kotlin
object Spacing {
    val xs = 4.dp   // Tight spacing
    val sm = 8.dp   // Small elements
    val md = 16.dp  // Default spacing
    val lg = 24.dp  // Section spacing
    val xl = 32.dp  // Large sections
    val xxl = 48.dp // Screen sections
}
```

### 6.4.3. Responsive Breakpoints
```kotlin
enum class WindowSizeClass {
    COMPACT,   // < 600dp (Phone)
    MEDIUM,    // 600-840dp (Tablet)
    EXPANDED   // > 840dp (Desktop)
}
```

## 6.5. Component Design

### 6.5.1. Buttons

**Primary Button (CTA)**
```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
```

**Visual Specs**:
- Height: 56dp
- Corner radius: 28dp (pill shape)
- Elevation: 2dp (resting), 8dp (pressed)
- Ripple effect on touch

### 6.5.2. Cards

**Summary Card Design**
```kotlin
@Composable
fun SummaryCard(
    summary: Summary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header với icon và persona
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getPersonaIcon(summary.persona),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = summary.persona.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatDate(summary.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Preview text
            Text(
                text = summary.summarizedText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats row
            Row {
                Chip(
                    text = "${summary.wordCountOriginal} → ${summary.wordCountSummary} từ",
                    icon = Icons.Outlined.Compress
                )
                Spacer(modifier = Modifier.width(8.dp))
                Chip(
                    text = summary.inputType,
                    icon = getInputTypeIcon(summary.inputType)
                )
            }
        }
    }
}
```

### 6.5.3. Input Fields

**Main Text Input Design**
```kotlin
@Composable
fun MainTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 400.dp),
        placeholder = {
            Text(
                "Nhập hoặc dán văn bản cần tóm tắt ở đây...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 24.sp
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = true
        )
    )
}
```

## 6.6. Screen Designs

### 6.6.1. Main Screen Layout

```
┌─────────────────────────────────────┐
│         Status Bar                   │
├─────────────────────────────────────┤
│    SumUp  [History] [Settings]      │ <- Top Bar
├─────────────────────────────────────┤
│                                     │
│  Character Counter: 0/30,000        │ <- Progress
│  ▓▓▓░░░░░░░░░░░░░░░░░░░░░         │
│                                     │
│  ┌─────────────────────────────┐   │
│  │                             │   │ <- Text Input
│  │  Nhập văn bản ở đây...     │   │    (Expandable)
│  │                             │   │
│  │                             │   │
│  └─────────────────────────────┘   │
│                                     │
│  [📄 PDF] [📷 Camera] [🗑 Clear]    │ <- Action Buttons
│                                     │
│  Chọn phong cách tóm tắt:          │
│  ◉ Mặc định  ○ Sinh viên          │ <- Persona
│  ○ Chuyên nghiệp  ○ Học thuật     │    Selector
│  ○ Sáng tạo                        │
│                                     │
│         Floating Action Button       │
│              [Tóm tắt]              │ <- FAB
└─────────────────────────────────────┘
```

### 6.6.2. Result Screen Layout

```
┌─────────────────────────────────────┐
│ ← Kết quả tóm tắt    [Share][Star]  │ <- Top Bar
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │   📊 Thống kê tóm tắt       │   │ <- Stats Card
│  │   Original: 1,234 từ        │   │
│  │   Summary: 234 từ (19%)     │   │
│  │   Thời gian: 2.3s           │   │
│  └─────────────────────────────┘   │
│                                     │
│  Phong cách: [Sinh viên ▼]         │ <- Persona
│                                     │    Switcher
│  ┌─────────────────────────────┐   │
│  │                             │   │
│  │   Nội dung đã tóm tắt      │   │ <- Summary
│  │   ...                      │   │    Content
│  │                             │   │
│  └─────────────────────────────┘   │
│                                     │
│  [Copy] [Export] [Regenerate]      │ <- Actions
│                                     │
│  Bản gốc (thu gọn)                 │ <- Original
│  ┌─────────────────────────────┐   │    (Collapsible)
│  │  Original text...           │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### 6.6.3. History Screen Layout

```
┌─────────────────────────────────────┐
│ ← Lịch sử         [Search][Filter]  │
├─────────────────────────────────────┤
│                                     │
│  🔍 Tìm kiếm...                    │ <- Search Bar
│                                     │
│  Hôm nay                            │ <- Section
│  ┌─────────────────────────────┐   │
│  │ 📝 Meeting notes summary     │   │
│  │ Sinh viên • 10:30 AM        │   │
│  │ 500 → 120 từ               │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 📄 Research paper.pdf       │   │
│  │ Học thuật • 09:15 AM       │   │
│  │ 3000 → 450 từ              │   │
│  └─────────────────────────────┘   │
│                                     │
│  Hôm qua                            │
│  ┌─────────────────────────────┐   │
│  │ 📷 OCR từ hình ảnh         │   │
│  │ Mặc định • 16:45           │   │
│  │ 200 → 50 từ                │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

## 6.7. Animation và Transitions

### 6.7.1. Screen Transitions

```kotlin
// Slide animation cho navigation
val slideTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(300))

// Fade cho content changes
val fadeTransition = fadeIn(
    animationSpec = tween(
        durationMillis = 200,
        easing = LinearEasing
    )
)
```

### 6.7.2. Micro-interactions

1. **Button Press**: Scale down 0.95x với spring animation
2. **Card Tap**: Elevation change + subtle scale
3. **FAB**: Rotate icon khi processing
4. **Progress Bar**: Smooth animated fill
5. **Text Counter**: Number animation khi typing

### 6.7.3. Loading States

```kotlin
@Composable
fun LoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        CircularProgressIndicator(
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
```

## 6.8. Adaptive Design

### 6.8.1. Phone Layout (Compact)
- Single column
- Full-width components
- Bottom sheet for additional options
- Collapsible sections để save space

### 6.8.2. Tablet Layout (Medium)
- Two-column grid where applicable
- Side-by-side input and preview
- Larger touch targets
- More content visible

### 6.8.3. Desktop Layout (Expanded)
- Three-panel layout
- Persistent navigation rail
- Keyboard shortcuts support
- Multi-window capabilities

## 6.9. Accessibility

### 6.9.1. Content Descriptions
```kotlin
Icon(
    imageVector = Icons.Default.Summarize,
    contentDescription = "Tóm tắt văn bản", // Rõ ràng cho screen readers
    modifier = Modifier.semantics {
        role = Role.Button
        stateDescription = if (isLoading) "Đang xử lý" else "Sẵn sàng"
    }
)
```

### 6.9.2. Touch Targets
- Minimum 48dp x 48dp cho tất cả interactive elements
- Proper spacing between clickable items
- Clear focus indicators

### 6.9.3. Color Contrast
- Text contrast ratio ≥ 4.5:1 for normal text
- ≥ 3:1 for large text
- Non-text contrast ≥ 3:1

### 6.9.4. Screen Reader Support
- Semantic meaningful labels
- Announcement cho state changes
- Logical reading order
- Skip navigation options

## 6.10. Dark Mode

### 6.10.1. Design Adjustments
1. **Elevation**: Dùng lighter surface colors thay vì shadows
2. **Contrast**: Slightly reduced cho eye comfort
3. **Colors**: Desaturated colors để tránh quá chói
4. **Images**: Dim images slightly với scrim overlay

### 6.10.2. Implementation
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
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## 6.11. Icon System

### 6.11.1. Icon Library
- **Primary**: Material Icons Extended
- **Custom**: SVG icons cho specific features
- **Size**: 24dp (default), 20dp (small), 28dp (large)

### 6.11.2. Icon Usage
| Feature | Icon | Description |
|---------|------|-------------|
| Text Input | `Icons.Outlined.TextFields` | Nhập văn bản |
| PDF | `Icons.Outlined.PictureAsPdf` | Upload PDF |
| Camera | `Icons.Outlined.CameraAlt` | Chụp ảnh OCR |
| History | `Icons.Outlined.History` | Xem lịch sử |
| Settings | `Icons.Outlined.Settings` | Cài đặt |
| Share | `Icons.Outlined.Share` | Chia sẻ |
| Copy | `Icons.Outlined.ContentCopy` | Sao chép |
| Delete | `Icons.Outlined.Delete` | Xóa |

## 6.12. Error States và Empty States

### 6.12.1. Error State Design
```kotlin
@Composable
fun ErrorState(
    error: AppError,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.error_illustration),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = error.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error.message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("Thử lại")
        }
    }
}
```

### 6.12.2. Empty State Design
```kotlin
@Composable
fun EmptyState(
    message: String = "Chưa có dữ liệu",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.empty_illustration),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .alpha(0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}
```

## 6.13. UI Components mới trong v1.0.3

### 6.13.1. Welcome Card Component
```kotlin
@Composable
fun WelcomeCard(
    onDismiss: () -> Unit,
    onQuickAction: (QuickAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Gradient header background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )
            
            // Welcome content
            Text(
                "Welcome to SumUp! 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Quick action chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionChip("Try Sample", Icons.Default.Description)
                QuickActionChip("Upload PDF", Icons.Default.PictureAsPdf)
                QuickActionChip("Scan Text", Icons.Default.CameraAlt)
            }
        }
    }
}
```

### 6.13.2. Enhanced Tooltip System
```kotlin
@Composable
fun ImprovedFeatureTooltip(
    tip: EnhancedFeatureTip,
    targetBounds: Rect,
    onDismiss: () -> Unit
) {
    val tooltipColors = when (tip.priority) {
        TooltipPriority.HIGH -> MaterialTheme.colorScheme.primaryContainer
        TooltipPriority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
        TooltipPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Popup(
        offset = DynamicPositioningEngine.calculate(targetBounds),
        onDismissRequest = onDismiss
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = tooltipColors),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            // Animated entrance
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                TooltipContent(tip)
            }
        }
    }
}
```

### 6.13.3. API Usage Dashboard
```kotlin
@Composable
fun ApiUsageDashboard(
    usage: ApiUsageData,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header với gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "API Usage Statistics",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            }
            
            // Usage metrics với animated progress
            UsageMetricRow(
                label = "Today",
                current = usage.dailyUsage,
                limit = usage.dailyLimit,
                animationSpec = tween(durationMillis = 1000)
            )
            
            // Visual chart
            LineChart(
                data = usage.last7DaysUsage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                lineColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            )
        }
    }
}
```

### 6.13.4. Logo System Design
```kotlin
// Logo variations với adaptive colors
@Composable
fun SumUpLogo(
    variant: LogoVariant = LogoVariant.GEOMETRIC,
    size: Dp = 48.dp,
    useDynamicColors: Boolean = true
) {
    val colors = if (useDynamicColors && Build.VERSION.SDK_INT >= 31) {
        dynamicLightColorScheme(LocalContext.current)
    } else {
        MaterialTheme.colorScheme
    }
    
    when (variant) {
        LogoVariant.GEOMETRIC -> LogoOption1Geometric(size, colors)
        LogoVariant.ABSTRACT -> LogoOption2Abstract(size, colors)
        LogoVariant.TYPOGRAPHY -> LogoOption3Typography(size, colors)
    }
}
```

### 6.13.5. Enhanced Dialog Design
```kotlin
@Composable
fun EnhancedApiKeyDialog(
    currentKey: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Icon với background màu
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Visual status indicator
                ApiKeyStatusIndicator(
                    hasKey = !currentKey.isNullOrEmpty(),
                    isValid = currentKey?.isValidApiKey() == true
                )
                
                // Input field với copy/paste support
                OutlinedTextField(
                    value = apiKeyState,
                    onValueChange = { apiKeyState = it },
                    label = { Text("API Key") },
                    visualTransformation = if (showKey) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showKey = !showKey }) {
                                Icon(
                                    if (showKey) Icons.Default.VisibilityOff 
                                    else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility"
                                )
                            }
                            IconButton(onClick = { pasteFromClipboard() }) {
                                Icon(Icons.Default.ContentPaste, "Paste")
                            }
                        }
                    }
                )
            }
        }
    }
}
```

## 6.14. Tóm tắt chương

Chương này đã trình bày chi tiết thiết kế giao diện người dùng của SumUp, bao gồm cả các cải tiến trong v1.0.3:

### Thiết kế cơ bản:
1. **Design Philosophy**: Material Design 3, Minimalism, User-Centered
2. **Visual Design**: Color system, Typography, Spacing
3. **Component Library**: Buttons, Cards, Inputs với detailed specs
4. **Screen Layouts**: Main, Result, History screens
5. **Animations**: Smooth transitions và micro-interactions
6. **Adaptive Design**: Phone, Tablet, Desktop layouts
7. **Accessibility**: WCAG compliance, screen reader support
8. **Dark Mode**: Full support với proper adjustments
9. **Error Handling**: Beautiful error và empty states

### Cải tiến UI/UX v1.0.3:
10. **Welcome Card**: Onboarding component với quick actions
11. **Enhanced Tooltips**: Dynamic positioning, priority-based styling
12. **API Usage Dashboard**: Beautiful data visualization
13. **Logo System**: 3 variants với adaptive colors
14. **Enhanced Dialogs**: Modern design với visual indicators

### Design Achievements:
- **Consistency**: Unified design language across all screens
- **Usability**: Intuitive với first-time user guidance
- **Accessibility**: Inclusive cho all users
- **Flexibility**: Adapts to different devices và themes
- **Delight**: Pleasant micro-interactions và animations
- **Production Quality**: Enterprise-grade UI components

Giao diện v1.0.3 không chỉ đẹp mắt mà còn thông minh, với feature discovery system giúp người dùng khám phá tính năng mới, dashboard trực quan cho API usage, và enhanced security UI cho API key management. Tất cả được thiết kế để tạo trải nghiệm tốt nhất cho người dùng Việt Nam và quốc tế.
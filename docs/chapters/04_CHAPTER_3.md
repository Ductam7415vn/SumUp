## CHƯƠNG 3: THIẾT KẾ VISUAL VÀ HỆ THỐNG THIẾT KẾ

**Mục tiêu Chương 3:**

Chương này chuyển đổi wireframes từ Chương 2 thành **high-fidelity mockups** hoàn chỉnh với:

1. **Design System** hoàn chỉnh (colors, typography, spacing, components)
2. **Visual designs** chi tiết cho tất cả màn hình
3. **Responsive layouts** cho nhiều kích thước màn hình
4. **Interactive prototypes** với animations và transitions

---

### 3.1. Xây dựng Hệ thống Thiết kế (Design System)

Design System là nền tảng của giao diện SumUp, đảm bảo **tính nhất quán** (consistency) và **khả năng mở rộng** (scalability) cho toàn bộ ứng dụng.

---

#### 3.1.1. Triết lý thiết kế

**Nguyên tắc thiết kế cốt lõi:**

**1. MATERIAL YOU - MODERN & PERSONAL**

SumUp áp dụng Material Design 3 (Material You) - hệ thống thiết kế mới nhất của Google:

✅ **Dynamic Color:**
- Tự động thích nghi với wallpaper của người dùng (Android 12+)
- Tạo ra trải nghiệm cá nhân hóa
- Fallback: Brand colors (Purple, Blue, Pink) cho thiết bị cũ

✅ **Adaptive Layouts:**
- Responsive cho mọi kích thước màn hình
- Bottom Nav (mobile) → Nav Rail (tablet) → Nav Drawer (desktop)

✅ **Accessibility First:**
- WCAG 2.1 Level AA compliance
- Contrast ratios: ≥4.5:1 (normal text), ≥3.0:1 (large text)
- Min touch target: 48dp × 48dp
- Screen reader support (TalkBack)

**2. CLEAN & MINIMALIST**

Giao diện tập trung vào nội dung, giảm thiểu distraction:

- **White space:** Sử dụng nhiều khoảng trống để tăng readability
- **Hierarchy rõ ràng:** Typography và color contrast tạo visual hierarchy
- **Progressive disclosure:** Ẩn advanced features, hiện khi cần

**3. FUNCTIONAL & INTUITIVE**

Mỗi element phải có mục đích rõ ràng:

- **Affordance:** Buttons trông có thể click được
- **Feedback:** Mọi action đều có visual/haptic feedback
- **Consistency:** Cùng một pattern cho cùng một action

**4. EMOTIONAL & DELIGHTFUL**

Tạo emotional connection với users:

- **Animations:** Smooth, natural, purposeful (not decorative)
- **Colors:** Vibrant nhưng không overwhelm
- **Illustrations:** Friendly, approachable (empty states, errors)
- **Micro-interactions:** Subtle animations khi hover, tap, swipe

---

#### 3.1.2. Bảng màu (Color Palette)

SumUp sử dụng **dual-theme system** với Light và Dark modes.

**A. LIGHT THEME PALETTE:**

```
┌─────────────────────────────────────────────────────────┐
│                  PRIMARY COLORS                         │
├─────────────────────────────────────────────────────────┤
│ Primary:           #6366F1  ████ (Brand Blue)          │
│ On Primary:        #FFFFFF  ████                        │
│ Primary Container: #E8E9FF  ████ (Light Blue)          │
│ On Primary Container: #000000 ████                      │
├─────────────────────────────────────────────────────────┤
│                  SECONDARY COLORS                       │
├─────────────────────────────────────────────────────────┤
│ Secondary:         #FF6B6B  ████ (Brand Pink)          │
│ On Secondary:      #FFFFFF  ████                        │
│ Tertiary:          #5B5FDE  ████ (Brand Purple)        │
│ On Tertiary:       #FFFFFF  ████                        │
├─────────────────────────────────────────────────────────┤
│                  SURFACE COLORS                         │
├─────────────────────────────────────────────────────────┤
│ Background:        #F8F9FE  ████ (Off-white)           │
│ Surface:           #FFFFFF  ████ (Pure white)          │
│ Surface Variant:   #F3F4F6  ████ (Light gray)          │
│ On Background:     #1A1D29  ████                        │
│ On Surface:        #1A1D29  ████                        │
│ On Surface Variant:#6B7280  ████                        │
├─────────────────────────────────────────────────────────┤
│                  SEMANTIC COLORS                        │
├─────────────────────────────────────────────────────────┤
│ Success:           #4CAF50  ████ (Green)               │
│ Success Container: #E8F5E9  ████                        │
│ On Success:        #FFFFFF  ████                        │
│                                                         │
│ Warning:           #FF9800  ████ (Orange)              │
│ Warning Container: #FFF3E0  ████                        │
│ On Warning:        #FFFFFF  ████                        │
│                                                         │
│ Info:              #2196F3  ████ (Blue)                │
│ Info Container:    #E3F2FD  ████                        │
│ On Info:           #FFFFFF  ████                        │
│                                                         │
│ Error:             #BA1A1A  ████ (Red)                 │
│ Error Container:   #FFDAD6  ████                        │
│ On Error:          #FFFFFF  ████                        │
└─────────────────────────────────────────────────────────┘
```

**B. DARK THEME PALETTE:**

```
┌─────────────────────────────────────────────────────────┐
│                  PRIMARY COLORS                         │
├─────────────────────────────────────────────────────────┤
│ Primary:           #5B5FDE  ████ (Brand Purple)        │
│ On Primary:        #FFFFFF  ████                        │
│ Primary Container: #4B4FCE  ████ (Dark Purple)         │
│ On Primary Container: #FFFFFF ████                      │
├─────────────────────────────────────────────────────────┤
│                  SURFACE COLORS                         │
├─────────────────────────────────────────────────────────┤
│ Background:        #121212  ████ (Almost black)        │
│ Surface:           #1E1E1E  ████ (Dark gray)           │
│ Surface Variant:   #2A2A2A  ████                        │
│ On Background:     #FFFFFF  ████                        │
│ On Surface:        #FFFFFF  ████                        │
│ On Surface Variant:#CAC4D0  ████                        │
├─────────────────────────────────────────────────────────┤
│                  SEMANTIC COLORS (Adjusted)             │
├─────────────────────────────────────────────────────────┤
│ Success:           #66BB6A  ████ (Lighter green)       │
│ Success Container: #2E7D32  ████                        │
│ Warning:           #FFA726  ████ (Lighter orange)      │
│ Warning Container: #F57C00  ████                        │
│ Info:              #42A5F5  ████ (Lighter blue)        │
│ Info Container:    #1976D2  ████                        │
│ Error:             #CF6679  ████ (Lighter red)         │
│ Error Container:   #93000A  ████                        │
└─────────────────────────────────────────────────────────┘
```

**C. NEUTRAL PALETTE (Used for both themes):**

```
Neutral10: #1A1D29  ████  (Darkest - text on light bg)
Neutral20: #2C303D  ████
Neutral30: #424654  ████
Neutral40: #5A5F70  ████
Neutral50: #73788C  ████  (Medium - secondary text)
Neutral60: #8E93A7  ████
Neutral70: #A9AEC1  ████
Neutral80: #C5C9DB  ████
Neutral90: #E2E5F0  ████
Neutral95: #F1F3F8  ████
Neutral99: #FBFCFE  ████  (Lightest - backgrounds)
```

**D. COLOR USAGE GUIDELINES:**

| **Use Case** | **Light Theme** | **Dark Theme** | **Purpose** |
|--------------|-----------------|----------------|-------------|
| **Primary Actions** | Primary (#6366F1) | Primary (#5B5FDE) | CTAs, FABs, selected tabs |
| **Text Input Focus** | Primary | Primary | Input borders when focused |
| **Links & Navigation** | Primary | Primary | Clickable elements |
| **Success States** | Success (#4CAF50) | Success (#66BB6A) | Completion, checkmarks |
| **Warnings** | Warning (#FF9800) | Warning (#FFA726) | Character limits, cautions |
| **Errors** | Error (#BA1A1A) | Error (#CF6679) | Form errors, failures |
| **Informational** | Info (#2196F3) | Info (#42A5F5) | Tips, help text |
| **Disabled Elements** | Neutral60 (60% opacity) | Neutral40 (38% opacity) | Inactive buttons, disabled inputs |

**E. DYNAMIC COLOR SUPPORT:**

- **Android 12+ (API 31+):** Sử dụng `dynamicLightColorScheme()` và `dynamicDarkColorScheme()`
- **Automatic extraction** từ wallpaper của user
- **Fallback:** Nếu device không support, sử dụng static brand colors

**Code Implementation:**
```kotlin
// Theme.kt
val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}
```

---

#### 3.1.3. Kiểu chữ (Typography)

SumUp sử dụng **Material 3 Typography System** với hệ thống phân cấp 13 levels.

**A. TYPOGRAPHY SCALE:**

```
┌──────────────────────────────────────────────────────────────────┐
│                      DISPLAY STYLES                              │
├────────────┬─────────┬─────────┬────────────┬────────────────────┤
│ Display    │ Size    │ Weight  │ Line Height│ Use Case          │
├────────────┼─────────┼─────────┼────────────┼────────────────────┤
│ Large      │ 57 sp   │ Normal  │ 64 sp      │ Hero sections     │
│ Medium     │ 45 sp   │ Normal  │ 52 sp      │ Onboarding titles │
│ Small      │ 36 sp   │ Normal  │ 44 sp      │ Large headings    │
└────────────┴─────────┴─────────┴────────────┴────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      HEADLINE STYLES                             │
├────────────┬─────────┬─────────┬────────────┬────────────────────┤
│ Headline   │ Size    │ Weight  │ Line Height│ Use Case          │
├────────────┼─────────┼─────────┼────────────┼────────────────────┤
│ Large      │ 32 sp   │ SemiBold│ 40 sp      │ Screen titles     │
│ Medium     │ 28 sp   │ SemiBold│ 36 sp      │ Section headings  │
│ Small      │ 24 sp   │ SemiBold│ 32 sp      │ Card titles       │
└────────────┴─────────┴─────────┴────────────┴────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      TITLE STYLES                                │
├────────────┬─────────┬─────────┬────────────┬────────────────────┤
│ Title      │ Size    │ Weight  │ Line Height│ Use Case          │
├────────────┼─────────┼─────────┼────────────┼────────────────────┤
│ Large      │ 22 sp   │ Medium  │ 28 sp      │ App bar titles    │
│ Medium     │ 16 sp   │ Medium  │ 24 sp      │ List item titles  │
│ Small      │ 14 sp   │ Medium  │ 20 sp      │ Subtitles         │
└────────────┴─────────┴─────────┴────────────┴────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      BODY STYLES                                 │
├────────────┬─────────┬─────────┬────────────┬────────────────────┤
│ Body       │ Size    │ Weight  │ Line Height│ Use Case          │
├────────────┼─────────┼─────────┼────────────┼────────────────────┤
│ Large      │ 16 sp   │ Normal  │ 24 sp      │ Main content      │
│ Medium     │ 14 sp   │ Normal  │ 20 sp      │ Secondary text    │
│ Small      │ 12 sp   │ Normal  │ 16 sp      │ Captions          │
└────────────┴─────────┴─────────┴────────────┴────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      LABEL STYLES                                │
├────────────┬─────────┬─────────┬────────────┬────────────────────┤
│ Label      │ Size    │ Weight  │ Line Height│ Use Case          │
├────────────┼─────────┼─────────┼────────────┼────────────────────┤
│ Large      │ 14 sp   │ Medium  │ 20 sp      │ Buttons           │
│ Medium     │ 12 sp   │ Medium  │ 16 sp      │ Tabs, chips       │
│ Small      │ 11 sp   │ Medium  │ 16 sp      │ Overlines         │
└────────────┴─────────┴─────────┴────────────┴────────────────────┘
```

**B. TYPOGRAPHY USAGE IN SCREENS:**

**Main Screen:**
```
TopAppBar Title:        Title Large (22sp, Medium)
Tab Labels:             Label Large (14sp, Medium)
Input Label:            Body Medium (14sp, Normal)
Input Text:             Body Large (16sp, Normal)
Character Counter:      Body Small (12sp, Normal) - color: Neutral60
Button Text:            Label Large (14sp, Medium)
Persona Chip:           Label Medium (12sp, Medium)
```

**Result Screen:**
```
Screen Title:           Headline Large (32sp, SemiBold)
Metrics Numbers:        Display Small (36sp, Normal)
Metrics Labels:         Body Small (12sp, Normal)
Section Headings:       Title Large (22sp, Medium)
Bullet Points:          Body Large (16sp, Normal) - lineHeight: 24sp
Paragraph Text:         Body Large (16sp, Normal) - lineHeight: 26sp
Timestamps:             Body Small (12sp, Normal) - color: Neutral50
```

**History Screen:**
```
Search Placeholder:     Body Medium (14sp, Normal)
Section Headers:        Title Small (14sp, Medium)
Item Titles:            Title Medium (16sp, Medium)
Item Metadata:          Body Small (12sp, Normal)
Filter Chips:           Label Medium (12sp, Medium)
Result Count:           Body Medium (14sp, Normal)
```

**C. FONT FAMILY:**

SumUp sử dụng **System Default Font** (Roboto on Android) để:

- Đảm bảo **performance** (không cần load custom fonts)
- **Native look & feel** trên Android
- **Multi-language support** out of the box
- **Variable font features** (Roboto Flex on Android 12+)

**Fallback hierarchy:**
```
Primary: Roboto (Android default)
Fallback: Sans-serif (system fallback)
```

**D. LETTER SPACING & LINE HEIGHT:**

Typography scales có **letter spacing** và **line height** tối ưu cho readability:

| **Style** | **Letter Spacing** | **Line Height** | **Ratio** |
|-----------|--------------------|-----------------|-----------|
| Display Large | -0.2 sp | 64 sp | 1.12 |
| Headline Large | 0 sp | 40 sp | 1.25 |
| Body Large | 0.5 sp | 24 sp | 1.5 |
| Body Medium | 0.25 sp | 20 sp | 1.43 |
| Label Large | 0.1 sp | 20 sp | 1.43 |

**Line height ratio 1.5** cho body text đảm bảo comfortable reading, especially cho văn bản dài (summaries).

---

#### 3.1.4. Hệ thống lưới và Khoảng cách (Grid & Spacing)

SumUp sử dụng **8dp Grid System** - standard của Material Design.

**A. SPACING TOKENS:**

```
┌────────────────────────────────────────────────────────┐
│              8DP SPACING SCALE                         │
├──────────────┬──────────┬──────────────────────────────┤
│ Token        │ Value    │ Use Case                     │
├──────────────┼──────────┼──────────────────────────────┤
│ spacingNone  │ 0 dp     │ No spacing                   │
│ spacingXxs   │ 2 dp     │ Divider thickness            │
│ spacingXs    │ 4 dp     │ Icon-text gap (tight)        │
│ spacingSm    │ 8 dp     │ Element spacing (compact)    │
│ spacingMd    │ 16 dp    │ Standard spacing             │
│ spacingLg    │ 24 dp    │ Section spacing              │
│ spacingXl    │ 32 dp    │ Large section breaks         │
│ spacingXxl   │ 48 dp    │ Screen padding (top/bottom)  │
│ spacingXxxl  │ 64 dp    │ Empty state spacing          │
└──────────────┴──────────┴──────────────────────────────┘
```

**B. PADDING TOKENS:**

```
┌────────────────────────────────────────────────────────┐
│              PADDING SCALE                             │
├──────────────┬──────────┬──────────────────────────────┤
│ Token        │ Value    │ Use Case                     │
├──────────────┼──────────┼──────────────────────────────┤
│ paddingXs    │ 4 dp     │ Chip padding                 │
│ paddingSm    │ 8 dp     │ Button vertical padding      │
│ paddingMd    │ 16 dp    │ Card padding, screen margins │
│ paddingLg    │ 24 dp    │ Dialog padding               │
│ paddingXl    │ 32 dp    │ Large container padding      │
└──────────────┴──────────┴──────────────────────────────┘
```

**C. COMPONENT-SPECIFIC SPACING:**

```
┌────────────────────────────────────────────────────────┐
│              COMPONENT DIMENSIONS                      │
├──────────────────────┬─────────┬───────────────────────┤
│ Component            │ Size    │ Notes                 │
├──────────────────────┼─────────┼───────────────────────┤
│ Min Touch Target     │ 48 dp   │ WCAG AA requirement   │
│ Icon (Small)         │ 16 dp   │ Inline icons          │
│ Icon (Medium)        │ 24 dp   │ Standard icons        │
│ Icon (Large)         │ 32 dp   │ Feature icons         │
│ Icon (XL)            │ 48 dp   │ Empty state icons     │
│ Button Height (Sm)   │ 36 dp   │ Compact buttons       │
│ Button Height (Md)   │ 48 dp   │ Standard buttons      │
│ Button Height (Lg)   │ 56 dp   │ Prominent CTAs        │
│ TextField Height     │ 56 dp   │ Outlined text fields  │
│ Chip Height          │ 32 dp   │ Filter chips          │
│ FAB Size             │ 56 dp   │ Floating action button│
│ Top Bar Height       │ 64 dp   │ Standard app bar      │
│ Bottom Nav Height    │ 80 dp   │ Navigation bar        │
└──────────────────────┴─────────┴───────────────────────┘
```

**D. BORDER RADIUS (ROUNDED CORNERS):**

```
┌────────────────────────────────────────────────────────┐
│              BORDER RADIUS SCALE                       │
├──────────────┬──────────┬──────────────────────────────┤
│ Token        │ Value    │ Use Case                     │
├──────────────┼──────────┼──────────────────────────────┤
│ radiusXs     │ 4 dp     │ Small chips                  │
│ radiusSm     │ 8 dp     │ Chips, small cards           │
│ radiusMd     │ 12 dp    │ Buttons, text fields         │
│ radiusLg     │ 16 dp    │ Cards, dialogs               │
│ radiusXl     │ 20 dp    │ Large cards                  │
│ radiusXxl    │ 28 dp    │ FAB, bottom sheets           │
│ radiusFull   │ 1000 dp  │ Pills, avatar (circular)     │
└──────────────┴──────────┴──────────────────────────────┘
```

Material 3 sử dụng **larger corner radii** so với Material 2 để tạo softer, friendlier look.

**E. ELEVATION (SHADOWS):**

```
┌────────────────────────────────────────────────────────┐
│              ELEVATION SCALE                           │
├──────────────┬──────────┬──────────────────────────────┤
│ Level        │ Value    │ Use Case                     │
├──────────────┼──────────┼──────────────────────────────┤
│ Level 0      │ 0 dp     │ Flat surfaces                │
│ Level 1      │ 2 dp     │ Cards (resting)              │
│ Level 2      │ 4 dp     │ FAB (resting)                │
│ Level 3      │ 8 dp     │ Bottom nav, app bars         │
│ Level 4      │ 12 dp    │ Nav drawer                   │
│ Level 5      │ 16 dp    │ Dialogs, menus               │
└──────────────┴──────────┴──────────────────────────────┘
```

Material 3 sử dụng **tonal elevation** (color shifts) thay vì shadows trong dark theme.

**F. GRID LAYOUT EXAMPLES:**

**Main Screen Grid (Compact - 360dp width):**
```
┌────────────────────────────────────────┐
│ 16dp                          16dp     │ ← Screen padding
│    ┌────────────────────────┐          │
│    │                        │          │
│    │   Text Input Area      │          │
│    │   (full width)         │          │
│    │                        │          │
│    └────────────────────────┘          │
│                                        │
│    8dp ← Vertical spacing              │
│                                        │
│    ┌────────────────────────┐          │
│    │   Persona Selector     │          │
│    └────────────────────────┘          │
│                                        │
│    16dp ← Section spacing              │
│                                        │
│    ┌────────────────────────┐          │
│    │   Summarize Button     │          │
│    └────────────────────────┘          │
└────────────────────────────────────────┘
```

**Result Screen Grid (with metrics):**
```
┌────────────────────────────────────────┐
│ 16dp                          16dp     │
│    ┌──────────┐  ┌──────────┐          │
│    │ Metric 1 │  │ Metric 2 │          │ ← 8dp gap between
│    └──────────┘  └──────────┘          │
│                                        │
│    8dp                                 │
│                                        │
│    ┌──────────┐  ┌──────────┐          │
│    │ Metric 3 │  │ Metric 4 │          │
│    └──────────┘  └──────────┘          │
│                                        │
│    24dp ← Section spacing              │
│                                        │
│    ┌────────────────────────┐          │
│    │                        │          │
│    │   Summary Content      │          │
│    │   (scrollable)         │          │
│    │                        │          │
│    └────────────────────────┘          │
└────────────────────────────────────────┘
```

---

#### 3.1.5. Biểu tượng (Iconography)

SumUp sử dụng **Material Icons Extended** - bộ icon official của Material Design.

**A. ICON LIBRARY:**

```
┌──────────────────────────────────────────────────────────────┐
│                  ICON CATEGORIES                             │
├────────────────┬─────────────────────────────────────────────┤
│ Category       │ Icons Used                                  │
├────────────────┼─────────────────────────────────────────────┤
│ Navigation     │ • ArrowBack                                 │
│                │ • Close                                     │
│                │ • Menu (hamburger)                          │
│                │ • MoreVert (3-dot menu)                     │
│                │ • Home, History, Settings                   │
│                │                                             │
│ Content        │ • Add, Remove                               │
│                │ • Edit, Delete, DeleteSweep                 │
│                │ • Search, FilterList                        │
│                │ • ContentCopy, Share                        │
│                │ • Favorite, FavoriteBorder                  │
│                │ • Star, StarBorder                          │
│                │ • CheckCircle, Cancel, Error                │
│                │                                             │
│ File/Document  │ • Description (document)                    │
│                │ • UploadFile                                │
│                │ • PictureAsPdf                              │
│                │ • Article                                   │
│                │                                             │
│ Action         │ • Send                                      │
│                │ • Download, Upload                          │
│                │ • Refresh                                   │
│                │ • Done, DoneAll                             │
│                │ • PlayArrow, Pause                          │
│                │                                             │
│ Communication  │ • Chat, Comment                             │
│                │ • Feedback                                  │
│                │ • Help, Info, Warning                       │
│                │                                             │
│ Toggle         │ • CheckBox, CheckBoxOutlineBlank            │
│                │ • RadioButtonChecked, RadioButtonUnchecked  │
│                │ • ToggleOn, ToggleOff                       │
│                │                                             │
│ Image/Media    │ • CameraAlt (OCR)                           │
│                │ • Image, PhotoCamera                        │
│                │ • Visibility, VisibilityOff                 │
│                │                                             │
│ Device         │ • Smartphone, Tablet, Laptop                │
│                │ • DarkMode, LightMode                       │
│                │ • Language                                  │
│                │                                             │
│ Social         │ • Person, People                            │
│                │ • School (Student persona)                  │
│                │ • Work (Professional persona)               │
│                │ • Science (Academic persona)                │
│                │ • Palette (Creative persona)                │
└────────────────┴─────────────────────────────────────────────┘
```

**B. ICON STYLES:**

Material Icons có 2 styles chính:

1. **Filled:** Solid icons - used for selected/active states
   - Example: `Icons.Filled.Favorite`, `Icons.Filled.Star`

2. **Outlined:** Stroke icons - used for unselected/inactive states
   - Example: `Icons.Outlined.FavoriteBorder`, `Icons.Outlined.StarBorder`

**Usage Pattern:**
```kotlin
// Toggle favorite
if (isFavorite) {
    Icon(Icons.Filled.Favorite, tint = Color.Red)
} else {
    Icon(Icons.Outlined.FavoriteBorder, tint = Color.Gray)
}
```

**C. ICON SIZING:**

```
┌────────────────────────────────────────────────────────┐
│              ICON SIZE GUIDE                           │
├──────────────┬──────────┬──────────────────────────────┤
│ Size         │ Value    │ Use Case                     │
├──────────────┼──────────┼──────────────────────────────┤
│ Small        │ 16 dp    │ Inline with text (body)      │
│ Medium       │ 24 dp    │ Standard UI icons            │
│ Large        │ 32 dp    │ Feature icons, FAB icons     │
│ Extra Large  │ 48 dp    │ Empty state illustrations    │
│ Hero         │ 64-96 dp │ Onboarding, splash screens   │
└──────────────┴──────────┴──────────────────────────────┘
```

**D. ICON COLOR USAGE:**

| **Context** | **Color (Light Theme)** | **Color (Dark Theme)** | **Opacity** |
|-------------|-------------------------|------------------------|-------------|
| **Active/Selected** | Primary (#6366F1) | Primary (#5B5FDE) | 100% |
| **Default** | Neutral30 | Neutral70 | 100% |
| **Disabled** | Neutral60 | Neutral40 | 38% |
| **Error** | Error (#BA1A1A) | Error (#CF6679) | 100% |
| **Success** | Success (#4CAF50) | Success (#66BB6A) | 100% |
| **Warning** | Warning (#FF9800) | Warning (#FFA726) | 100% |

**E. ICON ACCESSIBILITY:**

✅ **Content Descriptions:**
- Mọi icon đều có `contentDescription` cho screen readers
- Example: `Icon(Icons.Default.Search, contentDescription = "Search summaries")`

✅ **Touch Targets:**
- IconButton tối thiểu 48dp × 48dp (WCAG AA)
- Padding around icon để ensure touch area

✅ **Color Contrast:**
- Icons on backgrounds phải có contrast ≥3:1 (WCAG AA for graphics)

---

#### 3.1.6. Thư viện Components (Component Library)

SumUp có **component library** gồm 40+ reusable components.

**A. BUTTON COMPONENTS:**

**1. Primary Button (Filled):**
```
┌──────────────────────────┐
│     Summarize Text       │ ← Label Large (14sp, Medium)
└──────────────────────────┘
    Background: Primary
    Padding: 16dp horizontal, 12dp vertical
    Corner radius: 12dp
    Height: 48dp
    Min width: 64dp
```

**2. Secondary Button (Outlined):**
```
┌──────────────────────────┐
│      Try Again           │
└──────────────────────────┘
    Border: 1dp, Primary
    Background: Transparent
    Text color: Primary
    Same sizing as Primary
```

**3. Text Button:**
```
  Cancel  ← No border, no background
  Text color: Primary
  Padding: 8dp horizontal
```

**4. FAB (Floating Action Button):**
```
   ╭─────╮
   │  +  │ ← 56dp × 56dp
   ╰─────╯
   Background: Primary Container
   Icon color: On Primary Container
   Elevation: 4dp (resting), 8dp (pressed)
   Corner radius: 16dp (Material 3 style)
```

**B. INPUT COMPONENTS:**

**1. Text Field (Outlined):**
```
┌──────────────────────────┐
│ Label (floating)         │
│ ┌──────────────────────┐ │
│ │ Type or paste text...│ │ ← Body Large (16sp)
│ │                      │ │
│ └──────────────────────┘ │
│ 0/5,000 characters       │ ← Helper text
└──────────────────────────┘
    Border: 1dp (default), 2dp (focused)
    Border color: Neutral60 → Primary (focus)
    Corner radius: 12dp
    Padding: 16dp
```

**2. Auto-Save Text Field:**

Same as Text Field + Auto-save indicator:
```
┌──────────────────────────┐
│ Your text...       ●●●   │ ← Pulsing dots when saving
└──────────────────────────┘
    Shows "Draft saved" checkmark when done
```

**3. Search Bar:**
```
┌──────────────────────────┐
│ 🔍 Search summaries... ⨯ │
└──────────────────────────┘
    Background: Surface Variant
    No border (filled style)
    Corner radius: 28dp (pill shape)
    Height: 56dp
```

**C. CARD COMPONENTS:**

**1. Summary Card (History):**
```
╭────────────────────────────╮
│ ⭐ Meeting Notes           │ ← Title Medium (16sp)
│ Student • 10:30 AM         │ ← Body Small (12sp)
│ From: document.pdf         │ ← Metadata
│                            │
│ 3 bullet points • 234 words│
╰────────────────────────────╯
    Background: Surface
    Elevation: 2dp (Light), tonal (Dark)
    Corner radius: 16dp
    Padding: 16dp
    Swipeable (delete, favorite actions)
```

**2. Metrics Card:**
```
╭─────────────╮
│   1,234     │ ← Display Small (36sp)
│   Words     │ ← Body Small (12sp)
│     📝      │ ← Icon 32dp
╰─────────────╯
    2-column grid (2x2 for 4 metrics)
    Background: Surface Variant
    Corner radius: 12dp
    Padding: 16dp
```

**D. CHIP COMPONENTS:**

**1. Filter Chip (selected):**
```
┌───────────┐
│ Today  ⨯  │ ← Label Medium (12sp)
└───────────┘
    Background: Primary Container
    Text: On Primary Container
    Corner radius: 8dp
    Height: 32dp
    Padding: 12dp horizontal
```

**2. Persona Chip:**
```
┌──────────────────┐
│ 👨‍🎓 Student  ▾   │
└──────────────────┘
    Background: Surface Variant
    Icon + Text + Dropdown arrow
    Clickable → opens dropdown menu
```

**E. DIALOG COMPONENTS:**

**1. Standard Dialog:**
```
╭─────────────────────────────────╮
│ Title (Headline Small, 24sp)    │
│                                 │
│ Body text explaining the        │
│ action or providing info...     │
│                                 │
│           [Cancel] [Confirm]    │
╰─────────────────────────────────╯
    Max width: 400dp
    Corner radius: 28dp
    Padding: 24dp
    Elevation: 16dp
```

**2. Error Dialog:**
```
╭─────────────────────────────────╮
│          ⚠️                      │
│                                 │
│ No Internet Connection          │
│                                 │
│ Please check your connection    │
│ and try again.                  │
│                                 │
│               [Retry] [Cancel]  │
╰─────────────────────────────────╯
    Icon color: Error
    Same sizing as Standard Dialog
```

**F. LOADING COMPONENTS:**

**1. Circular Progress:**
```
      ⏳
    ╱   ╲
   ╱     ╲  ← Indeterminate spinner
   ╲     ╱     48dp diameter
    ╲   ╱      Primary color
      ⏲
```

**2. Linear Progress:**
```
├───────■■■■───────────────┤
    Thickness: 4dp
    Color: Primary
    Indeterminate animation
```

**3. Shimmer Loading:**
```
╭────────────────────────────╮
│ ▓▓▓▓░░░░░░░░░░░░░░░░░░░░  │ ← Animated gradient
│ ▓▓▓░░░░░░░░░░░░░░░░░░░░░  │
│ ▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░  │
╰────────────────────────────╯
    Used for skeleton screens
    Gradient animation: Left to right sweep
```

**G. EMPTY STATE COMPONENTS:**

```
       📝
   ╱ ╲   ╱ ╲

  No Summaries Yet

  Tap "+" to create your
  first summary

  [Start Summarizing]
```

Components:
- Illustration: 96dp icon (colored)
- Title: Headline Small (24sp)
- Body: Body Medium (14sp)
- CTA Button: Primary button

**H. NAVIGATION COMPONENTS:**

**1. Bottom Navigation (Compact):**
```
┌─────────────────────────────────┐
│   [🏠]    [📝]      [⚙️]        │
│   Home   History  Settings      │
└─────────────────────────────────┘
    Height: 80dp
    Icon: 24dp
    Label: Label Medium (12sp)
    Selected color: Primary
    Inactive color: Neutral50
```

**2. Navigation Rail (Medium/Expanded):**
```
┌─────┐
│ [☰] │ ← Menu
│     │
│ [🏠]│ ← Active (Primary Container bg)
│     │
│ [📝]│
│     │
│ [⚙️]│
│     │
└─────┘
    Width: 80dp
    Icons: 24dp
    Selected: Primary Container bg + Primary icon
    Inactive: Transparent bg + Neutral50 icon
```

**I. SNACKBAR COMPONENT:**

```
┌──────────────────────────────────────┐
│ Summary deleted         [Undo]       │
└──────────────────────────────────────┘
    Background: Inverse Surface (dark in light theme)
    Text: Inverse On Surface
    Corner radius: 4dp
    Min width: 344dp, Max width: 672dp
    Position: Bottom (with bottom nav offset)
```

**J. ACCESSIBILITY COMPONENTS:**

**1. Focus Indicator:**
```
╭────────────────────────────╮
│║  Focused Element         ║│ ← 2dp border
╰────────────────────────────╯
    Border: 2dp, Primary
    Offset: 2dp from element
```

**2. Screen Reader Announcements:**
- Live regions for dynamic content
- Content descriptions for all icons
- Semantic labels for buttons

---

<div style="page-break-after: always;"></div>

### 3.2. Thiết kế Giao diện Hoàn chỉnh (High-Fidelity Mockups)

Phần này trình bày **high-fidelity mockups** cho tất cả 7 màn hình chính với colors, typography, và spacing thực tế.

---

#### 3.2.1. Main Screen - Text Tab (High-Fidelity)

**Hình 3.1: Main Screen - Light Theme**

```
┌─────────────────────────────────────────┐
│ ☰  SumUp            [🔍] [👤]          │ TopAppBar
│                                         │ • Bg: Surface (#FFFFFF)
│                                         │ • Title: Title Large (22sp)
├─────────────────────────────────────────┤
│  ┌─────────┬─────────┬─────────┐       │
│  │█ Text █│ Document│   OCR   │       │ Tabs
│  └─────────┴─────────┴─────────┘       │ • Selected: Primary (#6366F1)
│                                         │ • Label: Label Large (14sp)
│  ┌───────────────────────────────────┐ │
│  │ 📝 Enter or paste your text      │ │ TextField
│  │                                   │ │ • Border: Neutral60
│  │                                   │ │ • Corner radius: 12dp
│  │                                   │ │ • Padding: 16dp
│  │                                   │ │ • Text: Body Large (16sp)
│  │                                   │ │
│  │                                   │ │
│  │                                   │ │
│  └───────────────────────────────────┘ │
│  📊 0/5,000 characters                  │ • Body Small (12sp)
│                                         │ • Color: Neutral50
│  ─── Summary Options ───                │ • Title Small (14sp)
│                                         │
│  Select Persona:                        │
│  ┌─────────────────────────────────┐   │
│  │ 👤 General              ▾       │   │ Dropdown Chip
│  └─────────────────────────────────┘   │ • Bg: Surface Variant
│                                         │ • Corner radius: 8dp
│  Summary Style:                         │
│  [Brief]  [█Standard█]  [Detailed]      │ Segmented Buttons
│                                         │ • Selected: Primary Container
│  ┌───────────────────────────────────┐ │
│  │       Summarize Text              │ │ Primary Button
│  └───────────────────────────────────┘ │ • Bg: Primary (#6366F1)
│                                         │ • Text: White
├─────────────────────────────────────────┤ • Height: 48dp
│   [🏠]    [📝]      [⚙️]                │ • Corner radius: 12dp
│   Home   History  Settings              │
└─────────────────────────────────────────┘ Bottom Nav
                                            • Height: 80dp
                                            • Selected: Primary
```

**Design Notes:**

**Colors Applied:**
- **Primary actions:** #6366F1 (Brand Blue)
- **Surface:** #FFFFFF (white cards on #F8F9FE background)
- **Text:** #1A1D29 (Neutral10)
- **Secondary text:** #73788C (Neutral50)
- **Borders:** #8E93A7 (Neutral60)

**Typography Applied:**
- **App title:** Title Large (22sp, Medium)
- **Tab labels:** Label Large (14sp, Medium)
- **Input text:** Body Large (16sp, Normal)
- **Helper text:** Body Small (12sp, Normal)
- **Button label:** Label Large (14sp, Medium)

**Spacing Applied:**
- **Screen padding:** 16dp horizontal
- **Element spacing:** 8dp vertical (tight), 16dp (standard)
- **Section spacing:** 24dp
- **Button height:** 48dp (meets min touch target)

---

#### 3.2.2. Main Screen - Document Tab (High-Fidelity)

**Hình 3.2: Main Screen - Document Tab**

```
┌─────────────────────────────────────────┐
│ ☰  SumUp            [🔍] [👤]          │
├─────────────────────────────────────────┤
│  ┌─────────┬─────────┬─────────┐       │
│  │  Text  │█Document█│   OCR   │       │
│  └─────────┴─────────┴─────────┘       │
│                                         │
│         📄                              │ Icon 96dp
│                                         │ • Color: Neutral60
│     Upload Document                     │ • Headline Small (24sp)
│                                         │
│  Support: PDF, DOCX, TXT, RTF          │ • Body Medium (14sp)
│  Max size: 10MB                         │ • Color: Neutral50
│                                         │
│  ┌───────────────────────────────────┐ │
│  │     Select Document               │ │ Outlined Button
│  └───────────────────────────────────┘ │ • Border: Primary
│                                         │ • Text: Primary
│  ────────── or ──────────              │ Divider with text
│                                         │ • Color: Neutral70
│  ┌───────────────────────────────────┐ │
│  │     Drag & Drop Here              │ │ Drop Zone
│  └───────────────────────────────────┘ │ • Dashed border
│                                         │ • Bg: Surface Variant
├─────────────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]                │
└─────────────────────────────────────────┘
```

**With File Selected:**

```
┌─────────────────────────────────────────┐
│ ☰  SumUp            [🔍] [👤]          │
├─────────────────────────────────────────┤
│  ┌─────────┬─────────┬─────────┐       │
│  │  Text  │█Document█│   OCR   │       │
│  └─────────┴─────────┴─────────┘       │
│                                         │
│  ╭───────────────────────────────────╮ │
│  │ 📑 research-paper.pdf             │ │ File Card
│  │                                   │ │ • Bg: Success Container
│  │ 15 pages • 2.3 MB                 │ │ • Corner radius: 16dp
│  │ ✓ Ready to process                │ │
│  │                            [⨯]    │ │ Remove button
│  ╰───────────────────────────────────╯ │
│                                         │
│  Select Persona:                        │
│  ┌─────────────────────────────────┐   │
│  │ 🎓 Academic             ▾       │   │
│  └─────────────────────────────────┘   │
│                                         │
│  Summary Style:                         │
│  [Brief]  [█Standard█]  [Detailed]      │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │     Process Document              │ │ Primary Button
│  └───────────────────────────────────┘ │ • Enabled (file selected)
│                                         │
├─────────────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]                │
└─────────────────────────────────────────┘
```

---

#### 3.2.3. OCR Screen (High-Fidelity)

**Hình 3.3: OCR Screen - Camera View**

```
┌─────────────────────────────────────────┐
│                                         │
│                                         │ Full-screen camera
│          ┌─────────────────┐           │
│          │                 │           │ Viewfinder overlay
│          │                 │           │ • White border 2dp
│          │   [ Text area ] │           │ • Corner radius: 8dp
│          │                 │           │ • Semi-transparent bg
│          │                 │           │
│          └─────────────────┘           │
│                                         │
│                                         │
│  ⚠️ Hold steady                         │ Hint text
│  Align text within frame                │ • Body Medium (14sp)
│                                         │ • Color: White
│                                         │ • Drop shadow
├─────────────────────────────────────────┤
│  [⨯]                          [Flash]  │ Top controls
│  Close                         [💡]     │ • IconButtons 48dp
│                                         │ • Color: White
│                                         │
│                                         │
│              ┌───────┐                  │ Bottom controls
│              │   📷  │                  │
│              └───────┘                  │ Capture button
│                                         │ • 80dp diameter
│   [🖼️]                      [🔄]       │ • White ring
│  Gallery                    Rotate      │ • Primary center
└─────────────────────────────────────────┘
```

**After Capture - Preview:**

```
┌─────────────────────────────────────────┐
│                                         │
│    [Captured image preview]             │ Full-width image
│                                         │
│                                         │
│  ✓ Text detected successfully           │ Success message
│  247 characters                         │ • Color: Success
│                                         │
│  ┌───────────────────────────────────┐ │
│  │ Meeting agenda for tomorrow...    │ │ Extracted text preview
│  │ 1. Review Q4 results              │ │ • Bg: Surface Variant
│  │ 2. Discuss new projects...        │ │ • Scrollable
│  └───────────────────────────────────┘ │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │      Use This Text                │ │ Primary Button
│  └───────────────────────────────────┘ │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │      Retake Photo                 │ │ Outlined Button
│  └───────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

---

#### 3.2.4. Processing Screen (High-Fidelity)

**Hình 3.4: Processing Screen**

```
┌─────────────────────────────────────────┐
│                                         │
│                                         │
│                                         │
│              ⚙️ ↻                       │ Animated icon
│            ╱  │  ╲                      │ • 96dp size
│           ○   │   ○                     │ • Primary color
│            ╲  │  ╱                      │ • Rotation animation
│              ↻ ⚙️                       │
│                                         │
│                                         │
│     Analyzing your text...              │ Status text
│                                         │ • Headline Small (24sp)
│  ├────────■■■■────────────────┤         │ • Color: Primary
│  45% Complete                           │
│                                         │ Progress bar
│                                         │ • Thickness: 4dp
│  Processing 1,234 words                 │ • Primary color
│  Using Academic persona                 │
│  Estimated: 3 seconds                   │ Details
│                                         │ • Body Medium (14sp)
│                                         │ • Color: Neutral50
│                                         │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │          Cancel                   │ │ Text Button
│  └───────────────────────────────────┘ │ • Color: Primary
│                                         │
└─────────────────────────────────────────┘
```

**States:**

1. **Preparing (0-20%):** "Preparing your text..."
2. **Analyzing (20-40%):** "Analyzing your text..."
3. **Generating (40-80%):** "Generating summary..."
4. **Finalizing (80-100%):** "Finalizing summary..."

Each state có animation và icon khác nhau.

---

#### 3.2.5. Result Screen (High-Fidelity)

**Hình 3.5: Result Screen**

```
┌─────────────────────────────────────────┐
│ ←  Summary Result                       │ TopAppBar
│                              [⋮]        │ • Back + Menu
├─────────────────────────────────────────┤
│                                         │
│  ╭───────────╮ ╭───────────╮           │ Metrics Grid (2x2)
│  │   1,234   │ │    247    │           │
│  │   Words   │ │   Words   │           │ • Surface Variant bg
│  │    📝     │ │    ✂️     │           │ • Corner radius: 12dp
│  ╰───────────╯ ╰───────────╯           │ • Padding: 16dp
│                                         │ • Numbers: Display Small
│  ╭───────────╮ ╭───────────╮           │
│  │    80%    │ │   2 min   │           │
│  │  Reduced  │ │  To Read  │           │
│  │    📊     │ │    ⏱️     │           │
│  ╰───────────╯ ╰───────────╯           │
│                                         │
│  ───────────────────────────            │ Divider (1dp)
│                                         │
│  Academic Summary  [🎓 Academic ▾]      │ Persona selector
│                                         │ • Headline Small
│  ───────────────────────────            │
│                                         │
│  📌 Key Points:                         │ Section heading
│                                         │ • Title Large (22sp)
│  • Machine learning revolutionizes      │ • SemiBold
│    AI capabilities through adaptive     │
│    algorithms and pattern recognition   │ Bullet points
│                                         │ • Body Large (16sp)
│  • Neural networks enable computers     │ • Line height: 24sp
│    to learn from experience without     │ • Spacing: 8dp between
│    explicit programming                 │
│                                         │
│  • Applications span healthcare,        │
│    finance, autonomous vehicles, and    │
│    natural language processing          │
│                                         │
│  [Show More]                            │ Expand button
│                                         │
│  ───────────────────────────            │
│                                         │
│  Created: Today, 2:45 PM                │ Metadata
│  From: Text input                       │ • Body Small (12sp)
│                                         │ • Color: Neutral50
├─────────────────────────────────────────┤
│                              ╭────╮    │ FAB (Speed Dial)
│                              │ ⋮  │    │ • Primary Container bg
│                              ╰────╯    │ • 56dp diameter
└─────────────────────────────────────────┘ • Elevation: 6dp

FAB Menu (expanded):
  ╭────────────────╮
  │ 📤 Share       │
  │ 📋 Copy        │
  │ 💾 Export      │
  │ ⭐ Favorite    │
  ╰────────────────╯
```

---

#### 3.2.6. History Screen (High-Fidelity)

**Hình 3.6: History Screen**

```
┌─────────────────────────────────────────┐
│ ←  History                  [🗑️]        │ TopAppBar
│                                         │ • Delete All icon
├─────────────────────────────────────────┤
│  ╭───────────────────────────────────╮ │
│  │ 🔍 Search summaries...        [⨯]│ │ Search bar
│  ╰───────────────────────────────────╯ │ • Surface Variant bg
│                                         │ • Corner radius: 28dp
│  [Today ▾] [All Personas] [Favorites]   │ • Height: 56dp
│                                         │
│  📊 12 results                          │ Filter chips
│                                         │ • Primary Container
│  ───────── Today ─────────              │ • Corner radius: 8dp
│                                         │
│  ╭───────────────────────────────────╮ │ Section header
│  │ ⭐ Meeting Notes                  │ │ • Title Small
│  │ Student • 10:30 AM                │ │ • SemiBold
│  │ From: document.pdf                │ │
│  │ 3 bullets • 234 words             │ │ Summary Card
│  ╰───────────────────────────────────╯ │ • Surface bg
│  │← Swipe for actions                  │ • Elevation: 2dp
│                                         │ • Corner radius: 16dp
│  ╭───────────────────────────────────╮ │ • Padding: 16dp
│  │ [ ] Research Paper                │ │
│  │ Academic • 9:15 AM                │ │ • Title: Title Medium
│  │ From: text input                  │ │ • Meta: Body Small
│  │ 5 bullets • 456 words             │ │ • Color: Neutral50
│  ╰───────────────────────────────────╯ │
│                                         │
│  ───────── Yesterday ─────────          │
│                                         │
│  ╭───────────────────────────────────╮ │
│  │ ⭐ Project Proposal               │ │
│  │ Professional • 3:45 PM            │ │
│  │ From: proposal.docx               │ │
│  │ 8 bullets • 789 words             │ │
│  ╰───────────────────────────────────╯ │
│                                         │
├─────────────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]                │
└─────────────────────────────────────────┘
```

**Swipe Actions:**

```
Swipe Left:
╭───────────────────────────────────╮
│ Meeting Notes        [🗑️] [⭐]    │ ← Delete & Favorite
╰───────────────────────────────────╯
   Background: Error (delete), Warning (favorite)
   Icons: 24dp, white

Swipe Right:
╭───────────────────────────────────╮
│ [📤]  Meeting Notes               │ ← Share
╰───────────────────────────────────╯
   Background: Info
   Icon: 24dp, white
```

---

#### 3.2.7. Settings Screen (High-Fidelity)

**Hình 3.7: Settings Screen**

```
┌─────────────────────────────────────────┐
│ ←  Settings                             │
├─────────────────────────────────────────┤
│                                         │
│  ───────── API Keys ─────────           │ Section dividers
│                                         │ • Title Small
│  ╭───────────────────────────────────╮ │ • Neutral70
│  │ 🔑 API Key Management             │ │
│  │ 2 keys • 1 active            [>] │ │ Navigation Card
│  ╰───────────────────────────────────╯ │ • Surface bg
│                                         │ • Elevation: 2dp
│  ───────── Appearance ─────────         │ • Tappable
│                                         │
│  Theme                                  │ Preference items
│  ┌───────┬───────┬───────┐             │ • Label: Body Large
│  │█Light█│ Dark  │ Auto  │             │ • Value: Body Medium
│  └───────┴───────┴───────┘             │
│                                         │ Segmented buttons
│  Language                               │ • Selected: Primary
│  ○ English  ● Tiếng Việt                │ • Height: 40dp
│                                         │
│  ───────── Preferences ─────────        │ Radio buttons
│                                         │ • 24dp size
│  Default Persona                        │ • Primary when selected
│  ╭───────────────────────────────────╮ │
│  │ General                       ▾   │ │ Dropdown
│  ╰───────────────────────────────────╯ │ • Surface Variant bg
│                                         │
│  Auto-save Drafts                       │
│  [ON ━━━━━━━●]                          │ Toggle switch
│                                         │ • Track: 52dp width
│  ───────── Data ─────────               │ • Thumb: 20dp
│                                         │ • ON: Primary
│  ╭───────────────────────────────────╮ │
│  │ Clear History                     │ │
│  │ 45 summaries                  [>] │ │
│  ╰───────────────────────────────────╯ │
│                                         │
│  ───────── About ─────────              │
│                                         │
│  Version 1.0.3                          │ Static text
│  Help & Support                    [>] │ • Body Medium
│  Privacy Policy                    [>] │ • Color: Neutral40
│                                         │
├─────────────────────────────────────────┤
│   [🏠]    [📝]      [⚙️]                │
└─────────────────────────────────────────┘
```

---

<div style="page-break-after: always;"></div>

### 3.3. Thiết kế Đáp ứng (Responsive Design)

SumUp sử dụng **adaptive layouts** để tối ưu trải nghiệm trên mọi kích thước màn hình.

---

#### 3.3.1. Breakpoints và Window Size Classes

Material 3 định nghĩa **3 window size classes:**

```
┌──────────────────────────────────────────────────────────┐
│              WINDOW SIZE CLASSES                         │
├─────────────┬──────────────┬─────────────────────────────┤
│ Class       │ Width Range  │ Typical Devices             │
├─────────────┼──────────────┼─────────────────────────────┤
│ Compact     │ < 600 dp     │ Phones (portrait)           │
│             │              │ • Small phones: 360dp       │
│             │              │ • Medium phones: 411dp      │
│             │              │ • Large phones: 428dp       │
│             │              │                             │
│ Medium      │ 600-839 dp   │ Tablets (portrait)          │
│             │              │ Phones (landscape)          │
│             │              │ Foldables (unfolded)        │
│             │              │                             │
│ Expanded    │ ≥ 840 dp     │ Tablets (landscape)         │
│             │              │ Desktops                    │
│             │              │ Large foldables             │
└─────────────┴──────────────┴─────────────────────────────┘
```

**Code Implementation:**
```kotlin
val windowSizeClass = calculateWindowSizeClass(activity)

when (windowSizeClass.widthSizeClass) {
    WindowWidthSizeClass.Compact -> CompactLayout()
    WindowWidthSizeClass.Medium -> MediumLayout()
    WindowWidthSizeClass.Expanded -> ExpandedLayout()
}
```

---

#### 3.3.2. Navigation Adaptation

**Compact (< 600dp) - Bottom Navigation:**

```
┌─────────────────────────────────┐
│ Screen Content                  │
│                                 │
│                                 │
│                                 │
│                                 │
│                                 │
├─────────────────────────────────┤
│  [🏠]    [📝]     [⚙️]          │ ← Bottom Nav
│  Home   History  Settings       │    80dp height
└─────────────────────────────────┘
```

**Medium (600-839dp) - Navigation Rail:**

```
┌─────┬───────────────────────────┐
│ [☰] │ Screen Content            │
│     │                           │
│ [🏠]│                           │ ← Nav Rail
│     │                           │    80dp width
│ [📝]│                           │
│     │                           │
│ [⚙️]│                           │
└─────┴───────────────────────────┘
```

**Expanded (≥ 840dp) - Permanent Navigation Drawer:**

```
┌──────────────┬────────────────────┐
│              │ Screen Content     │
│ ☰ SumUp      │                    │
│              │                    │
│ 🏠 Home      │                    │ ← Drawer
│              │                    │    256dp width
│ 📝 History   │                    │
│              │                    │
│ ⚙️ Settings  │                    │
│              │                    │
└──────────────┴────────────────────┘
```

**Decision Logic:**
```kotlin
@Composable
fun AdaptiveNavigation(
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            BottomNavigationBar(navController)
        }
        WindowWidthSizeClass.Medium -> {
            NavigationRail(navController)
        }
        WindowWidthSizeClass.Expanded -> {
            PermanentNavigationDrawer(navController)
        }
    }
}
```

---

#### 3.3.3. Layout Adaptation - Main Screen

**Compact Layout (Phones):**

```
┌─────────────────────────────────┐
│ ☰  SumUp         [🔍] [👤]     │ Full-width TopBar
├─────────────────────────────────┤
│ [Text] [Document] [OCR]         │ Tabs
├─────────────────────────────────┤
│                                 │
│ ┌─────────────────────────────┐│ Single column
│ │ Text Input                  ││ Full width
│ │                             ││
│ └─────────────────────────────┘│
│                                 │
│ Persona Selector                │
│                                 │
│ Summary Style                   │
│                                 │
│ [Summarize Button]              │
│                                 │
├─────────────────────────────────┤
│ Bottom Navigation               │
└─────────────────────────────────┘
```

**Medium Layout (Small Tablets):**

```
┌─────┬───────────────────────────────────┐
│     │ ☰  SumUp          [🔍] [👤]      │
│ Nav ├───────────────────────────────────┤
│     │ [Text] [Document] [OCR]           │
│Rail ├───────────────────────────────────┤
│     │                                   │
│ [🏠]│ ┌───────────────────────────────┐│
│     │ │ Text Input (wider)            ││
│ [📝]│ │                               ││
│     │ └───────────────────────────────┘│
│ [⚙️]│                                   │
│     │ ┌──────────┐  ┌─────────────┐   │ 2-column options
│     │ │ Persona  │  │   Style     │   │
│     │ └──────────┘  └─────────────┘   │
│     │                                   │
│     │ [Summarize Button - centered]    │
└─────┴───────────────────────────────────┘
```

**Expanded Layout (Large Tablets/Desktop):**

```
┌──────────┬────────────────────────────────────────────┐
│          │ ☰  SumUp              [🔍] [👤]           │
│ Nav      ├────────────────────────────────────────────┤
│ Drawer   │                                            │
│          │ ┌──────────────────────┬─────────────────┐│
│ 🏠 Home  │ │                      │ Quick Options   ││
│          │ │                      │                 ││
│ 📝       │ │   Text Input         │ ┌─────────────┐││
│ History  │ │   (max 600dp width)  │ │ Persona     │││
│          │ │                      │ └─────────────┘││
│ ⚙️       │ │                      │                 ││
│ Settings │ │                      │ ┌─────────────┐││
│          │ │                      │ │ Style       │││
│          │ │                      │ └─────────────┘││
│          │ └──────────────────────┤                 ││
│          │                        │ [Summarize]    ││
│          │                        └─────────────────┘│
└──────────┴────────────────────────────────────────────┘
```

**Key Differences:**

| **Aspect** | **Compact** | **Medium** | **Expanded** |
|------------|-------------|------------|--------------|
| **Max Content Width** | Full width | 600dp | 800dp (centered) |
| **Options Layout** | Stacked | 2-column | Sidebar panel |
| **Button Width** | Full width | Centered, max 400dp | Max 300dp |
| **Padding** | 16dp | 24dp | 32dp |
| **Navigation** | Bottom | Rail | Drawer |

---

#### 3.3.4. Layout Adaptation - Result Screen

**Compact (Phone):**

```
┌─────────────────────────────────┐
│ ← Summary Result        [⋮]    │
├─────────────────────────────────┤
│ ┌─────────┐ ┌─────────┐        │ 2x2 Metrics Grid
│ │ 1,234   │ │  247    │        │
│ │ Words   │ │ Words   │        │
│ └─────────┘ └─────────┘        │
│ ┌─────────┐ ┌─────────┐        │
│ │  80%    │ │ 2 min   │        │
│ └─────────┘ └─────────┘        │
│                                 │
│ ──────────────────────          │
│                                 │
│ Summary Content                 │ Full width
│ (scrollable)                    │
│                                 │
│                                 │
│                      ╭────╮    │ FAB
│                      │ ⋮  │    │
│                      ╰────╯    │
└─────────────────────────────────┘
```

**Medium (Tablet - Portrait):**

```
┌─────┬───────────────────────────────────┐
│     │ ← Summary Result          [⋮]    │
│ Nav ├───────────────────────────────────┤
│Rail │ ┌────┐ ┌────┐ ┌────┐ ┌────┐      │ 1x4 Metrics
│     │ │1234│ │247 │ │80% │ │2min│      │ (horizontal)
│ [🏠]│ └────┘ └────┘ └────┘ └────┘      │
│     │                                   │
│ [📝]│ ──────────────────────            │
│     │                                   │
│ [⚙️]│ Summary Content                   │
│     │ (wider, max 600dp)                │
│     │                                   │
│     │                                   │
│     │                        ╭────╮    │
│     │                        │ ⋮  │    │
│     │                        ╰────╯    │
└─────┴───────────────────────────────────┘
```

**Expanded (Desktop):**

```
┌──────────┬────────────────────────────────────────────┐
│          │ ← Summary Result              [⋮]         │
│ Nav      ├────────────────────────────────────────────┤
│ Drawer   │ ┌──────────────────────┬─────────────────┐│
│          │ │                      │ Quick Actions   ││
│ 🏠 Home  │ │                      │                 ││
│          │ │                      │ [📤 Share]      ││
│ 📝       │ │  Summary Content     │ [📋 Copy]       ││
│ History  │ │  (max 600dp)         │ [💾 Export]     ││
│          │ │                      │ [⭐ Favorite]   ││
│ ⚙️       │ │                      │                 ││
│ Settings │ │                      │ ─────────────   ││
│          │ │                      │                 ││
│          │ │                      │ Metrics:        ││
│          │ │                      │ • 1,234 words   ││
│          │ │                      │ • 247 words     ││
│          │ │                      │ • 80% reduced   ││
│          │ │                      │ • 2 min read    ││
│          │ └──────────────────────┴─────────────────┘│
└──────────┴────────────────────────────────────────────┘
```

**Adaptation Benefits:**

✅ **Compact:** Maximizes vertical space for content
✅ **Medium:** Horizontal metrics save vertical space
✅ **Expanded:** Side panel for quick actions eliminates need for FAB

---

#### 3.3.5. Dialog Adaptation

**Compact (Phone) - Full Screen:**

```
┌─────────────────────────────────┐
│ ⨯  Export Summary               │ Full-screen dialog
├─────────────────────────────────┤ (for complex forms)
│                                 │
│ Format:                         │
│ ○ Text  ○ Markdown  ● PDF       │
│                                 │
│ Include:                        │
│ ☑ Bullet points                 │
│ ☑ Metrics                       │
│ ☐ Original text                 │
│                                 │
│                                 │
│ ┌─────────────────────────────┐│
│ │ Export                      ││
│ └─────────────────────────────┘│
└─────────────────────────────────┘
```

**Medium/Expanded - Centered Dialog:**

```
        ╭─────────────────────────╮
        │ Export Summary      [⨯]│
        ├─────────────────────────┤
        │                         │
        │ Format:                 │
        │ ○ Text  ○ MD  ● PDF     │
        │                         │
        │ Include:                │
        │ ☑ Bullets  ☑ Metrics    │
        │ ☐ Original              │
        │                         │
        │    [Cancel]  [Export]   │
        ╰─────────────────────────╯
```

**Decision Logic:**
```kotlin
if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
    FullScreenDialog(...)
} else {
    StandardDialog(maxWidth = 400.dp)
}
```

---

#### 3.3.6. Responsive Typography

Typography scales slightly với screen size:

| **Element** | **Compact** | **Medium** | **Expanded** |
|-------------|-------------|------------|--------------|
| **Display Large** | 45 sp | 52 sp | 57 sp |
| **Headline Large** | 28 sp | 30 sp | 32 sp |
| **Body Large** | 16 sp | 16 sp | 16 sp |
| **Button** | 14 sp | 15 sp | 16 sp |

**Code:**
```kotlin
val scaleFactor = when (windowSizeClass.widthSizeClass) {
    WindowWidthSizeClass.Compact -> 0.9f
    WindowWidthSizeClass.Medium -> 1.0f
    WindowWidthSizeClass.Expanded -> 1.1f
    else -> 1.0f
}

Text(
    text = "Title",
    fontSize = (22.sp * scaleFactor)
)
```

---

<div style="page-break-after: always;"></div>

### 3.4. Tạo Mẫu thử Tương tác (Interactive Prototype)

Phần này mô tả animations, transitions, và micro-interactions tạo nên trải nghiệm mượt mà.

---

#### 3.4.1. Animation Principles

SumUp tuân theo **Material Motion** principles:

**1. PURPOSEFUL (Có mục đích):**
- Mọi animation phải có lý do rõ ràng
- Không animate chỉ vì decoration
- Guide user's attention đến thông tin quan trọng

**2. NATURAL (Tự nhiên):**
- Easing curves: Ease-in-out (không linear)
- Physics-based animations (spring, fling)
- Respect real-world physics (gravity, friction)

**3. QUICK (Nhanh):**
- Duration: 150-300ms (short to medium)
- Avoid slow animations (>500ms)
- Users can interrupt animations

**4. CLEAR (Rõ ràng):**
- One animation at a time
- Simple transitions over complex choreography
- Maintain spatial relationships

---

#### 3.4.2. Animation Duration Standards

```
┌──────────────────────────────────────────────────────────┐
│              ANIMATION DURATION GUIDE                    │
├────────────────┬──────────┬──────────────────────────────┤
│ Type           │ Duration │ Use Case                     │
├────────────────┼──────────┼──────────────────────────────┤
│ Micro          │ 100ms    │ Checkbox check, toggle       │
│ Short          │ 150ms    │ Fade in/out, color change    │
│ Medium         │ 300ms    │ Screen transitions, slide    │
│ Long           │ 500ms    │ Complex state changes        │
│ Extra Long     │ 800ms    │ Onboarding, celebrations     │
└────────────────┴──────────┴──────────────────────────────┘
```

**Code Implementation:**
```kotlin
object Dimensions {
    const val animationShort = 150
    const val animationMedium = 300
    const val animationLong = 500
}
```

---

#### 3.4.3. Screen Transitions

**Navigation Transitions:**

**1. Forward Navigation (Enter new screen):**

```
Screen A                Screen B
┌────────┐             ┌────────┐
│        │  ─────────> │░░░░░░░░│ Slide in from right
│  Main  │             │ Result │ + Fade in
│        │             │░░░░░░░░│
└────────┘             └────────┘
```

Animation:
- **Slide:** From right (100% → 0%)
- **Fade:** Alpha 0 → 1
- **Duration:** 300ms
- **Easing:** Ease-out

**2. Backward Navigation (Return to previous screen):**

```
Screen B                Screen A
┌────────┐             ┌────────┐
│░░░░░░░░│  <───────── │        │ Slide out to right
│ Result │             │  Main  │ + Fade out
│░░░░░░░░│             │        │
└────────┘             └────────┘
```

Animation:
- **Slide:** To right (0% → 100%)
- **Fade:** Alpha 1 → 0
- **Duration:** 250ms (slightly faster)
- **Easing:** Ease-in

**3. Tab Switching (Same level):**

```
Tab A                   Tab B
┌────────┐             ┌────────┐
│ Text   │  <───────>  │Document│ Crossfade
│ Input  │             │ Upload │
└────────┘             └────────┘
```

Animation:
- **Crossfade:** Fade out + Fade in simultaneously
- **Duration:** 150ms
- **No slide** (maintains spatial context)

**Code Example:**
```kotlin
AnimatedContent(
    targetState = currentScreen,
    transitionSpec = {
        if (targetState > initialState) {
            // Forward navigation
            slideInHorizontally { it } + fadeIn() with
            slideOutHorizontally { -it } + fadeOut()
        } else {
            // Backward navigation
            slideInHorizontally { -it } + fadeIn() with
            slideOutHorizontally { it } + fadeOut()
        }
    }
) { screen ->
    ScreenContent(screen)
}
```

---

#### 3.4.4. Component Animations

**A. BUTTON PRESS:**

```
State: Resting
┌──────────────┐
│ Summarize    │
└──────────────┘

       ↓ (Press down)

State: Pressed
┌──────────────┐
│ Summarize    │ ← Scale 0.95, Elevation 0dp
└──────────────┘

       ↓ (Release)

State: Resting
┌──────────────┐
│ Summarize    │ ← Scale 1.0, Elevation 2dp
└──────────────┘
```

Animation:
- **Scale:** 1.0 → 0.95 → 1.0
- **Elevation:** 2dp → 0dp → 2dp
- **Duration:** 100ms (down), 150ms (up)
- **Haptic feedback:** Light tap

**B. FAB EXPAND/COLLAPSE:**

```
State: Collapsed
         ╭────╮
         │ ⋮  │
         ╰────╯

         ↓ (Tap)

State: Expanded
  ╭────────────────╮
  │ 📤 Share       │ ← Slide in + Fade in
  │ 📋 Copy        │   (stagger: 50ms delay)
  │ 💾 Export      │
  │ ⭐ Favorite    │
  ╰────────────────╯
         ╭────╮
         │ ⨯  │ ← Icon rotates
         ╰────╯
```

Animation:
- **Items:** Slide from bottom + Fade in
- **Stagger:** 50ms between each item
- **Icon rotation:** 0° → 45° (⋮ → ⨯)
- **Duration:** 300ms
- **Easing:** Spring animation

**C. SWIPE ACTIONS:**

```
State: Resting
╭───────────────────────────────────╮
│ Meeting Notes                     │
╰───────────────────────────────────╯

       ↓ (Swipe left)

State: Revealing Actions
╭───────────────────────────────────╮
│ Meeting Notes      [🗑️] [⭐]      │ ← Background color
╰───────────────────────────────────╯   reveals gradually
```

Animation:
- **Drag:** Follow finger position
- **Background reveal:** Opacity 0 → 1 based on drag distance
- **Icon scale:** 0.8 → 1.0 when threshold reached
- **Haptic feedback:** Medium when threshold reached
- **Spring back:** If released before threshold

**D. LOADING STATES:**

**Shimmer Effect:**

```
Frame 1:
╭────────────────────────────╮
│ ▓▓▓▓░░░░░░░░░░░░░░░░░░░░  │ ← Gradient starts left
│ ▓▓▓░░░░░░░░░░░░░░░░░░░░░  │
╰────────────────────────────╯

Frame 2:
╭────────────────────────────╮
│ ░░▓▓▓▓░░░░░░░░░░░░░░░░░░  │ ← Gradient moves right
│ ░░▓▓▓░░░░░░░░░░░░░░░░░░░  │
╰────────────────────────────╯

Frame 3:
╭────────────────────────────╮
│ ░░░░░░▓▓▓▓░░░░░░░░░░░░░░  │ ← Continuous loop
│ ░░░░░░▓▓▓░░░░░░░░░░░░░░░  │
╰────────────────────────────╯
```

Animation:
- **Gradient:** Linear gradient (3 colors: base, highlight, base)
- **Translation:** -100% → 100% (infinite loop)
- **Duration:** 1500ms per cycle
- **Easing:** Linear

**Circular Progress:**

```
     ╱ ╲
    ╱   ╲  ← Indeterminate rotation
    ╲   ╱     Clockwise
     ╲ ╱
```

Animation:
- **Rotation:** 0° → 360° (infinite)
- **Duration:** 1000ms per rotation
- **Easing:** Linear

---

#### 3.4.5. Micro-Interactions

**A. FAVORITE TOGGLE:**

```
State: Unfavorited
   ☆ ← Outline star

       ↓ (Tap)

Animation:
   ☆  →  ★  →  ★
   (Scale 1.0 → 1.3 → 1.0)
   (Outline → Filled)
   (Gray → Red color)

State: Favorited
   ★ ← Filled star (red)
```

Animation details:
- **Duration:** 200ms
- **Scale bounce:** 1.0 → 1.3 → 1.0
- **Color:** Neutral60 → Error (red)
- **Icon change:** Outlined → Filled
- **Haptic:** Light tap

**B. CHARACTER COUNTER COLOR:**

```
Safe (< 4,000 chars):
📊 1,234/5,000 characters
   (Color: Success #4CAF50)

Warning (4,000-4,900):
📊 4,567/5,000 characters
   (Color: Warning #FF9800)

Error (> 5,000):
📊 5,123/5,000 characters
   (Color: Error #BA1A1A)
```

Transition animation:
- **Color:** Animated color transition
- **Duration:** 150ms
- **Pulsing:** When in error state (scale 1.0 ↔ 1.05)

**C. AUTO-SAVE INDICATOR:**

```
State 1: Typing
Your text...     (No indicator)

       ↓ (2 seconds idle)

State 2: Saving
Your text... ●●● (Pulsing dots)

       ↓ (Save complete)

State 3: Saved
Your text... ✓   (Checkmark, fades out after 2s)
```

Pulsing dots animation:
- **Opacity:** 0.3 → 1.0 → 0.3 (loop)
- **Stagger:** 100ms between dots
- **Duration:** 600ms per cycle

**D. PULL TO REFRESH:**

```
Pull Distance: 0dp
┌─────────────────┐
│ History         │
│                 │

Pull Distance: 50dp
┌─────────────────┐
│       ↓         │ ← Arrow grows
│ History         │

Pull Distance: 80dp (threshold)
┌─────────────────┐
│       ⟳         │ ← Arrow becomes spinner
│ History         │ ← Haptic feedback

Release → Refresh
```

Animation:
- **Arrow scale:** 0 → 1 (based on pull distance)
- **Rotation:** Arrow → Spinner (when threshold reached)
- **Spring back:** If released before threshold
- **Haptic:** Medium when threshold crossed

---

#### 3.4.6. State Change Animations

**A. EMPTY → CONTENT:**

```
State: Empty
       📝
   No Summaries Yet

       ↓ (First summary created)

Transition:
   (Fade out empty state)
   ↓
   (Fade in + Slide up first item)

State: Content
╭───────────────────────────────────╮
│ Meeting Notes                     │ ← Animates in
╰───────────────────────────────────╯
```

Animation:
- **Empty state:** Fade out (200ms)
- **Content:** Slide up + Fade in (300ms)
- **Delay:** 100ms between states

**B. LOADING → SUCCESS:**

```
State: Loading
      ⏳
   Processing...

       ↓ (Complete)

Transition:
   (Scale out spinner)
   ↓
   (Scale in checkmark)

State: Success
      ✓
   Summary Ready!
```

Animation:
- **Spinner:** Scale 1.0 → 0 (150ms)
- **Checkmark:** Scale 0 → 1.2 → 1.0 (300ms)
- **Color:** Primary → Success
- **Haptic:** Success notification

**C. ERROR SHAKE:**

```
Input with error:
┌───────────────────────────┐
│ Invalid input             │
└───────────────────────────┘

       ↓ (Shake animation)

Frames:
[0%]  ─────────────
[25%] ──→ (offset +8dp)
[50%] ←── (offset -8dp)
[75%] ──→ (offset +4dp)
[100%]─────────────
```

Animation:
- **Horizontal offset:** 0 → +8dp → -8dp → +4dp → 0
- **Duration:** 400ms
- **Repetitions:** 2 cycles
- **Haptic:** Error notification (heavy)

---

#### 3.4.7. Shared Element Transitions

**Summary Card → Detail Screen:**

```
History Screen:
╭───────────────────────────────────╮
│ Meeting Notes                     │ ← Card
│ Student • 10:30 AM                │
╰───────────────────────────────────╯

       ↓ (Tap to open)

Transition:
   (Card expands to full screen)
   (Title stays in place)
   (Content fades in)

Result Screen:
┌─────────────────────────────────────┐
│ ← Meeting Notes                     │ ← Same title
│                                     │
│ (Full content revealed)             │
└─────────────────────────────────────┘
```

Animation:
- **Card expansion:** Bounds animate from card size to full screen
- **Title position:** Stays anchored, moves minimally
- **Content fade:** Alpha 0 → 1
- **Duration:** 400ms
- **Easing:** Ease-in-out

**Code:**
```kotlin
SharedTransitionLayout {
    AnimatedContent(...) {
        // History Screen
        Card(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState("summary-${id}"),
                animatedVisibilityScope = this
            )
        )

        // Result Screen
        Column(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState("summary-${id}"),
                animatedVisibilityScope = this
            )
        )
    }
}
```

---

#### 3.4.8. Predictive Back Gesture (Android 14+)

```
State: Normal
┌─────────────────────────────────┐
│ Screen Content                  │
│                                 │
└─────────────────────────────────┘

       ↓ (Swipe from left edge)

State: Peeking
┌─────────────────────────────────┐
│░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░│ ← Current screen scales
│░░░░░░░ Screen Content ░░░░░░░░░░│    & fades (follow gesture)
│░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░│
└─────────────────────────────────┘
┌──────  ← Previous screen visible

       ↓ (Release to complete)

State: Previous Screen
┌─────────────────────────────────┐
│ Previous Screen                 │
│                                 │
└─────────────────────────────────┘
```

Animation:
- **Scale:** 1.0 → 0.9 (follows gesture)
- **Alpha:** 1.0 → 0.5
- **Corner radius:** 0dp → 16dp (edges round)
- **Spring animation:** If released mid-gesture

---

#### 3.4.9. Accessibility Considerations

**Reduce Motion:**

Users có thể enable "Reduce motion" in system settings. SumUp respects this:

```kotlin
val context = LocalContext.current
val areAnimationsDisabled = remember {
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    ) == 0f
}

AnimatedVisibility(
    visible = isVisible,
    enter = if (areAnimationsDisabled) {
        EnterTransition.None
    } else {
        fadeIn() + slideInVertically()
    }
)
```

**Alternative behaviors when reduce motion enabled:**
- Screen transitions: Instant (no slide)
- Loading: Simple fade instead of shimmer
- Micro-interactions: Immediate state change
- Haptic feedback: Still enabled (separate setting)

---

**Kết luận Section 3.4:**

✅ **Animations** được thiết kế purposeful, natural, quick, và clear

✅ **Transitions** smooth giữa screens và states

✅ **Micro-interactions** tạo delight và provide feedback

✅ **Accessibility** được đảm bảo với reduce motion support

---

<div style="page-break-after: always;"></div>

**Kết luận Chương 3:**

Chương 3 đã hoàn thành việc thiết kế **Visual UI và Design System** cho SumUp:

✅ **Design System (3.1):**
- Triết lý thiết kế: Material You, Clean, Functional, Delightful
- Color palette: Light/Dark themes với 40+ semantic colors
- Typography: 13-level scale với Material 3
- Spacing: 8dp grid system với 20+ tokens
- Iconography: Material Icons Extended với 100+ icons
- Components: 40+ reusable components

✅ **High-Fidelity Mockups (3.2):**
- 7 màn hình với colors, typography, spacing thực tế
- All states: Normal, loading, error, empty, success
- Annotations chi tiết cho mọi design decision

✅ **Responsive Design (3.3):**
- 3 breakpoints: Compact, Medium, Expanded
- Adaptive navigation: Bottom Nav → Nav Rail → Nav Drawer
- Layout adaptation cho từng screen size
- Typography scaling

✅ **Interactive Prototypes (3.4):**
- Animation principles: Material Motion
- Screen transitions với timing và easing
- Component animations: Buttons, FAB, Cards, Loading
- Micro-interactions: Favorite, Counter, Auto-save
- Accessibility: Reduce motion support

**Design System metrics:**
- **40+ components** fully documented
- **50+ color tokens** for light/dark themes
- **13 typography levels** with semantic names
- **20+ spacing tokens** on 8dp grid
- **100+ icons** from Material Icons
- **3 breakpoints** for responsive design
- **20+ animations** with durations and easing

Với Design System và Visual UI hoàn chỉnh, **Chương 4** sẽ cover **Testing và Evaluation** để đảm bảo design thực sự hoạt động tốt cho users.

---

<div style="page-break-after: always;"></div>


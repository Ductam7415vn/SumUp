# Navigation Drawer Design Specification

## 🎯 Design Vision
Thiết kế Navigation Drawer theo phong cách modern AI apps (ChatGPT, Claude, Gemini) với focus vào:
- **Lịch sử tóm tắt** dễ truy cập
- **Tổ chức thông minh** theo thời gian/chủ đề
- **Trải nghiệm mượt mà** với animations tinh tế

## 📐 Layout Structure

### 1. **Drawer Header (240dp height)**
```
┌─────────────────────────────┐
│  [Avatar]   SumUp Pro       │ <- User tier
│  user@email.com             │
│                             │
│  ┌─────────┬──────────┐    │
│  │ 156     │ 12.5 MB  │    │ <- Stats
│  │ Summaries│ Used     │    │
│  └─────────┴──────────┘    │
└─────────────────────────────┘
```

### 2. **Quick Actions Section**
```
┌─────────────────────────────┐
│ [+] New Summary             │ <- Primary action
│ ─────────────────────────── │
│ [📷] Scan Text              │
│ [📄] Upload PDF             │
│ [✏️] Paste Text            │
└─────────────────────────────┘
```

### 3. **History Section (Expandable)**
```
┌─────────────────────────────┐
│ 📅 Today (3)                │
│   └─ Marketing Report       │
│   └─ Meeting Notes         │
│   └─ Article Summary       │
│                             │
│ 📅 Yesterday (5)            │
│   └─ [collapsed]           │
│                             │
│ 📅 This Week (12)           │
│   └─ [collapsed]           │
│                             │
│ 📅 Older                    │
│   └─ [Show more...]        │
└─────────────────────────────┘
```

### 4. **Categories/Tags Section**
```
┌─────────────────────────────┐
│ 🏷️ Categories              │
│   • Work (45)              │
│   • Personal (23)          │
│   • Research (67)          │
│   • Study (34)             │
│   [+ Add Category]         │
└─────────────────────────────┘
```

### 5. **Bottom Section**
```
┌─────────────────────────────┐
│ [⚙️] Settings              │
│ [💎] Upgrade to Premium    │
│ [🌙] Dark Mode Toggle      │
│ ─────────────────────────── │
│ [?] Help & Feedback        │
│ [↗️] Export Data           │
└─────────────────────────────┘
```

## 🎨 Visual Design

### Colors & Theming
```kotlin
// Light Mode
drawerBackground = Color(0xFFFAFBFC)
headerBackground = Gradient(primary to primaryLight)
itemBackground = Color.White
selectedItemBackground = primaryContainer.copy(alpha = 0.12f)

// Dark Mode
drawerBackground = Color(0xFF1A1B1E)
headerBackground = Gradient(primaryDark to surface)
itemBackground = surfaceVariant
selectedItemBackground = primaryContainer.copy(alpha = 0.24f)
```

### Typography
- **Header Name**: HeadlineSmall, Bold
- **Section Headers**: LabelLarge, Medium
- **List Items**: BodyLarge, Regular
- **Metadata**: BodySmall, Color.onSurfaceVariant

### Spacing & Dimensions
- **Drawer Width**: 320dp (80% of screen max)
- **Header Height**: 240dp
- **Item Height**: 56dp
- **Section Padding**: 16dp horizontal, 8dp vertical
- **Icon Size**: 24dp
- **Corner Radius**: 16dp for cards, 28dp for drawer

## 🎭 Animations & Interactions

### Drawer Animation
```kotlin
// Slide + Fade animation
slideInHorizontally(initialOffsetX = { -it }) + 
fadeIn(animationSpec = tween(300))

// Scrim animation
animateFloatAsState(
    targetValue = if (drawerOpen) 0.5f else 0f,
    animationSpec = tween(300)
)
```

### Item Interactions
1. **Hover/Press**: Scale(0.98f) + Elevation change
2. **Selection**: Animated background color transition
3. **Swipe Actions**: Delete/Archive summaries with swipe
4. **Long Press**: Multi-select mode for bulk actions

## 📱 Responsive Behavior

### Phone (< 600dp)
- Full overlay drawer
- Swipe from left edge to open
- Tap scrim to close

### Tablet (>= 600dp)
- Rail navigation by default
- Expandable to full drawer
- Persistent rail shows icons only

### Desktop (>= 1200dp)
- Always visible sidebar
- Collapsible to rail
- Keyboard shortcuts support

## 🔧 Implementation Components

### 1. **NavigationDrawer.kt**
Main drawer component with:
- DrawerState management
- Gesture handling
- Animation orchestration

### 2. **DrawerHeader.kt**
User profile section with:
- Avatar/initial display
- Stats visualization
- Quick upgrade CTA

### 3. **DrawerHistorySection.kt**
Collapsible history with:
- Time-based grouping
- Search functionality
- Smart sorting (recent, frequent)

### 4. **DrawerMenuItem.kt**
Reusable item component with:
- Icon + Text layout
- Badge support
- Selection state
- Ripple effects

## 🎯 User Flows

### Primary Flow
1. Swipe from left edge → Drawer opens
2. Tap history item → Navigate to summary
3. Drawer auto-closes → Show selected content

### Quick Actions
1. Tap "New Summary" → Close drawer → Show input
2. Tap "Scan Text" → Close drawer → Open camera
3. Long press summary → Multi-select → Bulk actions

### Search Flow
1. Pull down in history → Show search bar
2. Type to filter → Real-time results
3. Tap result → Navigate to summary

## 🚀 Advanced Features

### 1. **Smart Grouping**
- Auto-categorize by content type
- Time-based sections
- Frequency-based ordering

### 2. **Quick Preview**
- Long press for preview popup
- Show first 100 chars
- Quick actions (share, delete)

### 3. **Sync Indicator**
- Show sync status in header
- Offline badge for items
- Pull-to-refresh gesture

### 4. **Personalization**
- Remember collapsed sections
- Custom category colors
- Preferred sort order

## 📊 Analytics Events
```kotlin
// Track drawer usage
"drawer_opened" -> source: gesture/button
"drawer_item_clicked" -> item_type, position
"drawer_search_used" -> query_length, results_count
"drawer_category_created" -> category_name
```

## ⚡ Performance Optimizations

1. **Lazy Loading**: Load history items on scroll
2. **Image Caching**: Cache user avatars
3. **State Persistence**: Save drawer state
4. **Debounced Search**: 300ms delay
5. **Virtualization**: For long lists

## 🎨 Accessibility

- **Screen Reader**: Full content descriptions
- **Keyboard Navigation**: Tab through items
- **Focus Management**: Trap focus in drawer
- **High Contrast**: Support system settings
- **Motion Reduction**: Respect user preference
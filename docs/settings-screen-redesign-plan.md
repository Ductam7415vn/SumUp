# SettingsScreen Redesign Plan

## Current State: 4/10
- Basic Material 3 implementation
- No custom styling or animations
- Plain cards and dialogs
- Missing visual polish

## Target State: 9/10
Transform SettingsScreen into a modern, visually stunning experience that matches ResultScreen's quality.

## Design Concept

### 1. **Profile Header Section** 🎯
- Animated gradient background (like ResultScreen)
- User avatar with edit button
- Name and email display
- Stats summary (total summaries, time saved)
- Animated on scroll (parallax effect)

### 2. **Enhanced Preference Cards** 💎
- Glassmorphism effect with blur
- Animated gradients on hover/press
- Smooth spring animations
- 3D transform on interaction
- Custom icons with animations

### 3. **Visual Improvements** ✨
- Animated page transitions
- Skeleton loading with shimmer
- Custom themed dialogs with blur backgrounds
- Floating action patterns
- Micro-interactions everywhere

## Implementation Phases

### Phase 1: Visual Foundation (2-3 hours)
1. **Profile Header**
   - Animated gradient background
   - Avatar with edit functionality
   - User stats display
   - Parallax scroll effect

2. **Enhanced Cards**
   - Glassmorphism design
   - Gradient borders
   - Shadow elevations
   - Hover/press animations

3. **Typography & Colors**
   - Custom font weights
   - Gradient text for headers
   - Improved color hierarchy

### Phase 2: Interactions & Animations (2-3 hours)
1. **Preference Items**
   - Spring animations on tap
   - Animated icons
   - Ripple effects with gradients
   - Smooth state transitions

2. **Custom Dialogs**
   - Blur background overlay
   - Slide/fade animations
   - Custom styled buttons
   - Animated selection states

3. **Haptic Feedback**
   - Light taps for selections
   - Success vibrations
   - Error feedback
   - Swipe haptics

### Phase 3: Advanced Features (2 hours)
1. **Search & Filter**
   - Animated search bar
   - Real-time filtering
   - Highlighted results

2. **Achievements Integration**
   - Settings milestones
   - Visual rewards
   - Progress tracking

3. **Export/Import**
   - Visual file picker
   - Progress animations
   - Success celebrations

## Key Components to Create

### 1. ProfileHeaderSection
```kotlin
@Composable
fun ProfileHeaderSection(
    user: UserProfile,
    stats: UserStats,
    onEditProfile: () -> Unit,
    scrollOffset: Float
)
```
- Animated gradient background
- Parallax avatar
- Blur effect on scroll
- Stats with animated numbers

### 2. AnimatedPreferenceCard
```kotlin
@Composable
fun AnimatedPreferenceCard(
    title: String,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
)
```
- Glassmorphism effect
- Spring animations
- Gradient borders
- 3D transforms

### 3. EnhancedSettingItem
```kotlin
@Composable
fun EnhancedSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit
)
```
- Animated icon
- Gradient text option
- Smooth transitions
- Haptic feedback

### 4. BlurDialog
```kotlin
@Composable
fun BlurDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
)
```
- Background blur
- Smooth animations
- Custom styling
- Gesture dismissal

## Visual Mockup

```
┌─────────────────────────────────┐
│   🌈 Animated Gradient Header   │
│ ┌─────┐                         │
│ │ 👤  │  John Doe              │
│ └─────┘  john@example.com      │
│                                 │
│ 📊 156 Summaries | 🕐 48h saved │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ 🎨 Appearance      [Glassmorphic]│
├─────────────────────────────────┤
│ 🌓 Theme           System    >  │
│ 🎨 Dynamic Colors  [━━━━●]     │
│ 📱 Compact Mode    [━━━━○]     │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ 📝 Summarization                │
├─────────────────────────────────┤
│ 📏 Default Length  Medium    >  │
│ 🌍 Language        English   >  │
│ 🎯 Auto-detect     [━━━━●]     │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ 💾 Data & Privacy               │
├─────────────────────────────────┤
│ 📊 Storage         1.2 MB       │
│ 🗑️  Clear Data                > │
│ 📤 Export          JSON/CSV  >  │
│ 🔐 Privacy         Protected    │
└─────────────────────────────────┘
```

## Color Palette
- Primary gradient: Blue → Purple (matching app theme)
- Glass effect: White 10% with blur
- Accent: Vibrant gradients for interactions
- Text: High contrast with subtle shadows

## Animation Specs
- Page transition: 300ms ease-out
- Card hover: Spring(dampingRatio = 0.8)
- Dialog appear: 250ms fade + scale
- Icon animations: 200ms rotation/scale
- Haptic timing: Immediate on touch

## Expected Improvements
- From 4/10 → 9/10 visual score
- Professional, modern appearance
- Delightful interactions
- Consistent with ResultScreen quality
- Enhanced user engagement
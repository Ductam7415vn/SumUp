# Haptic & Ripple Effects Guide for SumUp App

## Overview

The SumUp app already includes a comprehensive haptic feedback system with built-in ripple effects. All interactive components automatically provide both visual (ripple) and tactile (haptic) feedback.

## Available Haptic Components

### 1. **HapticButton**
- Standard button with ripple effect and haptic feedback
- Scale animation on press (0.95x)
- Default haptic type: `CLICK`

```kotlin
HapticButton(
    onClick = { /* action */ },
    hapticType = HapticFeedbackType.SUCCESS // optional
) {
    Text("Submit")
}
```

### 2. **HapticIconButton**
- Icon button with ripple and scale animation
- Scale animation on press (0.9x)
- Perfect for toolbar actions

```kotlin
HapticIconButton(
    onClick = { /* action */ }
) {
    Icon(Icons.Default.Menu, contentDescription = "Menu")
}
```

### 3. **HapticCard**
- Card with ripple effect and optional long-click
- Press animation with elevation changes
- Supports both click and long-click with different haptics

```kotlin
HapticCard(
    onClick = { /* navigate */ },
    onLongClick = { /* show options */ }
) {
    // Card content
}
```

### 4. **HapticFloatingActionButton**
- FAB with enhanced ripple and bounce effect
- Scale animation (0.92x)
- Prominent haptic feedback

```kotlin
HapticFloatingActionButton(
    onClick = { /* primary action */ }
) {
    Icon(Icons.Default.Add, contentDescription = "Add")
}
```

### 5. **HapticSwitch**
- Switch with different haptics for on/off
- Success haptic when turned on
- Tick haptic when turned off

```kotlin
HapticSwitch(
    checked = isEnabled,
    onCheckedChange = { isEnabled = it }
)
```

### 6. **HapticCheckbox**
- Checkbox with success/tick haptics
- Similar to switch behavior

```kotlin
HapticCheckbox(
    checked = isSelected,
    onCheckedChange = { isSelected = it }
)
```

### 7. **HapticSlider**
- Slider with tick feedback during drag
- Click feedback when released

```kotlin
HapticSlider(
    value = progress,
    onValueChange = { progress = it }
)
```

### 8. **HapticFilterChip**
- Filter chip with ripple and haptic
- Scale animation on press

```kotlin
HapticFilterChip(
    selected = isActive,
    onClick = { /* toggle */ },
    label = { Text("Filter") }
)
```

### 9. **HapticRadioButton**
- Radio button with selection haptics

```kotlin
HapticRadioButton(
    selected = isSelected,
    onClick = { /* select */ }
)
```

### 10. **hapticClickable Modifier**
- For custom components
- Adds ripple and haptic to any composable

```kotlin
Box(
    modifier = Modifier
        .hapticClickable { /* action */ }
) {
    // Custom content
}
```

## Haptic Feedback Types

The app uses different haptic patterns for different interactions:

- **CLICK** - Standard button/tap feedback
- **LONG_PRESS** - Long press actions
- **SUCCESS** - Positive actions (save, complete)
- **ERROR** - Error states
- **WARNING** - Warning actions
- **TICK** - Light feedback for toggles
- **NAVIGATION** - Navigation actions
- **DRAG** - Drag interactions
- **SWIPE** - Swipe gestures
- **SELECTION** - Item selection

## Best Practices

### 1. Use Appropriate Haptic Types
```kotlin
// Success action
HapticButton(
    onClick = { saveData() },
    hapticType = HapticFeedbackType.SUCCESS
) { Text("Save") }

// Destructive action
HapticButton(
    onClick = { deleteItem() },
    hapticType = HapticFeedbackType.WARNING,
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error
    )
) { Text("Delete") }
```

### 2. Combine with Animations
All haptic components include smooth animations:
- Scale animations for press states
- Elevation changes for depth
- Color transitions for state changes

### 3. Accessibility
- Haptic feedback enhances accessibility
- Works with TalkBack
- Respects system haptic settings

### 4. Performance
- Haptic components are optimized
- Use `remember` for interaction sources
- Animations use hardware acceleration

## Migration from Standard Components

Simply replace standard Material 3 components with their haptic equivalents:

```kotlin
// Before
Button(onClick = { }) { Text("Click") }

// After
HapticButton(onClick = { }) { Text("Click") }

// Before
Card(modifier = Modifier.clickable { }) { }

// After
HapticCard(onClick = { }) { }

// Before
IconButton(onClick = { }) { Icon(...) }

// After
HapticIconButton(onClick = { }) { Icon(...) }
```

## Custom Ripple Effects

The default ripple effects are already optimized for the app's design system. They automatically adapt to:
- Light/dark theme
- Material You dynamic colors
- Component surface colors

## Examples in the App

### MainScreen
- Input type selector uses `hapticClickable`
- Clear and paste buttons use `HapticButton`
- Menu icon uses `HapticIconButton`

### ResultScreen
- Action buttons use `HapticButton`
- Share FAB uses `HapticFloatingActionButton`
- Persona chips use `HapticFilterChip`

### SettingsScreen
- Toggle switches use `HapticSwitch`
- Setting items use `HapticCard`
- Back button uses `HapticIconButton`

## Troubleshooting

1. **No haptic feedback?**
   - Check device settings (Settings > Sound & vibration > Touch feedback)
   - Ensure haptic permission is granted

2. **Ripple not visible?**
   - Check if component has proper bounds
   - Ensure surface color contrasts with ripple

3. **Performance issues?**
   - Use `remember` for interaction sources
   - Avoid creating new instances in recomposition

## Summary

The SumUp app's haptic system provides:
- ✅ Automatic ripple effects on all interactive components
- ✅ Contextual haptic feedback
- ✅ Smooth press animations
- ✅ Theme-aware colors
- ✅ Accessibility support
- ✅ Optimal performance

No additional ripple implementation is needed - just use the existing `Haptic*` components!
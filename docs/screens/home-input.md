# Home/Input Screen - Technical Specification

## 🎯 Overview
The central hub of SumUp where users input text via typing, pasting, or triggering camera OCR. This screen handles 60% of user interactions and must be bulletproof.

## 📱 Layout Structure

### Portrait Layout (360-430dp width)
```
┌─────────────────────────────┐
│ StatusBar (edge-to-edge)     │
├─────────────────────────────┤
│ TopAppBar                    │ 56dp
│ [Logo] SumUp    [Settings]   │
├─────────────────────────────┤
│ ▼ Content (scrollable)       │
│ ┌─────────────────────────┐  │
│ │ OutlinedTextField        │  │ 16dp padding
│ │ minHeight: 120dp         │  │
│ │ "Paste or type your     │  │
│ │  text here..."           │  │
│ │                          │  │
│ │ expandable to full      │  │
│ │ screen height           │  │
│ └─────────────────────────┘  │
│                              │
│ Helper Section               │ 8dp gap
│ [0/5000 characters]    [?]   │ 12sp
│                              │
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░  │ Spacer
├─────────────────────────────┤
│ Bottom Action Bar            │ 64dp
│ [Clear All]    [Summarize →] │ 16dp padding
├─────────────────────────────┤
│ BottomNavBar                 │ 80dp
│ [Home] [History]    (FAB)    │
└─────────────────────────────┘
```

## 🛠️ Component Implementation

### Main TextField
```kotlin
OutlinedTextField(
    value = textState,
    onValueChange = { newText ->
        if (newText.length <= 5000) {
            textState = newText
        } else {
            textState = newText.take(5000)
            showToast("Text limited to 5000 characters")
        }
    },
    modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 120.dp)
        .weight(1f, fill = false)
        .padding(horizontal = 16.dp)
        .testTag("input_field"),
    placeholder = {
        Text(
            "Paste or type your text here...\n\nTip: Works best with 100-2000 words",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
        )
    },
    supportingText = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CharacterCounter(
                current = textState.length,
                max = 5000,
                modifier = Modifier.testTag("char_counter")
            )
            IconButton(
                onClick = { showHelp = true },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(Icons.Outlined.Help, "Help")
            }
        }
    },
    isError = textState.length > 5000,
    keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        autoCorrect = true,
        keyboardType = KeyboardType.Text
    ),
    maxLines = Int.MAX_VALUE
)
```

### Character Counter Component
```kotlin
@Composable
fun CharacterCounter(current: Int, max: Int, modifier: Modifier) {
    val ratio = current.toFloat() / max
    val color = when {
        ratio > 0.95 -> MaterialTheme.colorScheme.error
        ratio > 0.9 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = "$current/$max characters",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier
    )
}
```

### Bottom Action Bar
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    // Clear Button
    TextButton(
        onClick = { showClearDialog = true },
        enabled = textState.isNotEmpty(),
        modifier = Modifier
            .alpha(if (textState.isNotEmpty()) 1f else 0f)
            .testTag("clear_button")
    ) {
        Text("Clear All")
    }

    // Summarize Button
    SummarizeButton(
        enabled = textState.trim().length >= 50,
        loading = isProcessing,
        onClick = { onSummarize() },
        modifier = Modifier.testTag("summarize_button")
    )
}
```

## 🎮 Interaction States

### Button State Machine
```kotlin
sealed class SummarizeButtonState {
    object Disabled : SummarizeButtonState()    // <50 chars
    object Enabled : SummarizeButtonState()     // Ready to go
    object Loading : SummarizeButtonState()     // Processing
    object Error : SummarizeButtonState()       // Failed
}
```

### Visual Feedback
```kotlin
@Composable
fun SummarizeButton(
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    FilledButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.widthIn(min = 140.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Summarize")
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
```

## ⚠️ Edge Cases & Solutions

| **Scenario** | **Handling** |
|--------------|--------------|
| **Paste 10MB text** | Truncate to 5000 chars + Toast warning |
| **Only whitespace** | Trim silently, keep Summarize disabled |
| **>30% emoji content** | Warning dialog with cleanup option |
| **Network disconnected** | Check before summarize, show inline error |
| **Device rotation** | Preserve text & cursor position |
| **Process death** | Auto-save draft every 5s |
| **Rapid tap Summarize** | Debounce 500ms |
| **Voice input** | Support built-in speech recognition |

## 🎨 Animations

### TextField Transitions
```kotlin
val textFieldHeight by animateDpAsState(
    targetValue = if (isFocused) 200.dp else 120.dp,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

### Button State Transitions
```kotlin
val buttonScale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessMedium
    )
)
```

## 🔍 Testing Checklist

### Functional
- [ ] Text input accepts exactly 5000 characters
- [ ] Character counter updates in real-time
- [ ] Summarize enables at 50 characters
- [ ] Clear button appears/hides correctly
- [ ] Paste handling works for large text
- [ ] Share intent pre-fills correctly

### Performance
- [ ] Typing remains smooth at 60fps
- [ ] No lag with 5000 characters
- [ ] Paste of large text < 2s
- [ ] Memory usage reasonable
- [ ] No ANRs during input

### Accessibility
- [ ] Screen reader announces changes
- [ ] Tab navigation works
- [ ] Text remains readable at 200% scale
- [ ] Voice input integration works

## 🚨 Critical Implementation Notes

### P0 - Ship or Die
1. Basic text input works reliably
2. Character limit enforced properly
3. Summarize triggers correctly
4. Clear actually clears everything

### Known Technical Debt
- No rich text editing (complexity)
- No formatting preservation (scope)
- Basic emoji support only (time)
- English-first optimization (market)

### Performance Targets
- **Cold start**: <500ms
- **Keyboard appears**: <300ms
- **Character count updates**: <16ms
- **Button state changes**: <100ms

---

**Reality Check**: This screen is 60% of your app experience. Every bug here equals a bad first impression. Test with real content from Gmail, Chrome, WhatsApp. The edge cases listed above? They'll all happen in week 1.

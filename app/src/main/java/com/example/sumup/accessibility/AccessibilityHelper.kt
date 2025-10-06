package com.example.sumup.accessibility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager

/**
 * Accessibility Helper for WCAG AA Compliance and TalkBack Support
 * Implements NFR-5: Accessibility Requirements
 */

/**
 * Check if TalkBack or other accessibility services are enabled
 * AC-NFR5.1: Detect accessibility services
 */
@Composable
fun rememberIsAccessibilityEnabled(): Boolean {
    val context = LocalContext.current
    return remember {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        am.isEnabled && am.isTouchExplorationEnabled
    }
}

/**
 * Accessibility modifier for interactive elements
 * AC-NFR5.2: Proper semantic labels for TalkBack
 */
fun Modifier.accessibleClickable(
    label: String,
    role: Role = Role.Button,
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.semantics {
        contentDescription = label
        this.role = role
        this.onClick {
            onClick()
            true
        }
    }
)

/**
 * Accessibility modifier for headings
 * AC-NFR5.3: Proper heading hierarchy for screen readers
 */
fun Modifier.accessibleHeading(
    level: Int = 1,
    text: String
): Modifier = this.then(
    Modifier.semantics {
        heading()
        contentDescription = text
        this.customActions = listOf(
            CustomAccessibilityAction("Heading Level $level") { true }
        )
    }
)

/**
 * Accessibility modifier for images
 * AC-NFR5.4: Descriptive alt text for images
 */
fun Modifier.accessibleImage(
    description: String,
    isDecorative: Boolean = false
): Modifier = this.then(
    Modifier.semantics {
        if (isDecorative) {
            // Mark as decorative - TalkBack will skip
            contentDescription = ""
        } else {
            contentDescription = description
            this.role = Role.Image
        }
    }
)

/**
 * Accessibility modifier for text input fields
 * AC-NFR5.5: Clear labels and hints for input fields
 */
fun Modifier.accessibleTextField(
    label: String,
    value: String,
    hint: String? = null,
    error: String? = null
): Modifier = this.then(
    Modifier.semantics {
        contentDescription = label
        if (hint != null) {
            stateDescription = hint
        }
        if (error != null) {
            this.error(error)
        }
        editableText = androidx.compose.ui.text.AnnotatedString(value)
    }
)

/**
 * Accessibility modifier for lists
 * AC-NFR5.6: Announce list size and position
 */
fun Modifier.accessibleListItem(
    position: Int,
    totalItems: Int,
    itemDescription: String
): Modifier = this.then(
    Modifier.semantics {
        contentDescription = "$itemDescription, item $position of $totalItems"
        this.customActions = listOf(
            CustomAccessibilityAction("Position $position of $totalItems") { true }
        )
    }
)

/**
 * Accessibility modifier for progress indicators
 * AC-NFR5.7: Announce progress state
 */
fun Modifier.accessibleProgress(
    currentProgress: Float,
    label: String
): Modifier = this.then(
    Modifier.semantics {
        progressBarRangeInfo = ProgressBarRangeInfo(currentProgress, 0f..1f)
        contentDescription = "$label: ${(currentProgress * 100).toInt()}%"
    }
)

/**
 * Accessibility modifier for toggles/switches
 * AC-NFR5.8: Announce toggle state
 */
fun Modifier.accessibleToggle(
    label: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
): Modifier = this.then(
    Modifier.semantics {
        this.role = Role.Switch
        contentDescription = label
        stateDescription = if (isChecked) "On" else "Off"
        this.toggleableState = if (isChecked) ToggleableState.On else ToggleableState.Off
    }
)

/**
 * Accessibility modifier for tabs
 * AC-NFR5.9: Announce tab selection
 */
fun Modifier.accessibleTab(
    label: String,
    isSelected: Boolean,
    position: Int,
    totalTabs: Int
): Modifier = this.then(
    Modifier.semantics {
        this.role = Role.Tab
        contentDescription = "$label tab, ${position + 1} of $totalTabs"
        selected = isSelected
        stateDescription = if (isSelected) "Selected" else "Not selected"
    }
)

/**
 * Accessibility modifier for cards/containers
 * AC-NFR5.10: Group related content
 */
fun Modifier.accessibleContainer(
    label: String,
    children: String? = null
): Modifier = this.then(
    Modifier.semantics(mergeDescendants = true) {
        contentDescription = if (children != null) {
            "$label, $children"
        } else {
            label
        }
    }
)

/**
 * Accessibility modifier for live regions
 * AC-NFR5.11: Announce dynamic content changes
 */
fun Modifier.accessibleLiveRegion(
    content: String,
    importance: LiveRegionMode = LiveRegionMode.Polite
): Modifier = this.then(
    Modifier.semantics {
        liveRegion = importance
        contentDescription = content
    }
)

/**
 * Accessibility modifier for dismissible content
 * AC-NFR5.12: Announce dismissible state
 */
fun Modifier.accessibleDismissible(
    label: String,
    onDismiss: () -> Unit
): Modifier = this.then(
    Modifier.semantics {
        contentDescription = "$label, swipe to dismiss"
        dismiss {
            onDismiss()
            true
        }
    }
)

/**
 * Accessibility descriptions for common UI elements
 */
object AccessibilityDescriptions {
    // Buttons
    const val BUTTON_BACK = "Navigate back"
    const val BUTTON_CLOSE = "Close"
    const val BUTTON_SUBMIT = "Submit"
    const val BUTTON_CANCEL = "Cancel"
    const val BUTTON_DELETE = "Delete"
    const val BUTTON_EDIT = "Edit"
    const val BUTTON_SHARE = "Share"
    const val BUTTON_COPY = "Copy to clipboard"
    const val BUTTON_EXPORT = "Export summary"
    const val BUTTON_RETRY = "Retry operation"

    // Icons
    const val ICON_MENU = "Open menu"
    const val ICON_SETTINGS = "Open settings"
    const val ICON_SEARCH = "Search"
    const val ICON_FILTER = "Filter results"
    const val ICON_SORT = "Sort options"
    const val ICON_MORE = "More options"
    const val ICON_INFO = "Information"
    const val ICON_WARNING = "Warning"
    const val ICON_ERROR = "Error"
    const val ICON_SUCCESS = "Success"

    // Inputs
    const val INPUT_TEXT = "Text input field"
    const val INPUT_SEARCH = "Search input field"
    const val INPUT_API_KEY = "API key input field"

    // Content
    const val CONTENT_SUMMARY = "Summary content"
    const val CONTENT_METRICS = "Summary metrics"
    const val CONTENT_HISTORY = "Summary history list"
    const val CONTENT_EMPTY = "No items to display"
    const val CONTENT_LOADING = "Loading content"
    const val CONTENT_ERROR = "Error message"

    // Actions
    const val ACTION_SUMMARIZE = "Create summary"
    const val ACTION_CAPTURE_OCR = "Capture text from camera"
    const val ACTION_SELECT_PDF = "Select PDF file"
    const val ACTION_SELECT_PERSONA = "Select summarization style"
    const val ACTION_CHANGE_THEME = "Change app theme"
    const val ACTION_CHANGE_LANGUAGE = "Change language"

    // States
    const val STATE_PROCESSING = "Processing your request"
    const val STATE_SUCCESS = "Operation completed successfully"
    const val STATE_ERROR = "An error occurred"
    const val STATE_EMPTY = "No content available"
    const val STATE_LOADING = "Loading"

    // Announcements
    const val ANNOUNCE_SUMMARY_CREATED = "Summary created successfully"
    const val ANNOUNCE_SUMMARY_DELETED = "Summary deleted"
    const val ANNOUNCE_SUMMARY_COPIED = "Summary copied to clipboard"
    const val ANNOUNCE_EXPORT_SUCCESS = "Export completed"
    const val ANNOUNCE_EXPORT_FAILED = "Export failed"
    const val ANNOUNCE_API_KEY_SAVED = "API key saved"
    const val ANNOUNCE_SETTINGS_SAVED = "Settings saved"
}

/**
 * WCAG AA Compliance Constants
 */
object WCAGCompliance {
    // Color contrast ratios
    const val MIN_CONTRAST_NORMAL = 4.5f  // WCAG AA for normal text
    const val MIN_CONTRAST_LARGE = 3.0f   // WCAG AA for large text (18pt+)
    const val MIN_CONTRAST_AAA = 7.0f     // WCAG AAA for normal text

    // Touch target sizes (in dp)
    const val MIN_TOUCH_TARGET_SIZE = 48  // WCAG AA minimum
    const val RECOMMENDED_TOUCH_TARGET = 56 // Material Design recommendation

    // Text sizes (in sp)
    const val MIN_BODY_TEXT_SIZE = 12     // Minimum for body text
    const val MIN_BUTTON_TEXT_SIZE = 14   // Minimum for buttons
    const val LARGE_TEXT_THRESHOLD = 18   // Text above this is "large"

    // Timing
    const val MIN_NOTIFICATION_DURATION_MS = 4000L // 4 seconds for important notifications
    const val MIN_ERROR_DURATION_MS = 6000L        // 6 seconds for error messages
}

/**
 * Helper function to calculate color contrast ratio
 * AC-NFR5.13: Ensure WCAG AA color contrast
 */
fun calculateContrastRatio(foreground: Int, background: Int): Float {
    val fgLuminance = getRelativeLuminance(foreground)
    val bgLuminance = getRelativeLuminance(background)

    val lighter = maxOf(fgLuminance, bgLuminance)
    val darker = minOf(fgLuminance, bgLuminance)

    return (lighter + 0.05f) / (darker + 0.05f)
}

private fun getRelativeLuminance(color: Int): Float {
    val r = android.graphics.Color.red(color) / 255f
    val g = android.graphics.Color.green(color) / 255f
    val b = android.graphics.Color.blue(color) / 255f

    val rLum = if (r <= 0.03928f) r / 12.92f else Math.pow((r + 0.055) / 1.055, 2.4).toFloat()
    val gLum = if (g <= 0.03928f) g / 12.92f else Math.pow((g + 0.055) / 1.055, 2.4).toFloat()
    val bLum = if (b <= 0.03928f) b / 12.92f else Math.pow((b + 0.055) / 1.055, 2.4).toFloat()

    return 0.2126f * rLum + 0.7152f * gLum + 0.0722f * bLum
}

/**
 * Check if color contrast meets WCAG AA
 */
fun meetsWCAGAA(foreground: Int, background: Int, isLargeText: Boolean = false): Boolean {
    val ratio = calculateContrastRatio(foreground, background)
    val minRatio = if (isLargeText) WCAGCompliance.MIN_CONTRAST_LARGE else WCAGCompliance.MIN_CONTRAST_NORMAL
    return ratio >= minRatio
}

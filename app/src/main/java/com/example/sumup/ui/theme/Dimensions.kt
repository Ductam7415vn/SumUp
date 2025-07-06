package com.example.sumup.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens for consistent spacing throughout the app
 */
object Dimensions {
    // Spacing tokens
    val spacingNone = 0.dp
    val spacingXxs = 2.dp
    val spacingXs = 4.dp
    val spacingSm = 8.dp
    val spacingMd = 16.dp
    val spacingLg = 24.dp
    val spacingXl = 32.dp
    val spacingXxl = 48.dp
    val spacingXxxl = 64.dp
    
    // Padding tokens
    val paddingXs = 4.dp
    val paddingSm = 8.dp
    val paddingMd = 16.dp
    val paddingLg = 24.dp
    val paddingXl = 32.dp
    
    // Component specific
    val minTouchTarget = 48.dp
    val iconSizeSm = 16.dp
    val iconSizeMd = 24.dp
    val iconSizeLg = 32.dp
    val iconSizeXl = 48.dp
    
    // Border radius
    val radiusXs = 4.dp
    val radiusSm = 8.dp
    val radiusMd = 12.dp
    val radiusLg = 16.dp
    val radiusXl = 20.dp
    val radiusXxl = 28.dp
    val radiusFull = 1000.dp
    
    // Elevation
    val elevationNone = 0.dp
    val elevationXs = 2.dp
    val elevationSm = 4.dp
    val elevationMd = 8.dp
    val elevationLg = 12.dp
    val elevationXl = 16.dp
    
    // Text sizes (backup for when not using Material Typography)
    val textXs = 11.sp
    val textSm = 12.sp
    val textMd = 14.sp
    val textLg = 16.sp
    val textXl = 18.sp
    val textXxl = 20.sp
    val textXxxl = 24.sp
    
    // Component heights
    val buttonHeightSm = 36.dp
    val buttonHeightMd = 48.dp
    val buttonHeightLg = 56.dp
    
    val textFieldHeightSm = 56.dp
    val textFieldHeightMd = 64.dp
    val textFieldHeightLg = 72.dp
    
    // Specific measurements
    val bottomNavHeight = 80.dp
    val topBarHeight = 64.dp
    val topBarBigBoy = 128.dp
    val fabSize = 56.dp
    val chipHeight = 32.dp
    
    // Content widths
    val contentMaxWidth = 600.dp
    val dialogMaxWidth = 400.dp
    
    // Animation durations (in milliseconds)
    const val animationShort = 150
    const val animationMedium = 300
    const val animationLong = 500
}

/**
 * Semantic spacing for specific use cases
 */
object Spacing {
    // Screen padding
    val screenPadding = Dimensions.paddingMd
    val screenPaddingLarge = Dimensions.paddingLg
    
    // Content spacing
    val contentSpacing = Dimensions.spacingMd
    val sectionSpacing = Dimensions.spacingLg
    
    // List item spacing
    val listItemSpacing = Dimensions.spacingSm
    val listSectionSpacing = Dimensions.spacingMd
    
    // Card padding
    val cardPadding = Dimensions.paddingMd
    val cardContentSpacing = Dimensions.spacingMd
    
    // Dialog padding
    val dialogPadding = Dimensions.paddingLg
    val dialogContentSpacing = Dimensions.spacingMd
    
    // Button padding
    val buttonPaddingHorizontal = Dimensions.paddingLg
    val buttonPaddingVertical = Dimensions.paddingMd
    val buttonIconSpacing = Dimensions.spacingSm
}

/**
 * Accessibility constants
 */
object Accessibility {
    // Minimum contrast ratios
    const val minContrastNormal = 4.5f
    const val minContrastLarge = 3.0f
    
    // Alpha values that maintain accessibility
    const val textPrimaryAlpha = 1.0f
    const val textSecondaryAlpha = 0.87f
    const val textDisabledAlpha = 0.60f
    const val textHintAlpha = 0.60f
    
    // Focus indicator
    val focusIndicatorWidth = 2.dp
    val focusIndicatorOffset = 2.dp
}
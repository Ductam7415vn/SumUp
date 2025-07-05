package com.example.sumup.presentation.utils

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive layout utilities
 */

/**
 * Device type based on window size
 */
enum class DeviceType {
    PHONE,
    FOLDABLE,
    TABLET,
    DESKTOP
}

/**
 * Orientation type
 */
enum class Orientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Adaptive layout information containing device type and orientation
 */
data class AdaptiveLayoutInfo(
    val windowSizeClass: WindowSizeClass,
    val deviceType: DeviceType,
    val orientation: Orientation,
    val isLandscape: Boolean,
    val screenWidthDp: Int,
    val screenHeightDp: Int
)

/**
 * Remember adaptive layout info based on window size class
 */
@Composable
fun rememberAdaptiveLayoutInfo(windowSizeClass: WindowSizeClass): AdaptiveLayoutInfo {
    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
        Orientation.LANDSCAPE
    } else {
        Orientation.PORTRAIT
    }
    
    return remember(windowSizeClass, configuration) {
        AdaptiveLayoutInfo(
            windowSizeClass = windowSizeClass,
            deviceType = windowSizeClass.toDeviceType(),
            orientation = orientation,
            isLandscape = orientation == Orientation.LANDSCAPE,
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp
        )
    }
}

/**
 * Get device type from window size class
 */
fun WindowSizeClass.toDeviceType(): DeviceType {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> DeviceType.PHONE
        WindowWidthSizeClass.Medium -> {
            // Could be foldable or small tablet
            if (heightSizeClass == WindowHeightSizeClass.Compact) {
                DeviceType.FOLDABLE
            } else {
                DeviceType.TABLET
            }
        }
        WindowWidthSizeClass.Expanded -> DeviceType.DESKTOP
        else -> DeviceType.PHONE
    }
}

/**
 * Responsive dimensions based on device type
 */
object ResponsiveDimensions {
    fun getScreenPadding(deviceType: DeviceType): Dp {
        return when (deviceType) {
            DeviceType.PHONE -> 16.dp
            DeviceType.FOLDABLE -> 20.dp
            DeviceType.TABLET -> 24.dp
            DeviceType.DESKTOP -> 32.dp
        }
    }
    
    fun getContentMaxWidth(deviceType: DeviceType): Dp {
        return when (deviceType) {
            DeviceType.PHONE -> 600.dp
            DeviceType.FOLDABLE -> 700.dp
            DeviceType.TABLET -> 840.dp
            DeviceType.DESKTOP -> 1200.dp
        }
    }
    
    fun getCardColumns(deviceType: DeviceType): Int {
        return when (deviceType) {
            DeviceType.PHONE -> 1
            DeviceType.FOLDABLE -> 2
            DeviceType.TABLET -> 3
            DeviceType.DESKTOP -> 4
        }
    }
    
    fun getTextFieldHeight(deviceType: DeviceType): Dp {
        return when (deviceType) {
            DeviceType.PHONE -> 240.dp
            DeviceType.FOLDABLE -> 280.dp
            DeviceType.TABLET -> 320.dp
            DeviceType.DESKTOP -> 360.dp
        }
    }
}

/**
 * Responsive layout that adapts to screen size
 */
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (DeviceType, Orientation, Dp) -> Unit
) {
    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
        Orientation.LANDSCAPE
    } else {
        Orientation.PORTRAIT
    }
    
    BoxWithConstraints(modifier = modifier) {
        val deviceType = when {
            maxWidth < 600.dp -> DeviceType.PHONE
            maxWidth < 840.dp -> DeviceType.FOLDABLE
            maxWidth < 1200.dp -> DeviceType.TABLET
            else -> DeviceType.DESKTOP
        }
        
        content(deviceType, orientation, maxWidth)
    }
}

/**
 * Adaptive value based on device type
 */
@Composable
fun <T> adaptiveValue(
    phone: T,
    foldable: T = phone,
    tablet: T = foldable,
    desktop: T = tablet
): T {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> phone
        screenWidth < 840.dp -> foldable
        screenWidth < 1200.dp -> tablet
        else -> desktop
    }
}

/**
 * Check if device is in landscape mode
 */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Check if device is foldable
 */
@Composable
fun isFoldable(): Boolean {
    // This is a simplified check - in production, use Window Manager
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    // Heuristic: foldables often have unusual aspect ratios
    val aspectRatio = screenWidth.toFloat() / screenHeight.toFloat()
    return aspectRatio > 1.5f || aspectRatio < 0.67f
}

/**
 * Breakpoints for responsive design
 */
object Breakpoints {
    val PHONE_MAX = 600.dp
    val FOLDABLE_MAX = 840.dp
    val TABLET_MAX = 1200.dp
    
    // Common breakpoints
    val SMALL = 360.dp
    val MEDIUM = 600.dp
    val LARGE = 840.dp
    val XLARGE = 1200.dp
    val XXLARGE = 1600.dp
}
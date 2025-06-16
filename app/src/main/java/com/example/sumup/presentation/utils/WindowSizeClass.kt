package com.example.sumup.presentation.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Different device types based on window size
 */
enum class DeviceType {
    PHONE,
    FOLDABLE,
    TABLET,
    DESKTOP
}

/**
 * Different postures for foldable devices
 */
enum class DevicePosture {
    NORMAL,
    BOOK,
    SEPARATING
}

/**
 * Utility class to determine device type and optimal layouts
 */
data class AdaptiveLayoutInfo(
    val windowSizeClass: WindowSizeClass,
    val deviceType: DeviceType,
    val devicePosture: DevicePosture,
    val isLandscape: Boolean,
    val shouldShowNavRail: Boolean,
    val shouldShowBottomBar: Boolean,
    val columnsCount: Int
)

/**
 * Extension to determine device type from WindowSizeClass
 */
fun WindowSizeClass.toDeviceType(): DeviceType {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> DeviceType.PHONE
        WindowWidthSizeClass.Medium -> {
            // Could be tablet in portrait or foldable
            if (heightSizeClass == WindowHeightSizeClass.Expanded) {
                DeviceType.TABLET
            } else {
                DeviceType.FOLDABLE
            }
        }
        WindowWidthSizeClass.Expanded -> DeviceType.TABLET
        else -> DeviceType.PHONE
    }
}

/**
 * Calculate adaptive layout info based on window size
 */
@Composable
fun rememberAdaptiveLayoutInfo(
    windowSizeClass: WindowSizeClass,
    devicePosture: DevicePosture = DevicePosture.NORMAL
): AdaptiveLayoutInfo {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    return remember(windowSizeClass, devicePosture, isLandscape) {
        val deviceType = windowSizeClass.toDeviceType()
        
        // Determine navigation pattern
        val shouldShowNavRail = when {
            deviceType == DeviceType.TABLET -> true
            deviceType == DeviceType.FOLDABLE && isLandscape -> true
            deviceType == DeviceType.DESKTOP -> true
            else -> false
        }
        
        val shouldShowBottomBar = !shouldShowNavRail
        
        // Determine columns for grid layouts
        val columnsCount = when {
            deviceType == DeviceType.PHONE && !isLandscape -> 1
            deviceType == DeviceType.PHONE && isLandscape -> 2
            deviceType == DeviceType.FOLDABLE && devicePosture == DevicePosture.NORMAL -> 2
            deviceType == DeviceType.FOLDABLE && devicePosture == DevicePosture.SEPARATING -> 2
            deviceType == DeviceType.TABLET && !isLandscape -> 2
            deviceType == DeviceType.TABLET && isLandscape -> 3
            else -> 1
        }
        
        AdaptiveLayoutInfo(
            windowSizeClass = windowSizeClass,
            deviceType = deviceType,
            devicePosture = devicePosture,
            isLandscape = isLandscape,
            shouldShowNavRail = shouldShowNavRail,
            shouldShowBottomBar = shouldShowBottomBar,
            columnsCount = columnsCount
        )
    }
}

/**
 * Breakpoints for responsive design
 */
object Breakpoints {
    val COMPACT_WIDTH = 600.dp
    val MEDIUM_WIDTH = 840.dp
    val EXPANDED_WIDTH = 1200.dp
    
    val COMPACT_HEIGHT = 480.dp
    val MEDIUM_HEIGHT = 900.dp
    val EXPANDED_HEIGHT = 1200.dp
}

/**
 * Calculate optimal content width based on device type
 */
fun getOptimalContentWidth(deviceType: DeviceType, isLandscape: Boolean): Float {
    return when (deviceType) {
        DeviceType.PHONE -> 1f
        DeviceType.FOLDABLE -> if (isLandscape) 0.5f else 0.9f
        DeviceType.TABLET -> if (isLandscape) 0.7f else 0.85f
        DeviceType.DESKTOP -> 0.6f
    }
}
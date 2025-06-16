package com.example.sumup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen size classifications for adaptive layouts
 */
enum class ScreenSize {
    COMPACT,    // Phone portrait, small phone landscape
    MEDIUM,     // Large phone landscape, small tablet
    EXPANDED    // Large tablet, foldable unfolded
}

/**
 * Window orientation detection
 */
enum class WindowOrientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Device type classification
 */
enum class DeviceType {
    PHONE,
    FOLDABLE,
    TABLET
}

/**
 * Adaptive layout information
 */
data class AdaptiveInfo(
    val screenSize: ScreenSize,
    val orientation: WindowOrientation,
    val deviceType: DeviceType,
    val isTablet: Boolean,
    val isFoldable: Boolean,
    val useTwoPane: Boolean,
    val useCompactLayout: Boolean
)

/**
 * Remember adaptive layout information
 */
@Composable
fun rememberAdaptiveInfo(): AdaptiveInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenWidthDp = with(density) { configuration.screenWidthDp.dp }
    val screenHeightDp = with(density) { configuration.screenHeightDp.dp }
    
    val screenSize = when {
        screenWidthDp >= 840.dp -> ScreenSize.EXPANDED
        screenWidthDp >= 600.dp -> ScreenSize.MEDIUM
        else -> ScreenSize.COMPACT
    }
    
    val orientation = if (screenWidthDp > screenHeightDp) {
        WindowOrientation.LANDSCAPE
    } else {
        WindowOrientation.PORTRAIT
    }
    
    val deviceType = when {
        screenWidthDp >= 840.dp -> DeviceType.TABLET
        screenWidthDp >= 600.dp && orientation == WindowOrientation.LANDSCAPE -> DeviceType.FOLDABLE
        else -> DeviceType.PHONE
    }
    
    val isTablet = deviceType == DeviceType.TABLET
    val isFoldable = deviceType == DeviceType.FOLDABLE
    val useTwoPane = screenSize == ScreenSize.EXPANDED || 
                    (screenSize == ScreenSize.MEDIUM && orientation == WindowOrientation.LANDSCAPE)
    val useCompactLayout = screenSize == ScreenSize.COMPACT && orientation == WindowOrientation.PORTRAIT
    
    return AdaptiveInfo(
        screenSize = screenSize,
        orientation = orientation,
        deviceType = deviceType,
        isTablet = isTablet,
        isFoldable = isFoldable,
        useTwoPane = useTwoPane,
        useCompactLayout = useCompactLayout
    )
}

/**
 * Adaptive two-pane layout for tablets and foldables
 */
@Composable
fun AdaptiveTwoPane(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    splitRatio: Float = 0.5f
) {
    val adaptiveInfo = rememberAdaptiveInfo()
    
    if (adaptiveInfo.useTwoPane) {
        Row(
            modifier = modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(splitRatio)
            ) {
                first()
            }
            
            VerticalDivider()
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f - splitRatio)
            ) {
                second()
            }
        }
    } else {
        // Single pane layout for compact screens
        Box(modifier = modifier.fillMaxSize()) {
            first()
        }
    }
}

/**
 * Adaptive grid layout that adjusts columns based on screen size
 */
@Composable
fun AdaptiveGrid(
    modifier: Modifier = Modifier,
    minItemWidth: Dp = 280.dp,
    horizontalSpacing: Dp = 16.dp,
    verticalSpacing: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val adaptiveInfo = rememberAdaptiveInfo()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenWidthDp = with(density) { configuration.screenWidthDp.dp }
    val availableWidth = screenWidthDp - 32.dp // Account for padding
    val columns = maxOf(1, (availableWidth / (minItemWidth + horizontalSpacing)).toInt())
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        content()
    }
}

/**
 * Adaptive card that adjusts its layout based on screen size
 */
@Composable
fun AdaptiveCard(
    modifier: Modifier = Modifier,
    compactContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit = compactContent
) {
    val adaptiveInfo = rememberAdaptiveInfo()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (adaptiveInfo.isTablet) 8.dp else 4.dp
        )
    ) {
        if (adaptiveInfo.useCompactLayout) {
            compactContent()
        } else {
            expandedContent()
        }
    }
}

/**
 * Responsive padding that adjusts based on screen size
 */
@Composable
fun responsivePadding(): PaddingValues {
    val adaptiveInfo = rememberAdaptiveInfo()
    
    return when (adaptiveInfo.screenSize) {
        ScreenSize.COMPACT -> PaddingValues(16.dp)
        ScreenSize.MEDIUM -> PaddingValues(24.dp)
        ScreenSize.EXPANDED -> PaddingValues(32.dp)
    }
}

/**
 * Responsive horizontal padding for content containers
 */
@Composable
fun responsiveHorizontalPadding(): Dp {
    val adaptiveInfo = rememberAdaptiveInfo()
    
    return when (adaptiveInfo.screenSize) {
        ScreenSize.COMPACT -> 16.dp
        ScreenSize.MEDIUM -> 32.dp
        ScreenSize.EXPANDED -> 64.dp
    }
}

/**
 * Responsive max width for content to prevent it from being too wide on large screens
 */
@Composable
fun responsiveMaxWidth(): Dp {
    val adaptiveInfo = rememberAdaptiveInfo()
    
    return when (adaptiveInfo.deviceType) {
        DeviceType.PHONE -> Dp.Unspecified
        DeviceType.FOLDABLE -> 800.dp
        DeviceType.TABLET -> 1200.dp
    }
}

/**
 * Adaptive bottom bar that becomes a side rail on large screens
 */
@Composable
fun AdaptiveNavigationContainer(
    navigationContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val adaptiveInfo = rememberAdaptiveInfo()
    
    if (adaptiveInfo.useTwoPane && adaptiveInfo.orientation == WindowOrientation.LANDSCAPE) {
        // Side navigation for large landscape screens
        Row(modifier = modifier.fillMaxSize()) {
            NavigationRail {
                navigationContent()
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                content()
            }
        }
    } else {
        // Bottom navigation for other screens
        Column(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
            navigationContent()
        }
    }
}
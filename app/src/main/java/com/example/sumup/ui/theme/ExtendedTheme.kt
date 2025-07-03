package com.example.sumup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Extended color scheme for semantic colors not included in Material 3
 */
data class ExtendedColorScheme(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val neutral10: Color,
    val neutral20: Color,
    val neutral30: Color,
    val neutral40: Color,
    val neutral50: Color,
    val neutral60: Color,
    val neutral70: Color,
    val neutral80: Color,
    val neutral90: Color,
    val neutral95: Color,
    val neutral99: Color
)

val LightExtendedColorScheme = ExtendedColorScheme(
    success = Success,
    onSuccess = Color.White,
    successContainer = SuccessContainer,
    onSuccessContainer = OnSuccess,
    warning = Warning,
    onWarning = Color.White,
    warningContainer = WarningContainer,
    onWarningContainer = OnWarning,
    info = Info,
    onInfo = Color.White,
    infoContainer = InfoContainer,
    onInfoContainer = OnInfo,
    neutral10 = Neutral10,
    neutral20 = Neutral20,
    neutral30 = Neutral30,
    neutral40 = Neutral40,
    neutral50 = Neutral50,
    neutral60 = Neutral60,
    neutral70 = Neutral70,
    neutral80 = Neutral80,
    neutral90 = Neutral90,
    neutral95 = Neutral95,
    neutral99 = Neutral99
)

val DarkExtendedColorScheme = ExtendedColorScheme(
    success = Color(0xFF66BB6A),
    onSuccess = Color.Black,
    successContainer = Color(0xFF2E7D32),
    onSuccessContainer = Color(0xFFC8E6C9),
    warning = Color(0xFFFFA726),
    onWarning = Color.Black,
    warningContainer = Color(0xFFF57C00),
    onWarningContainer = Color(0xFFFFE0B2),
    info = Color(0xFF42A5F5),
    onInfo = Color.Black,
    infoContainer = Color(0xFF1976D2),
    onInfoContainer = Color(0xFFBBDEFB),
    neutral10 = Color(0xFFE1E2E8),
    neutral20 = Color(0xFFC5C6CE),
    neutral30 = Color(0xFFAAABB4),
    neutral40 = Color(0xFF8F909A),
    neutral50 = Color(0xFF747680),
    neutral60 = Color(0xFF5C5E6A),
    neutral70 = Color(0xFF454754),
    neutral80 = Color(0xFF2E313E),
    neutral90 = Color(0xFF1C1F2B),
    neutral95 = Color(0xFF141621),
    neutral99 = Color(0xFF0A0C12)
)

val LocalExtendedColorScheme = staticCompositionLocalOf {
    LightExtendedColorScheme
}

@Composable
@ReadOnlyComposable
fun extendedColorScheme() = LocalExtendedColorScheme.current

object ExtendedTheme {
    val colorScheme: ExtendedColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColorScheme.current
}

/**
 * Helper function to get extended colors
 */
val MaterialTheme.extendedColorScheme: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current
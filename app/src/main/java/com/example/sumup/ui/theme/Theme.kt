package com.example.sumup.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.CompositionLocalProvider
import com.example.sumup.ui.theme.*

// Custom brand colors - Modern palette
private val BrandPurple = Color(0xFF5B5FDE)
private val BrandPurpleDark = Color(0xFF4B4FCE)
private val BrandBlue = Color(0xFF6366F1)
private val BrandPink = Color(0xFFFF6B6B)

// Fallback color schemes when dynamic color is not available
private val DarkColorScheme = darkColorScheme(
    primary = BrandPurple,
    onPrimary = Color.White,
    primaryContainer = BrandPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = BrandPink,
    onSecondary = Color.White,
    tertiary = BrandBlue,
    onTertiary = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCAC4D0)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E9FF),
    onPrimaryContainer = Color.Black,
    secondary = BrandPink,
    onSecondary = Color.White,
    tertiary = BrandPurple,
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F9FE),
    surface = Color.White,
    surfaceVariant = Color(0xFFF3F4F6),
    onBackground = Color(0xFF1A1D29),
    onSurface = Color(0xFF1A1D29),
    onSurfaceVariant = Color(0xFF6B7280)
)

@Composable
fun SumUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val extendedColorScheme = if (darkTheme) {
        DarkExtendedColorScheme
    } else {
        LightExtendedColorScheme
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            try {
                android.util.Log.d("SumUpTheme", "Using dynamic colors (dark=$darkTheme)")
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } catch (e: Exception) {
                android.util.Log.e("SumUpTheme", "Dynamic color failed: ${e.message}")
                // Fallback if dynamic color fails
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
        darkTheme -> {
            android.util.Log.d("SumUpTheme", "Using dark theme")
            DarkColorScheme
        }
        else -> {
            android.util.Log.d("SumUpTheme", "Using light theme")
            LightColorScheme
        }
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make status bar transparent for edge-to-edge
            window.statusBarColor = Color.Transparent.toArgb()
            // Make navigation bar transparent too
            window.navigationBarColor = Color.Transparent.toArgb()
            
            // Set system bar icon colors based on theme
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extendedColorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
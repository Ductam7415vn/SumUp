package com.example.sumup.presentation.screens.result.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Enhanced haptic feedback for ResultScreen interactions
 */
class ResultScreenHaptics(
    private val context: Context,
    private val hapticFeedback: HapticFeedback
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    /**
     * Success haptic - Used when summary is loaded or action succeeds
     */
    fun success() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 30, 50, 30), // Pattern: wait, vibrate, wait, vibrate
                    intArrayOf(0, 120, 0, 180), // Amplitudes
                    -1 // Don't repeat
                )
            )
        } else {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    
    /**
     * Selection haptic - Used when selecting personas or buttons
     */
    fun selection() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Light tap - Used for minor interactions
     */
    fun lightTap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }
    
    /**
     * Error haptic - Used for failures or errors
     */
    fun error() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 100, 100), // Stronger pattern for errors
                    intArrayOf(0, 255, 0, 255), // Max amplitude
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 100, 100), -1)
        }
    }
    
    /**
     * Swipe gesture haptic
     */
    fun swipe() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(15, 100)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }
    
    /**
     * Copy action haptic - Quick double tap
     */
    fun copy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 20, 40, 20),
                    intArrayOf(0, 150, 0, 150),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 20, 40, 20), -1)
        }
    }
}

/**
 * Composable helper to remember haptic instance
 */
@Composable
fun rememberResultScreenHaptics(): ResultScreenHaptics {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    
    return remember(context, hapticFeedback) {
        ResultScreenHaptics(context, hapticFeedback)
    }
}
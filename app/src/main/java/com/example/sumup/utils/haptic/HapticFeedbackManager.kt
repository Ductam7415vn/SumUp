package com.example.sumup.utils.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Haptic feedback types for different interactions
 */
enum class HapticFeedbackType {
    // Light feedback for subtle interactions
    CLICK,           // Button taps, selection
    TICK,            // Toggle switches, checkboxes
    
    // Medium feedback for important actions
    SUCCESS,         // Successful operations
    WARNING,         // Warning actions
    
    // Strong feedback for critical interactions
    ERROR,           // Error states
    LONG_PRESS,      // Long press interactions
    SELECTION_START, // Start of selection mode
    
    // Custom feedback for specific UX
    SWIPE_REFRESH,   // Pull to refresh
    SWIPE_DELETE,    // Swipe to delete
    NAVIGATION,      // Screen transitions
    IMPACT_LIGHT,    // Light impact
    IMPACT_MEDIUM,   // Medium impact  
    IMPACT_HEAVY     // Heavy impact
}

/**
 * Manager class for haptic feedback across the app
 */
class HapticFeedbackManager(private val context: Context) {
    
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    private val isHapticEnabled: Boolean
        get() = vibrator.hasVibrator()
    
    /**
     * Perform haptic feedback based on type
     */
    fun performHapticFeedback(type: HapticFeedbackType) {
        if (!isHapticEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use predefined effects for API 29+
            val vibrationEffect = when (type) {
                HapticFeedbackType.CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                HapticFeedbackType.TICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                HapticFeedbackType.LONG_PRESS -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                HapticFeedbackType.SUCCESS -> createCustomEffect(longArrayOf(0, 50, 50, 100), intArrayOf(0, 255, 0, 255))
                HapticFeedbackType.WARNING -> createCustomEffect(longArrayOf(0, 100, 100, 100), intArrayOf(0, 200, 0, 200))
                HapticFeedbackType.ERROR -> createCustomEffect(longArrayOf(0, 100, 50, 100, 50, 100), intArrayOf(0, 255, 0, 255, 0, 255))
                HapticFeedbackType.SELECTION_START -> createCustomEffect(longArrayOf(0, 80), intArrayOf(0, 200))
                HapticFeedbackType.SWIPE_REFRESH -> createCustomEffect(longArrayOf(0, 30, 30, 60), intArrayOf(0, 150, 0, 200))
                HapticFeedbackType.SWIPE_DELETE -> createCustomEffect(longArrayOf(0, 50, 30, 80), intArrayOf(0, 200, 0, 255))
                HapticFeedbackType.NAVIGATION -> createCustomEffect(longArrayOf(0, 40), intArrayOf(0, 120))
                HapticFeedbackType.IMPACT_LIGHT -> createCustomEffect(longArrayOf(0, 30), intArrayOf(0, 100))
                HapticFeedbackType.IMPACT_MEDIUM -> createCustomEffect(longArrayOf(0, 60), intArrayOf(0, 180))
                HapticFeedbackType.IMPACT_HEAVY -> createCustomEffect(longArrayOf(0, 120), intArrayOf(0, 255))
            }
            vibrator.vibrate(vibrationEffect)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use custom effects for API 26-28
            val vibrationEffect = when (type) {
                HapticFeedbackType.CLICK -> createCustomEffect(longArrayOf(0, 10), intArrayOf(0, 255))
                HapticFeedbackType.TICK -> createCustomEffect(longArrayOf(0, 20), intArrayOf(0, 200))
                HapticFeedbackType.SUCCESS -> createCustomEffect(longArrayOf(0, 50, 50, 100), intArrayOf(0, 255, 0, 255))
                HapticFeedbackType.WARNING -> createCustomEffect(longArrayOf(0, 100, 100, 100), intArrayOf(0, 200, 0, 200))
                HapticFeedbackType.ERROR -> createCustomEffect(longArrayOf(0, 100, 50, 100, 50, 100), intArrayOf(0, 255, 0, 255, 0, 255))
                HapticFeedbackType.LONG_PRESS -> createCustomEffect(longArrayOf(0, 200), intArrayOf(0, 255))
                HapticFeedbackType.SELECTION_START -> createCustomEffect(longArrayOf(0, 80), intArrayOf(0, 200))
                HapticFeedbackType.SWIPE_REFRESH -> createCustomEffect(longArrayOf(0, 30, 30, 60), intArrayOf(0, 150, 0, 200))
                HapticFeedbackType.SWIPE_DELETE -> createCustomEffect(longArrayOf(0, 50, 30, 80), intArrayOf(0, 200, 0, 255))
                HapticFeedbackType.NAVIGATION -> createCustomEffect(longArrayOf(0, 40), intArrayOf(0, 120))
                HapticFeedbackType.IMPACT_LIGHT -> createCustomEffect(longArrayOf(0, 30), intArrayOf(0, 100))
                HapticFeedbackType.IMPACT_MEDIUM -> createCustomEffect(longArrayOf(0, 60), intArrayOf(0, 180))
                HapticFeedbackType.IMPACT_HEAVY -> createCustomEffect(longArrayOf(0, 120), intArrayOf(0, 255))
            }
            
            vibrator.vibrate(vibrationEffect)
        } else {
            // Fallback for older devices
            @Suppress("DEPRECATION")
            val duration = when (type) {
                HapticFeedbackType.CLICK, HapticFeedbackType.TICK -> 10L
                HapticFeedbackType.SUCCESS, HapticFeedbackType.WARNING -> 50L
                HapticFeedbackType.ERROR, HapticFeedbackType.LONG_PRESS -> 100L
                else -> 30L
            }
            vibrator.vibrate(duration)
        }
    }
    
    /**
     * Perform haptic feedback using View's built-in constants (for compatibility)
     */
    fun performHapticFeedback(view: View, type: HapticFeedbackType) {
        val hapticConstant = when (type) {
            HapticFeedbackType.CLICK -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticFeedbackType.LONG_PRESS -> HapticFeedbackConstants.LONG_PRESS
            HapticFeedbackType.SELECTION_START -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.GESTURE_START
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                }
            }
            else -> {
                // For custom types, use direct vibration
                performHapticFeedback(type)
                return
            }
        }
        
        view.performHapticFeedback(hapticConstant)
    }
    
    private fun createCustomEffect(timings: LongArray, amplitudes: IntArray): VibrationEffect {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createWaveform(timings, amplitudes, -1)
        } else {
            // Fallback for API < 26
            VibrationEffect.createOneShot(timings.sum(), 255)
        }
    }
    
    /**
     * Check if device supports haptic feedback
     */
    fun isHapticFeedbackSupported(): Boolean = isHapticEnabled
    
    /**
     * Create haptic effect for specific UI patterns
     */
    fun createUIPatternFeedback(pattern: UIHapticPattern) {
        when (pattern) {
            UIHapticPattern.BUTTON_PRESS -> performHapticFeedback(HapticFeedbackType.CLICK)
            UIHapticPattern.TOGGLE_ON -> performHapticFeedback(HapticFeedbackType.SUCCESS)
            UIHapticPattern.TOGGLE_OFF -> performHapticFeedback(HapticFeedbackType.TICK)
            UIHapticPattern.SWIPE_ACTION -> performHapticFeedback(HapticFeedbackType.SWIPE_DELETE)
            UIHapticPattern.ERROR_SHAKE -> performHapticFeedback(HapticFeedbackType.ERROR)
            UIHapticPattern.SUCCESS_COMPLETE -> performHapticFeedback(HapticFeedbackType.SUCCESS)
            UIHapticPattern.NAVIGATION_TRANSITION -> performHapticFeedback(HapticFeedbackType.NAVIGATION)
            UIHapticPattern.SELECTION_FEEDBACK -> performHapticFeedback(HapticFeedbackType.SELECTION_START)
        }
    }
}

/**
 * UI-specific haptic patterns
 */
enum class UIHapticPattern {
    BUTTON_PRESS,
    TOGGLE_ON,
    TOGGLE_OFF,
    SWIPE_ACTION,
    ERROR_SHAKE,
    SUCCESS_COMPLETE,
    NAVIGATION_TRANSITION,
    SELECTION_FEEDBACK
}

/**
 * Compose hook for haptic feedback
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    val context = LocalContext.current
    return remember { HapticFeedbackManager(context) }
}

/**
 * Extension function for easy haptic feedback in Compose
 */
@Composable
fun View.performHapticFeedback(type: HapticFeedbackType) {
    val hapticManager = rememberHapticFeedback()
    hapticManager.performHapticFeedback(this, type)
}

/**
 * Haptic feedback modifier for Compose components
 */
@Composable
fun Modifier.hapticFeedback(
    enabled: Boolean = true,
    type: HapticFeedbackType = HapticFeedbackType.CLICK,
    onTrigger: () -> Unit = {}
): Modifier {
    val hapticManager = rememberHapticFeedback()
    
    return this.then(
        if (enabled) {
            Modifier.clickable {
                hapticManager.performHapticFeedback(type)
                onTrigger()
            }
        } else {
            Modifier.clickable { onTrigger() }
        }
    )
}
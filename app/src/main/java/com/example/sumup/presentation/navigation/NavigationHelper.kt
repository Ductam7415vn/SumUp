package com.example.sumup.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

/**
 * Helper class for consistent navigation patterns throughout the app
 */
object NavigationHelper {
    
    /**
     * Navigate to a destination with consistent behavior
     * - Single top launch mode to prevent duplicate destinations
     * - Save and restore state for bottom navigation
     */
    fun NavController.navigateWithDefaults(
        route: String,
        popUpToStart: Boolean = false,
        singleTop: Boolean = true,
        restoreState: Boolean = true,
        saveState: Boolean = true
    ) {
        navigate(route) {
            if (popUpToStart) {
                popUpTo(graph.findStartDestination().id) {
                    this.saveState = saveState
                }
            }
            launchSingleTop = singleTop
            this.restoreState = restoreState
        }
    }
    
    /**
     * Navigate back with consistent behavior
     */
    fun NavController.navigateBack(
        fallbackRoute: String? = null
    ): Boolean {
        return if (previousBackStackEntry != null) {
            popBackStack()
        } else if (fallbackRoute != null) {
            navigateWithDefaults(fallbackRoute, popUpToStart = true)
            true
        } else {
            false
        }
    }
    
    /**
     * Navigate to result screen with summary
     */
    fun NavController.navigateToResult(
        summaryId: String? = null
    ) {
        val route = if (summaryId != null) {
            Screen.Result.createRoute(summaryId)
        } else {
            Screen.Result.route
        }
        
        navigateWithDefaults(
            route = route,
            popUpToStart = false,
            singleTop = true
        )
    }
    
    /**
     * Navigate with shared element transition (for future implementation)
     */
    fun NavController.navigateWithTransition(
        route: String,
        sharedElements: Map<String, Any> = emptyMap()
    ) {
        // TODO: Implement shared element transitions when supported
        navigateWithDefaults(route)
    }
    
    /**
     * Clear back stack and navigate to destination
     */
    fun NavController.clearAndNavigate(route: String) {
        navigate(route) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}

/**
 * Navigation animation presets
 */
object NavigationAnimations {
    const val SLIDE_IN_DURATION = 300
    const val SLIDE_OUT_DURATION = 300
    const val FADE_IN_DURATION = 200
    const val FADE_OUT_DURATION = 200
}
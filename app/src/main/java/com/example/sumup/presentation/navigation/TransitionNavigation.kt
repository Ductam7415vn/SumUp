package com.example.sumup.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.sumup.presentation.components.SharedElementTransitionState
import com.example.sumup.presentation.components.rememberSharedElementTransition
import com.example.sumup.presentation.screens.history.HistoryScreen
import com.example.sumup.presentation.screens.main.AdaptiveMainScreen
import com.example.sumup.presentation.screens.ocr.OcrScreen
import com.example.sumup.presentation.screens.processing.ProcessingScreen
import com.example.sumup.presentation.screens.result.AdaptiveResultScreen
import com.example.sumup.presentation.screens.settings.SettingsScreen

/**
 * Enhanced navigation with shared element transitions
 */
@Composable
fun TransitionNavigation(
    navController: NavHostController = rememberNavController()
) {
    val sharedElementState = rememberSharedElementTransition()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        // Main Screen
        composable(
            route = Screen.Main.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Processing.route -> slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn()
                    Screen.Result.route -> scaleIn(
                        initialScale = 1.1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn()
                    else -> defaultEnterTransition()
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Processing.route -> slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeOut()
                    Screen.Ocr.route -> slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeOut()
                    else -> defaultExitTransition()
                }
            }
        ) {
            AdaptiveMainScreen(
                onNavigateToOcr = {
                    navController.navigate(Screen.Ocr.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToProcessing = {
                    navController.navigate(Screen.Processing.route)
                }
            )
        }
        
        // Processing Screen with custom transitions
        composable(
            route = Screen.Processing.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Result.route -> scaleOut(
                        targetScale = 0.9f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeOut()
                    else -> slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeOut()
                }
            }
        ) {
            ProcessingScreen(
                onCancel = {
                    navController.popBackStack()
                },
                onComplete = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Main.route)
                    }
                }
            )
        }
        
        // Result Screen with morphing transition
        composable(
            route = Screen.Result.route,
            enterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                scaleOut(
                    targetScale = 1.1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeOut()
            }
        ) {
            AdaptiveResultScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        
        // OCR Screen with slide up transition
        composable(
            route = Screen.Ocr.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn()
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut()
            }
        ) {
            OcrScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTextScanned = { scannedText ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("scanned_text", scannedText)
                    navController.popBackStack()
                }
            )
        }
        
        // Settings Screen with slide from right
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeOut()
            }
        ) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // History Screen with slide from left
        composable(
            route = Screen.History.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeOut()
            }
        ) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Default transition animations
 */
private fun defaultEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(300)
    ) + scaleIn(
        initialScale = 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
}

private fun defaultExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(300)
    ) + scaleOut(
        targetScale = 1.05f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
}

private fun defaultPopEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(300)
    ) + scaleIn(
        initialScale = 1.05f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
}

private fun defaultPopExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(300)
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
}
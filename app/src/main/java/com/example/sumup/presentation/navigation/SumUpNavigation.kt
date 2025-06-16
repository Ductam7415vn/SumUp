package com.example.sumup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sumup.presentation.screens.history.HistoryScreen
import com.example.sumup.presentation.screens.main.MainScreen
import com.example.sumup.presentation.screens.main.AdaptiveMainScreen
import com.example.sumup.presentation.screens.ocr.OcrScreen
import com.example.sumup.presentation.screens.onboarding.OnboardingScreen
import com.example.sumup.presentation.screens.processing.ProcessingScreen
import com.example.sumup.presentation.screens.result.ResultScreen
import com.example.sumup.presentation.screens.result.AdaptiveResultScreen
import com.example.sumup.presentation.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object Ocr : Screen("ocr")
    object Processing : Screen("processing")
    object Result : Screen("result")
    object Settings : Screen("settings")
    object History : Screen("history")
}

@Composable
fun SumUpNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
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
        
        composable(Screen.Processing.route) {
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
        
        composable(Screen.Result.route) {
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
        
        composable(Screen.Ocr.route) {
            OcrScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTextScanned = { scannedText ->
                    // Pass scanned text back to main screen
                    try {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("scanned_text", scannedText)
                        navController.popBackStack()
                    } catch (e: Exception) {
                        // Fallback if navigation state is invalid
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
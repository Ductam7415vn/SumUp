package com.example.sumup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sumup.presentation.screens.history.HistoryScreen
import com.example.sumup.presentation.screens.main.MainScreen
import com.example.sumup.presentation.screens.main.AdaptiveMainScreen
import com.example.sumup.presentation.screens.ocr.ImprovedOcrScreen
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
    object Result : Screen("result?summaryId={summaryId}") {
        fun createRoute(summaryId: String? = null) = if (summaryId != null) "result?summaryId=$summaryId" else "result"
    }
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
            MainScreen(
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
                },
                onNavigateToResult = { summaryId ->
                    navController.navigate(Screen.Result.createRoute(summaryId)) {
                        popUpTo(Screen.Main.route)
                    }
                }
            )
        }
        
        composable(Screen.Processing.route) {
            ProcessingScreen(
                onCancel = {
                    navController.popBackStack()
                },
                onComplete = { summaryId ->
                    if (summaryId != null) {
                        navController.navigate(Screen.Result.createRoute(summaryId)) {
                            popUpTo(Screen.Main.route)
                        }
                    } else {
                        // Fallback - navigate to result without ID (will load latest)
                        navController.navigate(Screen.Result.createRoute()) {
                            popUpTo(Screen.Main.route)
                        }
                    }
                }
            )
        }
        
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                androidx.navigation.navArgument("summaryId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val summaryId = backStackEntry.arguments?.getString("summaryId")
            AdaptiveResultScreen(
                summaryId = summaryId,
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
            ImprovedOcrScreen(
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
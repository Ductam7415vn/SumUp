package com.example.sumup.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sumup.R
import com.example.sumup.presentation.components.drawer.NavigationDrawer
import com.example.sumup.presentation.screens.history.HistoryScreen
import com.example.sumup.presentation.screens.history.HistoryViewModel
import com.example.sumup.presentation.screens.main.MainScreen
import com.example.sumup.presentation.screens.main.MainUiState
import com.example.sumup.presentation.screens.ocr.OcrScreen
import com.example.sumup.presentation.screens.processing.ProcessingScreen
import com.example.sumup.presentation.screens.result.AdaptiveResultScreen
import com.example.sumup.presentation.screens.settings.SettingsScreen
import com.example.sumup.presentation.utils.AdaptiveLayoutInfo
import com.example.sumup.presentation.utils.DeviceType
import com.example.sumup.presentation.utils.rememberAdaptiveLayoutInfo
import com.example.sumup.utils.haptic.rememberHapticFeedback
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveNavigation(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val adaptiveInfo = rememberAdaptiveLayoutInfo(windowSizeClass)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val hapticManager = rememberHapticFeedback()
    
    // Get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Main.route
    
    // Get history data
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val historyUiState by historyViewModel.uiState.collectAsStateWithLifecycle()
    
    // Flatten grouped summaries into a single list
    val allSummaries = historyUiState.groupedSummaries.values.flatten()
    
    // Get database size
    var databaseSize by remember { mutableStateOf("0 MB") }
    LaunchedEffect(Unit) {
        databaseSize = historyViewModel.getDatabaseSize()
    }
    
    NavigationDrawer(
        drawerState = drawerState,
        currentRoute = currentRoute,
        summaryHistory = allSummaries,
        userEmail = "user@example.com", // TODO: Get from user preferences
        totalSummaries = historyUiState.totalCount,
        storageUsed = databaseSize,
        onNavigateToHome = {
            navController.navigate(Screen.Main.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onNavigateToHistory = {
            navController.navigate(Screen.History.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onNavigateToSettings = {
            navController.navigate(Screen.Settings.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onNavigateToSummary = { summary ->
            // Navigate to result with summary ID
            navController.navigate("${Screen.Result.route}/${summary.id}")
        },
        onStartNewSummary = { inputType ->
            // Navigate to main and set input type
            navController.navigate(Screen.Main.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        hapticManager = hapticManager
    ) {
        // Main content with NavHost
        AdaptiveNavHost(
            navController = navController,
            adaptiveInfo = adaptiveInfo,
            drawerState = drawerState,
            modifier = modifier.fillMaxSize()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdaptiveNavHost(
    navController: NavHostController,
    adaptiveInfo: AdaptiveLayoutInfo,
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(300)
            )
        }
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
                    navController.navigate("${Screen.Result.route}/$summaryId") {
                        popUpTo(Screen.Main.route)
                    }
                },
                onOpenDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
        
        composable(Screen.Ocr.route) {
            OcrScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTextScanned = { scannedText ->
                    // Navigate to main with scanned text
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(Screen.Processing.route) {
            // Get the MainViewModel from the parent NavBackStackEntry
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.Main.route)
            }
            val mainViewModel: com.example.sumup.presentation.screens.main.MainViewModel = hiltViewModel(parentEntry)
            
            ProcessingScreen(
                viewModel = mainViewModel,
                onCancel = {
                    navController.popBackStack()
                },
                onComplete = { summaryId ->
                    if (summaryId != null) {
                        navController.navigate("${Screen.Result.route}/$summaryId") {
                            popUpTo(Screen.Main.route)
                        }
                    } else {
                        navController.navigate(Screen.Result.route) {
                            popUpTo(Screen.Processing.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
        
        // Result screen with summary ID parameter
        composable("${Screen.Result.route}/{summaryId}") { backStackEntry ->
            val summaryId = backStackEntry.arguments?.getString("summaryId")
            AdaptiveResultScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                adaptiveInfo = adaptiveInfo,
                summaryId = summaryId
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSummaryClick = { summaryId ->
                    // Navigate to result with summary ID
                    navController.navigate("${Screen.Result.route}/$summaryId")
                },
                adaptiveInfo = adaptiveInfo
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                adaptiveInfo = adaptiveInfo
            )
        }
    }
}
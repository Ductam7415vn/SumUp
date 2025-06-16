package com.example.sumup.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sumup.R
import com.example.sumup.presentation.screens.history.HistoryScreen
import com.example.sumup.presentation.screens.main.AdaptiveMainScreen
import com.example.sumup.presentation.screens.ocr.OcrScreen
import com.example.sumup.presentation.screens.processing.ProcessingScreen
import com.example.sumup.presentation.screens.result.AdaptiveResultScreen
import com.example.sumup.presentation.screens.settings.SettingsScreen
import com.example.sumup.presentation.utils.AdaptiveLayoutInfo
import com.example.sumup.presentation.utils.DeviceType
import com.example.sumup.presentation.utils.rememberAdaptiveLayoutInfo

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val showInBottomBar: Boolean = true,
    val showInNavRail: Boolean = true
)

val navigationItems = listOf(
    NavigationItem(
        route = Screen.Main.route,
        icon = Icons.Default.Home,
        selectedIcon = Icons.Default.Home,
        label = "Home"
    ),
    NavigationItem(
        route = Screen.History.route,
        icon = Icons.Default.History,
        selectedIcon = Icons.Default.History,
        label = "History"
    ),
    NavigationItem(
        route = Screen.Settings.route,
        icon = Icons.Default.Settings,
        selectedIcon = Icons.Default.Settings,
        label = "Settings"
    )
)

@Composable
fun AdaptiveNavigation(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val adaptiveInfo = rememberAdaptiveLayoutInfo(windowSizeClass)
    
    when {
        adaptiveInfo.shouldShowNavRail -> {
            NavigationRailLayout(
                navController = navController,
                adaptiveInfo = adaptiveInfo,
                modifier = modifier
            )
        }
        else -> {
            BottomNavigationLayout(
                navController = navController,
                adaptiveInfo = adaptiveInfo,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun NavigationRailLayout(
    navController: NavHostController,
    adaptiveInfo: AdaptiveLayoutInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        // Navigation Rail
        AdaptiveNavigationRail(
            navController = navController,
            adaptiveInfo = adaptiveInfo
        )
        
        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AdaptiveNavHost(
                navController = navController,
                adaptiveInfo = adaptiveInfo,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BottomNavigationLayout(
    navController: NavHostController,
    adaptiveInfo: AdaptiveLayoutInfo,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (adaptiveInfo.shouldShowBottomBar) {
                AdaptiveBottomNavigation(
                    navController = navController,
                    adaptiveInfo = adaptiveInfo
                )
            }
        }
    ) { paddingValues ->
        AdaptiveNavHost(
            navController = navController,
            adaptiveInfo = adaptiveInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun AdaptiveNavigationRail(
    navController: NavHostController,
    adaptiveInfo: AdaptiveLayoutInfo,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // FAB for tablets
        if (adaptiveInfo.deviceType == DeviceType.TABLET) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Ocr.route) },
                modifier = Modifier.padding(bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Scan Text"
                )
            }
        }
        
        navigationItems.filter { it.showInNavRail }.forEach { item ->
            NavigationRailItem(
                icon = {
                    Icon(
                        if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                            item.selectedIcon
                        } else {
                            item.icon
                        },
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = adaptiveInfo.deviceType == DeviceType.TABLET
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AdaptiveBottomNavigation(
    navController: NavHostController,
    adaptiveInfo: AdaptiveLayoutInfo,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        navigationItems.filter { it.showInBottomBar }.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                            item.selectedIcon
                        } else {
                            item.icon
                        },
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun AdaptiveNavHost(
    navController: NavHostController,
    adaptiveInfo: AdaptiveLayoutInfo,
    modifier: Modifier = Modifier
) {
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
                },
                adaptiveInfo = adaptiveInfo
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
            ProcessingScreen(
                onCancel = {
                    navController.popBackStack()
                },
                onComplete = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Processing.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(Screen.Result.route) {
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
                adaptiveInfo = adaptiveInfo
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSummaryClick = { summaryId ->
                    // Navigate to result with summary ID
                    navController.navigate(Screen.Result.route)
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
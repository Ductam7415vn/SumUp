package com.example.sumup

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sumup.domain.repository.SettingsRepository
import com.example.sumup.presentation.navigation.AdaptiveNavigation
import com.example.sumup.presentation.navigation.Screen
import com.example.sumup.presentation.navigation.TransitionNavigation
import com.example.sumup.presentation.screens.onboarding.OnboardingScreen
import com.example.sumup.presentation.screens.settings.ThemeMode
import com.example.sumup.ui.theme.SumUpTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var apiKeyMigration: com.example.sumup.utils.migration.ApiKeyMigration
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge and configure system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Perform API key migration on app startup
        lifecycleScope.launch {
            try {
                val migrated = apiKeyMigration.migrateIfNeeded()
                if (migrated) {
                    android.util.Log.d("MainActivity", "API key migration completed successfully")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error during API key migration", e)
            }
        }
        
        // Predictive back gesture is handled by individual screens
        
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val themeMode by settingsRepository.getThemeMode()
                .collectAsStateWithLifecycle(
                    initialValue = ThemeMode.SYSTEM,
                    lifecycle = lifecycle
                )
            
            val isDynamicColorEnabled by settingsRepository.isDynamicColorEnabled()
                .collectAsStateWithLifecycle(
                    initialValue = true,
                    lifecycle = lifecycle
                )
                
            val isOnboardingCompleted by settingsRepository.isOnboardingCompleted
                .collectAsStateWithLifecycle(initialValue = false)
            
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
            // Debug logging
            android.util.Log.d("MainActivity", "Theme: $themeMode, Dark: $darkTheme, Dynamic: $isDynamicColorEnabled")
            
            SumUpTheme(
                darkTheme = darkTheme,
                dynamicColor = isDynamicColorEnabled
            ) {
                val navController = rememberNavController()
                
                val startDestination = if (isOnboardingCompleted) {
                    "main_flow"
                } else {
                    Screen.Onboarding.route
                }
                
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(
                            onOnboardingComplete = {
                                navController.navigate("main_flow") {
                                    popUpTo(Screen.Onboarding.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            onNavigateToSettings = {
                                // Navigate to main flow and then to settings
                                navController.navigate("main_flow") {
                                    popUpTo(Screen.Onboarding.route) {
                                        inclusive = true
                                    }
                                }
                                // Don't automatically mark onboarding as completed when navigating to settings
                                // User should explicitly complete onboarding through the proper flow
                            }
                        )
                    }
                    
                    composable("main_flow") {
                        AdaptiveNavigation(
                            windowSizeClass = windowSizeClass
                        )
                    }
                }
            }
        }
    }
}
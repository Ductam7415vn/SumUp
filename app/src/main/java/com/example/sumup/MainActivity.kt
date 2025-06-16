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
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Predictive back gesture is handled by individual screens
        
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val themeMode by settingsRepository.getThemeMode()
                .stateIn(
                    scope = lifecycleScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = ThemeMode.SYSTEM
                )
                .collectAsState()
            
            val isDynamicColorEnabled by settingsRepository.isDynamicColorEnabled()
                .stateIn(
                    scope = lifecycleScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = false
                )
                .collectAsState()
                
            val isOnboardingCompleted by settingsRepository.isOnboardingCompleted
                .collectAsStateWithLifecycle(initialValue = false)
            
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
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
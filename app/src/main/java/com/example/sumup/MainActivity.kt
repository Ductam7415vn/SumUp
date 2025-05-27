package com.example.sumup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.sumup.domain.repository.SettingsRepository
import com.example.sumup.presentation.navigation.SumUpNavigation
import com.example.sumup.presentation.screens.settings.ThemeMode
import com.example.sumup.ui.theme.SumUpTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
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
            
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
            SumUpTheme(
                darkTheme = darkTheme,
                dynamicColor = isDynamicColorEnabled
            ) {
                SumUpNavigation()
            }
        }
    }
}
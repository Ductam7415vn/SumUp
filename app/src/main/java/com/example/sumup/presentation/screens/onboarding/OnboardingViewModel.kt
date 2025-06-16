package com.example.sumup.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.OnboardingData
import com.example.sumup.domain.model.OnboardingPage
import com.example.sumup.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPage> = OnboardingData.pages,
    val isLastPage: Boolean = false,
    val isLoading: Boolean = false,
    val canNavigateNext: Boolean = true,
    val showSkipButton: Boolean = true
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    init {
        updatePageState()
    }
    
    fun nextPage() {
        val currentState = _uiState.value
        if (currentState.currentPage < currentState.pages.size - 1) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage + 1
            )
            updatePageState()
        }
    }
    
    fun previousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage > 0) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage - 1
            )
            updatePageState()
        }
    }
    
    fun skipOnboarding() {
        completeOnboarding()
    }
    
    fun completeOnboarding() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                // Mark onboarding as completed in settings
                settingsRepository.setOnboardingCompleted(true)
            } catch (e: Exception) {
                // Handle error - for now just complete anyway
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    fun jumpToPage(pageIndex: Int) {
        val currentState = _uiState.value
        if (pageIndex in 0 until currentState.pages.size) {
            _uiState.value = currentState.copy(
                currentPage = pageIndex
            )
            updatePageState()
        }
    }
    
    private fun updatePageState() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isLastPage = currentState.currentPage == currentState.pages.size - 1,
            showSkipButton = currentState.currentPage < currentState.pages.size - 1
        )
    }
}
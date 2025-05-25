package ru.takeshiko.matuleme.presentation.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.usecase.CompleteOnboardingUseCase

class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboardingUseCase
) : ViewModel() {

    private val _onboardingSuccess = MutableStateFlow(false)
    val onboardingSuccess: StateFlow<Boolean> = _onboardingSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun attemptCompleteOnboarding() {
        viewModelScope.launch {
            completeOnboarding()
            _onboardingSuccess.value = true
        }
    }
}
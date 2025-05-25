package ru.takeshiko.matuleme.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.usecase.CheckFirstLaunchUseCase
import ru.takeshiko.matuleme.domain.usecase.CheckLoginStatusUseCase
import ru.takeshiko.matuleme.presentation.components.navigation.NavigationState

class SplashViewModel(
    private val checkFirstLaunch: CheckFirstLaunchUseCase,
    private val checkLogin: CheckLoginStatusUseCase
) : ViewModel() {

    private val _navState = MutableStateFlow<NavigationState>(NavigationState.Loading)
    val navState: StateFlow<NavigationState> = _navState.asStateFlow()

    private val _checkSuccess = MutableStateFlow(false)
    val checkSuccess: StateFlow<Boolean> = _checkSuccess.asStateFlow()

    fun retryCheck(
        attempts: Int = 5,
        retryDelayMs: Long = 500L
    ) = viewModelScope.launch {
        val firstLaunch = try {
            checkFirstLaunch()
        } catch (_: Exception) {
            false
        }

        if (firstLaunch) {
            _navState.value = NavigationState.Onboarding
            _checkSuccess.value = true
            return@launch
        }

        repeat(attempts) { index ->
            val loginResult = runCatching { checkLogin().getOrElse { false } }

            if (loginResult.isSuccess && loginResult.getOrNull() == true) {
                _navState.value = NavigationState.Main
                _checkSuccess.value = true
                return@launch
            }

            if (index < attempts - 1) delay(retryDelayMs)
        }

        _navState.value = NavigationState.Login
        _checkSuccess.value = true
    }
}

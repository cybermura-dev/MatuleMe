package ru.takeshiko.matuleme.presentation.components.navigation

sealed class NavigationState {
    data object Loading : NavigationState()
    data object Onboarding : NavigationState()
    data object Login : NavigationState()
    data object Main : NavigationState()
    data class Error(val message: String) : NavigationState()
}
package ru.takeshiko.matuleme.presentation.screen.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.usecase.ValidateEmailUseCase

class ForgotPasswordViewModel(
    private val validateEmail: ValidateEmailUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _inputSuccess = MutableStateFlow(false)
    val inputSuccess: StateFlow<Boolean> = _inputSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
    }

    fun validateEmail() {
        _emailError.value = null

        val currentEmail = _email.value

        val emailValidationResult = validateEmail(currentEmail)

        if (emailValidationResult.isFailure) {
            val errorMessage = emailValidationResult.exceptionOrNull()?.message ?: "Invalid email"
            _emailError.value = errorMessage
            _errorMessage.value = errorMessage
            return
        }

        _inputSuccess.value = true
    }
}
package ru.takeshiko.matuleme.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.usecase.LoginWithEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidateEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidatePasswordUseCase

class LoginViewModel(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val loginWithEmail: LoginWithEmailUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail;
        _emailError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword;
        _passwordError.value = null
    }

    fun attemptLogin() {
        _emailError.value = null
        _passwordError.value = null

        val currentEmail = _email.value
        val currentPassword = _password.value

        val emailValidationResult = validateEmail(currentEmail)
        val passwordValidationResult = validatePassword(currentPassword)

        if (emailValidationResult.isFailure) {
            val errorMessage = emailValidationResult.exceptionOrNull()?.message ?: "Invalid email"
            _emailError.value = errorMessage
            _errorMessage.value = errorMessage
            return
        }

        if (passwordValidationResult.isFailure) {
            val errorMessage = passwordValidationResult.exceptionOrNull()?.message ?: "Invalid password"
            _passwordError.value = errorMessage
            _errorMessage.value = errorMessage
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            loginWithEmail(currentEmail, currentPassword).onSuccess { session ->
                _loginSuccess.value = true
                _isLoading.value = false
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "An unknown error has occurred"
                _errorMessage.value = errorMessage
                _isLoading.value = false
            }
        }
    }
}
package ru.takeshiko.matuleme.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.RegisterWithEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidateEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidatePasswordUseCase

class RegisterViewModel(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val registerWithEmail: RegisterWithEmailUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword: StateFlow<String> = _repeatPassword.asStateFlow()

    private val _policyAccepted = MutableStateFlow(false)
    val policyAccepted: StateFlow<Boolean> = _policyAccepted.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _repeatPasswordError = MutableStateFlow<String?>(null)
    val repeatPasswordError: StateFlow<String?> = _repeatPasswordError.asStateFlow()

    private val _policyError = MutableStateFlow<String?>(null)
    val policyError: StateFlow<String?> = _policyError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = null
    }

    fun onRepeatPasswordChange(newRepeatPassword: String) {
        _repeatPassword.value = newRepeatPassword
        _repeatPasswordError.value = null
    }

    fun onPolicyCheckedChange(isChecked: Boolean) {
        _policyAccepted.value = isChecked
        _policyError.value = null
    }

    fun attemptRegister() {
        _emailError.value = null
        _passwordError.value = null
        _repeatPasswordError.value = null
        _policyError.value = null
        _errorMessage.value = null

        val currentEmail = _email.value
        val currentPassword = _password.value
        val currentRepeatPassword = _repeatPassword.value
        val currentPolicyAccepted = _policyAccepted.value

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

        if (currentPassword != currentRepeatPassword) {
            val errorMessage = stringResourceProvider.getString(R.string.error_repeat_password_verify)
            _repeatPasswordError.value = errorMessage
            _errorMessage.value = errorMessage
            return
        }

        if (!currentPolicyAccepted) {
            val errorMessage = stringResourceProvider.getString(R.string.error_policy_check)
            _errorMessage.value = errorMessage
            _policyError.value = errorMessage
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            registerWithEmail(currentEmail, currentPassword).onSuccess {
                _registrationSuccess.value = true
                _isLoading.value = false
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "An unknown error has occurred!"
                _errorMessage.value = errorMessage
                _isLoading.value = false
            }
        }
    }

    fun clearRegistrationSuccess() {
        _registrationSuccess.value = false
    }
}
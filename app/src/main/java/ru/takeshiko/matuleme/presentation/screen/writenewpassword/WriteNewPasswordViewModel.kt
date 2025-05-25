package ru.takeshiko.matuleme.presentation.screen.writenewpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.SetNewPasswordUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidatePasswordUseCase

class WriteNewPasswordViewModel(
    private val validatePassword: ValidatePasswordUseCase,
    private val setNewPassword: SetNewPasswordUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _newPasswordError = MutableStateFlow<String?>(null)
    val newPasswordError: StateFlow<String?> = _newPasswordError.asStateFlow()

    private val _newPasswordRepeat = MutableStateFlow("")
    val newPasswordRepeat: StateFlow<String> = _newPasswordRepeat.asStateFlow()

    private val _newPasswordRepeatError = MutableStateFlow<String?>(null)
    val newPasswordRepeatError: StateFlow<String?> = _newPasswordRepeatError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _passwordResetSuccess = MutableStateFlow(false)
    val passwordResetSuccess: StateFlow<Boolean> = _passwordResetSuccess.asStateFlow()

    fun onNewPasswordChange(password: String) {
        _newPassword.value = password
        _newPasswordError.value = null
        if (_newPasswordRepeatError.value != null) {
            _newPasswordRepeatError.value = null
        }
    }

    fun onNewPasswordRepeatChange(password: String) {
        _newPasswordRepeat.value = password
        _newPasswordRepeatError.value = null
    }

    fun validateNewPassword() {
        _newPasswordError.value = null
        _newPasswordRepeatError.value = null
        _errorMessage.value = null
        _passwordResetSuccess.value = false

        val newPass = _newPassword.value
        val repeatPass = _newPasswordRepeat.value

        val passwordValidationResult = validatePassword(newPass)
        if (passwordValidationResult.isFailure) {
            _newPasswordError.value = passwordValidationResult.exceptionOrNull()?.message
            _errorMessage.value = _newPasswordError.value
            return
        }

        if (newPass != repeatPass) {
            _newPasswordRepeatError.value = stringResourceProvider.getString(R.string.error_passwords_do_not_match)
            _errorMessage.value = _newPasswordRepeatError.value
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val result = setNewPassword(newPass)

            _isLoading.value = false

            result.onSuccess {
                _passwordResetSuccess.value = true
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: stringResourceProvider.getString(R.string.error_password_reset_failed)
            }
        }
    }

    fun passwordResetHandled() {
        _passwordResetSuccess.value = false
    }
}
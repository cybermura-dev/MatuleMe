package ru.takeshiko.matuleme.presentation.screen.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.ConfirmResetPasswordUseCase
import ru.takeshiko.matuleme.domain.usecase.ResetPasswordUseCase

class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val confirmResetPasswordUseCase: ConfirmResetPasswordUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    var email: String? = null
        set(value) {
            field = value
            if (!value.isNullOrBlank() && !_isCodeSent.value) {
                sendResetCode()
            }
        }

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _otpCodeError = MutableStateFlow<String?>(null)
    val otpCodeError: StateFlow<String?> = _otpCodeError.asStateFlow()

    private val _resetSuccess = MutableStateFlow(false)
    val resetSuccess: StateFlow<Boolean> = _resetSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isCodeSent = MutableStateFlow(false)
    val isCodeSent: StateFlow<Boolean> = _isCodeSent.asStateFlow()

    fun onOtpCodeChange(newOtpCode: String) {
        if (newOtpCode.length <= 6) {
            _otpCode.value = newOtpCode
            _otpCodeError.value = null
        }
    }

    fun sendResetCode() {
        _errorMessage.value = null
        val currentEmail = email

        if (currentEmail.isNullOrBlank()) {
            _errorMessage.value = stringResourceProvider.getString(R.string.error_email_empty)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            resetPasswordUseCase(currentEmail).onSuccess {
                _isCodeSent.value = true
                _isLoading.value = false
                _errorMessage.value = null
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: stringResourceProvider.getString(R.string.unknown_error_occurred)
                _isLoading.value = false
            }
        }
    }

    fun resendOtpCode() {
        sendResetCode()
    }

    fun resetPassword() {
        _errorMessage.value = null
        val currentEmail = email
        val currentOtpCode = _otpCode.value

        if (currentEmail.isNullOrBlank()) {
            _errorMessage.value = stringResourceProvider.getString(R.string.error_email_empty)
            return
        }

        if (currentOtpCode.isBlank()) {
            _otpCodeError.value = stringResourceProvider.getString(R.string.error_otp_code_blank)
            return
        }

        if (currentOtpCode.length != 6 || !currentOtpCode.all { it.isDigit() }) {
            _otpCodeError.value = stringResourceProvider.getString(R.string.error_otp_code_invalid_format)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            confirmResetPasswordUseCase(currentEmail, currentOtpCode).onSuccess {
                _resetSuccess.value = true
                _isLoading.value = false
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: stringResourceProvider.getString(R.string.unknown_error_occurred)
                _isLoading.value = false
                if (exception is IllegalArgumentException) {
                    _otpCodeError.value = exception.message
                }
            }
        }
    }
}
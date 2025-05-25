package ru.takeshiko.matuleme.presentation.screen.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.usecase.ResendOtpUseCase
import ru.takeshiko.matuleme.domain.usecase.VerifyOtpUseCase

class OtpVerificationViewModel(
    private val resendOtp: ResendOtpUseCase,
    private val verifyOtp: VerifyOtpUseCase
) : ViewModel() {

    var email: String? = null

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _otpCodeError = MutableStateFlow<String?>("")
    val otpCodeError: StateFlow<String?> = _otpCodeError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _verificationSuccess = MutableStateFlow(false)
    val verificationSuccess: StateFlow<Boolean> = _verificationSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onOtpCodeChange(newOtpCode: String) {
        _otpCode.value = newOtpCode
        _otpCodeError.value = null
    }

    fun resendOtpCode() {
        _errorMessage.value = null

        viewModelScope.launch {
            _isLoading.value = true

            resendOtp(email ?: "").onSuccess {
                _isLoading.value = false
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "An unknown error has occurred!"
                _errorMessage.value = errorMessage
                _isLoading.value = false
            }
        }
    }

    fun verify() {
        _errorMessage.value = null

        viewModelScope.launch {
            _isLoading.value = true
            val result = verifyOtp(email ?: "", _otpCode.value)

            result.onSuccess {
                _verificationSuccess.value = true
                _isLoading.value = false
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "An unknown error has occurred!"
                _errorMessage.value = errorMessage
                _isLoading.value = false
            }
        }
    }
}
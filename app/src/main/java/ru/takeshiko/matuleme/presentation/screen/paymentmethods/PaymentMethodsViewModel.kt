package ru.takeshiko.matuleme.presentation.screen.paymentmethods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.DeletePaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultPaymentUseCase

class PaymentMethodsViewModel(
    private val getUser: GetUserUseCase,
    private val getPayments: GetPaymentsUseCase,
    private val deletePayment: DeletePaymentUseCase,
    private val setDefaultPayment: SetDefaultPaymentUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _payments = MutableStateFlow<List<UserPaymentDto>>(emptyList())
    val payments: StateFlow<List<UserPaymentDto>> = _payments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadPayments() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val userResult = getUser()
            val user = userResult.getOrNull()
            if (userResult.isFailure || user == null) {
                _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            userId = user.id

            getPayments(user.id).onSuccess { list ->
                _payments.value = list.sortedByDescending { it.isDefault }

                if (list.none { it.isDefault } && list.isNotEmpty()) {
                    val firstPaymentId = list.first().id ?: ""

                    if (firstPaymentId.isNotEmpty()) {
                        setDefault(firstPaymentId)
                    }
                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun deleteExistingPayment(paymentId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val currentPayment = _payments.value.find { it.id == paymentId }
            val isDefault = currentPayment?.isDefault == true

            deletePayment(paymentId).onSuccess {
                if (isDefault) {
                    loadPayments()

                    val remainingPayments = _payments.value

                    if (remainingPayments.isNotEmpty()) {
                        val newDefaultPaymentId = remainingPayments.first().id ?: ""

                        if (newDefaultPaymentId.isNotEmpty()) {
                            setDefault(newDefaultPaymentId)
                        }
                    }
                } else {
                    loadPayments()
                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun setDefault(paymentId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                _isLoading.value = false
                return@launch
            }

            setDefaultPayment(cachedUserId, paymentId).onSuccess {
                loadPayments()
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }
}
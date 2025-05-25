package ru.takeshiko.matuleme.presentation.screen.updatepaymentmethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.usecase.GetPaymentByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultPaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdatePaymentUseCase

class UpdatePaymentMethodViewModel(
    private val getPaymentById: GetPaymentByIdUseCase,
    private val updatePayment: UpdatePaymentUseCase,
    private val getUser: GetUserUseCase,
    private val getPayments: GetPaymentsUseCase,
    private val setDefaultPayment: SetDefaultPaymentUseCase
) : ViewModel() {

    private val _payment = MutableStateFlow<UserPaymentDto?>(null)
    val payment: StateFlow<UserPaymentDto?> = _payment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun fetchPayment(paymentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _saved.value = false

            val userResult = getUser()
            val user = userResult.getOrNull()

            if (userResult.isFailure || user == null) {
                _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            getPaymentById(paymentId).onSuccess {
                    _payment.value = it
                }.onFailure {
                    _errorMessage.value = it.localizedMessage
                }
            _isLoading.value = false
        }
    }

    fun updatePaymentMethod(updatedPayment: UserPaymentDto) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _saved.value = false

            if (userId == null) {
                val userResult = getUser()
                val user = userResult.getOrNull()
                if (userResult.isFailure || user == null) {
                    _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                    _isLoading.value = false
                    return@launch
                }
                userId = user.id
            }

            updatedPayment.userId = userId ?: ""

            val mainUpdateResult = updatePayment(updatedPayment)

            if (mainUpdateResult.isFailure) {
                _errorMessage.value = mainUpdateResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            _payment.value = mainUpdateResult.getOrNull()

            if (updatedPayment.isDefault == true) {
                val setDefaultResult = setDefaultPayment(userId!!, updatedPayment.id!!)
                if (setDefaultResult.isFailure) {
                    _errorMessage.value = setDefaultResult.exceptionOrNull()?.localizedMessage
                }
                _saved.value = true
            } else {
                val paymentsResult = getPayments(userId!!)
                val currentPayments = paymentsResult.getOrNull() ?: emptyList()

                if (paymentsResult.isFailure) {
                    _errorMessage.value = paymentsResult.exceptionOrNull()?.localizedMessage
                } else if (currentPayments.isNotEmpty() && currentPayments.none { it.isDefault == true }) {
                    val firstPaymentId = currentPayments.firstOrNull()?.id
                    if (firstPaymentId != null && firstPaymentId.isNotEmpty()) {
                        setDefaultPayment(userId!!, firstPaymentId).onFailure {
                            _errorMessage.value = it.localizedMessage
                        }
                    }
                }
                _saved.value = true
            }

            _isLoading.value = false
        }
    }
}
package ru.takeshiko.matuleme.presentation.screen.addpaymentmethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.usecase.AddPaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultPaymentUseCase

class AddPaymentMethodViewModel(
    private val addPayment: AddPaymentUseCase,
    private val getUser: GetUserUseCase,
    private val getPayments: GetPaymentsUseCase,
    private val setDefaultPayment: SetDefaultPaymentUseCase
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun addNewPayment(payment: UserPaymentDto) {
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

            payment.userId = userId ?: ""

            if (payment.isDefault) {
                val payments = getPayments(userId ?: "").getOrNull() ?: emptyList()

                if (payments.isNotEmpty()) {
                    val addResult = addPayment(payment)
                    if (addResult.isFailure) {
                        _errorMessage.value = addResult.exceptionOrNull()?.localizedMessage
                        _isLoading.value = false
                        return@launch
                    }

                    val newPaymentId = addResult.getOrNull()?.id ?: ""
                    if (newPaymentId.isNotEmpty()) {
                        setDefaultPayment(userId ?: "", newPaymentId).onFailure {
                            _errorMessage.value = it.localizedMessage
                            _isLoading.value = false
                            return@launch
                        }
                    }

                    _saved.value = true
                } else {
                    addPayment(payment).onSuccess {
                        _saved.value = true
                    }.onFailure {
                        _errorMessage.value = it.localizedMessage
                    }
                }
            } else {
                addPayment(payment).onSuccess {
                    _saved.value = true
                }.onFailure {
                    _errorMessage.value = it.localizedMessage
                }

                val payments = getPayments(userId ?: "").getOrNull() ?: emptyList()
                if (payments.isNotEmpty() && payments.none { it.isDefault }) {
                    val firstPaymentId = payments.first().id ?: ""
                    if (firstPaymentId.isNotEmpty()) {
                        setDefaultPayment(userId ?: "", firstPaymentId).onFailure {
                            _errorMessage.value = it.localizedMessage
                        }
                    }
                }
            }

            _isLoading.value = false
        }
    }
}
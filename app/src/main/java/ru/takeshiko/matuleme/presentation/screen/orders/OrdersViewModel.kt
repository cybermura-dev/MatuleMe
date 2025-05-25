package ru.takeshiko.matuleme.presentation.screen.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.GetOrdersUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _orders = MutableStateFlow<List<UserOrderDto>>(emptyList())
    val orders: StateFlow<List<UserOrderDto>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _orders.value = emptyList()

            if (userId == null) {
                val userResult = getUserUseCase()
                userResult.onSuccess { user ->
                    userId = user?.id
                }.onFailure { exception ->
                    _errorMessage.value = exception.localizedMessage ?: stringResourceProvider.getString(R.string.unknown_error_occurred)
                    _isLoading.value = false
                    return@launch
                }
            }

            val currentUserId = userId
            if (currentUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                _isLoading.value = false
                _orders.value = emptyList()
                return@launch
            }

            getOrdersUseCase(currentUserId).onSuccess { ordersList ->
                _orders.value = ordersList
            }.onFailure { exception ->
                _errorMessage.value = exception.localizedMessage ?: stringResourceProvider.getString(R.string.unknown_error_occurred)
                _orders.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
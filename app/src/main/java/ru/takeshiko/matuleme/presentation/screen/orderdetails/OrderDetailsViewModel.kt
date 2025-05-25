package ru.takeshiko.matuleme.presentation.screen.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.GetOrderByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetOrderItemsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateOrderStatusUseCase

class OrderDetailsViewModel(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getOrderItemsUseCase: GetOrderItemsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {
    private val _order = MutableStateFlow<UserOrderDto?>(null)
    val order: StateFlow<UserOrderDto?> = _order.asStateFlow()

    private val _items = MutableStateFlow<List<OrderProductItemDto>>(emptyList())
    val items: StateFlow<List<OrderProductItemDto>> = _items.asStateFlow()

    private val _products = MutableStateFlow<Map<String, ProductDto>>(emptyMap())
    val products: StateFlow<Map<String, ProductDto>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _order.value = null
            _items.value = emptyList()
            _products.value = emptyMap()

            getOrderByIdUseCase(orderId).onSuccess { orderDto ->
                _order.value = orderDto
                getOrderItemsUseCase(orderId).onSuccess { itemsList ->
                    _items.value = itemsList
                    loadProducts(itemsList)
                }.onFailure { ex ->
                    _errorMessage.value = ex.localizedMessage ?: stringResourceProvider.getString(ru.takeshiko.matuleme.R.string.unknown_error_occurred)
                }
            }.onFailure { ex ->
                _errorMessage.value = ex.localizedMessage ?: stringResourceProvider.getString(ru.takeshiko.matuleme.R.string.unknown_error_occurred)
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadProducts(items: List<OrderProductItemDto>) {
        val productsMap = mutableMapOf<String, ProductDto>()
        items.forEach { item ->
            getProductByIdUseCase(item.productId).onSuccess { product ->
                if (product != null) {
                    productsMap[item.productId] = product
                }
            }.onFailure { ex ->
                _errorMessage.value = ex.localizedMessage ?: stringResourceProvider.getString(ru.takeshiko.matuleme.R.string.unknown_error_occurred)
            }
        }
        _products.value = productsMap
    }

    fun payForOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            updateOrderStatusUseCase(orderId, OrderStatus.PAID).onSuccess { orderDto ->
                _order.value = orderDto
            }.onFailure { ex ->
                _errorMessage.value = ex.localizedMessage ?: stringResourceProvider.getString(ru.takeshiko.matuleme.R.string.unknown_error_occurred)
            }
            _isLoading.value = false
        }
    }

    fun confirmOrderReceived(orderId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            updateOrderStatusUseCase(orderId, OrderStatus.COMPLETED).onSuccess { orderDto ->
                _order.value = orderDto
                onSuccess()
            }.onFailure { ex ->
                _errorMessage.value = ex.localizedMessage ?: stringResourceProvider.getString(ru.takeshiko.matuleme.R.string.unknown_error_occurred)
            }
            _isLoading.value = false
        }
    }

    fun cancelOrder(orderId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentOrder = _order.value
            if (currentOrder != null && currentOrder.status != OrderStatus.SHIPPED && currentOrder.status != OrderStatus.COMPLETED) {
                updateOrderStatusUseCase(orderId, OrderStatus.CANCELLED).onSuccess { orderDto ->
                    _order.value = orderDto
                    onSuccess()
                }.onFailure { ex ->
                    _errorMessage.value = ex.localizedMessage ?: stringResourceProvider.getString(R.string.unknown_error_occurred)
                }
            } else {
                _errorMessage.value = stringResourceProvider.getString(R.string.cannot_cancel_shipped_or_completed_order)
            }
            _isLoading.value = false
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
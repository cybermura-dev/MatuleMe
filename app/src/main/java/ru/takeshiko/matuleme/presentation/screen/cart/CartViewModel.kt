package ru.takeshiko.matuleme.presentation.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.domain.models.UserCartItemDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.GetActivePromotionUseCase
import ru.takeshiko.matuleme.domain.usecase.GetCartItemsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.RemoveFromCartItemUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateCartItemQuantityUseCase

class CartViewModel(
    private val getUser: GetUserUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val updateCartItemQuantity: UpdateCartItemQuantityUseCase,
    private val removeFromCartItem: RemoveFromCartItemUseCase,
    private val getProductById: GetProductByIdUseCase,
    private val getActivePromotion: GetActivePromotionUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<UserCartItemDto>>(emptyList())
    val cartItems: StateFlow<List<UserCartItemDto>> = _cartItems.asStateFlow()

    private val _productDetailsMap = MutableStateFlow<Map<String, ProductDto>>(emptyMap())
    val productDetailsMap: StateFlow<Map<String, ProductDto>> = _productDetailsMap.asStateFlow()

    private val _promotionsMap = MutableStateFlow<Map<String, PromotionDto>>(emptyMap())
    val promotionsMap: StateFlow<Map<String, PromotionDto>> = _promotionsMap.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _processingCartItemIds = MutableStateFlow<Set<String>>(emptySet())
    val processingCartItemIds: StateFlow<Set<String>> = _processingCartItemIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val userResult = getUser()
            val user = userResult.getOrNull()

            if (userResult.isFailure || user == null) {
                _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                _cartItems.value = emptyList()
                _productDetailsMap.value = emptyMap()
                _promotionsMap.value = emptyMap()
                return@launch
            }

            userId = user.id

            val cartItemsResult = getCartItems(user.id)

            if (cartItemsResult.isFailure) {
                _errorMessage.value = cartItemsResult.exceptionOrNull()?.message
                _isLoading.value = false
                _cartItems.value = emptyList()
                _productDetailsMap.value = emptyMap()
                _promotionsMap.value = emptyMap()
                return@launch
            }

            val userCartItems = cartItemsResult.getOrThrow()
            _cartItems.value = userCartItems

            loadAdditionalDataForCartItems(userCartItems)

            _isLoading.value = false
        }
    }

    private fun loadAdditionalDataForCartItems(cartItems: List<UserCartItemDto>) {
        if (cartItems.isEmpty()) return

        viewModelScope.launch {
            coroutineScope {
                val productsMap = _productDetailsMap.value.toMutableMap()
                val promotionsMap = _promotionsMap.value.toMutableMap()
                val productIds = cartItems.map { it.productId }.toSet()

                productIds.map { productId ->
                    launch {
                        if (productsMap[productId] == null) {
                            getProductById(productId).onSuccess { product ->
                                product?.let { productsMap[productId] = it }
                            }
                        }

                        if (promotionsMap[productId] == null) {
                            getActivePromotion(productId).onSuccess { promotion ->
                                promotion?.let { promotionsMap[productId] = it }
                            }
                        }
                    }
                }.forEach { it.join() }

                _productDetailsMap.value = productsMap
                _promotionsMap.value = promotionsMap
            }
        }
    }

    fun incrementQuantity(productId: String) {
        modifyQuantity(productId) { current ->
            if (current >= 100) throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_max_quantity_reached)
            )
            current + 1
        }
    }

    fun decrementQuantity(productId: String) {
        modifyQuantity(productId) { current ->
            if (current <= 1) throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_min_quantity_reached)
            )
            current - 1
        }
    }

    private fun modifyQuantity(productId: String, update: (Int) -> Int) {
        viewModelScope.launch {
            _errorMessage.value = null

            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            val item = _cartItems.value.find { it.productId == productId } ?: run {
                return@launch
            }

            val newQuantity = try {
                update(item.quantity)
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message
                return@launch
            }

            val currentItemId = item.id
            if (currentItemId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_product_id)
                return@launch
            }

            try {
                _processingCartItemIds.value = _processingCartItemIds.value + currentItemId

                val result = updateCartItemQuantity(currentItemId, newQuantity)

                result.onSuccess {
                    _cartItems.value = _cartItems.value.map {
                        if (it.id == currentItemId) it.copy(quantity = newQuantity) else it
                    }
                }.onFailure {
                    _errorMessage.value = it.message
                }
            } finally {
                _processingCartItemIds.value = _processingCartItemIds.value - currentItemId
            }
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            _errorMessage.value = null

            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            val itemToRemove = _cartItems.value.find { it.productId == productId && it.userId == cachedUserId } ?: run {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            val currentItemId = itemToRemove.id
            if (currentItemId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_product_id)
                return@launch
            }

            try {
                _processingCartItemIds.value = _processingCartItemIds.value + currentItemId

                val result = removeFromCartItem(cachedUserId, productId)

                result.onSuccess {
                    _cartItems.value = _cartItems.value.filterNot { it.id == currentItemId }
                }.onFailure {
                    _errorMessage.value = it.message
                }
            } finally {
                _processingCartItemIds.value = _processingCartItemIds.value - currentItemId
            }
        }
    }
}
package ru.takeshiko.matuleme.presentation.screen.productdetails

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
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.AddToCartItemUseCase
import ru.takeshiko.matuleme.domain.usecase.AddToFavoritesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetActivePromotionUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAverageRatingUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetReviewCountUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.IsFavoriteUseCase
import ru.takeshiko.matuleme.domain.usecase.IsInCartUseCase
import ru.takeshiko.matuleme.domain.usecase.RemoveFromCartItemUseCase
import ru.takeshiko.matuleme.domain.usecase.RemoveFromFavoritesUseCase

class ProductDetailsViewModel(
    private val getUser: GetUserUseCase,
    private val getProductById: GetProductByIdUseCase,
    private val getAverageRating: GetAverageRatingUseCase,
    private val getActivePromotion: GetActivePromotionUseCase,
    private val getReviewCount: GetReviewCountUseCase,
    private val isInFavorite: IsFavoriteUseCase,
    private val addToFavorites: AddToFavoritesUseCase,
    private val removeFromFavorites: RemoveFromFavoritesUseCase,
    private val isInCart: IsInCartUseCase,
    private val addToCart: AddToCartItemUseCase,
    private val removeFromCartItem: RemoveFromCartItemUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _product = MutableStateFlow<ProductDto?>(null)
    val product: StateFlow<ProductDto?> = _product.asStateFlow()

    private val _rating = MutableStateFlow<Double?>(null)
    val rating: StateFlow<Double?> = _rating.asStateFlow()

    private val _promotion = MutableStateFlow<PromotionDto?>(null)
    val promotion: StateFlow<PromotionDto?> = _promotion.asStateFlow()

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount.asStateFlow()

    private val _isCart = MutableStateFlow(false)
    val isCart: StateFlow<Boolean> = _isCart.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadProductDetails(productId: String) {
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

            val productResult = getProductById(productId)
            if (productResult.isFailure) {
                _errorMessage.value = productResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            _product.value = productResult.getOrNull()

            loadAdditionalProductData(user.id, productId)

            _isLoading.value = false
        }
    }

    private fun loadAdditionalProductData(userId: String, productId: String) {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    getAverageRating(productId).onSuccess { rating ->
                        _rating.value = rating
                    }
                }

                launch {
                    getActivePromotion(productId).onSuccess { promotion ->
                        _promotion.value = promotion
                    }
                }

                launch {
                    getReviewCount(productId).onSuccess { count ->
                        _reviewCount.value = count
                    }
                }

                launch {
                    isInFavorite(userId, productId).onSuccess {
                        _isFavorite.value = it
                    }
                }

                launch {
                    isInCart(userId, productId).onSuccess {
                        _isCart.value = it
                    }
                }
            }
        }
    }

    fun toggleCart(productId: String) {
        viewModelScope.launch {
            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            val currentCartState = _isCart.value

            _isCart.value = !currentCartState

            val result = if (currentCartState) {
                removeFromCartItem(cachedUserId, productId)
            } else {
                addToCart(cachedUserId, productId)
            }

            result.onFailure {
                _isCart.value = currentCartState
                _errorMessage.value = it.localizedMessage
            }
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                _isLoading.value = false
                return@launch
            }

            val currentFavoriteState = _isFavorite.value

            _isFavorite.value = !currentFavoriteState

            val result = if (currentFavoriteState) {
                removeFromFavorites(cachedUserId, productId)
            } else {
                addToFavorites(cachedUserId, productId)
            }

            result.onFailure {
                _isFavorite.value = currentFavoriteState
                _errorMessage.value = it.localizedMessage
            }
        }
    }
}
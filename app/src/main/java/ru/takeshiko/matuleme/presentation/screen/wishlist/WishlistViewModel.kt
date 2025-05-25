package ru.takeshiko.matuleme.presentation.screen.wishlist

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
import ru.takeshiko.matuleme.domain.usecase.GetActivePromotionUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAverageRatingUseCase
import ru.takeshiko.matuleme.domain.usecase.GetFavoritesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetReviewCountUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.RemoveFromFavoritesUseCase

class WishlistViewModel(
    private val getFavorites: GetFavoritesUseCase,
    private val getProductById: GetProductByIdUseCase,
    private val removeFromFavorites: RemoveFromFavoritesUseCase,
    private val getUser: GetUserUseCase,
    private val getAverageRating: GetAverageRatingUseCase,
    private val getActivePromotion: GetActivePromotionUseCase,
    private val getReviewCount: GetReviewCountUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _wishlistProducts = MutableStateFlow<List<ProductDto>>(emptyList())
    val wishlistProducts: StateFlow<List<ProductDto>> = _wishlistProducts.asStateFlow()

    private val _ratings = MutableStateFlow<Map<String, Double>>(emptyMap())
    val ratings: StateFlow<Map<String, Double>> = _ratings.asStateFlow()

    private val _activePromotions = MutableStateFlow<Map<String, PromotionDto>>(emptyMap())
    val activePromotions: StateFlow<Map<String, PromotionDto>> = _activePromotions.asStateFlow()

    private val _reviewCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val reviewCounts: StateFlow<Map<String, Int>> = _reviewCounts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _processingFavoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val processingFavoriteIds: StateFlow<Set<String>> = _processingFavoriteIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadWishlistProducts() {
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

            getFavorites(user.id).onSuccess { favoriteIds ->
                coroutineScope {
                    val productList = mutableListOf<ProductDto>()
                    val ratingMap = mutableMapOf<String, Double>()
                    val promoMap = mutableMapOf<String, PromotionDto>()
                    val countMap = mutableMapOf<String, Int>()

                    favoriteIds.map { favId ->
                        launch {
                            getProductById(favId.productId).onSuccess { product ->
                                product?.let { prod ->
                                    productList += prod
                                    val productId = prod.id ?: return@launch

                                    getAverageRating(productId).onSuccess {
                                        it?.let { ratingMap[productId] = it }
                                    }

                                    getActivePromotion(productId).onSuccess {
                                        it?.let { promoMap[productId] = it }
                                    }

                                    getReviewCount(productId).onSuccess {
                                        countMap[productId] = it
                                    }
                                }
                            }
                        }
                    }.forEach { it.join() }

                    _wishlistProducts.value = productList.sortedByDescending { promoMap.containsKey(it.id) }
                    _ratings.value = ratingMap
                    _activePromotions.value = promoMap
                    _reviewCounts.value = countMap

                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun removeProductFromWishlist(productId: String) {
        viewModelScope.launch {
            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            try {
                _processingFavoriteIds.value = _processingFavoriteIds.value + productId

                removeFromFavorites(cachedUserId, productId).onSuccess {
                    _wishlistProducts.value = _wishlistProducts.value.filter { it.id != productId }

                    _ratings.value = _ratings.value.filterKeys { it != productId }
                    _activePromotions.value = _activePromotions.value.filterKeys { it != productId }
                    _reviewCounts.value = _reviewCounts.value.filterKeys { it != productId }
                }.onFailure {
                    _errorMessage.value = it.localizedMessage
                }
            } finally {
                _processingFavoriteIds.value = _processingFavoriteIds.value - productId
            }
        }
    }
}
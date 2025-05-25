package ru.takeshiko.matuleme.presentation.screen.home

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
import ru.takeshiko.matuleme.domain.usecase.*

class HomeViewModel(
    private val getProducts: GetProductsUseCase,
    private val getAverageRating: GetAverageRatingUseCase,
    private val getActivePromotion: GetActivePromotionUseCase,
    private val getReviewCount: GetReviewCountUseCase,
    private val isFavorite: IsFavoriteUseCase,
    private val getUser: GetUserUseCase,
    private val addToFavorites: AddToFavoritesUseCase,
    private val removeFromFavorites: RemoveFromFavoritesUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    private val _ratings = MutableStateFlow<Map<String, Double>>(emptyMap())
    val ratings: StateFlow<Map<String, Double>> = _ratings.asStateFlow()

    private val _activePromotions = MutableStateFlow<Map<String, PromotionDto>>(emptyMap())
    val activePromotions: StateFlow<Map<String, PromotionDto>> = _activePromotions.asStateFlow()

    private val _reviewCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val reviewCounts: StateFlow<Map<String, Int>> = _reviewCounts.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _processingFavoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val processingFavoriteIds: StateFlow<Set<String>> = _processingFavoriteIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    private var currentOffset = 0
    private val pageSize = 10
    private var canLoadMore = true

    fun loadInitialData() {
        if (_isLoading.value || _isLoadingMore.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            currentOffset = 0
            canLoadMore = true

            if (userId == null) {
                val userResult = getUser()
                userResult.onSuccess { user ->
                    userId = user?.id
                }.onFailure { exception ->
                    handleFailure(exception)
                    _isLoading.value = false
                    return@launch
                }
            }

            val currentUserId = userId
            if (currentUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                _isLoading.value = false
                resetStates()
                return@launch
            }

            getProducts(limit = pageSize, offset = currentOffset).onSuccess { productsList ->
                if (productsList.isEmpty()) {
                    canLoadMore = false
                    resetStates()
                } else {
                    _products.value = productsList
                    currentOffset = productsList.size
                    canLoadMore = productsList.size == pageSize

                    loadAdditionalDataForProducts(productsList)
                }
            }.onFailure { exception ->
                handleFailure(exception)
            }

            _isLoading.value = false
        }
    }

    fun loadMoreProducts() {
        if (_isLoadingMore.value || _isLoading.value || !canLoadMore) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            _errorMessage.value = null

            getProducts(limit = pageSize, offset = currentOffset).onSuccess { newProducts ->
                if (newProducts.isNotEmpty()) {
                    _products.value = _products.value + newProducts
                    currentOffset += newProducts.size
                    canLoadMore = newProducts.size == pageSize

                    loadAdditionalDataForProducts(newProducts)
                } else {
                    canLoadMore = false
                }
            }.onFailure { exception ->
                _errorMessage.value = exception.localizedMessage
            }

            _isLoadingMore.value = false
        }
    }

    private fun loadAdditionalDataForProducts(productsList: List<ProductDto>) {
        if (productsList.isEmpty() || userId == null) return

        viewModelScope.launch {
            coroutineScope {
                val ratingMap = _ratings.value.toMutableMap()
                val promoMap = _activePromotions.value.toMutableMap()
                val countMap = _reviewCounts.value.toMutableMap()
                val favSet = _favoriteIds.value.toMutableSet()
                val currentUserId = userId ?: return@coroutineScope

                productsList.mapNotNull { it.id }.map { id ->
                    launch {
                        if (ratingMap[id] == null) {
                            getAverageRating(id).onSuccess { it?.let { ratingMap[id] = it } }
                        }

                        if (promoMap[id] == null) {
                            getActivePromotion(id).onSuccess { it?.let { promoMap[id] = it } }
                        }

                        if (countMap[id] == null) {
                            getReviewCount(id).onSuccess { countMap[id] = it }
                        }

                        isFavorite(currentUserId, id).onSuccess { isFav ->
                            if (isFav) favSet += id else favSet -= id
                        }
                    }
                }.forEach { it.join() }

                updateStates(ratingMap, promoMap, countMap, favSet)
            }
        }
    }

    private fun updateStates(
        ratingMap: Map<String, Double>,
        promoMap: Map<String, PromotionDto>,
        countMap: Map<String, Int>,
        favSet: Set<String>
    ) {
        _ratings.value = ratingMap
        _activePromotions.value = promoMap
        _reviewCounts.value = countMap
        _favoriteIds.value = favSet

        _products.value = _products.value.sortedByDescending { promoMap.containsKey(it.id) }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            try {
                _processingFavoriteIds.value = _processingFavoriteIds.value + productId
                val currentlyFav = _favoriteIds.value.contains(productId)

                val newFavorites = if (currentlyFav) {
                    _favoriteIds.value - productId
                } else {
                    _favoriteIds.value + productId
                }
                _favoriteIds.value = newFavorites

                val result = if (currentlyFav) {
                    removeFromFavorites(cachedUserId, productId)
                } else {
                    addToFavorites(cachedUserId, productId)
                }

                result.onFailure { exception ->
                    _favoriteIds.value = if (currentlyFav) {
                        _favoriteIds.value + productId
                    } else {
                        _favoriteIds.value - productId
                    }
                    _errorMessage.value = exception.localizedMessage
                }
            } finally {
                _processingFavoriteIds.value = _processingFavoriteIds.value - productId
            }
        }
    }

    private fun resetStates() {
        _products.value = emptyList()
        _ratings.value = emptyMap()
        _activePromotions.value = emptyMap()
        _reviewCounts.value = emptyMap()
        _favoriteIds.value = emptySet()
    }

    private fun handleFailure(exception: Throwable) {
        _errorMessage.value = exception.localizedMessage
        resetStates()
    }
}
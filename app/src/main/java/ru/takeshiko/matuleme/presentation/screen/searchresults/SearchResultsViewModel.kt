package ru.takeshiko.matuleme.presentation.screen.searchresults

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

class SearchResultsViewModel(
    private val getProductsByQuery: GetProductsByQueryUseCase,
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

    private val _processingFavoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val processingFavoriteIds: StateFlow<Set<String>> = _processingFavoriteIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadInitialData(query: String) {
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

            getProductsByQuery(query).onSuccess { productsList ->
                coroutineScope {
                    val ratingMap = mutableMapOf<String, Double>()
                    val promoMap = mutableMapOf<String, PromotionDto>()
                    val countMap = mutableMapOf<String, Int>()
                    val favSet = mutableSetOf<String>()

                    productsList.mapNotNull { it.id }.map { id ->
                        launch {
                            getAverageRating(id).onSuccess {
                                it?.let { ratingMap[id] = it }
                            }

                            getActivePromotion(id).onSuccess {
                                it?.let { promoMap[id] = it }
                            }

                            getReviewCount(id).onSuccess {
                                countMap[id] = it
                            }

                            isFavorite(user.id, id).onSuccess {
                                if (it) favSet += id
                            }
                        }
                    }.forEach { it.join() }

                    _ratings.value = ratingMap
                    _activePromotions.value = promoMap
                    _reviewCounts.value = countMap
                    _favoriteIds.value = favSet.toSet()
                    _products.value = productsList.sortedByDescending { promoMap.containsKey(it.id) }
                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
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

                val currentlyFav = isFavorite(cachedUserId, productId).getOrDefault(false)

                val result = if (currentlyFav) {
                    removeFromFavorites(cachedUserId, productId)
                } else {
                    addToFavorites(cachedUserId, productId)
                }

                result.onSuccess {
                    _favoriteIds.value = if (currentlyFav) {
                        _favoriteIds.value - productId
                    } else {
                        _favoriteIds.value + productId
                    }
                }.onFailure {
                    _errorMessage.value = it.localizedMessage
                }
            } finally {
                _processingFavoriteIds.value = _processingFavoriteIds.value - productId
            }
        }
    }
}
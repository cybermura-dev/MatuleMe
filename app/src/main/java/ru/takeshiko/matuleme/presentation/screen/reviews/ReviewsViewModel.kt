package ru.takeshiko.matuleme.presentation.screen.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.ProductReviewDto
import ru.takeshiko.matuleme.domain.usecase.GetProductReviewsUseCase

class ReviewsViewModel(
    private val getProductReviews: GetProductReviewsUseCase
) : ViewModel() {

    private val _allReviews = MutableStateFlow<List<ProductReviewDto>>(emptyList())

    private val _filteredReviews = MutableStateFlow<List<ProductReviewDto>>(emptyList())
    val filteredReviews: StateFlow<List<ProductReviewDto>> = _filteredReviews.asStateFlow()

    private val _currentFilter = MutableStateFlow<Int?>(null)
    val currentFilter: StateFlow<Int?> = _currentFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadReviews(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            getProductReviews(productId).onSuccess { reviews ->
                _allReviews.value = reviews
                applyFilter()
            }.onFailure { error ->
                _errorMessage.value = error.localizedMessage
            }
            _isLoading.value = false
        }
    }

    fun setFilter(rating: Int?) {
        _currentFilter.value = rating
        applyFilter()
    }

    private fun applyFilter() {
        val currentFilterValue = _currentFilter.value
        val allReviewsValue = _allReviews.value

        val newFilteredList = if (currentFilterValue == null) {
            allReviewsValue
        } else {
            allReviewsValue.filter { it.rating == currentFilterValue }
        }
        _filteredReviews.value = newFilteredList
    }
}
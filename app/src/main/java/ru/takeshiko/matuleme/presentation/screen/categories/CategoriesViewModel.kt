package ru.takeshiko.matuleme.presentation.screen.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.ProductCategoryDto
import ru.takeshiko.matuleme.domain.usecase.GetProductCategoriesUseCase

class CategoriesViewModel(
    private val getProductCategoriesUseCase: GetProductCategoriesUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ProductCategoryDto>>(emptyList())
    val categories: StateFlow<List<ProductCategoryDto>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                getProductCategoriesUseCase().onSuccess { categoryList ->
                    _categories.value = categoryList
                }.onFailure { exception ->
                    _errorMessage.value = exception.localizedMessage
                    _categories.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                _categories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
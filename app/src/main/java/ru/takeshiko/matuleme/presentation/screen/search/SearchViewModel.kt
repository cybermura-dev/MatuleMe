package ru.takeshiko.matuleme.presentation.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.SearchQueryDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.DeleteSearchQueryUseCase
import ru.takeshiko.matuleme.domain.usecase.GetRecentSearchQueriesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.LogSearchQueryUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateSearchQueryUseCase

class SearchViewModel(
    private val getUser: GetUserUseCase,
    private val logSearchQuery: LogSearchQueryUseCase,
    private val updateSearchQuery: UpdateSearchQueryUseCase,
    private val deleteSearchQuery: DeleteSearchQueryUseCase,
    private val getRecentSearchQueries: GetRecentSearchQueriesUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchQueryDto>>(emptyList())
    val searchHistory: StateFlow<List<SearchQueryDto>> = _searchHistory.asStateFlow()

    private val _searchSubmitted = MutableStateFlow("")
    val searchSubmitted: StateFlow<String> = _searchSubmitted.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _processingItems = MutableStateFlow<Set<String>>(emptySet())
    val processingItems: StateFlow<Set<String>> = _processingItems.asStateFlow()

    private var userId: String? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onClearHistoryItem(query: String) {
        deleteHistoryItem(query)
    }

    fun onSearchSubmit() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _errorMessage.value = null

            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            val existingQuery = _searchHistory.value.find { it.query == query }
            if (existingQuery == null) {
                logSearchQuery(SearchQueryDto(
                    userId = cachedUserId,
                    query = query,
                    searchedAt = Clock.System.now()
                ))
            } else {
                updateSearchQuery(existingQuery.copy(searchedAt = Clock.System.now()))
            }

            _searchSubmitted.value = _searchQuery.value
        }
    }

    fun deleteHistoryItem(query: String) {
        viewModelScope.launch {
            _errorMessage.value = null

            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                return@launch
            }

            _processingItems.value = _processingItems.value + query

            try {
                _searchHistory.value = _searchHistory.value.filterNot { it.query == query }

                val result = deleteSearchQuery(cachedUserId, query)

                if (result.isFailure) {
                    loadSearchHistoryWithoutLoading()
                    _errorMessage.value = result.exceptionOrNull()?.localizedMessage
                }
            } finally {
                _processingItems.value = _processingItems.value - query
            }
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            loadSearchHistoryInternal()
        }
    }

    private fun loadSearchHistoryWithoutLoading() {
        viewModelScope.launch {
            loadSearchHistoryInternal()
        }
    }

    private suspend fun loadSearchHistoryInternal() {
        _errorMessage.value = null

        val userResult = getUser()
        val user = userResult.getOrNull()

        if (userResult.isFailure || user == null) {
            _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
            _isLoading.value = false
            return
        }

        userId = user.id

        val historyResult = getRecentSearchQueries(user.id)

        historyResult.onSuccess { query ->
            _searchHistory.value = historyResult.getOrDefault(emptyList())
        }.onFailure { exception ->
            _searchHistory.value = emptyList()
            _errorMessage.value = exception.localizedMessage
        }

        _isLoading.value = false
    }

    fun resetSearchSubmit() {
        _searchSubmitted.value = ""
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }
}
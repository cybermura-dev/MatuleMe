package ru.takeshiko.matuleme.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserFavoriteDto
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.usecase.GetAddressesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetFavoritesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetOrdersUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.LogoutUseCase

class ProfileViewModel(
    private val getUser: GetUserUseCase,
    private val getOrders: GetOrdersUseCase,
    private val getFavorites: GetFavoritesUseCase,
    private val getAddresses: GetAddressesUseCase,
    private val getPayments: GetPaymentsUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    private val _orders = MutableStateFlow<List<UserOrderDto>>(emptyList())
    val orders: StateFlow<List<UserOrderDto>> = _orders.asStateFlow()

    private val _favorites = MutableStateFlow<List<UserFavoriteDto>>(emptyList())
    val favorites: StateFlow<List<UserFavoriteDto>> = _favorites.asStateFlow()

    private val _addresses = MutableStateFlow<List<UserDeliveryAddressDto>>(emptyList())
    val addresses: StateFlow<List<UserDeliveryAddressDto>> = _addresses.asStateFlow()

    private val _payments = MutableStateFlow<List<UserPaymentDto>>(emptyList())
    val payments: StateFlow<List<UserPaymentDto>> = _payments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLogoutInProgress = MutableStateFlow(false)
    val isLogoutInProgress: StateFlow<Boolean> = _isLogoutInProgress.asStateFlow()

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess.asStateFlow()

    fun loadUserProfile() {
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

            _user.value = user

            coroutineScope {
                launch {
                    getOrders(user.id).onSuccess { ordersList ->
                        _orders.value = ordersList
                    }
                }

                launch {
                    getFavorites(user.id).onSuccess { favoritesList ->
                        _favorites.value = favoritesList
                    }
                }

                launch {
                    getAddresses(user.id).onSuccess { addressesList ->
                        _addresses.value = addressesList
                    }
                }

                launch {
                    getPayments(user.id).onSuccess { paymentsList ->
                        _payments.value = paymentsList
                    }
                }
            }

            _isLoading.value = false
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _isLogoutInProgress.value = true

            logout().onSuccess {
                _logoutSuccess.value = true
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLogoutInProgress.value = false
        }
    }
}
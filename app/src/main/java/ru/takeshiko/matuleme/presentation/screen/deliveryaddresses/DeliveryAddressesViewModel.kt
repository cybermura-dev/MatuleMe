package ru.takeshiko.matuleme.presentation.screen.deliveryaddresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.*

class DeliveryAddressesViewModel(
    private val getUser: GetUserUseCase,
    private val getAddresses: GetAddressesUseCase,
    private val deleteAddress: DeleteAddressUseCase,
    private val setDefaultAddress: SetDefaultAddressUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<UserDeliveryAddressDto>>(emptyList())
    val addresses: StateFlow<List<UserDeliveryAddressDto>> = _addresses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun loadAddresses() {
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

            getAddresses(user.id).onSuccess { list ->
                _addresses.value = list.sortedByDescending { it.isDefault }

                if (list.none { it.isDefault } && list.isNotEmpty()) {
                    val firstAddressId = list.first().id ?: ""

                    if (firstAddressId.isNotEmpty()) {
                        setDefault(firstAddressId)
                    }
                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun deleteExistingAddress(addressId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val currentAddress = _addresses.value.find { it.id == addressId }
            val isDefault = currentAddress?.isDefault == true

            deleteAddress(addressId).onSuccess {
                if (isDefault) {
                    loadAddresses()

                    val remainingAddresses = _addresses.value

                    if (remainingAddresses.isNotEmpty()) {
                        val newDefaultAddressId = remainingAddresses.first().id ?: ""

                        if (newDefaultAddressId.isNotEmpty()) {
                            setDefault(newDefaultAddressId)
                        }
                    }
                } else {
                    loadAddresses()
                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun setDefault(addressId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val cachedUserId = userId
            if (cachedUserId == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                _isLoading.value = false
                return@launch
            }

            setDefaultAddress(cachedUserId, addressId).onSuccess {
                loadAddresses()
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }
}

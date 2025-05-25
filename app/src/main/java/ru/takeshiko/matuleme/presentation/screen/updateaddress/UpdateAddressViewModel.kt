package ru.takeshiko.matuleme.presentation.screen.updateaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.usecase.GetAddressByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAddressesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultAddressUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateAddressUseCase

class UpdateAddressViewModel(
    private val getAddressById: GetAddressByIdUseCase,
    private val updateDeliveryAddress: UpdateAddressUseCase,
    private val getUser: GetUserUseCase,
    private val getAddresses: GetAddressesUseCase,
    private val setDefaultAddress: SetDefaultAddressUseCase
) : ViewModel() {

    private val _address = MutableStateFlow<UserDeliveryAddressDto?>(null)
    val address: StateFlow<UserDeliveryAddressDto?> = _address.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun fetchAddress(addressId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _saved.value = false

            val userResult = getUser()
            val user = userResult.getOrNull()

            if (userResult.isFailure || user == null) {
                _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            getAddressById(addressId).onSuccess {
                _address.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }
            _isLoading.value = false
        }
    }

    fun updateAddress(updatedAddress: UserDeliveryAddressDto) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _saved.value = false

            if (userId == null) {
                val userResult = getUser()
                val user = userResult.getOrNull()
                if (userResult.isFailure || user == null) {
                    _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                    _isLoading.value = false
                    return@launch
                }
                userId = user.id
            }

            updatedAddress.userId = userId ?: ""

            val mainUpdateResult = updateDeliveryAddress(updatedAddress)

            if (mainUpdateResult.isFailure) {
                _errorMessage.value = mainUpdateResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            _address.value = mainUpdateResult.getOrNull()

            if (updatedAddress.isDefault == true) {
                val setDefaultResult = setDefaultAddress(userId!!, updatedAddress.id!!)
                if (setDefaultResult.isFailure) {
                    _errorMessage.value = setDefaultResult.exceptionOrNull()?.localizedMessage
                }
                _saved.value = true
            } else {
                val addressesResult = getAddresses(userId!!)
                val currentAddresses = addressesResult.getOrNull() ?: emptyList()

                if (addressesResult.isFailure) {
                    _errorMessage.value = addressesResult.exceptionOrNull()?.localizedMessage
                } else if (currentAddresses.isNotEmpty() && currentAddresses.none { it.isDefault == true }) {
                    val firstAddressId = currentAddresses.firstOrNull()?.id
                    if (firstAddressId != null && firstAddressId.isNotEmpty()) {
                        setDefaultAddress(userId!!, firstAddressId).onFailure {
                            _errorMessage.value = it.localizedMessage
                        }
                    }
                }
                _saved.value = true
            }

            _isLoading.value = false
        }
    }
}
package ru.takeshiko.matuleme.presentation.screen.addaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.usecase.AddAddressUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAddressesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultAddressUseCase

class AddAddressViewModel(
    private val addAddress: AddAddressUseCase,
    private val getUser: GetUserUseCase,
    private val getAddresses: GetAddressesUseCase,
    private val setDefaultAddress: SetDefaultAddressUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userId: String? = null

    fun addNewAddress(address: UserDeliveryAddressDto) {
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

            address.userId = userId ?: ""

            if (address.isDefault) {
                val addresses = getAddresses(userId ?: "").getOrNull() ?: emptyList()

                if (addresses.isNotEmpty()) {
                    val addResult = addAddress(address)
                    if (addResult.isFailure) {
                        _errorMessage.value = addResult.exceptionOrNull()?.localizedMessage
                        _isLoading.value = false
                        return@launch
                    }

                    val newAddressId = addResult.getOrNull()?.id ?: ""
                    if (newAddressId.isNotEmpty()) {
                        setDefaultAddress(userId ?: "", newAddressId).onFailure {
                            _errorMessage.value = it.localizedMessage
                            _isLoading.value = false
                            return@launch
                        }
                    }

                    _saved.value = true
                } else {
                    addAddress(address).onSuccess {
                        _saved.value = true
                    }.onFailure {
                        _errorMessage.value = it.localizedMessage
                    }
                }
            } else {
                addAddress(address).onSuccess {
                    _saved.value = true
                }.onFailure {
                    _errorMessage.value = it.localizedMessage
                }

                val addresses = getAddresses(userId ?: "").getOrNull() ?: emptyList()
                if (addresses.isNotEmpty() && addresses.none { it.isDefault }) {
                    val firstAddressId = addresses.first().id ?: ""
                    if (firstAddressId.isNotEmpty()) {
                        setDefaultAddress(userId ?: "", firstAddressId).onFailure {
                            _errorMessage.value = it.localizedMessage
                        }
                    }
                }
            }

            _isLoading.value = false
        }
    }
}

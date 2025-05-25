package ru.takeshiko.matuleme.presentation.screen.checkout

import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.models.UserPaymentDto

data class CheckoutState(
    val cartItems: List<CheckoutCartItem> = emptyList(),
    val selectedAddress: UserDeliveryAddressDto? = null,
    val addresses: List<UserDeliveryAddressDto> = emptyList(),
    val selectedPayment: UserPaymentDto? = null,
    val payments: List<UserPaymentDto> = emptyList(),
    val email: String = "",
    val phone: String = "",
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0
)

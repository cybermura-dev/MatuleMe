package ru.takeshiko.matuleme.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPaymentDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") var userId: String,
    @SerialName("card_number") val cardNumber: String,
    @SerialName("card_holder_name") val cardHolderName: String,
    @SerialName("expiration_date") val expirationDate: String,
    @SerialName("is_default") val isDefault: Boolean
)
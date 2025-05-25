package ru.takeshiko.matuleme.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDeliveryAddressDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") var userId: String,
    @SerialName("address") val address: String,
    @SerialName("is_default") val isDefault: Boolean
)
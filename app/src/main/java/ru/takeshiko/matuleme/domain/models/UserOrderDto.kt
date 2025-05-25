package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class UserOrderDto(
    @SerialName("id") val id: String? = null,
    @SerialName("order_number") val orderNumber: String,
    @SerialName("user_id") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String,
    @SerialName("card_number") val cardNumber: String,
    @SerialName("card_holder") val cardHolder: String,
    @SerialName("address") val address: String,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("status") var status: OrderStatus,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class) val createdAt: Instant,
    @SerialName("updated_at") @Serializable(with = FlexibleInstantSerializer::class) val updatedAt: Instant
)
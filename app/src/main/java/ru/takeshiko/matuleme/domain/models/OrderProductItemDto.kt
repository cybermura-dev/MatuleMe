package ru.takeshiko.matuleme.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderProductItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("price") val price: Double
)
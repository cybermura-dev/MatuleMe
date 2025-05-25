package ru.takeshiko.matuleme.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationType {
    @SerialName("order_update") ORDER_UPDATE,
    @SerialName("discount") DISCOUNT,
    @SerialName("message") MESSAGE,
    @SerialName("info") INFO
}
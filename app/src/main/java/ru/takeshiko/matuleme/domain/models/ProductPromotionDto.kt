package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class ProductPromotionDto(
    @SerialName("promotion_id") val promotionId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("assigned_at") @Serializable(with = FlexibleInstantSerializer::class) val assignedAt: Instant? = null,
    @SerialName("removed_at") @Serializable(with = FlexibleInstantSerializer::class) val removedAt: Instant? = null,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class) val createdAt: Instant,
    @SerialName("updated_at") @Serializable(with = FlexibleInstantSerializer::class) val updatedAt: Instant
)
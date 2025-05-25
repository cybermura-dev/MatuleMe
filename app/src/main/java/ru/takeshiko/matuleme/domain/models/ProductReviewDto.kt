package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class ProductReviewDto(
    @SerialName("product_id") val productId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("rating") val rating: Int,
    @SerialName("review_text") val reviewText: String? = null,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class)  val createdAt: Instant,
    @SerialName("updated_at") @Serializable(with = FlexibleInstantSerializer::class)  val updatedAt: Instant
)

package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class ProductDto(
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("base_price") val basePrice: Double,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class)  val createdAt: Instant,
    @SerialName("updated_at") @Serializable(with = FlexibleInstantSerializer::class)  val updatedAt: Instant
)

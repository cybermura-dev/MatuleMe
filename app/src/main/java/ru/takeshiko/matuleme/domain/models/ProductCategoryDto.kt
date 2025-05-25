package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class ProductCategoryDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class) val createdAt: Instant,
    @SerialName("updated_at") @Serializable(with = FlexibleInstantSerializer::class) val updatedAt: Instant
)

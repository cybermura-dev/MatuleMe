package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class PromotionDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("discount_percent") val discountPercent: Double,
    @SerialName("start_at") @Serializable(with = FlexibleInstantSerializer::class) val startAt: Instant? = null,
    @SerialName("end_at") @Serializable(with = FlexibleInstantSerializer::class) val endAt: Instant? = null,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class) val createdAt: Instant,
    @SerialName("updated_at") @Serializable(with = FlexibleInstantSerializer::class)val updatedAt: Instant
)

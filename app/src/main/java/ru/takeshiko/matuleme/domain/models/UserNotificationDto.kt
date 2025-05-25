package ru.takeshiko.matuleme.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.takeshiko.matuleme.presentation.utils.FlexibleInstantSerializer

@Serializable
data class UserNotificationDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("type") val type: NotificationType,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("created_at") @Serializable(with = FlexibleInstantSerializer::class) val createdAt: Instant
)
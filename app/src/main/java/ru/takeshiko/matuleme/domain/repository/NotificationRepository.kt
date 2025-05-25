package ru.takeshiko.matuleme.domain.repository

import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.UserNotificationDto

interface NotificationRepository {
    suspend fun getNotifications(userId: String): Result<List<UserNotificationDto>>
    suspend fun markNotificationAsRead(notificationId: String, userId: String): Result<Unit>
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit>
    suspend fun getUnreadNotificationCount(userId: String): Result<Long>
}

class NotificationRepositoryImpl(
    supabase: SupabaseClientManager
) : NotificationRepository {

    private val postgrest = supabase.postgrest
    private val tableName = "user_notifications"

    override suspend fun getNotifications(userId: String): Result<List<UserNotificationDto>> {
        return TODO("Provide the return value")
    }

    override suspend fun markNotificationAsRead(
        notificationId: String,
        userId: String
    ): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun markAllNotificationsAsRead(userId: String): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun getUnreadNotificationCount(userId: String): Result<Long> {
        return TODO("Provide the return value")
    }
}
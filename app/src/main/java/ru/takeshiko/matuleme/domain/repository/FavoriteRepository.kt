package ru.takeshiko.matuleme.domain.repository

import kotlinx.datetime.Clock
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.UserFavoriteDto

interface FavoriteRepository {
    suspend fun getFavorites(userId: String): Result<List<UserFavoriteDto>>
    suspend fun isFavorite(userId: String, productId: String): Result<Boolean>
    suspend fun addToFavorites(userId: String, productId: String): Result<Unit>
    suspend fun removeFromFavorites(userId: String, productId: String): Result<Unit>
}

class FavoriteRepositoryImpl(
    supabase: SupabaseClientManager
) : FavoriteRepository {

    private val postgrest = supabase.postgrest

    private val tableName = "user_favorites"

    override suspend fun getFavorites(userId: String): Result<List<UserFavoriteDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<UserFavoriteDto>()
        }

    override suspend fun isFavorite(userId: String, productId: String): Result<Boolean> =
        runCatching {
            val list = postgrest
                .from(tableName)
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", productId)
                    }
                }
                .decodeList<UserFavoriteDto>()
            list.isNotEmpty()
        }

    override suspend fun addToFavorites(userId: String, productId: String): Result<Unit> =
        runCatching {
            val favorite = UserFavoriteDto(
                userId = userId,
                productId = productId,
                addedAt = Clock.System.now()
            )
            postgrest
                .from(tableName)
                .insert(favorite)
        }

    override suspend fun removeFromFavorites(userId: String, productId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", productId)
                    }
                }
        }
}
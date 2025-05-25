package ru.takeshiko.matuleme.domain.repository

import kotlinx.datetime.Clock
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.UserCartItemDto
import ru.takeshiko.matuleme.domain.models.UserFavoriteDto

interface CartRepository {
    suspend fun getCartItems(userId: String): Result<List<UserCartItemDto>>
    suspend fun getCartItem(userId: String, productId: String): Result<List<UserCartItemDto>>
    suspend fun isInCart(userId: String, productId: String): Result<Boolean>
    suspend fun addToCartItem(userId: String, productId: String): Result<Unit>
    suspend fun updateQuantity(productId: String, newQuantity: Int): Result<Unit>
    suspend fun removeFromCartItem(userId: String, productId: String): Result<Unit>
    suspend fun clearCart(userId: String): Result<Unit>
}

class CartRepositoryImpl(
    supabase: SupabaseClientManager
) : CartRepository {

    private val postgrest = supabase.postgrest

    private val tableName = "user_cart_items"

    override suspend fun getCartItems(userId: String): Result<List<UserCartItemDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<UserCartItemDto>()
        }

    override suspend fun getCartItem(userId: String, productId: String): Result<List<UserCartItemDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", productId)
                    }
                }
                .decodeList<UserCartItemDto>()
        }

    override suspend fun isInCart(userId: String, productId: String): Result<Boolean> =
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

    override suspend fun addToCartItem(userId: String, productId: String): Result<Unit> =
        runCatching {
            val cart = UserCartItemDto(
                userId = userId,
                productId = productId,
                quantity = 1,
                addedAt = Clock.System.now()
            )
            postgrest
                .from(tableName)
                .insert(cart)
        }

    override suspend fun updateQuantity(productId: String, newQuantity: Int): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .update({ set("quantity", newQuantity) }) {
                    filter { eq("id", productId) }
                }
        }

    override suspend fun removeFromCartItem(userId: String, productId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .delete {
                    filter {
                        eq("product_id", productId)
                        eq("user_id", userId)
                    }
                }
        }

    override suspend fun clearCart(userId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .delete { filter { eq("user_id", userId) } }
        }
}
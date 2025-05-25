package ru.takeshiko.matuleme.domain.repository

import io.github.jan.supabase.postgrest.query.Order
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.domain.models.UserOrderDto

interface OrderRepository {
    suspend fun getOrders(userId: String): Result<List<UserOrderDto>>
    suspend fun getOrderById(orderId: String): Result<UserOrderDto>
    suspend fun createOrder(order: UserOrderDto): Result<UserOrderDto>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<UserOrderDto>
    suspend fun getOrderItems(orderId: String): Result<List<OrderProductItemDto>>
    suspend fun addOrderItem(item: OrderProductItemDto): Result<OrderProductItemDto>
    suspend fun updateOrderItem(itemId: String, item: OrderProductItemDto): Result<OrderProductItemDto>
    suspend fun deleteOrderItem(itemId: String): Result<Unit>
}

class OrderRepositoryImpl(
    supabase: SupabaseClientManager
) : OrderRepository {

    private val postgrest = supabase.postgrest

    private val orderTableName = "user_orders"
    private val orderItemsTableName = "order_product_items"

    override suspend fun getOrders(userId: String): Result<List<UserOrderDto>> =
        runCatching {
            postgrest
                .from(orderTableName)
                .select {
                    filter { eq("user_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList()
        }

    override suspend fun getOrderById(orderId: String): Result<UserOrderDto> =
        runCatching {
            postgrest
                .from(orderTableName)
                .select {
                    filter { eq("id", orderId) }
                }
                .decodeSingle()
        }

    override suspend fun createOrder(order: UserOrderDto): Result<UserOrderDto> =
        runCatching {
            postgrest
                .from(orderTableName)
                .insert(order) { select() }
                .decodeSingle()
        }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<UserOrderDto> =
        runCatching {
            postgrest
                .from(orderTableName)
                .update({ set("status", status) }) {
                    filter { eq("id", orderId) }
                    select()
                }
                .decodeSingle()
        }

    override suspend fun getOrderItems(orderId: String): Result<List<OrderProductItemDto>> =
        runCatching {
            postgrest
                .from(orderItemsTableName)
                .select {
                    filter { eq("order_id", orderId) }
                }
                .decodeList()
        }

    override suspend fun addOrderItem(item: OrderProductItemDto): Result<OrderProductItemDto> =
        runCatching {
            postgrest
                .from(orderItemsTableName)
                .insert(item) { select() }
                .decodeSingle()
        }

    override suspend fun updateOrderItem(itemId: String, item: OrderProductItemDto): Result<OrderProductItemDto> =
        runCatching {
            postgrest
                .from(orderItemsTableName)
                .update(item) {
                    select()
                    filter { eq("id", itemId) }
                }
                .decodeSingle()
        }

    override suspend fun deleteOrderItem(itemId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(orderItemsTableName)
                .delete {
                    filter { eq("id", itemId) }
                }
        }
}
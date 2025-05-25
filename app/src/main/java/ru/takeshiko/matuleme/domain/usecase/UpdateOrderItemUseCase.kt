package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.OrderRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdateOrderItemUseCase(
    private val repository: OrderRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(itemId: String, item: OrderProductItemDto): Result<OrderProductItemDto> {
        if (itemId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_order_item_id)
                )
            )
        }
        if (item.orderId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_order_id)
                )
            )
        }
        if (item.productId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_product_id)
                )
            )
        }
        if (item.quantity <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_quantity)
                )
            )
        }
        if (item.price <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_price)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.updateOrderItem(itemId, item).getOrThrow()
        }
    }
}
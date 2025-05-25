package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.OrderRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetOrderItemsUseCase(
    private val repository: OrderRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(orderId: String): Result<List<OrderProductItemDto>> {
        if (orderId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_order_id)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getOrderItems(orderId).getOrThrow()
        }
    }
}

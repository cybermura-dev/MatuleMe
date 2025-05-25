package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.OrderRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdateOrderStatusUseCase(
    private val repository: OrderRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(orderId: String, status: OrderStatus): Result<UserOrderDto> {
        if (orderId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_order_id)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.updateOrderStatus(orderId, status).getOrThrow()
        }
    }
}
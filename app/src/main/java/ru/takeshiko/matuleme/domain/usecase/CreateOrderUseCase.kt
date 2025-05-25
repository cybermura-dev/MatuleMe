package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.OrderRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class CreateOrderUseCase(
    private val repository: OrderRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(order: UserOrderDto): Result<UserOrderDto> {
        if (order.userId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_user_id)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.createOrder(order).getOrThrow()
        }
    }
}
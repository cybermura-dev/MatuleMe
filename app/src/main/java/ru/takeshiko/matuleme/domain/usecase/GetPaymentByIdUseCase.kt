package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.PaymentRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetPaymentByIdUseCase(
    private val repository: PaymentRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(paymentId: String): Result<UserPaymentDto?> {
        if (paymentId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_invalid_payment_id))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getPaymentById(paymentId).getOrThrow()
        }
    }
}
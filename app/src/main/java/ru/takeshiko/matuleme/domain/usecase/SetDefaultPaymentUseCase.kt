package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.PaymentRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class SetDefaultPaymentUseCase(
    private val repository: PaymentRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String, paymentId: String): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_user_id)
                )
            )
        }

        if (paymentId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_payment_id)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.setDefaultPayment(userId, paymentId).getOrThrow()
        }
    }
}
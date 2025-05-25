package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.PaymentRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class AddPaymentUseCase(
    private val repository: PaymentRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(payment: UserPaymentDto): Result<UserPaymentDto> {
        if (payment.userId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_user_id)
                )
            )
        }
        if (payment.cardNumber.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_payment_card_number_empty)
                )
            )
        }
        if (payment.cardHolderName.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_payment_card_holder_empty)
                )
            )
        }

        if (payment.expirationDate.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_payment_expiration_date_empty)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.addPayment(payment).getOrThrow()
        }
    }
}
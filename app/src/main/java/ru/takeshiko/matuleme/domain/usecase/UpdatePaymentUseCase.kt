package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.PaymentRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdatePaymentUseCase(
    private val repository: PaymentRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(paymentDetailsToUpdate: UserPaymentDto): Result<UserPaymentDto> {
        if (paymentDetailsToUpdate.cardNumber.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_payment_card_number_empty)
                )
            )
        }

        if (paymentDetailsToUpdate.cardHolderName.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_payment_card_holder_empty)
                )
            )
        }

        if (paymentDetailsToUpdate.expirationDate.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_payment_expiration_date_empty)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.updatePayment(paymentDetailsToUpdate).getOrThrow()
        }
    }
}
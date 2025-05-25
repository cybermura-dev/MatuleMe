package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.DeliveryAddressRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdateAddressUseCase(
    private val repository: DeliveryAddressRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(address: UserDeliveryAddressDto): Result<UserDeliveryAddressDto> {
        if (address.userId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_user_id)
                )
            )
        }

        if (address.address.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_delivery_address_empty)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.updateAddress(address).getOrThrow()
        }
    }
}
package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.DeliveryAddressRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class DeleteAddressUseCase(
    private val repository: DeliveryAddressRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(addressId: String): Result<Unit> {
        if (addressId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_address_id)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.deleteAddress(addressId).getOrThrow()
        }
    }
}
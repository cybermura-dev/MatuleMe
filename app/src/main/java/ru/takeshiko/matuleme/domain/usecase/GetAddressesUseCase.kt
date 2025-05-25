package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.DeliveryAddressRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetAddressesUseCase(
    private val repository: DeliveryAddressRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String): Result<List<UserDeliveryAddressDto>> {
        if (userId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    stringResourceProvider.getString(R.string.error_invalid_user_id)
                )
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getAddresses(userId).getOrThrow()
        }
    }
}


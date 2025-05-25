package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserCartItemDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.CartRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetCartItemsUseCase(
    private val repository: CartRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String): Result<List<UserCartItemDto>> {
        if (userId.isBlank()) {
            throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_invalid_user_id)
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getCartItems(userId).getOrThrow()
        }
    }
}
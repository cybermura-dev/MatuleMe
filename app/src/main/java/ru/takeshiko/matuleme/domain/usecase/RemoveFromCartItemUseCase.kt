package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.CartRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class RemoveFromCartItemUseCase(
    private val repository: CartRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String, itemId: String): Result<Unit> {
        if (userId.isBlank()) {
            throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_invalid_user_id)
            )
        }
        if (itemId.isBlank()) {
            throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_invalid_item_id)
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.removeFromCartItem(userId, itemId).getOrThrow()
        }
    }
}
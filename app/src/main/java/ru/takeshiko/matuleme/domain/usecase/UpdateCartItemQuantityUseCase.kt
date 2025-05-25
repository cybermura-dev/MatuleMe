package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.CartRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdateCartItemQuantityUseCase(
    private val repository: CartRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(itemId: String, newQuantity: Int): Result<Unit> {
        if (itemId.isBlank()) {
            throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_invalid_product_id)
            )
        }

        if (newQuantity < 1 || newQuantity > 100) {
            throw IllegalArgumentException(
                stringResourceProvider.getString(R.string.error_invalid_quantity)
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.updateQuantity(itemId, newQuantity).getOrThrow()
        }
    }
}
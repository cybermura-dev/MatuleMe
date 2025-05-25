package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.FavoriteRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class AddToFavoritesUseCase(
    private val repository: FavoriteRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String, productId: String): Result<Unit> {
        if (userId.isBlank() || productId.isBlank()) {
            throw IllegalArgumentException(stringResourceProvider.getString(R.string.error_invalid_input))
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.addToFavorites(userId, productId).getOrThrow()
        }
    }
}
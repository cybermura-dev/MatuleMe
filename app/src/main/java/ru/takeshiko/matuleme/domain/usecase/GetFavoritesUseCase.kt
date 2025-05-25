package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserFavoriteDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.FavoriteRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetFavoritesUseCase(
    private val repository: FavoriteRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String): Result<List<UserFavoriteDto>> {
        if (userId.isBlank()) {
            throw IllegalArgumentException(stringResourceProvider.getString(R.string.error_invalid_user_id))
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getFavorites(userId).getOrThrow()
        }
    }
}
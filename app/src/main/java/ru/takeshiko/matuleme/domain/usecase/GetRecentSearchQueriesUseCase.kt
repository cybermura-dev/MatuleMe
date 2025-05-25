package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.models.SearchQueryDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.SearchRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetRecentSearchQueriesUseCase(
    private val searchRepository: SearchRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String, limit: Int = 10): Result<List<SearchQueryDto>> =
        stringResourceProvider.safeSupabaseCall {
            searchRepository.getRecentQueriesByUser(userId, limit).getOrThrow()
        }
}
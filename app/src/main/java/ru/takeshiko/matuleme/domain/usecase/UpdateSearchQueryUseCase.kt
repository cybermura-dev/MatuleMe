package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.models.SearchQueryDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.SearchRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdateSearchQueryUseCase(
    private val searchRepository: SearchRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(query: SearchQueryDto): Result<SearchQueryDto> =
        stringResourceProvider.safeSupabaseCall {
            searchRepository.updateQuery(query).getOrThrow()
        }
}
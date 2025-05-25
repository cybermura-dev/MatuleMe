package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.SearchRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class DeleteSearchQueryUseCase(
    private val searchRepository: SearchRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(userId: String, query: String): Result<String> =
        stringResourceProvider.safeSupabaseCall {
            searchRepository.deleteQuery(userId, query)
                .getOrThrow()
        }
}
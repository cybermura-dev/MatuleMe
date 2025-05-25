package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.PromotionRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetPromotionsUseCase(
    private val repository: PromotionRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(): Result<List<PromotionDto>> =
        stringResourceProvider.safeSupabaseCall {
            repository.getPromotions().getOrThrow()
        }
}
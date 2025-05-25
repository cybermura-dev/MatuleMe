package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.models.ProductCategoryDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.ProductRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetProductCategoriesUseCase(
    private val repository: ProductRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(): Result<List<ProductCategoryDto>> =
        stringResourceProvider.safeSupabaseCall {
            repository.getProductCategories().getOrThrow()
        }
}
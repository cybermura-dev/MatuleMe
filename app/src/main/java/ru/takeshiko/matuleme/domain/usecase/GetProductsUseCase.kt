package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.ProductRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetProductsUseCase(
    private val repository: ProductRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(limit: Int, offset: Int): Result<List<ProductDto>> =
        stringResourceProvider.safeSupabaseCall {
            repository.getProducts(limit, offset).getOrThrow()
        }
}
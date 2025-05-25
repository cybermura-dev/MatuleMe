package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.ProductRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetProductsByQueryUseCase(
    private val repository: ProductRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(query: String): Result<List<ProductDto>> {
        if (query.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_invalid_input))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getProductByQuery(query).getOrThrow()
        }
    }
}
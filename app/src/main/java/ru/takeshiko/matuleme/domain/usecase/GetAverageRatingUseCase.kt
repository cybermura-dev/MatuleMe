package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.ProductRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetAverageRatingUseCase(
    private val repository: ProductRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(productId: String): Result<Double?> {
        if (productId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_invalid_product_id))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            repository.getAverageRating(productId).getOrThrow()
        }
    }
}
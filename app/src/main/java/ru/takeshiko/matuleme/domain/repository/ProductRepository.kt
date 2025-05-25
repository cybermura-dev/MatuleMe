package ru.takeshiko.matuleme.domain.repository

import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.Clock
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.ProductCategoryDto
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.ProductPromotionDto
import ru.takeshiko.matuleme.domain.models.ProductReviewDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import java.util.Objects.isNull

interface ProductRepository {
    suspend fun getProductById(productId: String): Result<ProductDto?>
    suspend fun getProductByQuery(query: String): Result<List<ProductDto>>
    suspend fun getProducts(limit: Int, offset: Int): Result<List<ProductDto>>
    suspend fun getProductCategories(): Result<List<ProductCategoryDto>>
    suspend fun getProductsByCategory(categoryId: String): Result<List<ProductDto>>
    suspend fun getActivePromotionForProduct(productId: String): Result<PromotionDto?>
    suspend fun getProductReviews(productId: String): Result<List<ProductReviewDto>>
    suspend fun getAverageRating(productId: String): Result<Double?>
    suspend fun getReviewCount(productId: String): Result<Int>
}

class ProductRepositoryImpl(
    supabase: SupabaseClientManager
) : ProductRepository {

    private val postgrest = supabase.postgrest

    private val productsTableName = "products"
    private val productCategoriesTableName = "product_categories"
    private val promotionsTableName = "promotions"
    private val productPromotionsTableName = "product_promotions"
    private val productReviewsTableName = "product_reviews"

    override suspend fun getProductById(productId: String): Result<ProductDto?> =
        runCatching {
            postgrest
                .from(productsTableName)
                .select {
                    filter { eq("id", productId) }
                    limit(1)
                }
                .decodeSingleOrNull<ProductDto>()
        }

    override suspend fun getProductByQuery(query: String): Result<List<ProductDto>> =
        runCatching {
            val searchQuery = "%${query.trim()}%"

            postgrest
                .from(productsTableName)
                .select {
                    filter {
                        or {
                            ProductDto::title ilike searchQuery
                            ProductDto::description ilike searchQuery
                        }
                    }
                }
                .decodeList<ProductDto>()
        }

    override suspend fun getProducts(limit: Int, offset: Int): Result<List<ProductDto>> =
        runCatching {
            postgrest
                .from(productsTableName)
                .select {
                    range(from = offset.toLong(), to = (offset + limit - 1).toLong())
                }
                .decodeList<ProductDto>()
        }


    override suspend fun getProductCategories(): Result<List<ProductCategoryDto>> =
        runCatching {
            postgrest
                .from(productCategoriesTableName)
                .select()
                .decodeList<ProductCategoryDto>()
        }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<ProductDto>> =
        runCatching {
            postgrest
                .from(productsTableName)
                .select {
                    filter {
                        eq("category_id", categoryId)
                    }
                }
                .decodeList<ProductDto>()
        }

    override suspend fun getActivePromotionForProduct(productId: String): Result<PromotionDto?> =
        runCatching {
            val currentTime = Clock.System.now()

            val productPromotion = postgrest
                .from(productPromotionsTableName)
                .select {
                    filter {
                        eq("product_id", productId)
                    }
                    order("assigned_at", Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<ProductPromotionDto>()

            if (productPromotion != null) {
                val promotion = postgrest
                    .from(promotionsTableName)
                    .select {
                        filter {
                            eq("id", productPromotion.promotionId)
                            eq("is_active", true)
                            and {
                                or {
                                    isNull("start_at")
                                    lte("start_at", currentTime.toString())
                                }
                                or {
                                    isNull("end_at")
                                    gte("end_at", currentTime.toString())
                                }
                            }
                        }
                        limit(1)
                    }
                    .decodeSingleOrNull<PromotionDto>()
                promotion
            } else {
                null
            }
        }

    override suspend fun getProductReviews(productId: String): Result<List<ProductReviewDto>> =
        runCatching {
            postgrest
                .from(productReviewsTableName)
                .select {
                    filter {
                        eq("product_id", productId)
                        neq("review_text", "")
                    }
                }
                .decodeList<ProductReviewDto>()
        }

    override suspend fun getAverageRating(productId: String): Result<Double?> = runCatching {
        val reviews: List<ProductReviewDto> = postgrest
            .from(productReviewsTableName)
            .select {
                filter { eq("product_id", productId) }
            }
            .decodeList<ProductReviewDto>()

        if (reviews.isEmpty()) null
        else reviews.map { it.rating }.average()
    }

    override suspend fun getReviewCount(productId: String): Result<Int> = runCatching {
        postgrest
            .from(productReviewsTableName)
            .select {
                filter { eq("product_id", productId) }
            }
            .decodeList<ProductReviewDto>()
            .size
    }
}
package ru.takeshiko.matuleme.presentation.screen.checkout

import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.domain.models.UserCartItemDto

data class CheckoutCartItem(
    val cartItem: UserCartItemDto,
    val product: ProductDto? = null,
    val promotion: PromotionDto? = null,
    val finalPrice: Double = 0.0
)
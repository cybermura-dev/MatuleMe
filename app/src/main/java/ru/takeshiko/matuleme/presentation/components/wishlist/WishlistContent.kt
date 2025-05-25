package ru.takeshiko.matuleme.presentation.components.wishlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.components.product.ProductItem
import ru.takeshiko.matuleme.presentation.components.product.ShimmerProductItem
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun WishlistContent(
    products: List<ProductDto>,
    activePromotions: Map<String, PromotionDto>,
    ratings: Map<String, Double?>,
    reviewCounts: Map<String, Int>,
    isLoading: Boolean,
    processingFavoriteIds: Set<String> = emptySet(),
    onProductClick: (String) -> Unit,
    onRemoveFromWishlistClick: (String) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val brush = createShimmerBrush()
    val appColors = rememberAppColors()
    val typography = AppTypography

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(products, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            WishlistTopBar(
                products = products,
                onNavigateToBack = onNavigateToBack,
                isLoading = isLoading
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = !isLoading && products.isEmpty(),
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.empty_wishlist),
                        style = typography.titleLarge,
                        color = appColors.surfaceColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.no_products_in_wishlist),
                        style = typography.bodyLarge,
                        color = appColors.surfaceColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    repeat(8) {
                        item {
                            ShimmerProductItem(brush = brush)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    itemsIndexed(
                        items = products,
                        key = { index, product -> product.id ?: product.hashCode() }
                    ) { index, product ->
                        val productId = product.id ?: return@itemsIndexed

                        AnimatedVisibility(
                            visible = animateItems,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 50 * index,
                                    easing = FastOutSlowInEasing
                                )
                            ) + slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = 50 * index,
                                    easing = FastOutSlowInEasing
                                ),
                                initialOffsetY = { it / 5 }
                            ),
                            exit = fadeOut()
                        ) {
                            ProductItem(
                                product = product,
                                promotion = activePromotions[productId],
                                averageRating = ratings[productId],
                                reviewCount = reviewCounts[productId] ?: 0,
                                isFavorite = true,
                                isProcessingFavorite = processingFavoriteIds.contains(productId),
                                onClick = { onProductClick(productId) },
                                onFavoriteClick = { onRemoveFromWishlistClick(productId) },
                            )
                        }
                    }
                }
            }
        }
    }
}
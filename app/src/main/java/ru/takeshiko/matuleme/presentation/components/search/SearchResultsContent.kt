package ru.takeshiko.matuleme.presentation.components.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.components.product.ProductItem
import ru.takeshiko.matuleme.presentation.components.product.ShimmerProductItem

@Composable
fun SearchResultsContent(
    query: String,
    products: List<ProductDto>,
    activePromotions: Map<String, PromotionDto>,
    ratings: Map<String, Double?>,
    reviewCounts: Map<String, Int>,
    favoriteIds: Set<String>,
    isLoading: Boolean,
    processingFavoriteIds: Set<String> = emptySet(),
    onSearchClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    val brush = createShimmerBrush()

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(products, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SearchTopBar(
                searchText = query,
                onSearchClick = onSearchClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = !isLoading && products.isEmpty(),
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 150))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptySearchResultsCard(query)
                }
            }

            if (isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
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
                    contentPadding = PaddingValues(8.dp)
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
                                isFavorite = favoriteIds.contains(productId),
                                isProcessingFavorite = processingFavoriteIds.contains(productId),
                                onClick = { onProductClick(productId) },
                                onFavoriteClick = { onFavoriteClick(productId) },
                            )
                        }
                    }
                }
            }
        }
    }
}
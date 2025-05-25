package ru.takeshiko.matuleme.presentation.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
fun HomeContent(
    products: List<ProductDto>,
    activePromotions: Map<String, PromotionDto>,
    ratings: Map<String, Double?>,
    reviewCounts: Map<String, Int>,
    favoriteIds: Set<String>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    processingFavoriteIds: Set<String> = emptySet(),
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onScrolledToEnd: () -> Unit
) {
    val brush = createShimmerBrush()

    val listState = rememberLazyGridState()

    val reachedEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0 || isLoading || isLoadingMore) {
                false
            } else {
                val lastIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastIndex >= totalItems - 3
            }
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd && !isLoadingMore && !isLoading && products.isNotEmpty()) {
            onScrolledToEnd()
        }
    }

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(products, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            HomeTopBar(
                onSearchClick = onSearchClick,
                onNotificationsClick = onNotificationsClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading && products.isEmpty()) {
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
                    contentPadding = PaddingValues(8.dp),
                    state = listState
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

                    if (isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
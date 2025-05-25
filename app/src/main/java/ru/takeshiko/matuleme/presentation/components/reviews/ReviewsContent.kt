package ru.takeshiko.matuleme.presentation.components.reviews

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.ProductReviewDto
import ru.takeshiko.matuleme.presentation.components.cart.ShimmerCartItem
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ReviewsContent(
    reviews: List<ProductReviewDto>,
    currentFilter: Int?,
    onFilterChanged: (Int?) -> Unit,
    onNavigateToBack: () -> Unit,
    isLoading: Boolean = false
) {
    val appColors = rememberAppColors()
    val brush = createShimmerBrush()
    val typography = AppTypography

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(reviews, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = appColors.backgroundColor,
        topBar = {
            ReviewTopBar(onNavigateToBack = onNavigateToBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val filtered =
                reviews.filter { currentFilter == null || it.rating == currentFilter }

            ReviewFilterRow(currentFilter, onFilterChanged)

            when {
                isLoading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(5) {
                            ShimmerCartItem(brush = brush)
                        }
                    }
                }

                filtered.isEmpty() && !isLoading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentFilter == null)
                                stringResource(R.string.no_reviews_yet)
                            else
                                stringResource(R.string.no_reviews_for_filter),
                            style = typography.bodyMedium,
                            color = appColors.textSecondary
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(items = filtered) { index, review ->
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
                                ReviewItem(review)
                            }
                        }
                    }
                }
            }
        }
    }
}
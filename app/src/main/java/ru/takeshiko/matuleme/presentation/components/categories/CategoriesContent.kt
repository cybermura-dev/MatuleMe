package ru.takeshiko.matuleme.presentation.components.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.domain.models.ProductCategoryDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush

@Composable
fun CategoriesContent(
    categories: List<ProductCategoryDto>,
    isLoading: Boolean,
    onCategoryClick: (String) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val brush = createShimmerBrush()

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(categories, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CategoriesTopBar(onNavigateToBack = onNavigateToBack)
        }
    ) { padding ->
        when {
            isLoading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(6) {
                        ShimmerCategoryItem(brush = brush)
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = categories,
                        key = { index, category -> category.id ?: category.hashCode() }
                    ) { index, category ->
                        val categoryId = category.id ?: return@itemsIndexed

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
                            CategoryItem(
                                category = category,
                                onClick = { onCategoryClick(categoryId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
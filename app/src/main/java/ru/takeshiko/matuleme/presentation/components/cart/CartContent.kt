package ru.takeshiko.matuleme.presentation.components.cart

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserCartItemDto
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun CartContent(
    cartItems: List<UserCartItemDto>,
    productDetailsMap: Map<String, ProductDto>,
    promotionsMap: Map<String, PromotionDto>,
    isLoading: Boolean,
    processingCartItemIds: Set<String> = emptySet(),
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateToBack: () -> Unit,
    onCheckout: () -> Unit
) {
    val appColors = rememberAppColors()
    val brush = createShimmerBrush()
    val typography = AppTypography
    val density = LocalDensity.current

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(cartItems, isLoading) {
        animateItems = !isLoading
    }

    val totalPrice by remember(cartItems, productDetailsMap, promotionsMap) {
        derivedStateOf {
            cartItems.sumOf { cartItem ->
                val product = productDetailsMap[cartItem.productId]
                val promotion = promotionsMap[cartItem.productId]

                if (product != null) {
                    val basePrice = product.basePrice
                    val finalPrice = if (promotion != null) {
                        basePrice * (1 - promotion.discountPercent / 100)
                    } else {
                        basePrice
                    }

                    finalPrice * cartItem.quantity
                } else {
                    0.0
                }
            }
        }
    }

    val hasItems = cartItems.isNotEmpty()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CartTopBar(
                cartItems = cartItems,
                onNavigateToBack = onNavigateToBack,
                isLoading = isLoading
            )
        },
        bottomBar = {
            if (!isLoading && hasItems) {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(700)) + slideInVertically(
                        initialOffsetY = { with(density) { 100.dp.roundToPx() } },
                        animationSpec = tween(700)
                    ),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = appColors.primaryColor.copy(alpha = 0.2f)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = appColors.surfaceColor.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val priceText = String.format(Locale.ROOT, "%.0f ₽", totalPrice)

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.total_price),
                                    style = typography.bodyMedium.copy(
                                        color = appColors.textSecondary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = priceText,
                                    style = typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = appColors.textPrimary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Button(
                                onClick = onCheckout,
                                enabled = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = appColors.secondaryColor
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 0.dp
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_cart),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = stringResource(R.string.proceed_to_checkout),
                                    style = typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            isLoading -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(5) {
                        ShimmerCartItem(brush = brush)
                    }
                }
            }

            cartItems.isEmpty() && !isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = appColors.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = stringResource(R.string.empty_cart),
                            style = typography.titleMedium.copy(
                                color = appColors.textSecondary,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        )

                        Text(
                            text = stringResource(R.string.add_some_products),
                            style = typography.bodyMedium.copy(
                                color = appColors.textSecondary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = cartItems,
                        key = { _, item -> item.id ?: item.productId }
                    ) { index, item ->
                        val product = productDetailsMap[item.productId]
                        val promotion = promotionsMap[item.productId]

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
                            val isProcessing = processingCartItemIds.contains(item.id)

                            CartItem(
                                cartItem = item,
                                product = product,
                                promotion = promotion,
                                isProcessing = isProcessing,
                                isLoading = isLoading,
                                onProductClick = onProductClick,
                                onIncreaseQuantity = onIncreaseQuantity,
                                onDecreaseQuantity = onDecreaseQuantity,
                                onRemoveItem = onRemoveItem
                            )
                        }
                    }
                }
            }
        }
    }
}
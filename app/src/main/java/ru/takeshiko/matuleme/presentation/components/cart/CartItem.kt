package ru.takeshiko.matuleme.presentation.components.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserCartItemDto
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.presentation.components.product.ProductImage
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun CartItem(
    cartItem: UserCartItemDto,
    product: ProductDto?,
    promotion: PromotionDto?,
    isProcessing: Boolean,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    if (isLoading || product == null) {
        ShimmerCartItem(brush = createShimmerBrush())
        return
    }

    val appColors = rememberAppColors()
    val typography = AppTypography

    val discountScale by animateFloatAsState(
        targetValue = if (promotion != null) 1f else 0.8f,
        animationSpec = tween(durationMillis = 300)
    )

    val priceColor by animateColorAsState(
        targetValue = if (promotion != null) appColors.errorColor else appColors.textPrimary,
        animationSpec = tween(durationMillis = 300)
    )

    val productName = product.title
    val basePrice = product.basePrice
    val productImageUrl = product.imageUrl
    val currentPrice = promotion?.let { promo ->
        basePrice * (1 - promo.discountPercent / 100)
    } ?: basePrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = appColors.primaryColor.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    appColors.primaryColor.copy(alpha = 0.1f),
                                    appColors.primaryColor.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .clickable { onProductClick(cartItem.productId) }
                ) {
                    ProductImage(
                        imageUrl = productImageUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )

                    promotion?.let {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(36.dp)
                                .scale(discountScale)
                                .shadow(4.dp, CircleShape)
                                .background(color = appColors.errorColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-${it.discountPercent.toInt()}%",
                                style = typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = productName,
                        style = typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp,
                            lineHeight = 20.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onProductClick(cartItem.productId) }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format(Locale.ROOT, "%.0f ₽", currentPrice),
                            style = typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            ),
                            color = priceColor
                        )

                        promotion?.let {
                            Text(
                                text = String.format(Locale.ROOT, "%.0f ₽", basePrice),
                                style = typography.bodySmall.copy(
                                    textDecoration = TextDecoration.LineThrough,
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    QuantityControls(
                        quantity = cartItem.quantity,
                        productId = cartItem.productId,
                        isProcessing = isProcessing,
                        onDecreaseQuantity = onDecreaseQuantity,
                        onIncreaseQuantity = onIncreaseQuantity
                    )
                }
            }

            HorizontalDivider(color = appColors.primaryColor.copy(alpha = 0.1f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.cart_total),
                    style = typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = appColors.textSecondary
                    )
                )

                val totalPrice = currentPrice * cartItem.quantity
                
                Text(
                    text = String.format(Locale.ROOT, "%.0f ₽", totalPrice),
                    style = typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = appColors.primaryColor
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { onRemoveItem(cartItem.productId) },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, appColors.errorColor.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = appColors.errorColor
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = stringResource(R.string.remove_cart_item),
                        style = typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
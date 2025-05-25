package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import ru.takeshiko.matuleme.presentation.screen.checkout.CheckoutCartItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.product.ProductImage
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun CartItemRow(item: CheckoutCartItem) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            appColors.primaryColor.copy(alpha = 0.1f),
                            appColors.primaryColor.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            ProductImage(
                imageUrl = item.product!!.imageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = item.product?.title ?: stringResource(R.string.unknown_product),
                style = typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = appColors.textPrimary,
                    lineHeight = 18.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val basePrice = item.product?.basePrice ?: 0.0
                val currentPrice = item.finalPrice

                if (item.promotion != null && basePrice > currentPrice) {
                    Text(
                        text = String.format(Locale.ROOT, "%.0f ₽", basePrice),
                        style = typography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = appColors.textSecondary.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    )
                }

                Text(
                    text = String.format(Locale.ROOT, "%.0f ₽", currentPrice),
                    style = typography.bodySmall.copy(
                        fontWeight = if (item.promotion != null && basePrice > currentPrice) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (item.promotion != null && basePrice > currentPrice)
                            appColors.errorColor
                        else
                            appColors.textPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "x${item.cartItem.quantity}",
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = appColors.textSecondary
            )
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = String.format(Locale.ROOT, "%.0f ₽", item.finalPrice * item.cartItem.quantity),
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = appColors.primaryColor
            )
        )
    }
}
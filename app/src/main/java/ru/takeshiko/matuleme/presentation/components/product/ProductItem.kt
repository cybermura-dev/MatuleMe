package ru.takeshiko.matuleme.presentation.components.product

import androidx.compose.runtime.Composable
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun ProductItem(
    product: ProductDto,
    promotion: PromotionDto?,
    averageRating: Double?,
    reviewCount: Int,
    isFavorite: Boolean,
    isProcessingFavorite: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = appColors.surfaceColor.copy(alpha = 0.05f)),
                contentAlignment = Alignment.TopEnd
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )

                promotion?.let {
                    Text(
                        text = "-${promotion.discountPercent.toInt()}%",
                        color = Color.White,
                        style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                color = appColors.errorColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(16.dp),
                    enabled = !isProcessingFavorite
                ) {
                    if (isProcessingFavorite) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = appColors.primaryColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                if (isFavorite) R.drawable.ic_favorite_fill else R.drawable.ic_favorite
                            ),
                            contentDescription = null,
                            tint = if (isFavorite) appColors.errorColor else Color.White
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (promotion != null) {
                    val discountedPrice = product.basePrice * (1 - promotion.discountPercent / 100)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = String.format(Locale.ROOT, "%.0f₽", product.basePrice),
                            style = typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.Gray.copy(alpha = 0.6f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = String.format(Locale.ROOT, "%.0f₽", discountedPrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = appColors.errorColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = String.format(Locale.ROOT, "%.0f₽", product.basePrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = appColors.primaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = product.title,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = appColors.ratingColor,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = averageRating?.let { String.format(Locale.ROOT, "%.1f", it) } ?: "-",
                        style = typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = pluralStringResource(
                            id = R.plurals.review_count,
                            count = reviewCount,
                            reviewCount
                        ),
                        style = typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Light
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
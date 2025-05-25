package ru.takeshiko.matuleme.presentation.components.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.PromotionDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun ProductDetailsContent(
    product: ProductDto,
    rating: Double?,
    reviewCount: Int,
    promotion: PromotionDto?,
    isFavorite: Boolean,
    isInCart: Boolean,
    isLoading: Boolean,
    onNavigateToBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onViewReviews: () -> Unit,
    onToggleCart: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val density = LocalDensity.current

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(product, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = appColors.backgroundColor,
        bottomBar = {
            if (!isLoading) {
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
                            val priceText = if (promotion != null) {
                                val discountedPrice =
                                    product.basePrice * (1 - promotion.discountPercent / 100)
                                String.format(Locale.ROOT, "%.0f ₽", discountedPrice)
                            } else {
                                String.format(Locale.ROOT, "%.0f ₽", product.basePrice)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = priceText,
                                    style = typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (promotion != null) appColors.errorColor else appColors.textPrimary
                                    )
                                )
                                if (promotion != null) {
                                    Text(
                                        text = String.format(Locale.ROOT, "%.0f ₽", product.basePrice),
                                        style = typography.bodySmall.copy(
                                            color = appColors.textSecondary,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = onToggleCart,
                                enabled = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isInCart) appColors.primaryLightColor
                                    else appColors.secondaryColor
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 0.dp
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (isInCart) R.drawable.ic_checkmark else R.drawable.ic_cart
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = if (isInCart) appColors.primaryColor else MaterialTheme.colorScheme.onSecondary
                                )

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = if (isInCart) stringResource(R.string.added_to_cart)
                                    else stringResource(R.string.add_in_cart),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = if (isInCart) appColors.primaryColor
                                    else MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = appColors.primaryColor,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.loading_product),
                        style = typography.bodyMedium,
                        color = appColors.textSecondary
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(700)) + slideInVertically(
                        initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                        animationSpec = tween(700)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .aspectRatio(1f)
                            .clip(
                                RoundedCornerShape(
                                    0.dp,
                                    0.dp,
                                    16.dp,
                                    16.dp
                                )
                            )
                            .background(color = appColors.surfaceColor.copy(alpha = 0.1f)),
                    ) {
                        ProductImage(
                            imageUrl = product.imageUrl,
                            modifier = Modifier.matchParentSize()
                        )

                        Box(
                            Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        0f to Color.Transparent,
                                        0.7f to Color.Black.copy(alpha = 0.3f),
                                        1f to Color.Black.copy(alpha = 0.5f)
                                    )
                                )
                        )

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onNavigateToBack,
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(8.dp, CircleShape)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = null,
                                    tint = appColors.textPrimary
                                )
                            }

                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(8.dp, CircleShape)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (isFavorite) R.drawable.ic_favorite_fill
                                        else R.drawable.ic_favorite
                                    ),
                                    contentDescription = null,
                                    tint = if (isFavorite) appColors.errorColor else appColors.textSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        promotion?.let {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 16.dp, bottom = 16.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(appColors.errorColor)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_discount),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "-${it.discountPercent.toInt()}%",
                                    style = typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(700)) + slideInVertically(
                        initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                        animationSpec = tween(700)
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = product.title,
                            style = typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = appColors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = appColors.ratingColor,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                text = rating?.let { String.format(Locale.ROOT, "%.1f", it) } ?: "-",
                                style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Text(
                                text = "•",
                                style = typography.bodyMedium,
                                color = appColors.textSecondary
                            )

                            Text(
                                text = pluralStringResource(
                                    R.plurals.review_count, reviewCount, reviewCount
                                ),
                                style = typography.bodyMedium.copy(color = appColors.textSecondary)
                            )
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(900)) + slideInVertically(
                        initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                        animationSpec = tween(900)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = appColors.surfaceColor
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.price),
                                    style = typography.titleMedium.copy(
                                        color = appColors.textSecondary
                                    )
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (promotion != null) {
                                        val discounted = product.basePrice * (1 - promotion.discountPercent / 100)

                                        Text(
                                            text = String.format(Locale.ROOT, "%.0f ₽", product.basePrice),
                                            style = typography.bodyMedium.copy(
                                                textDecoration = TextDecoration.LineThrough,
                                                color = appColors.textSecondary
                                            )
                                        )

                                        Text(
                                            text = String.format(Locale.ROOT, "%.0f ₽", discounted),
                                            style = typography.headlineSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = appColors.errorColor
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = String.format(Locale.ROOT, "%.0f ₽", product.basePrice),
                                            style = typography.headlineSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = appColors.textPrimary
                                            )
                                        )
                                    }
                                }
                            }

                            promotion?.let { promo ->
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(appColors.promotionBackgroundColor.copy(alpha = 0.15f))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(appColors.primaryColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_promotion),
                                            contentDescription = null,
                                            tint = appColors.primaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = promo.name,
                                            style = typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = appColors.primaryColor
                                            )
                                        )

                                        promo.description?.let { description ->
                                            Text(
                                                text = description,
                                                style = typography.bodySmall.copy(
                                                    color = appColors.textSecondary
                                                ),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(1100)) + slideInVertically(
                        initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                        animationSpec = tween(1100)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = appColors.surfaceColor
                        ),
                        onClick = onViewReviews,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = appColors.ratingColor
                                        ) {
                                            Text(
                                                text = "$reviewCount",
                                                style = typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color.White
                                            )
                                        }
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(appColors.ratingColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = appColors.ratingColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = stringResource(R.string.view_reviews),
                                        style = typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Text(
                                        text = rating?.let {
                                            String.format(Locale.ROOT, stringResource(R.string.rating_format, rating), it)
                                        } ?: stringResource(R.string.no_ratings),
                                        style = typography.bodySmall.copy(
                                            color = appColors.textSecondary
                                        )
                                    )
                                }
                            }

                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_right),
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(1300)) + slideInVertically(
                        initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                        animationSpec = tween(1300)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = appColors.surfaceColor
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.description),
                                style = typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = product.description,
                                style = typography.bodyMedium.copy(
                                    color = appColors.textSecondary,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
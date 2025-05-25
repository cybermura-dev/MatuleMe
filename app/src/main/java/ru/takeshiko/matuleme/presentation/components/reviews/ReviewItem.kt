package ru.takeshiko.matuleme.presentation.components.reviews

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.domain.models.ProductReviewDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

@Composable
fun ReviewItem(
    review: ProductReviewDto
) {
    val colors = rememberAppColors()
    val typography = AppTypography

    val avatarColorSeed = review.userId.hashCode()
    val avatarColor = Color(
        red = min(0.9f, 0.4f + (avatarColorSeed % 1000) / 2000f),
        green = min(0.9f, 0.4f + (avatarColorSeed % 500) / 1000f),
        blue = min(0.9f, 0.4f + (avatarColorSeed % 300) / 600f),
        alpha = 1.0f
    )

    val userInitial = review.userId.take(1).uppercase().ifEmpty { "U" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = colors.primaryColor.copy(alpha = 0.1f)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.surfaceColor,
                            colors.surfaceColor.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        avatarColor,
                                        avatarColor.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .shadow(4.dp, CircleShape)
                    ) {
                        Text(
                            text = userInitial,
                            style = typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "User #${review.userId}",
                            style = typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val dateString = runCatching {
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(Date(review.createdAt.toEpochMilliseconds()))
                        }.getOrDefault("—")

                        Text(
                            text = dateString,
                            style = typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.sp
                            ),
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                color = if (review.rating >= 4)
                                    colors.primaryColor.copy(alpha = 0.1f)
                                else
                                    colors.errorColor.copy(alpha = 0.1f)
                            )
                    ) {
                        Text(
                            text = "${review.rating}",
                            style = typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (review.rating >= 4)
                                    colors.primaryColor
                                else
                                    colors.errorColor
                            )
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    repeat(5) { idx ->
                        val isFilled = idx < review.rating
                        Icon(
                            imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (isFilled) colors.ratingColor else colors.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                review.reviewText?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                    style = typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 22.sp,
                        letterSpacing = 0.1.sp
                    ),
                    color = colors.textPrimary.copy(alpha = 0.9f),
                    fontSize = 15.sp
                    )
                }
            }
        }
    }
}
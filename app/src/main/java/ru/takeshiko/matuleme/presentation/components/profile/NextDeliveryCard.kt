package ru.takeshiko.matuleme.presentation.components.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun NextDeliveryCard(
    orders: List<UserOrderDto>,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val brush = createShimmerBrush()
    val animate = !isLoading

    val nextDelivery = orders
        .filter { it.status != OrderStatus.COMPLETED }
        .minByOrNull { it.createdAt }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_marker),
                contentDescription = null,
                tint = appColors.primaryColor,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_next_delivery_title),
                    style = typography.titleMedium,
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AnimatedVisibility(
                    visible = animate,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
                    exit = fadeOut()
                ) {
                    if (nextDelivery != null) {
                        DeliveryInfo(
                            nextDelivery = nextDelivery,
                            appColors = appColors,
                            typography = typography
                        )
                    } else {
                        if (isLoading) {
                            LoadingContent(brush = brush)
                        } else {
                            EmptyStateContent(
                                appColors = appColors,
                                typography = typography
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = appColors.primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DeliveryInfo(
    nextDelivery: UserOrderDto,
    appColors: ru.takeshiko.matuleme.presentation.theme.AppColorsInstance,
    typography: androidx.compose.material3.Typography
) {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(locale)
    val created = nextDelivery.createdAt
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toJavaLocalDateTime()
        .format(formatter)

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(
                R.string.profile_delivery_order_number,
                nextDelivery.orderNumber
            ),
            style = typography.bodyLarge,
            color = appColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (nextDelivery.totalAmount > 0) {
            Text(
                text = stringResource(
                    R.string.profile_order_total_price,
                    "%.2f".format(nextDelivery.totalAmount)
                ),
                style = typography.bodySmall,
                color = appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = stringResource(
                R.string.profile_delivery_status,
                nextDelivery.status.name.lowercase(locale)
            ),
            style = typography.bodySmall,
            color = appColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(R.string.profile_delivery_date, created),
            style = typography.bodySmall,
            color = appColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoadingContent(
    brush: androidx.compose.ui.graphics.Brush
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .height(16.dp)
                .fillMaxWidth(0.6f)
                .background(brush, RoundedCornerShape(4.dp))
        )

        Box(
            Modifier
                .height(16.dp)
                .fillMaxWidth(0.4f)
                .background(brush, RoundedCornerShape(4.dp))
        )

        Box(
            Modifier
                .height(12.dp)
                .fillMaxWidth(0.5f)
                .background(brush, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun EmptyStateContent(
    appColors: ru.takeshiko.matuleme.presentation.theme.AppColorsInstance,
    typography: androidx.compose.material3.Typography
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_no_upcoming_deliveries),
            style = typography.bodyMedium,
            color = appColors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(R.string.profile_create_first_order_prompt),
            style = typography.bodySmall,
            color = appColors.textSecondary.copy(alpha = 0.7f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun kotlinx.datetime.LocalDateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(
        this.year,
        this.monthNumber,
        this.dayOfMonth,
        this.hour,
        this.minute,
        this.second,
        this.nanosecond
    )
}
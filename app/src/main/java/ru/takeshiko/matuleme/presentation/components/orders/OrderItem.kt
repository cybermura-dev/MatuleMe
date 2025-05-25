package ru.takeshiko.matuleme.presentation.components.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderItem(
    order: UserOrderDto,
    onOrderClick: (String) -> Unit,
    onViewDetailsClick: (String) -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable { onOrderClick(order.id.orEmpty()) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = appColors.surfaceColor
        )
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                appColors.primaryColor.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 60f
                        ),
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.order_number_format, order.orderNumber),
                            style = typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = order.createdAt.toFormattedDate(),
                            style = typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OrderStatusBadge(status = order.status)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    appColors.textSecondary.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow(
                        label = "Email:",
                        value = order.email,
                        valueColor = appColors.textPrimary
                    )

                    InfoRow(
                        label = stringResource(R.string.address_label),
                        value = order.address,
                        valueColor = appColors.textSecondary
                    )

                    InfoRow(
                        label = stringResource(R.string.payment_method_label),
                        value = "${order.cardHolder} (${order.cardNumber.takeLast(4)})",
                        valueColor = appColors.textPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.order_total_label),
                            style = typography.labelMedium,
                            color = appColors.textSecondary
                        )

                        Text(
                            text = order.totalAmount.formatPrice(),
                            style = typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = appColors.successColor
                        )
                    }

                    Button(
                        onClick = { onViewDetailsClick(order.id.orEmpty()) },
                        modifier = Modifier.height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryColor,
                            contentColor = appColors.surfaceColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.view_details),
                            style = typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF333333)
) {
    val appColors = rememberAppColors()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = appColors.textSecondary.copy(alpha = 0.8f),
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            style = AppTypography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = valueColor,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val statusConfig = getStatusConfig(status)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(statusConfig.backgroundColor)
            .border(
                width = 1.dp,
                color = statusConfig.borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = statusConfig.icon,
                contentDescription = null,
                tint = statusConfig.textColor,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = statusConfig.text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                ),
                color = statusConfig.textColor
            )
        }
    }
}

@Composable
private fun getStatusConfig(status: OrderStatus): StatusConfig {
    return when (status) {
        OrderStatus.NEW -> StatusConfig(
            backgroundColor = Color(0xFFFFF3CD),
            borderColor = Color(0xFFFFE066),
            textColor = Color(0xFFB8860B),
            icon = Icons.Default.Schedule,
            text = stringResource(R.string.order_status_new)
        )
        OrderStatus.PAID -> StatusConfig(
            backgroundColor = Color(0xFFE3F2FD),
            borderColor = Color(0xFF90CAF9),
            textColor = Color(0xFF1565C0),
            icon = Icons.Default.Payment,
            text = stringResource(R.string.order_status_paid)
        )
        OrderStatus.SHIPPED -> StatusConfig(
            backgroundColor = Color(0xFFE0F2F1),
            borderColor = Color(0xFF80CBC4),
            textColor = Color(0xFF00695C),
            icon = Icons.Default.LocalShipping,
            text = stringResource(R.string.order_status_shipped)
        )
        OrderStatus.COMPLETED -> StatusConfig(
            backgroundColor = Color(0xFFE8F5E9),
            borderColor = Color(0xFFA5D6A7),
            textColor = Color(0xFF2E7D32),
            icon = Icons.Default.CheckCircle,
            text = stringResource(R.string.order_status_completed)
        )
        OrderStatus.CANCELLED -> StatusConfig(
            backgroundColor = Color(0xFFFFEBEE),
            borderColor = Color(0xFFEF9A9A),
            textColor = Color(0xFFC62828),
            icon = Icons.Default.Cancel,
            text = stringResource(R.string.order_status_cancelled)
        )
    }
}

private data class StatusConfig(
    val backgroundColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val icon: ImageVector,
    val text: String
)

fun Instant.toFormattedDate(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val year = localDateTime.year
    return "$day.$month.$year"
}

fun Double.formatPrice(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    format.maximumFractionDigits = 0
    return format.format(this)
}
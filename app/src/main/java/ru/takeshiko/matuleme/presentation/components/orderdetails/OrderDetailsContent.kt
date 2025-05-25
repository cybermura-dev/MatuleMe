package ru.takeshiko.matuleme.presentation.components.orderdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.models.ProductDto
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.presentation.components.orders.OrderStatusBadge
import ru.takeshiko.matuleme.presentation.components.orders.formatPrice
import ru.takeshiko.matuleme.presentation.components.product.ProductImage
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import ru.takeshiko.matuleme.R

@Composable
fun OrderDetailsContent(
    order: UserOrderDto?,
    items: List<OrderProductItemDto>,
    products: Map<String, ProductDto>,
    isLoading: Boolean,
    onPayClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onNavigateToBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            OrderDetailsTopBar(
                orderId = order?.id,
                onNavigateToBack = onNavigateToBack
            )
        },
        bottomBar = {
            if (order != null) {
                OrderDetailsBottomBar(
                    orderStatus = order.status,
                    items = items,
                    isLoading = isLoading,
                    onPayClick = onPayClick,
                    onConfirmClick = onConfirmClick,
                    onCancelClick = onCancelClick
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (order != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = appColors.textPrimary.copy(alpha = 0.1f),
                            spotColor = appColors.textPrimary.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = appColors.surfaceColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OrderStatusBadge(status = order.status)
                        }

                        Text(
                            text = stringResource(id = R.string.delivery_and_payment),
                            style = typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary
                        )
                        HorizontalDivider(color = appColors.textSecondary.copy(alpha = 0.2f))

                        InfoRow(
                            label = stringResource(id = R.string.address_label),
                            value = order.address,
                            valueColor = appColors.textPrimary
                        )

                        InfoRow(
                            label = stringResource(id = R.string.payment_method_label),
                            value = "${order.cardHolder} (${order.cardNumber.takeLast(4)})",
                            valueColor = appColors.textPrimary
                        )

                        InfoRow(
                            label = stringResource(id = R.string.order_total_label),
                            value = items.sumOf { it.quantity * it.price }.formatPrice(),
                            valueColor = appColors.successColor
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = appColors.textPrimary.copy(alpha = 0.1f),
                            spotColor = appColors.textPrimary.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = appColors.surfaceColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.order_items),
                            style = typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary
                        )

                        HorizontalDivider(color = appColors.textSecondary.copy(alpha = 0.2f))

                        items.forEach { item ->
                            val product = products[item.productId]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProductImage(
                                    imageUrl = product?.imageUrl ?: "",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = product?.title ?: stringResource(id = R.string.product_not_found),
                                        style = typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = appColors.textPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.quantity_and_price,
                                            item.quantity,
                                            item.price
                                        ),
                                        style = typography.bodySmall,
                                        color = appColors.textSecondary
                                    )
                                }

                                Text(
                                    text = stringResource(
                                        id = R.string.total_price,
                                        item.quantity * item.price
                                    ),
                                    style = typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.textPrimary
                                )
                            }

                            if (item != items.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = appColors.textSecondary.copy(alpha = 0.2f)
                                )
                            }
                        }
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
    valueColor: Color
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = appColors.textSecondary.copy(alpha = 0.8f),
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = valueColor,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
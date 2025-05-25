package ru.takeshiko.matuleme.presentation.components.orderdetails

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun OrderDetailsBottomBar(
    orderStatus: OrderStatus,
    items: List<OrderProductItemDto>,
    isLoading: Boolean,
    onPayClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = appColors.surfaceColor,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = ru.takeshiko.matuleme.R.string.total),
                    style = typography.titleMedium,
                    color = appColors.textPrimary
                )

                Text(
                    text = stringResource(
                        id = ru.takeshiko.matuleme.R.string.order_total_price,
                        items.sumOf { it.quantity * it.price }
                    ),
                    style = typography.titleMedium,
                    color = appColors.textPrimary
                )
            }

            when (orderStatus) {
                OrderStatus.NEW -> {
                    Button(
                        onClick = onPayClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryColor,
                            contentColor = appColors.surfaceColor,
                            disabledContainerColor = appColors.primaryColor.copy(alpha = 0.5f),
                            disabledContentColor = appColors.surfaceColor.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(text = stringResource(id = ru.takeshiko.matuleme.R.string.pay))
                    }
                }
                OrderStatus.SHIPPED -> {
                    Button(
                        onClick = onConfirmClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryColor,
                            contentColor = appColors.surfaceColor,
                            disabledContainerColor = appColors.primaryColor.copy(alpha = 0.5f),
                            disabledContentColor = appColors.surfaceColor.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(text = stringResource(id = ru.takeshiko.matuleme.R.string.confirm_order_received))
                    }
                }
                else -> {}
            }

            if (orderStatus != OrderStatus.SHIPPED && orderStatus != OrderStatus.COMPLETED && orderStatus != OrderStatus.CANCELLED) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = appColors.errorColor
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(appColors.errorColor)
                    )
                ) {
                    Text(text = stringResource(id = ru.takeshiko.matuleme.R.string.cancel_order))
                }
            }
        }
    }
} 
package ru.takeshiko.matuleme.presentation.components.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun OrdersContent(
    orders: List<UserOrderDto>,
    isLoading: Boolean,
    onOrderClick: (String) -> Unit,
    onViewDetails: (String) -> Unit,
    onNavigateToBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val brush = createShimmerBrush()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            OrdersTopBar(
                orders = orders,
                onNavigateToBack = onNavigateToBack,
                isLoading = isLoading
            )
        }
    ) { padding ->
        Box(modifier = modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        repeat(5) {
                            ShimmerOrderItem(brush)
                        }
                    }
                }
                orders.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_orders_yet),
                        style = typography.bodyMedium.copy(
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(orders, key = { it.id ?: it.orderNumber }) { order ->
                            OrderItem(
                                order = order,
                                onOrderClick = onOrderClick,
                                onViewDetailsClick = onViewDetails
                            )
                        }
                    }
                }
            }
        }
    }
}
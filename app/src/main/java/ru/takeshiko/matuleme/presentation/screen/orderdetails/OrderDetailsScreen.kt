package ru.takeshiko.matuleme.presentation.screen.orderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog
import ru.takeshiko.matuleme.presentation.components.orderdetails.OrderDetailsContent

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    orderId: String,
    viewModel: OrderDetailsViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    
    val order by viewModel.order.collectAsState()
    val items by viewModel.items.collectAsState()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    LaunchedEffect(errorMessage) {
        showNotificationCard = !errorMessage.isNullOrBlank()
    }

    Box(Modifier.fillMaxSize()) {
        OrderDetailsContent(
            order = order,
            items = items,
            products = products,
            isLoading = isLoading,
            onPayClick = { viewModel.payForOrder(orderId) },
            onConfirmClick = { 
                viewModel.confirmOrderReceived(orderId) {
                    navController.navigateUp()
                }
            },
            onCancelClick = {
                viewModel.cancelOrder(orderId) {
                    navController.navigateUp()
                }
            },
            onNavigateToBack = navController::navigateUp,
            modifier = Modifier.fillMaxSize()
        )

        if (showNotificationCard) {
            EventDialog(
                icon = Icons.Default.Close,
                title = stringResource(R.string.unknown_error_occurred),
                message = errorMessage.orEmpty(),
                onDismiss = { 
                    showNotificationCard = false
                    viewModel.clearErrorMessage()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
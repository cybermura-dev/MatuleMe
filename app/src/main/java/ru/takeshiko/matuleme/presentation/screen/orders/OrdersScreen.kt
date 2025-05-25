package ru.takeshiko.matuleme.presentation.screen.orders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog
import ru.takeshiko.matuleme.presentation.components.orders.OrdersContent

@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrders().also {
            delay(1000)
            initialLoadDone = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone && orders.isEmpty() && !isLoading && errorMessage == null) {
                    viewModel.loadOrders()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(errorMessage) {
        showNotificationCard = !errorMessage.isNullOrBlank()
    }

    Box(Modifier.fillMaxSize()) {
        OrdersContent(
            orders = orders,
            isLoading = isLoading,
            onOrderClick = { navController.navigate("order/{$it}")},
            onViewDetails = { navController.navigate("order/{$it}") },
            onNavigateToBack = navController::navigateUp,
            modifier = Modifier.fillMaxSize()
        )

        if (showNotificationCard) {
            EventDialog(
                icon = Icons.Default.Close,
                title = stringResource(R.string.unknown_error_occurred),
                message = errorMessage.orEmpty(),
                onDismiss = { showNotificationCard = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
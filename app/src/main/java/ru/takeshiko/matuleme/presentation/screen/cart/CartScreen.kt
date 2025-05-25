package ru.takeshiko.matuleme.presentation.screen.cart

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
import ru.takeshiko.matuleme.presentation.components.cart.CartContent
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val cartItems by viewModel.cartItems.collectAsState()
    val productDetailsMap by viewModel.productDetailsMap.collectAsState()
    val promotionsMap by viewModel.promotionsMap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val processingCartItemIds by viewModel.processingCartItemIds.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCartItems().also {
            delay(1000)
            initialLoadDone = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone) {
                    viewModel.loadCartItems()
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
        CartContent(
            cartItems = cartItems,
            productDetailsMap = productDetailsMap,
            promotionsMap = promotionsMap,
            isLoading = isLoading,
            processingCartItemIds = processingCartItemIds,
            onIncreaseQuantity = viewModel::incrementQuantity,
            onDecreaseQuantity = viewModel::decrementQuantity,
            onRemoveItem = viewModel::removeItem,
            onProductClick = { productId -> navController.navigate("product/${productId}") },
            onNavigateToBack = navController::navigateUp,
            onCheckout = { navController.navigate("checkout") }
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
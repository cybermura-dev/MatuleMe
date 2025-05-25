package ru.takeshiko.matuleme.presentation.screen.productdetails

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
import ru.takeshiko.matuleme.presentation.components.product.ProductDetailsContent

@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productId: String? = null,
    viewModel: ProductDetailsViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val product by viewModel.product.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val promotion by viewModel.promotion.collectAsState()
    val reviewCount by viewModel.reviewCount.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isCart by viewModel.isCart.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(productId) {
        productId?.let {
            viewModel.loadProductDetails(it).also {
                delay(1000)
                initialLoadDone = true
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone) {
                    productId?.let { viewModel.loadProductDetails(it) }
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
        if (product != null) {
            ProductDetailsContent(
                product = product!!,
                rating = rating,
                reviewCount = reviewCount,
                promotion = promotion,
                isFavorite = isFavorite,
                isLoading = isLoading,
                isInCart = isCart,
                onNavigateToBack = navController::navigateUp,
                onToggleFavorite = { productId?.let { viewModel.toggleFavorite(it) } },
                onViewReviews = { productId?.let { navController.navigate("reviews/${productId}") } },
                onToggleCart = { productId?.let { viewModel.toggleCart(it) } }
            )
        }

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

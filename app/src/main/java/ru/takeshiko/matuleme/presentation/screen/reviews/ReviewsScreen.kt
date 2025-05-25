package ru.takeshiko.matuleme.presentation.screen.reviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog
import ru.takeshiko.matuleme.presentation.components.reviews.ReviewsContent

@Composable
fun ReviewsScreen(
    navController: NavController,
    productId: String? = null,
    viewModel: ReviewsViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val filteredReviews by viewModel.filteredReviews.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(productId) {
        productId?.let {
            viewModel.loadReviews(it).also {
                delay(1000)
                initialLoadDone = true
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone) {
                    productId?.let { viewModel.loadReviews(it) }
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

    Box(modifier = Modifier.fillMaxSize()) {
        ReviewsContent(
            reviews = filteredReviews,
            currentFilter = currentFilter,
            onFilterChanged = viewModel::setFilter,
            onNavigateToBack = navController::navigateUp,
            isLoading = isLoading
        )

        if (showNotificationCard) {
            EventDialog(
                icon = Icons.Default.Close,
                title = stringResource(R.string.unknown_error_occurred),
                message = errorMessage.orEmpty(),
                onDismiss = { showNotificationCard = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
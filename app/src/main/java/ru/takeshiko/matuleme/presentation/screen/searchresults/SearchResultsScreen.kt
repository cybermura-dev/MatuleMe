package ru.takeshiko.matuleme.presentation.screen.searchresults

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
import ru.takeshiko.matuleme.presentation.components.search.SearchResultsContent

@Composable
fun SearchResultsScreen(
    navController: NavController,
    viewModel: SearchResultsViewModel = koinViewModel(),
    query: String
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val products by viewModel.products.collectAsState()
    val activePromotions by viewModel.activePromotions.collectAsState()
    val ratings by viewModel.ratings.collectAsState()
    val reviewCounts by viewModel.reviewCounts.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val processingFavoriteIds by viewModel.processingFavoriteIds.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(query) {
        viewModel.loadInitialData(query).also {
            delay(1000)
            initialLoadDone = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone) {
                    viewModel.loadInitialData(query)
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
        SearchResultsContent(
            query = query,
            products = products,
            activePromotions = activePromotions,
            ratings = ratings,
            reviewCounts = reviewCounts,
            favoriteIds = favoriteIds,
            isLoading = isLoading,
            processingFavoriteIds = processingFavoriteIds,
            onSearchClick = navController::navigateUp,
            onProductClick = { navController.navigate("product/$it") },
            onFavoriteClick = viewModel::toggleFavorite
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
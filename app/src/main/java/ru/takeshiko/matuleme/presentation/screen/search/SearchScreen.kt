package ru.takeshiko.matuleme.presentation.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog
import ru.takeshiko.matuleme.presentation.components.search.SearchContent

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val processingItems by viewModel.processingItems.collectAsStateWithLifecycle()
    val searchSubmitted by viewModel.searchSubmitted.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadSearchHistory().also {
            delay(1000)
            initialLoadDone = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone) {
                    viewModel.loadSearchHistory()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(searchSubmitted) {
        if (searchSubmitted.isNotEmpty()) {
            navController.navigate("search_results/${searchSubmitted}")
            viewModel.resetSearchSubmit()
            viewModel.clearSearchQuery()
            viewModel.loadSearchHistory()
        }
    }

    LaunchedEffect(errorMessage) {
        showNotificationCard = !errorMessage.isNullOrBlank()
    }

    Box(Modifier.fillMaxSize()) {
        SearchContent(
            searchQuery = searchQuery,
            isLoading = isLoading,
            searchHistory = searchHistory,
            processingItems = processingItems,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onClearHistoryItem = viewModel::onClearHistoryItem,
            onSearchSubmit = viewModel::onSearchSubmit,
            onNavigateToBack = navController::navigateUp
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

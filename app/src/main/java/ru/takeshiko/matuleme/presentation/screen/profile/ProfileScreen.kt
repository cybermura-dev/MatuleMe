package ru.takeshiko.matuleme.presentation.screen.profile

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog
import ru.takeshiko.matuleme.presentation.components.profile.ProfileContent
import ru.takeshiko.matuleme.presentation.screen.login.LoginActivity

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var showNotificationCard by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    val lifecycleOwner = LocalLifecycleOwner.current

    val user by viewModel.user.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLogoutInProgress by viewModel.isLogoutInProgress.collectAsState()
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile().also {
            delay(1000)
            initialLoadDone = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (initialLoadDone) {
                    viewModel.loadUserProfile()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            activity?.let { act ->
                act.startActivity(Intent(act, LoginActivity::class.java))
                act.finish()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ProfileContent(
            user = user,
            orders = orders,
            addresses = addresses,
            favorites = favorites,
            payments = payments,
            isLogoutInProgress = isLogoutInProgress,
            isLoading = isLoading,
            onUserClick = { navController.navigate("userinfo") },
            onOrdersClick = { navController.navigate("orders") },
            onAddressesClick = { navController.navigate("addresses") },
            onFavoritesClick = { navController.navigate("wishlist") },
            onPaymentsClick = { navController.navigate("payments") },
            onAboutAppClick = { navController.navigate("about_app") },
            onLogout = viewModel::logoutUser
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
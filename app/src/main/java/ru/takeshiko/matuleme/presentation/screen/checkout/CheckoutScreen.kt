package ru.takeshiko.matuleme.presentation.screen.checkout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
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
import ru.takeshiko.matuleme.presentation.components.checkout.CheckoutContent
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    var showErrorCard by remember { mutableStateOf(false) }
    var showSuccessCard by remember { mutableStateOf(false) }

    val user by viewModel.user.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val orderCreated by viewModel.orderCreated.collectAsState()
    val orderCreateInProgress by viewModel.orderCreateInProgress.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCheckoutData()
    }

    LaunchedEffect(errorMessage) {
        showErrorCard = !errorMessage.isNullOrBlank()
    }

    LaunchedEffect(orderCreated) {
        if (orderCreated) {
            showSuccessCard = true
        }
    }

    Box(Modifier.fillMaxSize()) {
        CheckoutContent(
            state = state,
            user = user,
            onAddressSelected = viewModel::selectAddress,
            onPaymentSelected = viewModel::selectPayment,
            onPlaceOrder = viewModel::createUserOrder,
            orderInProgress = orderCreateInProgress,
            modifier = Modifier.fillMaxSize(),
            onNavigateToBack = navController::navigateUp,
            onAddAddress = { navController.navigate("address_edit/new") },
            onAddPaymentMethod = { navController.navigate("payment_edit/new") }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (showErrorCard) {
            EventDialog(
                icon = Icons.Default.Close,
                title = stringResource(R.string.unknown_error_occurred),
                message = errorMessage.orEmpty(),
                onDismiss = {
                    showErrorCard = false
                    viewModel.resetErrorMessage()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

        if (showSuccessCard) {
            EventDialog(
                icon = Icons.Default.CheckCircle,
                title = stringResource(R.string.order_placed_successfully),
                message = stringResource(R.string.order_confirmation_message),
                onDismiss = {
                    showSuccessCard = false
                    viewModel.resetOrderCreated()
                    navController.navigate("orders") {
                        popUpTo("checkout") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}
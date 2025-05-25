package ru.takeshiko.matuleme.presentation.screen.updatepaymentmethod

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
import ru.takeshiko.matuleme.presentation.components.paymentmethods.UpdatePaymentMethodContent

@Composable
fun UpdatePaymentMethodScreen(
    navController: NavController,
    viewModel: UpdatePaymentMethodViewModel = koinViewModel(),
    paymentId: String
) {
    var showNotificationCard by remember { mutableStateOf(false) }

    val payment by viewModel.payment.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saved by viewModel.saved.collectAsState()

    LaunchedEffect(paymentId) {
        viewModel.fetchPayment(paymentId)
    }

    LaunchedEffect(saved) {
        if (saved) {
            navController.navigateUp()
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showNotificationCard = true
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (payment != null) {
            UpdatePaymentMethodContent(
                payment = payment!!,
                isLoading = isLoading,
                onSavePayment = viewModel::updatePaymentMethod,
                onNavigateToBack = navController::navigateUp
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
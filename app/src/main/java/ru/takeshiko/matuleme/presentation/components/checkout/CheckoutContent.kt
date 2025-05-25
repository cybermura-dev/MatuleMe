package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.presentation.screen.checkout.CheckoutState

@Composable
fun CheckoutContent(
    state: CheckoutState,
    user: UserDto?,
    onAddressSelected: (UserDeliveryAddressDto) -> Unit,
    onPaymentSelected: (UserPaymentDto) -> Unit,
    onPlaceOrder: () -> Unit,
    orderInProgress: Boolean,
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit,
    onAddAddress: () -> Unit,
    onAddPaymentMethod: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CheckoutTopBar(
                onNavigateToBack = onNavigateToBack
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                state = state,
                onPlaceOrder = onPlaceOrder,
                orderInProgress = orderInProgress
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (user != null) {
                UserInfoSection(user = user)
            }

            Spacer(modifier = Modifier.height(16.dp))

            DeliveryAddressSection(
                addresses = state.addresses,
                selectedAddress = state.selectedAddress,
                onAddressSelected = onAddressSelected,
                onAddAddress = onAddAddress
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodSection(
                payments = state.payments,
                selectedPayment = state.selectedPayment,
                onPaymentSelected = onPaymentSelected,
                onAddPaymentMethod = onAddPaymentMethod
            )

            Spacer(modifier = Modifier.height(16.dp))

            CartItemsSection(state = state)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
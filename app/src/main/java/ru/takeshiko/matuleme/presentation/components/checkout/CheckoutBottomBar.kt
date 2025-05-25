package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.screen.checkout.CheckoutState
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun CheckoutBottomBar(
    state: CheckoutState,
    onPlaceOrder: () -> Unit,
    orderInProgress: Boolean
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val originalSubtotal = state.subtotal + state.discount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                spotColor = appColors.primaryColor.copy(alpha = 0.2f)
            )
            .background(
                color = appColors.surfaceColor,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(16.dp)
    ) {
        SummaryRow(
            label = stringResource(R.string.subtotal_original),
            amount = originalSubtotal
        )

        if (state.discount > 0) {
            SummaryRow(
                label = stringResource(R.string.discount),
                amount = -state.discount,
                isDiscount = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.total_amount_label),
                style = typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = appColors.textPrimary
                )
            )

            Text(
                text = String.format(Locale.ROOT, "%.0f ₽", state.total),
                style = typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = appColors.primaryColor
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onPlaceOrder,
            enabled = state.cartItems.isNotEmpty() &&
                    state.selectedAddress != null &&
                    state.selectedPayment != null &&
                    !orderInProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = appColors.primaryColor,
                contentColor = appColors.surfaceColor,
                disabledContainerColor = appColors.primaryColor.copy(alpha = 0.5f),
                disabledContentColor = appColors.surfaceColor.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (orderInProgress)
                    stringResource(R.string.processing_order)
                else
                    stringResource(R.string.place_order),
                style = typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
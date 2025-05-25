package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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

@Composable
fun CartItemsSection(state: CheckoutState) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = appColors.primaryColor.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = appColors.primaryColor
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = stringResource(R.string.your_cart),
                    style = typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = appColors.textPrimary
                    )
                )
            }

            HorizontalDivider(color = appColors.primaryColor.copy(alpha = 0.1f), thickness = 1.dp)

            if (state.cartItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.cart_is_empty),
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = typography.bodyMedium.copy(
                        color = appColors.errorColor
                    ),
                    fontWeight = FontWeight.Medium
                )
            } else {
                Column {
                    state.cartItems.forEach { item ->
                        CartItemRow(item)
                    }
                }
            }
        }
    }
}
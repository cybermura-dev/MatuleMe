package ru.takeshiko.matuleme.presentation.components.deliveryaddresses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun DeliveryAddressesTopBar(
    addresses: List<UserDeliveryAddressDto>,
    onNavigateToBack: () -> Unit,
    isLoading: Boolean
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = onNavigateToBack,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back),
                tint = appColors.textPrimary
            )
        }

        Text(
            text = stringResource(R.string.delivery_addresses),
            style = typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = appColors.textPrimary
        )

        if (!isLoading && addresses.isNotEmpty()) {
            Text(
                text = "(${addresses.size})",
                style = typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = appColors.primaryColor
                ),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun DeliveryAddressSection(
    addresses: List<UserDeliveryAddressDto>,
    selectedAddress: UserDeliveryAddressDto?,
    onAddressSelected: (UserDeliveryAddressDto) -> Unit,
    onAddAddress: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    var showAddressSelectionList by remember(selectedAddress) {
        mutableStateOf(selectedAddress == null)
    }

    LaunchedEffect(addresses.isEmpty()) {
        if (addresses.isEmpty()) {
            showAddressSelectionList = true
        }
    }

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
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = appColors.primaryColor
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.delivery_address),
                    style = typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = appColors.textPrimary
                    )
                )
            }

            HorizontalDivider(color = appColors.primaryColor.copy(alpha = 0.1f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            if (addresses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_saved_addresses),
                        style = typography.bodyMedium.copy(
                            color = appColors.textSecondary
                        ),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onAddAddress,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = appColors.primaryColor
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(appColors.primaryColor.copy(alpha = 0.5f))
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = stringResource(R.string.add_address),
                            style = typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            } else {
                if (showAddressSelectionList) {
                    Text(
                        text = stringResource(R.string.select_an_address),
                        style = typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = appColors.textPrimary
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        addresses.forEach { address ->
                            AddressItem(
                                address = address,
                                isSelected = address.id == selectedAddress?.id,
                                onSelect = {
                                    onAddressSelected(address)
                                    showAddressSelectionList = false
                                }
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onAddAddress,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.primaryColor),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(appColors.primaryColor.copy(alpha = 0.5f))
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))

                        Spacer(modifier = Modifier.size(4.dp))

                        Text(
                            text = stringResource(R.string.add_another_address),
                            style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                } else if (selectedAddress != null) {
                    SelectedAddressDisplay(
                        address = selectedAddress,
                        onChangeAddress = { showAddressSelectionList = true },
                        onAddNewAddress = onAddAddress
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedAddressDisplay(
    address: UserDeliveryAddressDto,
    onChangeAddress: () -> Unit,
    onAddNewAddress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = address.address,
            style = typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = appColors.textPrimary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            OutlinedButton(
                onClick = onChangeAddress,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.primaryColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(appColors.primaryColor.copy(alpha = 0.5f))
                )
            ) {
                Text(
                    text = stringResource(R.string.change),
                    style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
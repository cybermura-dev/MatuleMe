package ru.takeshiko.matuleme.presentation.components.deliveryaddresses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun DeliveryAddressesContent(
    addresses: List<UserDeliveryAddressDto>,
    isLoading: Boolean,
    onAddAddress: () -> Unit,
    onEditAddress: (UserDeliveryAddressDto) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onSetDefaultAddress: (UserDeliveryAddressDto) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val brush = createShimmerBrush()
    val typography = AppTypography
    val appColors = rememberAppColors()

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(addresses, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            DeliveryAddressesTopBar(
                addresses = addresses,
                onNavigateToBack = onNavigateToBack,
                isLoading = isLoading
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAddress,
                containerColor = appColors.primaryColor
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && addresses.isEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(3) {
                        ShimmerAddressItem(brush = brush)
                    }
                }
                return@Box
            }

            AnimatedVisibility(
                visible = !isLoading && addresses.isEmpty(),
                enter = fadeIn(tween(300)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ),
                exit = fadeOut(tween(150))
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.empty_addresses_title),
                            style = typography.titleLarge,
                            color = appColors.textPrimary
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.empty_addresses_message),
                            style = typography.bodyLarge,
                            color = appColors.textSecondary
                        )
                    }
                }
            }

            if (!isLoading && addresses.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    itemsIndexed(addresses) { index, address ->
                        AnimatedVisibility(
                            visible = animateItems,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 50 * index,
                                    easing = FastOutSlowInEasing
                                )
                            ) + slideInVertically(
                                initialOffsetY = { it / 5 },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = 50 * index,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            exit = fadeOut(tween(150))
                        ) {
                            AddressItem(
                                address = address,
                                onEditAddress = { onEditAddress(address) },
                                onDeleteAddress = { onDeleteAddress(address.id ?: "") },
                                onSetDefaultAddress = { onSetDefaultAddress(address) }
                            )
                        }
                    }
                }
            }
        }
    }
}
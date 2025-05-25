package ru.takeshiko.matuleme.presentation.components.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserFavoriteDto
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ProfileContent(
    user: UserDto?,
    orders: List<UserOrderDto>,
    addresses: List<UserDeliveryAddressDto>,
    favorites: List<UserFavoriteDto>,
    payments: List<UserPaymentDto>,
    isLoading: Boolean,
    isLogoutInProgress: Boolean,
    onUserClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPaymentsClick: () -> Unit,
    onAboutAppClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(orders, addresses, favorites, payments, isLoading) {
        animateItems = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ProfileTopBar(
                user = user,
                isLoading = isLoading,
                onClick = onUserClick
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NextDeliveryCard(
                    orders = orders,
                    isLoading = isLoading,
                    onClick = onOrdersClick
                )
            }

            item {
                ProfileFavoritesCard(
                    favorites = favorites,
                    isLoading = isLoading,
                    onClick = onFavoritesClick
                )
            }

            item {
                ProfileAddressesCard(
                    addresses = addresses,
                    isLoading = isLoading,
                    onClick = onAddressesClick
                )
            }

            item {
                ProfilePaymentsCard(
                    payments = payments,
                    isLoading = isLoading,
                    onClick = onPaymentsClick
                )
            }

            item {
                ProfileListItem(
                    titleRes = R.string.profile_about_app,
                    icon = Icons.Default.Info,
                    onClick = onAboutAppClick
                )
            }

            item {
                AnimatedVisibility(
                    visible = animateItems,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = onLogout,
                        enabled = !isLogoutInProgress,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.errorColor)
                    ) {
                        if (isLogoutInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        }

                        Text(
                            text = stringResource(R.string.profile_logout),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

package ru.takeshiko.matuleme.presentation.components.deliveryaddresses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.presentation.components.fields.CustomOutlinedTextField
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun UpdateAddressContent(
    address: UserDeliveryAddressDto,
    isLoading: Boolean,
    onSaveAddress: (UserDeliveryAddressDto) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val density = LocalDensity.current

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        animateItems = !isLoading
    }

    var addressLine by remember { mutableStateOf(address.address) }
    var isDefault by remember { mutableStateOf(address.isDefault == true) }

    var animateFields by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        animateFields = !isLoading
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            UpdateAddressTopBar(onNavigateToBack)
        },
        bottomBar = {
            AnimatedVisibility(
                visible = animateItems,
                enter = fadeIn(tween(700)) + slideInVertically(
                    initialOffsetY = { with(density) { 100.dp.roundToPx() } },
                    animationSpec = tween(700)
                ),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = appColors.primaryColor.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = appColors.surfaceColor.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                onSaveAddress(
                                    address.copy(
                                        address = addressLine,
                                        isDefault = isDefault
                                    )
                                )
                            },
                            enabled = addressLine.isNotBlank() && !isLoading,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.secondaryColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 0.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                style = typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = appColors.primaryColor)
                }
            }

            AnimatedVisibility(
                visible = animateFields,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(150))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CustomOutlinedTextField(
                            value = addressLine,
                            onValueChange = { addressLine = it },
                            textFieldSize = 64.dp,
                            label = stringResource(R.string.address_line),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = addressLine.isBlank()
                        )

                        if (addressLine.isBlank()) {
                            Text(
                                text = stringResource(R.string.error_address_required),
                                color = MaterialTheme.colorScheme.error,
                                style = typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it },
                            enabled = !isLoading
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.make_default_address),
                            style = typography.bodyLarge,
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
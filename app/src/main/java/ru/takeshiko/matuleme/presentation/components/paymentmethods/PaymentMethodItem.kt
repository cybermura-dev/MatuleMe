package ru.takeshiko.matuleme.presentation.components.paymentmethods

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun PaymentMethodItem(
    payment: UserPaymentDto,
    onEditPayment: () -> Unit,
    onDeletePayment: () -> Unit,
    onSetDefaultPayment: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    val radioColor by animateColorAsState(
        targetValue = if (payment.isDefault) appColors.primaryColor else appColors.textSecondary,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = appColors.primaryColor.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(appColors.primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_payment),
                        contentDescription = null,
                        tint = appColors.primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "•••• ${payment.cardNumber.takeLast(4)}",
                        style = typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp,
                            lineHeight = 24.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = payment.cardHolderName,
                        style = typography.bodyMedium,
                        color = appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = stringResource(R.string.expiration_date_format, payment.expirationDate),
                        style = typography.bodySmall,
                        color = appColors.textSecondary.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                RadioButton(
                    selected = payment.isDefault,
                    onClick = onSetDefaultPayment,
                    enabled = !payment.isDefault,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = appColors.primaryColor,
                        unselectedColor = appColors.textSecondary.copy(alpha = 0.6f)
                    )
                )
            }

            HorizontalDivider(
                color = appColors.primaryColor.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = !payment.isDefault) {
                        if (!payment.isDefault) onSetDefaultPayment()
                    }
                ) {
                    val statusText = if (payment.isDefault)
                        stringResource(R.string.default_payment)
                    else
                        stringResource(R.string.set_as_default)

                    Text(
                        text = statusText,
                        style = typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = radioColor
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onEditPayment,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(appColors.primaryColor.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = appColors.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeletePayment,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(appColors.errorColor.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = appColors.errorColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
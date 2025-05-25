package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun PaymentItem(
    payment: UserPaymentDto,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = appColors.textPrimary.copy(alpha = 0.05f),
                spotColor = appColors.textPrimary.copy(alpha = 0.05f)
            )
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = appColors.surfaceColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = if (isSelected) appColors.primaryColor
                    else appColors.surfaceColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    if (isSelected) appColors.primaryColor.copy(alpha = 0.08f)
                    else appColors.surfaceColor
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = appColors.primaryColor,
                    unselectedColor = appColors.textSecondary
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            Column {
                Text(
                    text = payment.cardHolderName,
                    style = typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.textPrimary
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (payment.cardNumber.length > 4) "•••• " + payment.cardNumber.takeLast(4) else payment.cardNumber,
                    style = typography.bodySmall.copy(
                        color = appColors.textSecondary
                    )
                )
            }
        }
    }
}
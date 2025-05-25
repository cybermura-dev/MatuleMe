package ru.takeshiko.matuleme.presentation.components.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun SummaryRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = appColors.textSecondary
            )
        )
        Text(
            text = String.format(Locale.ROOT, "%s%.0f ₽", if (amount > 0) "" else "-", kotlin.math.abs(amount)), // Formatted price
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isDiscount -> appColors.errorColor
                    amount < 0 -> appColors.errorColor
                    else -> appColors.textPrimary
                }
            )
        )
    }
}
package ru.takeshiko.matuleme.presentation.components.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun QuantityControls(
    quantity: Int,
    productId: String?,
    isProcessing: Boolean,
    onDecreaseQuantity: (String) -> Unit,
    onIncreaseQuantity: (String) -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        IconButton(
            onClick = { productId?.let(onDecreaseQuantity) },
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    spotColor = appColors.textSecondary.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(
                    color = if (quantity > 1)
                        appColors.surfaceColor
                    else
                        appColors.errorColor.copy(alpha = 0.1f)
                ),
            enabled = !isProcessing && quantity > 1
        ) {
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = appColors.primaryColor, strokeWidth = 2.dp)
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_minus),
                    contentDescription = null,
                    tint = if (quantity > 1)
                        appColors.textPrimary
                    else
                        appColors.errorColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, appColors.primaryColor.copy(alpha = 0.2f)),
            color = appColors.backgroundColor.copy(alpha = 0.7f),
            modifier = Modifier.shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = appColors.primaryColor.copy(alpha = 0.1f)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(width = 40.dp, height = 32.dp)
            ) {
                AnimatedContent(
                    targetState = quantity,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    },
                    label = "quantityAnimation"
                ) { targetCount ->
                    Text(
                        text = "$targetCount",
                        style = typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = appColors.primaryColor
                    )
                }
            }
        }

        IconButton(
            onClick = { productId?.let(onIncreaseQuantity) },
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    spotColor = appColors.primaryColor.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(appColors.primaryColor.copy(alpha = 0.1f)),
            enabled = !isProcessing && quantity < 100
        ) {
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = appColors.primaryColor, strokeWidth = 2.dp)
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    tint = appColors.primaryColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
package ru.takeshiko.matuleme.presentation.components.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ProfileAddressesCard(
    addresses: List<UserDeliveryAddressDto>,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val animate = !isLoading
    val brush = createShimmerBrush()

    val defaultAddress = addresses.find { it.isDefault }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_marker),
                contentDescription = stringResource(R.string.profile_address_title),
                tint = appColors.primaryColor,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_address_title),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AnimatedVisibility(
                    visible = animate,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
                    exit = fadeOut()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isLoading) {
                            Box(
                                Modifier
                                    .height(14.dp)
                                    .fillMaxWidth(0.4f)
                                    .background(brush, RoundedCornerShape(4.dp))
                            )
                        } else {
                            val count = addresses.size

                            val countText = if (count > 0) {
                                pluralStringResource(R.plurals.profile_address_count, count, count)
                            } else {
                                stringResource(R.string.profile_no_addresses)
                            }

                            Text(
                                text = countText,
                                style = typography.bodySmall,
                                color = appColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (defaultAddress != null) {
                                Text(
                                    text = stringResource(R.string.profile_address_primary, defaultAddress.address),
                                    style = typography.bodySmall,
                                    color = appColors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = appColors.primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
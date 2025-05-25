package ru.takeshiko.matuleme.presentation.components.reviews

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ReviewFilterRow(
    currentFilter: Int?,
    onFilterChanged: (Int?) -> Unit
) {
    val colors = rememberAppColors()
    val typography = AppTypography
    val options = listOf(null, 5, 4, 3, 2, 1)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(options) { opt ->
            val selected = opt == currentFilter

            val containerColor by animateColorAsState(
                targetValue = if (selected) colors.primaryColor else colors.surfaceColor,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "containerColor"
            )

            val textColor by animateColorAsState(
                targetValue = if (selected) Color.White else colors.textPrimary,
                animationSpec = tween(durationMillis = 300)
            )

            val iconTint by animateColorAsState(
                targetValue = if (selected) Color.White else colors.ratingColor,
                animationSpec = tween(durationMillis = 300)
            )

            ElevatedFilterChip(
                selected = selected,
                onClick = { onFilterChanged(if (selected) null else opt) },
                label = {
                    Text(
                        text = opt?.toString() ?: stringResource(R.string.all),
                        style = typography.bodyMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            letterSpacing = 0.sp
                        ),
                        color = textColor
                    )
                },
                leadingIcon = {
                    if (opt != null) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                border = if (selected) null else BorderStroke(1.dp, colors.primaryColor.copy(alpha = 0.3f)),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = containerColor,
                    containerColor = colors.surfaceColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = FilterChipDefaults.elevatedFilterChipElevation(
                    elevation = if (selected) 4.dp else 2.dp,
                    pressedElevation = 6.dp
                ),
                modifier = Modifier
                    .height(36.dp)
                    .shadow(
                        elevation = if (selected) 4.dp else 1.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = colors.primaryColor.copy(alpha = if (selected) 0.2f else 0.1f)
                    )
            )
        }
    }
}
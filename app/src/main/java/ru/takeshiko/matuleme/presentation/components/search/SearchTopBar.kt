package ru.takeshiko.matuleme.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun SearchTopBar(
    searchText: String? = null,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSearchClick
            )
            .border(
                width = 1.dp,
                color = appColors.primaryColor,
                shape = RoundedCornerShape(percent = 24)
            )
            .background(
                color = appColors.primaryLightColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(percent = 24)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            tint = appColors.primaryColor
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = searchText ?: stringResource(R.string.search),
            color = appColors.primaryColor,
            style = typography.bodyLarge
        )
    }
}
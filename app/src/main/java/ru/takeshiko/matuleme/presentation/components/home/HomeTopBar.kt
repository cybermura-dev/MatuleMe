package ru.takeshiko.matuleme.presentation.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.search.SearchTopBar
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun HomeTopBar(
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    val appColors = rememberAppColors()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchTopBar(
            onSearchClick = onSearchClick,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(40.dp)
                .background(
                    color = appColors.primaryLightColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_notifications),
                contentDescription = null,
                tint = appColors.primaryColor
            )
        }
    }
}
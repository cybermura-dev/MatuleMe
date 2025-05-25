package ru.takeshiko.matuleme.presentation.components.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    val appColors = rememberAppColors()

    val items = listOf(
        BottomNavigationItem(route = "home", iconResId = R.drawable.ic_home),
        BottomNavigationItem(route = "categories", iconResId = R.drawable.ic_category),
        BottomNavigationItem(route = "cart", iconResId = R.drawable.ic_cart),
        BottomNavigationItem(route = "profile", iconResId = R.drawable.ic_person)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shadowElevation = 8.dp,
        color = appColors.primaryColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val iconSize by animateDpAsState(
                    targetValue = if (selected) 36.dp else 32.dp,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onItemSelected(item.route)
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(item.iconResId),
                        contentDescription = null,
                        tint = if (selected) Color.White else appColors.surfaceColor.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(iconSize)
                            .graphicsLayer {
                                if (selected) {
                                    rotationY = 360f
                                }
                            }
                    )
                }
            }
        }
    }
}
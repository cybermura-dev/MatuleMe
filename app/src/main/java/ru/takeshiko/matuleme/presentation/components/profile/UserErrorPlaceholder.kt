package ru.takeshiko.matuleme.presentation.components.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun UserErrorPlaceholder(modifier: Modifier = Modifier) {
    val appColors = rememberAppColors()
    val infiniteTransition = rememberInfiniteTransition()

    val angle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = remember(angle.value) {
        Brush.linearGradient(
            colors = listOf(
                appColors.backgroundColor,
                appColors.surfaceColor.copy(alpha = 0.7f),
                appColors.backgroundColor.copy(alpha = 0.9f),
            ),
            start = Offset(0f, 0f),
            end = Offset(
                cos(angle.value * (PI / 180)).toFloat() * 1000,
                sin(angle.value * (PI / 180)).toFloat() * 1000
            )
        )
    }

    val pulseAnimation = infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_image_not_found),
                contentDescription = null,
                tint = appColors.primaryColor,
                modifier = Modifier
                    .size(48.dp * pulseAnimation.value)
                    .padding(8.dp)
            )
        }
    }
}
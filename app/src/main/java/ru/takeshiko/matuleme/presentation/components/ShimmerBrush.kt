package ru.takeshiko.matuleme.presentation.components // Или ваш выбранный пакет

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun createShimmerBrush(
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.9f),
        Color.LightGray.copy(alpha = 0.4f)
    ),
    durationMillis: Int = 1200,
    targetValue: Float = 1000f
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslateAnimation"
    )

    val brushStartOffset = Offset(
        translateAnimation - (targetValue / 3),
        translateAnimation - (targetValue / 3)
    )
    val brushEndOffset = Offset(translateAnimation, translateAnimation)

    return Brush.linearGradient(
        colors = shimmerColors,
        start = brushStartOffset,
        end = brushEndOffset
    )
}
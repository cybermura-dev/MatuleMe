package ru.takeshiko.matuleme.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.random.Random
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun SplashBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val appColors = rememberAppColors()

    val particles = remember {
        List(particleCount) {
            SplashParticle(
                initialX = Random.nextFloat() * 1000,
                initialY = Random.nextFloat() * 2000,
                radius = Random.nextFloat() * 12f + 3f,
                alpha = Random.nextFloat() * 0.5f + 0.1f,
                speedX = Random.nextFloat() * 2 - 1,
                speedY = -Random.nextFloat() * 2 - 1
            )
        }
    }

    val backgroundTransition = rememberInfiniteTransition()
    val pulseTransition = rememberInfiniteTransition()

    val pulseFactor by pulseTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientProgress by backgroundTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedGradientColors = listOf(
        lerp(Color.White, appColors.primaryLightColor, gradientProgress),
        lerp(Color.White, appColors.primaryColor, gradientProgress),
        lerp(Color.White, appColors.secondaryColor, gradientProgress),
        lerp(Color.White, appColors.secondaryDarkColor, gradientProgress)
    )

    particles.forEachIndexed { index, particle ->
        val particleAnimation = rememberInfiniteTransition()

        val xOffset by particleAnimation.animateFloat(
            initialValue = particle.initialX,
            targetValue = particle.initialX + particle.speedX * 500,
            animationSpec = infiniteRepeatable(
                animation = tween(5000 + index * 100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val yOffset by particleAnimation.animateFloat(
            initialValue = particle.initialY,
            targetValue = particle.initialY + particle.speedY * 500,
            animationSpec = infiniteRepeatable(
                animation = tween(3000 + index * 100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        particle.currentX = xOffset
        particle.currentY = yOffset
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = animatedGradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                ),
                size = size
            )
        }

        particles.forEach { particle ->
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(particle.alpha)
            ) {
                drawCircle(
                    color = lerp(
                        appColors.primaryLightColor,
                        appColors.secondaryColor,
                        Random.nextFloat()
                    ),
                    radius = particle.radius * pulseFactor,
                    center = Offset(particle.currentX, particle.currentY),
                    alpha = particle.alpha
                )
            }
        }
        content()
    }
}

data class SplashParticle(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var currentX: Float = initialX,
    var currentY: Float = initialY
)
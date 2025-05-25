package ru.takeshiko.matuleme.presentation.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SecondaryParticle(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val alpha: Float,
    val speedX: Float,
    val speedY: Float,
    val colorRatio: Float = Random.nextFloat(),
    val shapeType: SecondaryParticleShapeType = SecondaryParticleShapeType.entries.toTypedArray().random()
)

enum class SecondaryParticleShapeType {
    CIRCLE, STAR, POLYGON
}

@Composable
fun SecondaryBackground(
    modifier: Modifier = Modifier,
    paddingValues: Dp = 0.dp,
    particleCount: Int = 40,
    content: @Composable BoxScope.() -> Unit
) {
    val appColors = rememberAppColors()

    val backgroundColor = Color.White

    val waveColors = listOf(
        appColors.primaryLightColor.copy(alpha = 0.15f),
        appColors.primaryColor.copy(alpha = 0.15f),
        appColors.primaryDarkColor.copy(alpha = 0.15f)
    )

    val particles = remember {
        List(particleCount) {
            SecondaryParticle(
                initialX = Random.nextFloat() * 1200f,
                initialY = Random.nextFloat() * 2200f,
                radius = Random.nextFloat() * 12f + 3f,
                alpha = Random.nextFloat() * 0.5f + 0.1f,
                speedX = Random.nextFloat() * 2f - 1f,
                speedY = -Random.nextFloat() * 1.5f - 0.2f
            )
        }
    }

    val transition = rememberInfiniteTransition()

    val pulseState by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse)
    )

    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing))
    )

    val offsets = particles.mapIndexed { index, p ->
        val xAnim = transition.animateFloat(
            initialValue = p.initialX,
            targetValue = p.initialX + p.speedX * 1000f,
            animationSpec = infiniteRepeatable(
                tween(6000 + (p.radius * 200).toInt(), easing = LinearEasing),
                RepeatMode.Reverse
            )
        )

        val yAnim = transition.animateFloat(
            initialValue = p.initialY,
            targetValue = p.initialY + p.speedY * 1000f,
            animationSpec = infiniteRepeatable(
                tween(7000 + (p.radius * 150).toInt(), easing = LinearEasing),
                RepeatMode.Reverse
            )
        )

        val wobble by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(3000 + index * 50, easing = LinearEasing), RepeatMode.Reverse)
        )

        Triple(xAnim, yAnim, wobble)
    }

    Box(
        modifier
            .background(backgroundColor)
            .padding(paddingValues)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val waveWidth = size.width
            val baseHeight = size.height * 0.7f
            val heights = listOf(0.05f, 0.08f, 0.03f)
            val colors = waveColors

            heights.forEachIndexed { layerIndex, heightRatio ->
                val path = Path().apply {
                    val amplitude = size.height * heightRatio
                    moveTo(0f, baseHeight)
                    for (i in 0..waveWidth.toInt() step 10) {
                        val x = i.toFloat()
                        val y =
                            baseHeight + sin(x * 0.015f * (layerIndex + 1) + wavePhase * (layerIndex + 1)) * amplitude
                        lineTo(x, y)
                    }
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path,
                    Brush.horizontalGradient(
                        colors = listOf(
                            colors[layerIndex],
                            colors[(layerIndex + 1) % colors.size]
                        )
                    )
                )
            }

            particles.forEachIndexed { i, p ->
                val (xAnim, yAnim, wobble) = offsets[i]
                val x = xAnim.value + sin(wavePhase + i * 0.2f) * p.radius * wobble * 2
                val y = yAnim.value + cos(wavePhase + i * 0.2f) * p.radius * wobble
                val size = p.radius * pulseState
                val color = lerp(appColors.primaryColor, appColors.secondaryColor, p.colorRatio)
                when (p.shapeType) {
                    SecondaryParticleShapeType.CIRCLE -> {
                        drawCircle(color, size, Offset(x, y), alpha = p.alpha * (0.7f + wobble * 0.3f))
                        if (p.radius > 8f) drawCircle(color.copy(alpha = 0.3f), size * 1.8f, Offset(x, y), alpha = p.alpha * 0.2f)
                    }
                    SecondaryParticleShapeType.STAR -> {
                        val starPath = Path().apply {
                            val outer = size
                            val inner = size * 0.4f
                            repeat(10) { idx ->
                                val angle = Math.PI * idx / 5
                                val r = if (idx % 2 == 0) outer else inner
                                val px = x + cos(angle).toFloat() * r
                                val py = y + sin(angle).toFloat() * r
                                if (idx == 0) moveTo(px, py) else lineTo(px, py)
                            }
                            close()
                        }
                        drawPath(starPath, color, alpha = p.alpha * (0.6f + wobble * 0.4f))
                    }
                    SecondaryParticleShapeType.POLYGON -> {
                        val poly = Path().apply {
                            val sides = (3..6).random()
                            for (j in 0 until sides) {
                                val angle = 2 * Math.PI * j / sides
                                val px = x + cos(angle).toFloat() * size
                                val py = y + sin(angle).toFloat() * size
                                if (j == 0) moveTo(px, py) else lineTo(px, py)
                            }
                            close()
                        }
                        drawPath(poly, color, alpha = p.alpha * (0.6f + wobble * 0.4f))
                        if (p.radius > 7f) drawPath(poly, appColors.textPrimary.copy(alpha = 0.3f), style = Stroke(width = 1f, cap = StrokeCap.Round), alpha = p.alpha * 0.3f)
                    }
                }
            }
            if (particles.size > 15) {
                for (i in particles.indices) for (j in i + 1 until particles.size) {
                    val (x1, y1, _) = offsets[i]
                    val (x2, y2, _) = offsets[j]
                    val dx = x2.value - x1.value
                    val dy = y2.value - y1.value
                    val dist = kotlin.math.hypot(dx, dy)
                    if (dist < 200f) drawLine(appColors.primaryLightColor, Offset(x1.value, y1.value), Offset(x2.value, y2.value), alpha = (1 - dist / 200f) * 0.15f)
                }
            }
        }
        content()
    }
}

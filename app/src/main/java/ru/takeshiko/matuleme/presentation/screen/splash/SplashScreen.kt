package ru.takeshiko.matuleme.presentation.screen.splash

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.SplashBackground

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val totalAnimationDuration = 5000
    val logoAnimationDuration = 1500
    val logoAnimationDelay = 500

    var logoAnimationStarted by remember { mutableStateOf(false) }

    val pulseTransition = rememberInfiniteTransition()
    val pulseFactor by pulseTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (logoAnimationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = logoAnimationDuration,
            delayMillis = logoAnimationDelay,
            easing = EaseOutQuart
        )
    )

    val logoScale by animateFloatAsState(
        targetValue = if (logoAnimationStarted) 1f else 0.6f,
        animationSpec = tween(
            durationMillis = logoAnimationDuration,
            delayMillis = logoAnimationDelay,
            easing = EaseOutBack
        )
    )

    LaunchedEffect(true) {
        logoAnimationStarted = true
        delay(totalAnimationDuration.toLong())
        viewModel.retryCheck()
    }

    SplashBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_logo),
                modifier = Modifier
                    .size(200.dp)
                    .scale(logoScale * pulseFactor)
                    .alpha(logoAlpha),
                contentScale = ContentScale.Fit
            )
        }
    }
}
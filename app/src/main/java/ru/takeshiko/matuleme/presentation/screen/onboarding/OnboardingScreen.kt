package ru.takeshiko.matuleme.presentation.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.ButtonType
import ru.takeshiko.matuleme.presentation.components.CustomButton
import ru.takeshiko.matuleme.presentation.components.PrimaryBackground
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
) {
    val appColors = rememberAppColors()

    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    var showFinalAnimation by remember { mutableStateOf(false) }

    PrimaryBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            OnboardingPager(
                pagerState = pagerState,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                    val width = if (pagerState.currentPage == iteration) 25.dp else 10.dp
                    val scale by animateFloatAsState(
                        targetValue = if (pagerState.currentPage == iteration) 1.2f else 1f,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .scale(scale)
                            .height(10.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 30.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = pagerState.currentPage > 0,
                    enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                        initialOffsetX = { -40 }, animationSpec = tween(300)
                    ),
                    exit = fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                        targetOffsetX = { -40 }, animationSpec = tween(200)
                    )
                ) {
                    CustomButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier.width(120.dp),
                        type = ButtonType.SECONDARY,
                        shape = 50.dp
                    ) {
                        Text(
                            text = stringResource(R.string.previous),
                            color = appColors.primaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                val atLast = pagerState.currentPage == pagerState.pageCount - 1
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                        initialOffsetX = { 40 }, animationSpec = tween(300)
                    ),
                    exit = fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                        targetOffsetX = { 40 }, animationSpec = tween(200)
                    )
                ) {
                    if (!atLast) {
                        CustomButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier.width(120.dp),
                            type = ButtonType.SECONDARY,
                            shape = 50.dp
                        ) {
                            Text(
                                text = stringResource(R.string.next),
                                color = appColors.primaryColor
                            )
                        }
                    } else {
                        CustomButton(
                            onClick = {
                                coroutineScope.launch {
                                    showFinalAnimation = true
                                }
                            },
                            modifier = Modifier.width(120.dp),
                            type = ButtonType.SECONDARY,
                            shape = 50.dp
                        ) {
                            Text(
                                text = stringResource(R.string.get_started),
                                color = appColors.primaryColor
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFinalAnimation,
            enter = fadeIn(animationSpec = tween(700))
        ) {
            OnboardingFinalAnimation(viewModel)
        }
    }
}

@Composable
fun OnboardingFinalAnimation(viewModel: OnboardingViewModel) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    LaunchedEffect(Unit) {
        delay(4000)
        viewModel.attemptCompleteOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        appColors.primaryLightColor.copy(alpha = 0.9f),
                        appColors.primaryColor.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logoTransition = rememberInfiniteTransition()

            val logoBounce by logoTransition.animateFloat(
                initialValue = 0f,
                targetValue = -40f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = EaseInOutQuad),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val logoRotation by logoTransition.animateFloat(
                initialValue = -7f,
                targetValue = 7f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutQuad),
                    repeatMode = RepeatMode.Reverse
                ), label = "logoRotation"
            )

            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .offset(y = logoBounce.dp)
                    .graphicsLayer(rotationZ = logoRotation)
            )

            var titleVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(500)
                titleVisible = true
            }

            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(tween(700, easing = EaseOutQuad)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(700, easing = EaseOutQuad)
                        ) +
                        scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(700, easing = EaseOutBack)
                        )
            ) {
                Text(
                    text = stringResource(R.string.lets_begin).uppercase(),
                    style = typography.displayMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(20.dp))

            var subtitleVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(800)
                subtitleVisible = true
            }

            AnimatedVisibility(
                visible = subtitleVisible,
                enter = fadeIn(tween(700, delayMillis = 100, easing = EaseOutQuad)) +
                        slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(700, delayMillis = 100, easing = EaseOutQuad)
                        )
            ) {
                Text(
                    text = stringResource(R.string.your_journey_starts_now),
                    style = typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnboardingPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
            }
        }
    }
}

@Composable
fun OnboardingPage1() {
    val typography = AppTypography

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.welcome_greeting).uppercase(),
                    color = Color.White,
                    style = typography.displayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.welcome_prompt),
                    style = typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.75f)
        ) {
            Image(
                painter = painterResource(R.drawable.img_onboarding_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = -1f)
            )

            Image(
                painter = painterResource(R.drawable.highlight_02),
                contentDescription = null,
                modifier = Modifier
                    .align(BiasAlignment(
                        horizontalBias = (0.2f * 2f) - 1f,
                        verticalBias = (0.2f * 2f) - 1f
                    ))
                    .rotate(-50f)
            )

            Image(
                painter = painterResource(R.drawable.highlight_03),
                contentDescription = null,
                modifier = Modifier
                    .align(BiasAlignment(
                        horizontalBias = (0.75f * 2f) - 1f,
                        verticalBias = (0.4f * 2f) - 1f
                    ))
                    .rotate(65f)
            )
        }
    }
}

@Composable
fun OnboardingPage2() {
    val typography = AppTypography

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(top = 40.dp)
        ) {
            val dpAnimationSpec = remember {
                tween<Dp>(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            }

            val floatAnimationSpec = remember {
                tween<Float>(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            }

            var animateShoe by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                animateShoe = true
            }

            val shoeOffset by animateDpAsState(
                targetValue = if (animateShoe) 0.dp else (-100).dp,
                animationSpec = dpAnimationSpec
            )

            val shoeScale by animateFloatAsState(
                targetValue = if (animateShoe) 1.5f else 0.5f,
                animationSpec = floatAnimationSpec
            )

            val infiniteTransition = rememberInfiniteTransition()
            val rotationAngle by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val hoverOffset by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Image(
                painter = painterResource(R.drawable.img_onboarding_2),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = shoeOffset + hoverOffset.dp)
                    .graphicsLayer(
                        scaleX = shoeScale,
                        scaleY = shoeScale,
                        rotationZ = rotationAngle,
                        rotationX = 10f
                    )
                    .size(300.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = stringResource(R.string.journey_greeting),
                    color = Color.White,
                    style = typography.displayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.journey_prompt),
                    style = typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingPage3() {
    val typography = AppTypography

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(top = 40.dp)
        ) {
            val dpAnimationSpec = remember {
                tween<Dp>(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            }

            val floatAnimationSpec = remember {
                tween<Float>(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            }

            var animateShoe by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                animateShoe = true
            }

            val shoeOffset by animateDpAsState(
                targetValue = if (animateShoe) 0.dp else (-100).dp,
                animationSpec = dpAnimationSpec
            )

            val shoeScale by animateFloatAsState(
                targetValue = if (animateShoe) 1.5f else 0.5f,
                animationSpec = floatAnimationSpec
            )

            val infiniteTransition = rememberInfiniteTransition()
            val rotationAngle by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val hoverOffset by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Image(
                painter = painterResource(R.drawable.img_onboarding_3),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = shoeOffset + hoverOffset.dp)
                    .graphicsLayer(
                        scaleX = shoeScale,
                        scaleY = shoeScale,
                        rotationZ = rotationAngle,
                        rotationX = 10f
                    )
                    .size(300.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = stringResource(R.string.stand_out_greeting),
                    color = Color.White,
                    style = typography.displayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.stand_out_prompt),
                    style = typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}
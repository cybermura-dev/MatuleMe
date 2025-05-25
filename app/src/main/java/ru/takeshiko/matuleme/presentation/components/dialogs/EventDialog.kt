package ru.takeshiko.matuleme.presentation.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun EventDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    duration: Long = 5000L,
    modifier: Modifier,
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    var isVisible by remember { mutableStateOf(false) }

    var offsetY by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = with(LocalDensity.current) { -100.dp.toPx() }

    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LaunchedEffect(isVisible) {
        if (isVisible && duration > 0) {
            delay(duration)
            isVisible = false
        }
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(300)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }, animationSpec = tween(300)) + fadeIn(tween(300)),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }, animationSpec = tween(300)) + fadeOut(tween(300))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (offsetY <= dismissThreshold) {
                                isVisible = false
                            } else {
                                offsetY = 0f
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetY += dragAmount
                    }
                }
        ) {
            EventCardContent(
                icon = icon,
                title = title,
                message = message,
                cardBackgroundColor = appColors.surfaceColor,
                iconBackgroundColor = appColors.primaryColor,
                titleStyle = typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp,
                    color = appColors.textPrimary
                ),
                titleColor = appColors.textPrimary,
                messageStyle = typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    color = appColors.textSecondary
                ),
                messageColor = appColors.textSecondary,
            )
        }
    }
}

@Composable
fun EventCardContent(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    cardCornerRadius: Int = 16,
    cardElevation: Int = 12,
    cardBackgroundColor: Color = rememberAppColors().surfaceColor,
    iconSize: Int = 64,
    iconPadding: Int = 12,
    iconBackgroundColor: Color = rememberAppColors().primaryColor,
    titleStyle: TextStyle = AppTypography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleColor: Color = rememberAppColors().textPrimary,
    messageStyle: TextStyle = AppTypography.bodyMedium.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    messageColor: Color = rememberAppColors().textSecondary,
    messageVerticalMargin: Int = 8,
    horizontalPadding: Int = 16
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = horizontalPadding.dp),
        shape = RoundedCornerShape(cardCornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize.dp)
                    .background(iconBackgroundColor, CircleShape)
                    .padding(iconPadding.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = titleStyle,
                color = titleColor,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = messageVerticalMargin.dp),
                style = messageStyle,
                color = messageColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

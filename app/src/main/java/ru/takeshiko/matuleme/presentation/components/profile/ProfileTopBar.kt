package ru.takeshiko.matuleme.presentation.components.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ProfileTopBar(
    user: UserDto?,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val brush = createShimmerBrush()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = appColors.primaryColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        when {
            isLoading -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
                    exit = fadeOut()
                ) {
                    ShimmerProfileTopBar(brush = brush)
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    appColors.primaryColor,
                                    appColors.primaryLightColor.copy(alpha = 0.7f)
                                )
                            )
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300)) + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ),
                            exit = fadeOut()
                        ) {
                            Surface(
                                shape = CircleShape,
                                shadowElevation = 6.dp,
                                color = Color.Transparent,
                                modifier = Modifier.size(72.dp)
                            ) {
                                UserImage(
                                    imageUrl = user?.avatarUrl,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300)) + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ),
                            exit = fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            ) {
                                val displayName = listOfNotNull(user?.firstName, user?.lastName)
                                    .joinToString(" ")
                                    .ifBlank { stringResource(R.string.profile_placeholder_name) }

                                Text(
                                    text = displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = appColors.surfaceColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = user?.email.orEmpty(),
                                    style = typography.bodyMedium,
                                    color = appColors.surfaceColor.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
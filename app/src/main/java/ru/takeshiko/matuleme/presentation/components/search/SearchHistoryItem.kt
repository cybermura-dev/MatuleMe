package ru.takeshiko.matuleme.presentation.components.search

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.SearchQueryDto
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun SearchHistoryItem(
    historyItem: SearchQueryDto,
    onItemClick: () -> Unit,
    onClearItem: () -> Unit,
    isProcessing: Boolean
) {
    val appColors = rememberAppColors()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(appColors.primaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_history),
                    contentDescription = null,
                    tint = appColors.primaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = historyItem.query,
                    color = appColors.primaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatTimeAgo(historyItem.searchedAt),
                    color = appColors.primaryColor.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = { if (!isProcessing) onClearItem() },
                enabled = !isProcessing,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = appColors.primaryColor
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        tint = appColors.primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun formatTimeAgo(timestamp: Instant): String {
    val now = Clock.System.now()
    val diff = now - timestamp

    val resources = LocalContext.current.resources

    return when {
        diff < 1.minutes -> stringResource(R.string.just_now)
        diff < 60.minutes -> {
            val minutes = diff.inWholeMinutes.toInt()
            resources.getQuantityString(R.plurals.minutes_ago, minutes, minutes)
        }
        diff < 24.hours -> {
            val hours = diff.inWholeHours.toInt()
            resources.getQuantityString(R.plurals.hours_ago, hours, hours)
        }
        diff < 7.days -> {
            val days = diff.inWholeDays.toInt()
            resources.getQuantityString(R.plurals.days_ago, days, days)
        }
        else -> {
            val local = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
            "${local.dayOfMonth}.${local.monthNumber}.${local.year}"
        }
    }
}
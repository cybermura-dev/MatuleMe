package ru.takeshiko.matuleme.presentation.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ShimmerProductItem(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()

    Card(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = appColors.surfaceColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(brush, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .width(80.dp)
                        .height(24.dp)
                        .background(brush, RoundedCornerShape(50))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .width(90.dp)
                            .height(24.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Spacer(
                        modifier = Modifier
                            .width(60.dp)
                            .height(16.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(20.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(20.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(18.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Spacer(
                        modifier = Modifier
                            .width(30.dp)
                            .height(16.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Spacer(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}
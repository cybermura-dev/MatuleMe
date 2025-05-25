package ru.takeshiko.matuleme.presentation.components.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerProfileItem(brush: Brush) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .background(brush)
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.3f)
                        .background(brush)
                )
            }
        }
    }
}
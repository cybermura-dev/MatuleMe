package ru.takeshiko.matuleme.presentation.components.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun ShimmerCategoryItem(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    val appColors = rememberAppColors()

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = appColors.surfaceColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(brush, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .background(brush, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
                    .background(brush, RoundedCornerShape(4.dp))
            )
        }
    }
}
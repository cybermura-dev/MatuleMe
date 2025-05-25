package ru.takeshiko.matuleme.presentation.components.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.takeshiko.matuleme.BuildConfig
import ru.takeshiko.matuleme.domain.models.ProductCategoryDto
import ru.takeshiko.matuleme.presentation.components.product.ProductImage
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import ru.takeshiko.matuleme.presentation.utils.UniqueImageNumberProvider
import kotlin.random.Random


@Composable
fun CategoryItem(
    category: ProductCategoryDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val uniqueNumber = UniqueImageNumberProvider.next()
            val url = "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/shoes/shoe_$uniqueNumber.jpg"
            withContext(Dispatchers.Main) {
                imageUrl = url
            }
        }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    .clip(RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp)
                    )
                    .background(appColors.surfaceColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                imageUrl?.let { url ->
                    ProductImage(
                        imageUrl = url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                } ?: CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = category.name,
                style = typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}
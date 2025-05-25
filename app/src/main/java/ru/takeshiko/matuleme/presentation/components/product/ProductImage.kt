package ru.takeshiko.matuleme.presentation.components.product

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import org.koin.compose.getKoin

@Composable
fun ProductImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imageLoader: ImageLoader = getKoin().get()

    SubcomposeAsyncImage(
        imageLoader = imageLoader,
        model = imageUrl,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            ShimmerProductPlaceholder(Modifier.matchParentSize())
        },
        error = {
            ProductErrorPlaceholder(Modifier.matchParentSize())
        }
    )
}
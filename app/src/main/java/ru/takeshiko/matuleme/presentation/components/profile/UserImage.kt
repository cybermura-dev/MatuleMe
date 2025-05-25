package ru.takeshiko.matuleme.presentation.components.profile

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import org.koin.compose.getKoin

@Composable
fun UserImage(
    imageUrl: String? = null,
    imageUri: Uri? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imageLoader: ImageLoader = getKoin().get()

    SubcomposeAsyncImage(
        imageLoader = imageLoader,
        model = imageUrl ?: imageUri,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            ShimmerUserPlaceholder(Modifier.matchParentSize())
        },
        error = {
            UserErrorPlaceholder(Modifier.matchParentSize())
        }
    )
}

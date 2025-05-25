package ru.takeshiko.matuleme.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun MatuleMeTheme(
    content: @Composable () -> Unit
) {
    val appColors = rememberAppColors()

    val colorScheme = remember(appColors) {
        appColors.toColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
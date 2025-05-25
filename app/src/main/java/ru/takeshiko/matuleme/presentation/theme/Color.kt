package ru.takeshiko.matuleme.presentation.theme

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import ru.takeshiko.matuleme.R

fun Context.getColorFromRes(resId: Int): Color {
    return Color(ContextCompat.getColor(this, resId))
}

object AppColors {
    fun initializeFromContext(context: Context): AppColorsInstance {
        return AppColorsInstance(
            // Primary colors
            primaryColor = context.getColorFromRes(R.color.primary_color),
            primaryLightColor = context.getColorFromRes(R.color.primary_light_color),
            primaryDarkColor = context.getColorFromRes(R.color.primary_dark_color),

            // Secondary colors
            secondaryColor = context.getColorFromRes(R.color.secondary_color),
            secondaryLightColor = context.getColorFromRes(R.color.secondary_light_color),
            secondaryDarkColor = context.getColorFromRes(R.color.secondary_dark_color),

            // Background colors
            backgroundColor = context.getColorFromRes(R.color.background_color),
            surfaceColor = context.getColorFromRes(R.color.surface_color),

            // Text colors
            textPrimary = context.getColorFromRes(R.color.text_primary),
            textSecondary = context.getColorFromRes(R.color.text_secondary),

            // Status colors
            errorColor = context.getColorFromRes(R.color.error_color),
            successColor = context.getColorFromRes(R.color.success_color),
            warningColor = context.getColorFromRes(R.color.warning_color),

            // Product item colors
            ratingColor = context.getColorFromRes(R.color.rating_color),
            promotionBackgroundColor = context.getColorFromRes(R.color.promotion_background_color)
        )
    }
}

data class AppColorsInstance(
    // Primary colors
    val primaryColor: Color,
    val primaryLightColor: Color,
    val primaryDarkColor: Color,

    // Secondary colors
    val secondaryColor: Color,
    val secondaryLightColor: Color,
    val secondaryDarkColor: Color,

    // Background colors
    val backgroundColor: Color,
    val surfaceColor: Color,

    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,

    // Status colors
    val errorColor: Color,
    val successColor: Color,
    val warningColor: Color,

    // Product item colors
    val ratingColor: Color,
    val promotionBackgroundColor: Color
) {
    fun toColorScheme(): ColorScheme {
        return lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryLightColor,
            onPrimaryContainer = primaryDarkColor,

            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = secondaryLightColor,
            onSecondaryContainer = secondaryDarkColor,

            background = backgroundColor,
            onBackground = textPrimary,
            surface = surfaceColor,
            onSurface = textPrimary,

            error = errorColor,
            onError = Color.White
        )
    }
}

@Composable
fun rememberAppColors(): AppColorsInstance {
    val context = LocalContext.current
    return AppColors.initializeFromContext(context)
}
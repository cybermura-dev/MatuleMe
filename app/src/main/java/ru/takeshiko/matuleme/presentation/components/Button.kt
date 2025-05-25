package ru.takeshiko.matuleme.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

enum class ButtonType {
    PRIMARY,
    SECONDARY,
    OUTLINED,
    TEXT
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.PRIMARY,
    enabled: Boolean = true,
    shape: Dp = 12.dp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val appColors = rememberAppColors()
    val buttonShape = RoundedCornerShape(shape)
    val buttonHeightModifier = Modifier.height(56.dp)

    when (type) {
        ButtonType.PRIMARY -> Button(
            onClick = onClick,
            modifier = modifier.then(buttonHeightModifier),
            enabled = enabled,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = appColors.primaryColor,
                contentColor = appColors.backgroundColor,
                disabledContainerColor = appColors.primaryColor.copy(alpha = 0.5f),
                disabledContentColor = appColors.backgroundColor.copy(alpha = 0.5f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = contentPadding,
            content = content
        )

        ButtonType.SECONDARY -> Button(
            onClick = onClick,
            modifier = modifier.then(buttonHeightModifier),
            enabled = enabled,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = appColors.primaryColor,
                disabledContainerColor = Color.White.copy(alpha = 0.7f),
                disabledContentColor = appColors.primaryColor.copy(alpha = 0.5f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = contentPadding,
            content = content
        )

        ButtonType.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.then(buttonHeightModifier),
            enabled = enabled,
            shape = buttonShape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = appColors.primaryColor
            ),
            border = BorderStroke(1.dp, appColors.primaryColor),
            contentPadding = contentPadding,
            content = content
        )

        ButtonType.TEXT -> TextButton(
            onClick = onClick,
            modifier = modifier.then(buttonHeightModifier),
            enabled = enabled,
            shape = buttonShape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = appColors.primaryColor
            ),
            contentPadding = contentPadding,
            content = content
        )
    }
}
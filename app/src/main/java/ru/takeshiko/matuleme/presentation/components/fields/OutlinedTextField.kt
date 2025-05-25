package ru.takeshiko.matuleme.presentation.components.fields

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldSize: Dp = 56.dp,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val colors = rememberAppColors()
    val materialColors = MaterialTheme.colorScheme

    val boxModifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .background(Color.White.copy(alpha = 0.95f))

    Box(
        modifier = boxModifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldSize),
            label = label?.let { { Text(text = it) } },
            placeholder = placeholder?.let { { Text(text = it) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            supportingText = if (isError && errorText != null) {
                { Text(text = errorText) }
            } else null,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primaryColor,
                unfocusedBorderColor = colors.primaryColor.copy(alpha = 0.5f),
                disabledBorderColor = materialColors.onSurface.copy(alpha = 0.12f),
                errorBorderColor = materialColors.error,

                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,

                cursorColor = colors.primaryColor,
                errorCursorColor = materialColors.error,

                focusedTextColor = textStyle.color,
                unfocusedTextColor = textStyle.color,
                disabledTextColor = materialColors.onSurface.copy(alpha = 0.38f),
                errorTextColor = materialColors.error,

                focusedLabelColor = colors.primaryColor,
                unfocusedLabelColor = materialColors.onSurfaceVariant,
                disabledLabelColor = materialColors.onSurface.copy(alpha = 0.38f),
                errorLabelColor = materialColors.error,

                focusedPlaceholderColor = materialColors.onSurfaceVariant.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = materialColors.onSurfaceVariant.copy(alpha = 0.5f),
                disabledPlaceholderColor = materialColors.onSurfaceVariant.copy(alpha = 0.38f),
                errorPlaceholderColor = materialColors.error.copy(alpha = 0.5f),

                focusedLeadingIconColor = materialColors.onSurfaceVariant,
                unfocusedLeadingIconColor = materialColors.onSurfaceVariant,
                disabledLeadingIconColor = materialColors.onSurfaceVariant.copy(alpha = 0.38f),
                errorLeadingIconColor = materialColors.error,

                focusedTrailingIconColor = materialColors.onSurfaceVariant,
                unfocusedTrailingIconColor = materialColors.onSurfaceVariant,
                disabledTrailingIconColor = materialColors.onSurfaceVariant.copy(alpha = 0.38f),
                errorTrailingIconColor = materialColors.error,

                focusedSupportingTextColor = materialColors.onSurfaceVariant,
                unfocusedSupportingTextColor = materialColors.onSurfaceVariant,
                disabledSupportingTextColor = materialColors.onSurfaceVariant.copy(alpha = 0.38f),
                errorSupportingTextColor = materialColors.error,

                focusedPrefixColor = materialColors.onSurfaceVariant,
                unfocusedPrefixColor = materialColors.onSurfaceVariant,
                disabledPrefixColor = materialColors.onSurfaceVariant.copy(alpha = 0.38f),
                errorPrefixColor = materialColors.error,

                focusedSuffixColor = materialColors.onSurfaceVariant,
                unfocusedSuffixColor = materialColors.onSurfaceVariant,
                disabledSuffixColor = materialColors.onSurfaceVariant.copy(alpha = 0.38f),
                errorSuffixColor = materialColors.error
            ),
            textStyle = textStyle
        )
    }
}
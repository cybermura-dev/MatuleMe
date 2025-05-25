package ru.takeshiko.matuleme.presentation.screen.writenewpassword

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.ButtonType
import ru.takeshiko.matuleme.presentation.components.CustomButton
import ru.takeshiko.matuleme.presentation.components.PrimaryBackground
import ru.takeshiko.matuleme.presentation.components.fields.CustomOutlinedTextField
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog

@Composable
fun WriteNewPasswordScreen(
    viewModel: WriteNewPasswordViewModel,
    showBackButton: Boolean = false,
    onNavigateToBack: () -> Unit = {},
    onPasswordResetSuccess: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    val newPassword by viewModel.newPassword.collectAsStateWithLifecycle()
    val newPasswordRepeat by viewModel.newPasswordRepeat.collectAsStateWithLifecycle()

    val newPasswordError by viewModel.newPasswordError.collectAsStateWithLifecycle()
    val newPasswordRepeatError by viewModel.newPasswordRepeatError.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val passwordResetSuccess by viewModel.passwordResetSuccess.collectAsStateWithLifecycle()

    var newPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordRepeatVisible by remember { mutableStateOf(false) }

    val newPasswordVisibilityIcon =
        if (newPasswordVisible) painterResource(R.drawable.ic_eye_open)
        else painterResource(R.drawable.ic_eye_close)

    val newPasswordRepeatVisibilityIcon =
        if (newPasswordRepeatVisible) painterResource(R.drawable.ic_eye_open)
        else painterResource(R.drawable.ic_eye_close)

    var showErrorMessageCard by remember { mutableStateOf(false) }
    var showSuccessMessageCard by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        showErrorMessageCard = !errorMessage.isNullOrBlank()
    }

    LaunchedEffect(passwordResetSuccess) {
        if (passwordResetSuccess) {
            showSuccessMessageCard = true
            onPasswordResetSuccess()
        }
    }

    PrimaryBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(52.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onNavigateToBack,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowLeft,
                                contentDescription = stringResource(R.string.back),
                                tint = Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(R.string.write_new_password_title),
                            style = typography.displayMedium,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.write_new_password_prompt),
                            style = typography.headlineMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.input_password),
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        CustomOutlinedTextField(
                            value = newPassword,
                            onValueChange = viewModel::onNewPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (!newPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        painter = newPasswordVisibilityIcon,
                                        contentDescription = stringResource(R.string.toggle_password_visibility),
                                        tint = appColors.primaryColor.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = newPasswordError != null
                        )

                        newPasswordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.input_repeat_password),
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        CustomOutlinedTextField(
                            value = newPasswordRepeat,
                            onValueChange = viewModel::onNewPasswordRepeatChange,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (!newPasswordRepeatVisible) PasswordVisualTransformation() else VisualTransformation.None,
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { newPasswordRepeatVisible = !newPasswordRepeatVisible }) {
                                    Icon(
                                        painter = newPasswordRepeatVisibilityIcon,
                                        contentDescription = stringResource(R.string.toggle_password_visibility),
                                        tint = appColors.primaryColor.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = newPasswordRepeatError != null
                        )

                        newPasswordRepeatError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                CustomButton(
                    onClick = viewModel::validateNewPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    type = ButtonType.SECONDARY,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = appColors.primaryColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.save_new_password),
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = appColors.primaryColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }

            if (showSuccessMessageCard) {
                EventDialog(
                    icon = Icons.Default.Check,
                    title = stringResource(R.string.password_reset_success),
                    message = "",
                    onDismiss = {
                        showSuccessMessageCard = false
                        viewModel.passwordResetHandled()
                        onPasswordResetSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}
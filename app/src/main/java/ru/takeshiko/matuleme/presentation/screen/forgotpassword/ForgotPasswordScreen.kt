package ru.takeshiko.matuleme.presentation.screen.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.ButtonType
import ru.takeshiko.matuleme.presentation.components.CustomButton
import ru.takeshiko.matuleme.presentation.components.fields.CustomOutlinedTextField
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.takeshiko.matuleme.presentation.components.PrimaryBackground
import ru.takeshiko.matuleme.presentation.theme.AppTypography

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    showBackButton: Boolean = false,
    onNavigateToBack: () -> Unit = {}
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    val email by viewModel.email.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                            text = stringResource(R.string.forgot_password_greeting),
                            style = typography.displayMedium,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.forgot_password_prompt),
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
                            text = stringResource(R.string.input_email),
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        CustomOutlinedTextField(
                            value = email,
                            onValueChange = viewModel::onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = emailError != null
                        )

                        emailError?.let {
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
                    onClick = viewModel::validateEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    type = ButtonType.SECONDARY
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = appColors.primaryColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.send_code),
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = appColors.primaryColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
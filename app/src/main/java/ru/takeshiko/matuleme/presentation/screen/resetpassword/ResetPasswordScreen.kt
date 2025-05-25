package ru.takeshiko.matuleme.presentation.screen.resetpassword

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.ButtonType
import ru.takeshiko.matuleme.presentation.components.CustomButton
import ru.takeshiko.matuleme.presentation.components.fields.OtpTextField
import ru.takeshiko.matuleme.presentation.components.PrimaryBackground
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.util.Locale

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel,
    showBackButton: Boolean = true,
    resendDelaySeconds: Int = 60,
    onNavigateToBack: () -> Unit = {}
) {
    val appColors = rememberAppColors()
    val typography = AppTypography

    val otpLength = 6
    val otpCode by viewModel.otpCode.collectAsStateWithLifecycle()
    val otpCodeError by viewModel.otpCodeError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isCodeSent by viewModel.isCodeSent.collectAsStateWithLifecycle()

    var resendTimerSeconds by remember { mutableIntStateOf(resendDelaySeconds) }

    LaunchedEffect(isCodeSent) {
        if (isCodeSent) {
            resendTimerSeconds = resendDelaySeconds
            while (resendTimerSeconds > 0) {
                delay(1000L)
                resendTimerSeconds--
            }
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
                            text = stringResource(R.string.reset_password_greeting),
                            style = typography.displayMedium,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.reset_password_prompt, viewModel.email ?: ""),
                            style = typography.headlineMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.input_otp),
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OtpTextField(
                        otpValue = otpCode,
                        onOtpChange = viewModel::onOtpCodeChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    otpCodeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            if (resendTimerSeconds == 0) {
                                viewModel.resendOtpCode()
                            }
                        },
                        enabled = resendTimerSeconds == 0 && !isLoading
                    ) {
                        Text(
                            text = if (resendTimerSeconds > 0) {
                                val minutes = resendTimerSeconds / 60
                                val remainingSeconds = resendTimerSeconds % 60
                                String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
                            } else {
                                stringResource(R.string.resend_otp)
                            },
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                CustomButton(
                    onClick = viewModel::resetPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    type = ButtonType.SECONDARY,
                    enabled = otpCode.length == otpLength && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = appColors.primaryColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.reset_password),
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
package ru.takeshiko.matuleme.presentation.screen.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.components.dialogs.EventDialog
import ru.takeshiko.matuleme.presentation.screen.main.MainActivity
import ru.takeshiko.matuleme.presentation.screen.register.RegisterActivity
import ru.takeshiko.matuleme.presentation.screen.forgotpassword.ForgotPasswordActivity
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var showNotificationCard by remember { mutableStateOf(false) }
            val loginSuccess by viewModel.loginSuccess.collectAsState()
            val errorMessage by viewModel.errorMessage.collectAsState()

            LaunchedEffect(loginSuccess) {
                if (loginSuccess) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }

            LaunchedEffect(errorMessage) {
                showNotificationCard = !errorMessage.isNullOrBlank()
            }

            MatuleMeTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToRegister = {
                        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    },
                    onNavigateToForgotPassword = {
                        startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                    }
                )

                if (showNotificationCard) {
                    EventDialog(
                        icon = Icons.Default.Close,
                        title = stringResource(R.string.unknown_error_occurred),
                        message = errorMessage.orEmpty(),
                        onDismiss = { showNotificationCard = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
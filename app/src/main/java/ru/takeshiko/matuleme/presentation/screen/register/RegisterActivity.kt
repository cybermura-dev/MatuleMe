package ru.takeshiko.matuleme.presentation.screen.register

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
import ru.takeshiko.matuleme.presentation.screen.verification.OtpVerificationActivity
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme

class RegisterActivity : ComponentActivity() {

    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var showNotificationCard by remember { mutableStateOf(false) }
            val registrationSuccess by viewModel.registrationSuccess.collectAsState()
            val errorMessage by viewModel.errorMessage.collectAsState()

            LaunchedEffect(registrationSuccess) {
                if (registrationSuccess) {
                    val intent = Intent(this@RegisterActivity, OtpVerificationActivity::class.java)
                    intent.putExtra("email", viewModel.email.value)
                    startActivity(intent)
                    viewModel.clearRegistrationSuccess()
                }
            }

            LaunchedEffect(errorMessage) {
                showNotificationCard = !errorMessage.isNullOrBlank()
            }

            MatuleMeTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onNavigateToBack = { finish() }
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
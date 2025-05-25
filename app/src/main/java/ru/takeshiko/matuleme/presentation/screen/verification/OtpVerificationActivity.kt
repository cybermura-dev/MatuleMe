package ru.takeshiko.matuleme.presentation.screen.verification

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
import ru.takeshiko.matuleme.presentation.screen.login.LoginActivity
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme

class OtpVerificationActivity : ComponentActivity() {

    private val viewModel: OtpVerificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val email = intent.getStringExtra("email") ?: ""
        viewModel.email = email

        setContent {
            var showNotificationCard by remember { mutableStateOf(false) }
            val verificationSuccess by viewModel.verificationSuccess.collectAsState()
            val error by viewModel.errorMessage.collectAsState()

            LaunchedEffect(verificationSuccess) {
                if (verificationSuccess) {
                    startActivity(Intent(this@OtpVerificationActivity, LoginActivity::class.java))
                    finish()
                }
            }

            LaunchedEffect(error) {
                showNotificationCard = !error.isNullOrBlank()
            }

            MatuleMeTheme {
                OtpVerificationScreen(
                    viewModel = viewModel,
                    onNavigateToBack = { finish() },
                )

                if (showNotificationCard) {
                    EventDialog(
                        icon = Icons.Default.Close,
                        title = stringResource(R.string.unknown_error_occurred),
                        message = error.orEmpty(),
                        onDismiss = { showNotificationCard = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
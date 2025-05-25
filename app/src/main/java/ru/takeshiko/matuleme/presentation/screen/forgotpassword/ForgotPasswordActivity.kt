package ru.takeshiko.matuleme.presentation.screen.forgotpassword

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.takeshiko.matuleme.presentation.screen.resetpassword.ResetPasswordActivity
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme
import kotlin.getValue

class ForgotPasswordActivity : ComponentActivity() {

    private val viewModel: ForgotPasswordViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val inputSuccess by viewModel.inputSuccess.collectAsState()

            LaunchedEffect(inputSuccess) {
               if (inputSuccess) {
                   val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                   intent.putExtra("email", viewModel.email.value)
                   startActivity(intent)
               }
            }

            MatuleMeTheme {
                ForgotPasswordScreen(
                    viewModel = viewModel,
                    showBackButton = true,
                    onNavigateToBack = {
                        finish()
                    }
                )
            }
        }
    }
}
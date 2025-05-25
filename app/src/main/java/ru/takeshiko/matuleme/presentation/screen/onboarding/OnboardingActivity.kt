package ru.takeshiko.matuleme.presentation.screen.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.takeshiko.matuleme.presentation.screen.login.LoginActivity
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme

class OnboardingActivity : ComponentActivity() {

    private val viewModel: OnboardingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val onboardingSuccess by viewModel.onboardingSuccess.collectAsState()

            LaunchedEffect(onboardingSuccess) {
                if (onboardingSuccess) {
                    startActivity(
                        Intent(
                            this@OnboardingActivity,
                            LoginActivity::class.java
                        )
                    )
                    finish()
                }
            }

            MatuleMeTheme {
                OnboardingScreen(viewModel)
            }
        }
    }
}
package ru.takeshiko.matuleme.presentation.screen.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import ru.takeshiko.matuleme.presentation.components.navigation.NavigationState
import ru.takeshiko.matuleme.presentation.screen.login.LoginActivity
import ru.takeshiko.matuleme.presentation.screen.main.MainActivity
import ru.takeshiko.matuleme.presentation.screen.onboarding.OnboardingActivity
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme
import kotlin.jvm.java

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var showNotificationCard by remember { mutableStateOf(false) }
            val navState by viewModel.navState.collectAsState()
            val checkSuccess by viewModel.checkSuccess.collectAsState()

            LaunchedEffect(checkSuccess) {
                if (checkSuccess) {
                    when (navState) {
                        NavigationState.Onboarding -> {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    OnboardingActivity::class.java
                                )
                            )
                            finish()
                        }
                        NavigationState.Login -> {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()
                        }
                        NavigationState.Main -> {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                        else -> {}
                    }
                }
            }

            LaunchedEffect(navState) {
                if (navState is NavigationState.Error) {
                    showNotificationCard = true
                }
            }

            MatuleMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen(viewModel)

                    if (showNotificationCard) {
                        EventDialog(
                            icon = Icons.Default.Close,
                            title = stringResource(R.string.unknown_error_occurred),
                            message = (navState as NavigationState.Error).message,
                            onDismiss = { showNotificationCard = false },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
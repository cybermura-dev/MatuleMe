package ru.takeshiko.matuleme.presentation.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.takeshiko.matuleme.presentation.theme.MatuleMeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MatuleMeTheme {
                MainScreen()
            }
        }
    }
}
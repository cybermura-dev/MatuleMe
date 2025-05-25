package ru.takeshiko.matuleme.presentation.screen.aboutapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.takeshiko.matuleme.presentation.components.aboutapp.AboutAppContent
import ru.takeshiko.matuleme.presentation.components.aboutapp.AboutAppTopBar

@Composable
fun AboutAppScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            AboutAppTopBar(
                onBackClick = navController::navigateUp
            )
        }
    ) { paddingValues ->
        AboutAppContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
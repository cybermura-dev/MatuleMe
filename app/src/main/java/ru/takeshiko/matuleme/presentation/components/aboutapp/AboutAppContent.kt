package ru.takeshiko.matuleme.presentation.components.aboutapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun AboutAppContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appColors = rememberAppColors()
    val typography = AppTypography

    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val appName = stringResource(id = R.string.app_name)
    val appVersion = packageInfo.versionName

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = appName,
            style = typography.titleLarge,
            color = appColors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.about_app_version_label, appVersion!!),
            style = typography.bodyMedium,
            color = appColors.textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = appColors.textSecondary.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.about_app_description_label),
            style = typography.titleMedium,
            color = appColors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.about_app_description_text),
            style = typography.bodyMedium,
            color = appColors.textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = appColors.textSecondary.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.about_app_developer_label),
            style = typography.titleMedium,
            color = appColors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.about_app_developer_name),
            style = typography.bodyMedium,
            color = appColors.textSecondary
        )
    }
}
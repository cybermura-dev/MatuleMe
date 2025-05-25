package ru.takeshiko.matuleme.presentation.components.userinfo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserUpdateDto
import ru.takeshiko.matuleme.presentation.components.fields.CustomOutlinedTextField
import ru.takeshiko.matuleme.presentation.components.profile.UserImage
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors
import java.io.File
import androidx.compose.ui.graphics.Color

@Composable
fun UserInfoContent(
    user: UserDto,
    isLoading: Boolean,
    isUploadingAvatar: Boolean,
    onUpdate: (UserUpdateDto) -> Unit,
    selectedAvatarUri: Uri?,
    onAvatarSelected: (Uri) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val context = LocalContext.current

    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var phone by remember { mutableStateOf(user.phoneNumber) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        firstName = user.firstName
        lastName = user.lastName
        phone = user.phoneNumber
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let(onAvatarSelected) }

    val tempFile = remember { File.createTempFile("avatar_", ".jpg", context.cacheDir).also { it.deleteOnExit() } }
    val cameraUri = remember(tempFile) {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) onAvatarSelected(cameraUri) else tempFile.delete()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(cameraUri)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            UserInfoTopBar(
                onNavigateToBack = onNavigateToBack
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = appColors.primaryColor.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = appColors.surfaceColor.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onUpdate(
                                UserUpdateDto(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phoneNumber = phone,
                                    avatarUrl = selectedAvatarUri?.toString()
                                )
                            )
                        },
                        enabled = !isLoading && !isUploadingAvatar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryColor,
                            contentColor = appColors.surfaceColor,
                            disabledContainerColor = appColors.primaryColor.copy(alpha = 0.5f),
                            disabledContentColor = appColors.surfaceColor.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading || isUploadingAvatar) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = appColors.surfaceColor,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.save),
                                style = typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading && !isUploadingAvatar) {
                    CircularProgressIndicator()
                } else {
                    UserImage(
                        imageUri = selectedAvatarUri ?: user.avatarUrl?.toUri(),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { showDialog = true }
                    )

                    if (isUploadingAvatar) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).size(48.dp),
                            color = appColors.primaryColor
                        )
                    }
                }
            }

            CustomOutlinedTextField(
                value = firstName ?: "",
                onValueChange = { firstName = it },
                textFieldSize = 64.dp,
                label = stringResource(R.string.first_name),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            CustomOutlinedTextField(
                value = lastName ?: "",
                onValueChange = { lastName = it },
                textFieldSize = 64.dp,
                label = stringResource(R.string.last_name),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            CustomOutlinedTextField(
                value = phone ?: "",
                onValueChange = { phone = it },
                textFieldSize = 64.dp,
                label = stringResource(R.string.phone_number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.select_avatar)) },
                text = {
                    Column {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.choose_from_gallery)) },
                            leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                            modifier = Modifier.clickable {
                                galleryLauncher.launch("image/*")
                                showDialog = false
                            }
                        )

                        HorizontalDivider()

                        ListItem(
                            headlineContent = { Text(stringResource(R.string.take_photo)) },
                            leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                            modifier = Modifier.clickable {
                                val permissionCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )
                                when {
                                    permissionCheck == PackageManager.PERMISSION_GRANTED -> {
                                        cameraLauncher.launch(cameraUri)
                                    }
                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                                showDialog = false
                            }
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }
    }
}
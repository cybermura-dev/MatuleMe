package ru.takeshiko.matuleme.presentation.screen.userinfo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.exceptions.NotFoundRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.takeshiko.matuleme.BuildConfig
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserUpdateDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateUserDataUseCase
import ru.takeshiko.matuleme.domain.usecase.UploadFileUseCase

class UserInfoViewModel(
    private val getUser: GetUserUseCase,
    private val updateUserData: UpdateUserDataUseCase,
    private val uploadFile: UploadFileUseCase,
    private val applicationContext: Context,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedAvatarUri = MutableStateFlow<Uri?>(null)
    val selectedAvatarUri: StateFlow<Uri?> = _selectedAvatarUri.asStateFlow()

    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar: StateFlow<Boolean> = _isUploadingAvatar.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedAvatarUri.value = null

            getUser().onSuccess {
                _user.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun onAvatarSelected(imageUri: Uri) {
        _selectedAvatarUri.value = imageUri
    }

    fun updateUser(userUpdate: UserUpdateDto) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentUser = _user.value
            if (currentUser == null) {
                _errorMessage.value = stringResourceProvider.getString(R.string.error_invalid_user_id)
                _isLoading.value = false
                return@launch
            }

            var newSupabaseAvatarUrl: String? = null

            if (selectedAvatarUri.value != null) {
                _isUploadingAvatar.value = true
                val uri = selectedAvatarUri.value!!
                val bytes = applicationContext.contentResolver.openInputStream(uri)?.readBytes()

                if (bytes != null) {
                    val bucket = "avatars"
                    val fileName = "avatar_${currentUser.id}_${System.currentTimeMillis()}.jpg"
                    val path = "user_${currentUser.id}/$fileName"
                    val contentType = applicationContext.contentResolver.getType(uri)?.lowercase() ?: "image/jpeg"

                    uploadFile(bucket, path, bytes, contentType).onSuccess { uploadedPath ->
                        newSupabaseAvatarUrl = "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/$bucket/$path"
                    }.onFailure { exception ->
                        _errorMessage.value = exception.message
                        _isUploadingAvatar.value = false
                        _isLoading.value = false
                        return@launch
                    }
                } else {
                    _errorMessage.value = "Не удалось прочитать файл изображения."
                    _isUploadingAvatar.value = false
                    _isLoading.value = false
                    return@launch
                }
                _isUploadingAvatar.value = false
            }

            val finalAvatarUrlForUpdate: String?
            if (newSupabaseAvatarUrl != null) {
                finalAvatarUrlForUpdate = newSupabaseAvatarUrl
            } else if (selectedAvatarUri.value == null) {
                finalAvatarUrlForUpdate = currentUser.avatarUrl
            } else {
                finalAvatarUrlForUpdate = currentUser.avatarUrl
            }

            val update = UserUpdateDto(
                firstName = userUpdate.firstName,
                lastName = userUpdate.lastName,
                phoneNumber = userUpdate.phoneNumber,
                avatarUrl = finalAvatarUrlForUpdate
            )

            updateUserData(update)
                .onSuccess { updatedUser ->
                    _user.value = updatedUser
                    _selectedAvatarUri.value = null
                }
                .onFailure { exception ->
                    if (exception is NotFoundRestException) {
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value = exception.localizedMessage
                    }
                }

            _isLoading.value = false
        }
    }
}
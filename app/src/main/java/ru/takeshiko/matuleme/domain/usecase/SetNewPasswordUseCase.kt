package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class SetNewPasswordUseCase(
    private val userRepository: UserRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(newPassword: String): Result<Unit> {
        val trimmedPassword = newPassword.trim()

        if (trimmedPassword.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_password_empty))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            userRepository.updateUserData(ru.takeshiko.matuleme.domain.models.UserUpdateDto(password = trimmedPassword)).getOrThrow()
        }
    }
}
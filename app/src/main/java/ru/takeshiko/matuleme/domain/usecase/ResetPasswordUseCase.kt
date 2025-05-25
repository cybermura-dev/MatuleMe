package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class ResetPasswordUseCase(
    private val userRepository: UserRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_email_empty))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            userRepository.sendPasswordResetEmail(trimmedEmail)
                .getOrThrow()
        }
    }
}
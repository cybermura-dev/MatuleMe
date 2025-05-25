package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class ConfirmResetPasswordUseCase(
    private val userRepository: UserRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(email: String, otp: String): Result<Unit> {
        val trimmedEmail = email.trim()
        val trimmedOtp = otp.trim()

        if (trimmedEmail.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_email_empty))
            )
        }

        if (trimmedOtp.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_otp_code_blank))
            )
        }

        if (trimmedOtp.length != 6 || !trimmedOtp.all { it.isDigit() }) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_otp_code_invalid_format))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            userRepository.confirmPasswordReset(trimmedEmail, trimmedOtp)
                .getOrThrow()
        }
    }
}
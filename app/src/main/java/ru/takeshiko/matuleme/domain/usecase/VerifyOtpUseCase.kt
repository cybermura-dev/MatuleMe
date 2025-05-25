package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class VerifyOtpUseCase(
    private val userRepository: UserRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(email: String, otp: String): Result<Unit> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_email_empty))
            )
        }

        if (otp.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_otp_code_blank))
            )
        }

        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_otp_code_too_long))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            userRepository.verifyEmail(trimmedEmail, otp)
                .getOrThrow()
        }
    }
}
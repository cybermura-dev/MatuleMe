package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider

class ValidateEmailUseCase(
    private val stringResourceProvider: StringResourceProvider
) {
    private val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()

    operator fun invoke(email: String): Result<String> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_email_empty))
            )
        }

        if (!emailRegex.matches(trimmedEmail)) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_email_invalid_format))
            )
        }

        return Result.success(trimmedEmail)
    }
}
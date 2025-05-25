package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider

class ValidatePasswordUseCase(
    private val stringResourceProvider: StringResourceProvider
) {
    private val minLength = 6
    private val maxLength = 50
    private val minCharacterTypes = 2

    operator fun invoke(password: String): Result<Unit> {
        if (password.isBlank()) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_password_empty))
            )
        }

        if (password.length < minLength) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_password_too_short, minLength))
            )
        }

        if (password.length > maxLength) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_password_too_long, maxLength))
            )
        }

        if (!checkPasswordComplexity(password)) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_password_weak, minCharacterTypes))
            )
        }

        return Result.success(Unit)
    }

    private fun checkPasswordComplexity(
        password: String,
        minCharacterTypes: Int = this.minCharacterTypes
    ): Boolean {
        var metCount = 0
        if (password.any { it.isUpperCase() }) metCount++
        if (password.any { it.isLowerCase() }) metCount++
        if (password.any { it.isDigit() }) metCount++
        if (password.any { !it.isLetterOrDigit() }) metCount++

        return metCount >= minCharacterTypes
    }
}
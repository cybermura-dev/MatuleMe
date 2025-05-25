package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(): Result<UserDto?> {
        val user = userRepository.getCurrentUser()

        if (user.isFailure) {
            return Result.failure(
                IllegalArgumentException(stringResourceProvider.getString(R.string.error_email_empty))
            )
        }

        return stringResourceProvider.safeSupabaseCall {
            user.getOrThrow()
        }
    }
}
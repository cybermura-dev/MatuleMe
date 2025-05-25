package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserUpdateDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UpdateUserDataUseCase(
    private val repository: UserRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(user: UserUpdateDto) : Result<UserDto> {
        return stringResourceProvider.safeSupabaseCall {
            repository.updateUserData(user).getOrThrow()
        }
    }
}
package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class LogoutUseCase(
    private val userRepository: UserRepository,
    private val stringResourceProvider: StringResourceProvider

) {
    suspend operator fun invoke(): Result<Unit> =
        stringResourceProvider.safeSupabaseCall {
            userRepository.logout()
                .getOrThrow()
        }
}
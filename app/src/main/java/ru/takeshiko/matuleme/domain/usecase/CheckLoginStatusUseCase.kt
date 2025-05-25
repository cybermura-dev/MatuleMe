package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.repository.UserRepository

class CheckLoginStatusUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> =
        userRepository.isUserAuthenticated()
}
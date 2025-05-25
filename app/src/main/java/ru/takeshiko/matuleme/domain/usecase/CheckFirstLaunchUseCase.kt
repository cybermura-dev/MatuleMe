package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.repository.UserRepository

class CheckFirstLaunchUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean =
        userRepository.isFirstLaunch()
}
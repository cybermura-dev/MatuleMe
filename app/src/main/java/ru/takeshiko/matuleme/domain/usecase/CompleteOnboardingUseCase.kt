package ru.takeshiko.matuleme.domain.usecase

import ru.takeshiko.matuleme.domain.repository.UserRepository

class CompleteOnboardingUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() =
        userRepository.completeOnboarding()
}
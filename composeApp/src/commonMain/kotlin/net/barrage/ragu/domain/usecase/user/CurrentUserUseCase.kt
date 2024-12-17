package net.barrage.ragu.domain.usecase.user

import net.barrage.ragu.domain.repository.UserRepository

class CurrentUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getCurrentUser()
}

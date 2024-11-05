package net.barrage.chatwhitelabel.domain.usecase.user

import net.barrage.chatwhitelabel.domain.repository.UserRepository

class CurrentUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getCurrentUser()
}

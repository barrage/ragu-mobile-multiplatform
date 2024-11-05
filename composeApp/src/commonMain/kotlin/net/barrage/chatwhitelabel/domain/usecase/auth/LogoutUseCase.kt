package net.barrage.chatwhitelabel.domain.usecase.auth

import net.barrage.chatwhitelabel.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}

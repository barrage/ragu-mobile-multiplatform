package net.barrage.chatwhitelabel.domain.usecase.auth

import io.ktor.client.statement.HttpResponse
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Response<HttpResponse> = authRepository.logout()
}

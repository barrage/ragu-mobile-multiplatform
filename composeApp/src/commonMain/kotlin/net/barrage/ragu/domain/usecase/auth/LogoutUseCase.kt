package net.barrage.ragu.domain.usecase.auth

import io.ktor.client.statement.HttpResponse
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Response<HttpResponse> = authRepository.logout()
}

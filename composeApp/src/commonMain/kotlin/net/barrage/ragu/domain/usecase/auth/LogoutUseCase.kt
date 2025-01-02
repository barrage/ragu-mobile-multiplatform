package net.barrage.ragu.domain.usecase.auth

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Flow<Response<HttpResponse>> = authRepository.logout()
}

package net.barrage.chatwhitelabel.domain.usecase.auth

import io.ktor.http.Parameters
import io.ktor.http.parameters
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.AuthToken
import net.barrage.chatwhitelabel.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(code: String): Response<AuthToken> =
        authRepository.login(createLoginParameters(code))

    private fun createLoginParameters(code: String): Parameters {
        return parameters {
            append("code", code)
            append("redirect_uri", "https://llmao-kotlin-api-staging.m2.barrage.beer/auth/callback")
            append("grant_type", "authorization_code")
            append("source", "android")
            append("provider", "google")
        }
    }
}

package net.barrage.chatwhitelabel.domain.usecase.auth

import io.ktor.http.Parameters
import io.ktor.http.parameters
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.AuthToken
import net.barrage.chatwhitelabel.domain.repository.AuthRepository
import net.barrage.chatwhitelabel.utils.Constants

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(code: String, codeVerifier: String): Response<AuthToken> =
        authRepository.login(createLoginParameters(code, codeVerifier))

    private fun createLoginParameters(code: String, codeVerifier: String): Parameters {
        return parameters {
            append("code", code)
            append("redirect_uri", Constants.Auth.REDIRECT_URI)
            append("grant_type", "authorization_code")
            append("source", "android")
            append("provider", "google")
            append("code_verifier", codeVerifier)
        }
    }
}

package net.barrage.ragu.domain.usecase.auth

import io.ktor.http.Parameters
import io.ktor.http.parameters
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.AuthToken
import net.barrage.ragu.domain.repository.AuthRepository
import net.barrage.ragu.utils.Constants

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

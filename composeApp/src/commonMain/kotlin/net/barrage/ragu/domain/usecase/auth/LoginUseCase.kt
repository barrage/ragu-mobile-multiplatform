package net.barrage.ragu.domain.usecase.auth

import io.ktor.http.Parameters
import io.ktor.http.parameters
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.AuthToken
import net.barrage.ragu.domain.repository.AuthRepository
import net.barrage.ragu.utils.Constants

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        code: String,
        provider: String,
        grantType: String,
        source: String,
        codeVerifier: String
    ): Response<AuthToken> =
        authRepository.login(
            createLoginParameters(
                code = code,
                provider = provider,
                grantType = grantType,
                source = source,
                codeVerifier = codeVerifier
            )
        )

    private fun createLoginParameters(
        code: String,
        provider: String,
        grantType: String,
        source: String,
        codeVerifier: String
    ): Parameters {
        return parameters {
            append("code", code)
            append("redirect_uri", Constants.Auth.REDIRECT_URI)
            append("grant_type", grantType)
            append("source", source)
            append("provider", provider)
            append("code_verifier", codeVerifier)
        }
    }
}

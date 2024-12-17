package net.barrage.ragu.data.repository

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.AuthToken
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.AuthRepository

class AuthRepositoryImpl(private val api: Api) : AuthRepository {
    override suspend fun login(parameters: Parameters): Response<AuthToken> {
        return when (val response = api.login(parameters)) {
            is Response.Success -> {
                val cookie = extractCookie(response.data)
                if (cookie != null) {
                    Response.Success(AuthToken(cookie))
                } else {
                    Response.Failure(Exception("Auth cookie not found in response"))
                }
            }

            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }

    private fun extractCookie(response: HttpResponse): String? {
        return response.headers
            .getAll("Set-Cookie")
            ?.asSequence()
            ?.flatMap { it.split(',') }
            ?.flatMap { it.split(';') }
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("kappi=") }
    }

    override suspend fun logout(): Response<HttpResponse> = api.logout()
}

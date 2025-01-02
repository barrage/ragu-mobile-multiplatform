package net.barrage.ragu.data.repository

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.AuthToken
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.AuthRepository

class AuthRepositoryImpl(private val api: Api) : AuthRepository {
    override suspend fun login(parameters: Parameters): Flow<Response<AuthToken>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.login(parameters)) {
                is Response.Success -> {
                    val cookie = extractCookie(response.data)
                    if (cookie != null) {
                        emit(Response.Success(AuthToken(cookie)))
                    } else {
                        emit(Response.Failure(Exception("Auth cookie not found in response")))
                    }
                }

                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
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

    override suspend fun logout(): Flow<Response<HttpResponse>> = flow {
        emit(Response.Loading)
        try {
            emit(api.logout())
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}
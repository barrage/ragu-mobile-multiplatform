package net.barrage.ragu.domain.repository

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.AuthToken

interface AuthRepository {
    suspend fun login(parameters: Parameters): Response<AuthToken>

    suspend fun logout(): Response<HttpResponse>
}

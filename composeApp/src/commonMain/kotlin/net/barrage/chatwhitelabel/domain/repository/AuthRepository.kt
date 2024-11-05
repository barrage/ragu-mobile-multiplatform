package net.barrage.chatwhitelabel.domain.repository

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.AuthToken

interface AuthRepository {
    suspend fun login(parameters: Parameters): Response<AuthToken>

    suspend fun logout(): Response<HttpResponse>
}

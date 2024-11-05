package net.barrage.chatwhitelabel.data.remote.ktor

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.chatwhitelabel.data.remote.dto.user.CurrentUserDTO
import net.barrage.chatwhitelabel.domain.Response

interface Api {
    suspend fun login(parameters: Parameters): Response<HttpResponse>

    suspend fun logout(): Response<HttpResponse>

    suspend fun getCurrentUser(): Response<CurrentUserDTO>

    suspend fun getWebSocketToken(): Response<String>
}

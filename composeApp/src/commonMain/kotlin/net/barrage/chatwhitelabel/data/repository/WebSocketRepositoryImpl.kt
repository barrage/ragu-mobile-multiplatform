package net.barrage.chatwhitelabel.data.repository

import net.barrage.chatwhitelabel.data.remote.ktor.Api
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.WebSocketToken
import net.barrage.chatwhitelabel.domain.repository.WebSocketRepository

class WebSocketRepositoryImpl(private val api: Api) : WebSocketRepository {
    override suspend fun getWebSocketToken(): Response<WebSocketToken> {
        return when (val response = api.getWebSocketToken()) {
            is Response.Success -> Response.Success(WebSocketToken(response.data))
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}

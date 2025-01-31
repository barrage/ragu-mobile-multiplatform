package net.barrage.ragu.data.repository

import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.WebSocketToken
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.WebSocketRepository

class WebSocketRepositoryImpl(private val api: Api) : WebSocketRepository {
    override suspend fun getWebSocketToken(): Response<WebSocketToken> {
        return try {
            when (val response = api.getWebSocketToken()) {
                is Response.Success -> (Response.Success(WebSocketToken(response.data)))
                is Response.Failure -> (response)
                is Response.Unauthorized -> (response)
                else -> (Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            (Response.Failure(e))
        }
    }
}
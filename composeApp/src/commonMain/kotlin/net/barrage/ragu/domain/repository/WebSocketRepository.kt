package net.barrage.ragu.domain.repository

import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.WebSocketToken

interface WebSocketRepository {
    suspend fun getWebSocketToken(): Response<WebSocketToken>
}

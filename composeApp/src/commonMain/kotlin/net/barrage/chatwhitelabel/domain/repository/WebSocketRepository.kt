package net.barrage.chatwhitelabel.domain.repository

import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.WebSocketToken

interface WebSocketRepository {
    suspend fun getWebSocketToken(): Response<WebSocketToken>
}

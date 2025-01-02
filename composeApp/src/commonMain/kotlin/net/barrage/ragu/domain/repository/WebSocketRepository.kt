package net.barrage.ragu.domain.repository

import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.WebSocketToken

interface WebSocketRepository {
    suspend fun getWebSocketToken(): Flow<Response<WebSocketToken>>
}

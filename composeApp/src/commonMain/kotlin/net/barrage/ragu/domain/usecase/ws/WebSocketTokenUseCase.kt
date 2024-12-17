package net.barrage.ragu.domain.usecase.ws

import net.barrage.ragu.domain.repository.WebSocketRepository

class WebSocketTokenUseCase(private val webSocketRepository: WebSocketRepository) {
    suspend operator fun invoke() = webSocketRepository.getWebSocketToken()
}

package net.barrage.chatwhitelabel.domain.usecase.ws

import net.barrage.chatwhitelabel.domain.repository.WebSocketRepository

class WebSocketTokenUseCase(private val webSocketRepository: WebSocketRepository) {
    suspend operator fun invoke() = webSocketRepository.getWebSocketToken()
}

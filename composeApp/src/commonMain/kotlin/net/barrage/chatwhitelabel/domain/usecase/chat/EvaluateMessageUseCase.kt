package net.barrage.chatwhitelabel.domain.usecase.chat

import net.barrage.chatwhitelabel.domain.repository.ChatRepository

class EvaluateMessageUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(chatId: String, messageId: String, evaluation: Boolean) =
        chatRepository.evaluateMessage(chatId, messageId, evaluation)
}

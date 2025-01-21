package net.barrage.ragu.domain.usecase.chat

import net.barrage.ragu.domain.repository.ChatRepository

class EvaluateMessageUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(
        chatId: String,
        messageId: String,
        evaluation: Boolean,
        feedback: String?
    ) =
        chatRepository.evaluateMessage(chatId, messageId, evaluation, feedback)
}

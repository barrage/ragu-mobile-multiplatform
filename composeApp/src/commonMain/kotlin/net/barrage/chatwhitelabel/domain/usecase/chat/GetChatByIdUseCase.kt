package net.barrage.chatwhitelabel.domain.usecase.chat

import net.barrage.chatwhitelabel.domain.repository.HistoryRepository

class GetChatByIdUseCase(private val historyRepository: HistoryRepository) {
    suspend operator fun invoke(chatId: String) = historyRepository.getChatById(chatId)
}

package net.barrage.chatwhitelabel.domain.usecase.chat

import net.barrage.chatwhitelabel.domain.repository.HistoryRepository

class HistoryByIdUseCase(private val historyRepository: HistoryRepository) {
    suspend fun invoke(chatId: String) = historyRepository.getChatById(chatId)
}

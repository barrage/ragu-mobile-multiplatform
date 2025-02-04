package net.barrage.ragu.domain.usecase.chat

import net.barrage.ragu.domain.repository.HistoryRepository

class GetChatByIdUseCase(private val historyRepository: HistoryRepository) {
    suspend operator fun invoke(chatId: String) = historyRepository.getChatById(chatId)
}

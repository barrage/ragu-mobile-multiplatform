package net.barrage.ragu.domain.usecase.chat

import net.barrage.ragu.domain.repository.HistoryRepository

class GetChatMessagesByIdUseCase(private val historyRepository: HistoryRepository) {
    suspend operator fun invoke(chatId: String, page: Int, pageSize: Int) =
        historyRepository.getChatMessagesById(chatId, page, pageSize)
}

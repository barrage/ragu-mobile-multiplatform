package net.barrage.ragu.domain.usecase.chat

import net.barrage.ragu.domain.repository.ChatRepository

class UpdateChatTitleUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(chatId: String, title: String) =
        chatRepository.updateChatTitle(chatId, title)
}

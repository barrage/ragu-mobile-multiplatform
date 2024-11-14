package net.barrage.chatwhitelabel.domain.usecase.chat

import net.barrage.chatwhitelabel.domain.repository.ChatRepository

class UpdateChatTitleUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(chatId: String, title: String) =
        chatRepository.updateChatTitle(chatId, title)
}

package net.barrage.chatwhitelabel.domain.usecase.chat

import net.barrage.chatwhitelabel.domain.repository.ChatRepository

class DeleteChatUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(chatId: String) = chatRepository.deleteChat(chatId)
}

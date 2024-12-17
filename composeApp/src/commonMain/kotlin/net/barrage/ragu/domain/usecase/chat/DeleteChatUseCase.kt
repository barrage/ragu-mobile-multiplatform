package net.barrage.ragu.domain.usecase.chat

import net.barrage.ragu.domain.repository.ChatRepository

class DeleteChatUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(chatId: String) = chatRepository.deleteChat(chatId)
}

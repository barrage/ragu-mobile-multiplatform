@file:Suppress("LongParameterList")

package net.barrage.chatwhitelabel.domain.usecase.chat

import net.barrage.chatwhitelabel.domain.usecase.agents.GetAgentsUseCase

class ChatUseCase(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val getChatMessagesByIdUseCase: GetChatMessagesByIdUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val evaluateMessageUseCase: EvaluateMessageUseCase,
    private val getChatByIdUseCase: GetChatByIdUseCase,
    private val getAgentsUseCase: GetAgentsUseCase,
) {
    suspend fun getChatHistory(page: Int, pageSize: Int) = getChatHistoryUseCase(page, pageSize)

    suspend fun getChatMessagesById(id: String) = getChatMessagesByIdUseCase(id)

    suspend fun updateChatTitle(chatId: String, title: String) =
        updateChatTitleUseCase(chatId, title)

    suspend fun deleteChat(chatId: String) = deleteChatUseCase(chatId)

    suspend fun evaluateMessage(chatId: String, messageId: String, evaluation: Boolean) =
        evaluateMessageUseCase(chatId, messageId, evaluation)

    suspend fun getChatById(id: String) = getChatByIdUseCase(id)

    suspend fun getAgents() = getAgentsUseCase()
}

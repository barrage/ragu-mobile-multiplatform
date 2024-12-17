package net.barrage.ragu.data.repository

import io.ktor.client.statement.HttpResponse
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.ChatRepository

class ChatRepositoryImpl(private val api: Api) : ChatRepository {
    override suspend fun updateChatTitle(chatId: String, title: String): Response<HttpResponse> =
        api.updateChatTitle(chatId, title)

    override suspend fun deleteChat(chatId: String): Response<HttpResponse> = api.deleteChat(chatId)

    override suspend fun evaluateMessage(
        chatId: String,
        messageId: String,
        evaluation: Boolean,
    ): Response<HttpResponse> = api.evaluateMessage(chatId, messageId, evaluation)
}

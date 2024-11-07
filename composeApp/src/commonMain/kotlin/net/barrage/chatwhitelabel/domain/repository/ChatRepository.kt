package net.barrage.chatwhitelabel.domain.repository

import io.ktor.client.statement.HttpResponse
import net.barrage.chatwhitelabel.domain.Response

interface ChatRepository {
    suspend fun updateChatTitle(chatId: String, title: String): Response<HttpResponse>
}

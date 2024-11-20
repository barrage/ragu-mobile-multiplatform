package net.barrage.chatwhitelabel.domain.repository

import io.ktor.http.Parameters
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.ChatHistoryItem
import net.barrage.chatwhitelabel.domain.model.ChatItem
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem

interface HistoryRepository {
    suspend fun getChats(parameters: Parameters): Response<ImmutableList<ChatHistoryItem>>

    suspend fun getChatMessagesById(chatId: String): Response<List<ChatMessageItem>>

    suspend fun getChatById(chatId: String): Response<ChatItem>
}

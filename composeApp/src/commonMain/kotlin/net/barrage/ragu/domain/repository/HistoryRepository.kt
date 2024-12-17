package net.barrage.ragu.domain.repository

import io.ktor.http.Parameters
import kotlinx.collections.immutable.ImmutableList
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.domain.model.ChatItem
import net.barrage.ragu.domain.model.ChatMessageItem

interface HistoryRepository {
    suspend fun getChats(parameters: Parameters): Response<ImmutableList<ChatHistoryItem>>

    suspend fun getChatMessagesById(chatId: String): Response<List<ChatMessageItem>>

    suspend fun getChatById(chatId: String): Response<ChatItem>
}

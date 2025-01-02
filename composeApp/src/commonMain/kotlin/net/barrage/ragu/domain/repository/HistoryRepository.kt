package net.barrage.ragu.domain.repository

import io.ktor.http.Parameters
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.domain.model.ChatItem
import net.barrage.ragu.domain.model.ChatMessageItem

interface HistoryRepository {
    suspend fun getChats(parameters: Parameters): Flow<Response<ImmutableList<ChatHistoryItem>>>

    suspend fun getChatMessagesById(chatId: String): Flow<Response<List<ChatMessageItem>>>

    suspend fun getChatById(chatId: String): Flow<Response<ChatItem>>
}

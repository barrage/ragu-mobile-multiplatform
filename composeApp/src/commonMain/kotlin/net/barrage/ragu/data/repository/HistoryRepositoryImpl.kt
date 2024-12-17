package net.barrage.ragu.data.repository

import io.ktor.http.Parameters
import kotlinx.collections.immutable.ImmutableList
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.mapper.toDomain
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.domain.model.ChatItem
import net.barrage.ragu.domain.model.ChatMessageItem
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val api: Api) : HistoryRepository {
    override suspend fun getChats(
        parameters: Parameters
    ): Response<ImmutableList<ChatHistoryItem>> {
        return when (val response = api.getHistory(parameters)) {
            is Response.Success -> Response.Success(response.data.toDomain())
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }

    override suspend fun getChatMessagesById(chatId: String): Response<List<ChatMessageItem>> {
        return when (val response = api.getChatMessagesById(chatId)) {
            is Response.Success ->
                Response.Success(response.data.map { it.toDomain() }.sortedBy { it.updatedAt })

            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }

    override suspend fun getChatById(chatId: String): Response<ChatItem> {
        return when (val response = api.getChatById(chatId)) {
            is Response.Success -> Response.Success(response.data.toDomain())
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}

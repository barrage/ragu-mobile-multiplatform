package net.barrage.ragu.data.repository

import io.ktor.http.Parameters
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.mapper.toDomain
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.domain.model.ChatItem
import net.barrage.ragu.domain.model.ChatMessageItem
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val api: Api) : HistoryRepository {
    override suspend fun getChats(parameters: Parameters): Flow<Response<ImmutableList<ChatHistoryItem>>> =
        flow {
            emit(Response.Loading)
            try {
                when (val response = api.getHistory(parameters)) {
                    is Response.Success -> emit(Response.Success(response.data.toDomain()))
                    is Response.Failure -> emit(response)
                    is Response.Unauthorized -> emit(response)
                    else -> emit(Response.Failure(Exception("Unexpected response type")))
                }
            } catch (e: Exception) {
                emit(Response.Failure(e))
            }
        }

    override suspend fun getChatMessagesById(chatId: String): Flow<Response<List<ChatMessageItem>>> =
        flow {
            emit(Response.Loading)
            try {
                when (val response = api.getChatMessagesById(chatId)) {
                    is Response.Success ->
                        emit(Response.Success(response.data.map { it.toDomain() }
                            .sortedBy { it.updatedAt }))

                    is Response.Failure -> emit(response)
                    is Response.Unauthorized -> emit(response)
                    else -> emit(Response.Failure(Exception("Unexpected response type")))
                }
            } catch (e: Exception) {
                emit(Response.Failure(e))
            }
        }

    override suspend fun getChatById(chatId: String): Flow<Response<ChatItem>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.getChatById(chatId)) {
                is Response.Success -> emit(Response.Success(response.data.toDomain()))
                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}
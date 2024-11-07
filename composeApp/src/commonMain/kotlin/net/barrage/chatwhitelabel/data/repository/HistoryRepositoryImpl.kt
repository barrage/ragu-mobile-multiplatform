package net.barrage.chatwhitelabel.data.repository

import io.ktor.http.Parameters
import net.barrage.chatwhitelabel.data.remote.ktor.Api
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.mapper.toDomain
import net.barrage.chatwhitelabel.domain.model.History
import net.barrage.chatwhitelabel.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val api: Api) : HistoryRepository {
    override suspend fun getChats(parameters: Parameters): Response<History> {
        return when (val response = api.getHistory(parameters)) {
            is Response.Success -> Response.Success(response.data.toDomain())
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}

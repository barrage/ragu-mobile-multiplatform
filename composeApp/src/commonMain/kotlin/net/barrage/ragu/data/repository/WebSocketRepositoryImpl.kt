package net.barrage.ragu.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.WebSocketToken
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.WebSocketRepository

class WebSocketRepositoryImpl(private val api: Api) : WebSocketRepository {
    override suspend fun getWebSocketToken(): Flow<Response<WebSocketToken>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.getWebSocketToken()) {
                is Response.Success -> emit(Response.Success(WebSocketToken(response.data)))
                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}
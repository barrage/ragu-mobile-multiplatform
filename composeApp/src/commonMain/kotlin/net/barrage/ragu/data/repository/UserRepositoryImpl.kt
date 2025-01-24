package net.barrage.ragu.data.repository

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.mapper.toDomain
import net.barrage.ragu.domain.model.CurrentUser
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.UserRepository

class UserRepositoryImpl(private val api: Api) : UserRepository {
    override suspend fun getCurrentUser(withAvatar: Boolean): Flow<Response<CurrentUser>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.getCurrentUser(withAvatar)) {
                is Response.Success -> emit(Response.Success(response.data.toDomain()))
                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun updateAvatar(avatar: ByteArray): Flow<Response<HttpResponse>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.updateAvatar(avatar)) {
                is Response.Success -> emit(Response.Success(response.data))
                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun deleteAvatar(): Flow<Response<HttpResponse>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.deleteAvatar()) {
                is Response.Success -> emit(Response.Success(response.data))
                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

}

package net.barrage.ragu.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.mapper.toDomain
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.AgentRepository

class AgentRepositoryImpl(private val api: Api) : AgentRepository {
    override suspend fun getAgents(): Flow<Response<List<Agent>>> = flow {
        emit(Response.Loading)
        try {
            when (val response = api.getAgents()) {
                is Response.Success -> emit(Response.Success(response.data.items.map { it.toDomain() }))
                is Response.Failure -> emit(response)
                is Response.Unauthorized -> emit(response)
                else -> emit(Response.Failure(Exception("Unexpected response type")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}
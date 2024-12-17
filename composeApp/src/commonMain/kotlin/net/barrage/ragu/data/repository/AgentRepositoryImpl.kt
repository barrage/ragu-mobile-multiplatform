package net.barrage.ragu.data.repository

import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.mapper.toDomain
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.AgentRepository

class AgentRepositoryImpl(private val api: Api) : AgentRepository {
    override suspend fun getAgents(): Response<List<Agent>> {
        return when (val response = api.getAgents()) {
            is Response.Success -> Response.Success(response.data.items.map { it.toDomain() })
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}

package net.barrage.chatwhitelabel.data.repository

import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.mapper.toDomain
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.remote.ktor.Api
import net.barrage.chatwhitelabel.domain.repository.AgentRepository

class AgentRepositoryImpl(private val api: Api) : AgentRepository {
    override suspend fun getAgents(): Response<List<Agent>> {
        return when (val response = api.getAgents()) {
            is Response.Success -> Response.Success(response.data.items.map { it.toDomain() })
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}

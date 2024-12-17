package net.barrage.ragu.domain.repository

import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.Agent

interface AgentRepository {
    suspend fun getAgents(): Response<List<Agent>>
}

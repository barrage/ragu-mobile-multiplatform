package net.barrage.ragu.domain.repository

import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.Agent

interface AgentRepository {
    suspend fun getAgents(): Flow<Response<List<Agent>>>
}

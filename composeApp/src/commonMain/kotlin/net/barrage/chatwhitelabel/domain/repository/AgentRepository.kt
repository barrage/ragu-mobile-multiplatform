package net.barrage.chatwhitelabel.domain.repository

import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.Agent

interface AgentRepository {
    suspend fun getAgents(): Response<List<Agent>>
}

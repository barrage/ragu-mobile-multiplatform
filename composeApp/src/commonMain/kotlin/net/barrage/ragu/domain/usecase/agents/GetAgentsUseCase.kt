package net.barrage.ragu.domain.usecase.agents

import net.barrage.ragu.domain.repository.AgentRepository

class GetAgentsUseCase(private val agentRepository: AgentRepository) {
    suspend operator fun invoke() = agentRepository.getAgents()
}

package net.barrage.chatwhitelabel.domain.usecase.agents

import net.barrage.chatwhitelabel.domain.repository.AgentRepository

class GetAgentsUseCase(private val agentRepository: AgentRepository) {
    suspend operator fun invoke() = agentRepository.getAgents()
}

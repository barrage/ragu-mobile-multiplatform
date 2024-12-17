package net.barrage.ragu.ui.components.chat

import net.barrage.ragu.domain.model.Agent

data class AgentItemState(
    val agent: Agent,
    val selectedAgent: Agent?,
    val onAgentClick: (Agent) -> Unit,
)

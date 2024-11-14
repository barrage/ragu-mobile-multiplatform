package net.barrage.chatwhitelabel.ui.components.chat

import net.barrage.chatwhitelabel.domain.model.Agent

data class AgentItemState(
    val agent: Agent,
    val selectedAgent: Agent?,
    val onAgentClick: (Agent) -> Unit,
)

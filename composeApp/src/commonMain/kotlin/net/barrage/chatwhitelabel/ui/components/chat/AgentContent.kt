package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_brain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.barrage.chatwhitelabel.domain.model.Agent
import org.jetbrains.compose.resources.painterResource

@Composable
fun AgentContent(
    agents: ImmutableList<Agent>,
    selectedAgent: Agent?,
    onAgentClick: (Agent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_brain),
                contentDescription = null,
                modifier = Modifier.size(80.dp).padding(12.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("LLM Chat", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Choose an agent to start a chat with", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Agents:", style = MaterialTheme.typography.bodyMedium)
        AgentList(
            agents =
                agents
                    .map { agent ->
                        AgentItemState(
                            agent = agent,
                            onAgentClick = { onAgentClick(agent) },
                            selectedAgent = selectedAgent,
                        )
                    }
                    .toImmutableList(),
            modifier = Modifier.weight(1f),
        )
    }
}

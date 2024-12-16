package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.agents_label
import chatwhitelabel.composeapp.generated.resources.chat_header
import chatwhitelabel.composeapp.generated.resources.chat_sub_header
import com.svenjacobs.reveal.RevealState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.ui.components.AppIconCard
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.stringResource

@Composable
fun AgentContent(
    agents: ImmutableList<Agent>,
    selectedAgent: Agent?,
    onAgentClick: (Agent) -> Unit,
    revealState: RevealState,
    scope: CoroutineScope,
    changeInputEnabled: (Boolean) -> Unit,
    shouldShowOnboardingTutorial: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        AppIconCard()
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(Res.string.chat_header),
            style = MaterialTheme.typography.headlineMedium.fixCenterTextOnAllPlatforms(),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(Res.string.chat_sub_header),
            style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(Res.string.agents_label),
            style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
        )
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
            revealState = revealState,
            scope = scope,
            changeInputEnabled = changeInputEnabled,
            shouldShowOnboardingTutorial = shouldShowOnboardingTutorial,
            modifier = Modifier.weight(1f),
        )
    }
}

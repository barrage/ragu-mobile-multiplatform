package net.barrage.ragu.ui.components.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.reveal.RevealKeys
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.ic_chat_agent

@Composable
fun AgentItem(
    state: AgentItemState,
    revealState: RevealState,
    scope: CoroutineScope,
    index: Int,
    modifier: Modifier = Modifier
) {

    Card(
        shape = RoundedCornerShape(12.dp),
        onClick = { state.onAgentClick(state.agent) },
        modifier = modifier.then(
            if (index == 0) Modifier.revealable(
                key = RevealKeys.AgentItem,
                state = revealState,
                shape = RevealShape.RoundRect(12.dp),
                onClick = {
                    scope.launch {
                        revealState.hide()
                        delay(1000)
                        revealState.reveal(RevealKeys.ChatInput)
                    }
                },
            ) else Modifier
        ),
        colors =
        CardDefaults.cardColors(
            containerColor =
            if (state.selectedAgent == state.agent)
                MaterialTheme.colorScheme.surfaceContainerHighest
            else MaterialTheme.colorScheme.surfaceContainer
        ),
        border = CardDefaults.outlinedCardBorder(enabled = state.selectedAgent == state.agent),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chat_agent),
                    contentDescription = null,
                )
                Text(
                    text = state.agent.name,
                    style = MaterialTheme.typography.titleSmall.fixCenterTextOnAllPlatforms(),
                )
            }
            if (!state.agent.description.isNullOrEmpty()) {
                AnimatedVisibility(state.selectedAgent == state.agent) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.agent.description,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodySmall.fixCenterTextOnAllPlatforms(),
                        )
                    }
                }
            }
        }
    }
}

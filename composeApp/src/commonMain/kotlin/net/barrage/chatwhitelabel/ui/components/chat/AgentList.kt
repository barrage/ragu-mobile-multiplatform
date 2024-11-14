package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AgentList(agents: ImmutableList<AgentItemState>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(20.dp),
    ) {
        if (agents.isEmpty()) {
            item {
                Text(
                    "Currently, there are no available agents for conversation. " +
                        "To start a chat, it is necessary to add or enable at least one agent. " +
                        "Please contact your system administrator or check agent settings to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            items(agents) { agent -> AgentItem(agent, modifier = Modifier) }
        }
    }
}

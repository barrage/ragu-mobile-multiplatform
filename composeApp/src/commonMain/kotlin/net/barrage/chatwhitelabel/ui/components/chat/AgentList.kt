package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.no_agents_available
import com.svenjacobs.reveal.RevealState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import net.barrage.chatwhitelabel.ui.components.reveal.RevealKeys
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun AgentList(
    agents: ImmutableList<AgentItemState>,
    revealState: RevealState,
    scope: CoroutineScope,
    changeInputEnabled: (Boolean) -> Unit,
    shouldShowOnboardingTutorial: Boolean,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(agents) {
        if (agents.isNotEmpty()) {
            if (revealState.isVisible || shouldShowOnboardingTutorial.not()) return@LaunchedEffect
            changeInputEnabled(false)
            delay(1.seconds)
            revealState.reveal(RevealKeys.AgentItem)
        }
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(20.dp),
    ) {
        if (agents.isEmpty()) {
            item {
                Text(
                    stringResource(Res.string.no_agents_available),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            itemsIndexed(agents) { index, agent ->
                AgentItem(
                    state = agent,
                    revealState = revealState,
                    scope = scope,
                    index = index,
                    modifier = Modifier,
                )
            }
        }
    }
}

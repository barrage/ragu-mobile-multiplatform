package net.barrage.ragu.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.RevealState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import net.barrage.ragu.ui.components.reveal.RevealKeys
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.no_agents_available
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgentList(
    agents: ImmutableList<AgentItemState>,
    revealState: RevealState,
    scope: CoroutineScope,
    changeInputEnabled: (Boolean) -> Unit,
    shouldShowOnboardingTutorial: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
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

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
}
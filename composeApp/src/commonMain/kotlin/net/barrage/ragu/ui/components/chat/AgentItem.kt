package net.barrage.ragu.ui.components.chat

import RaguMultiplatform.composeApp.BuildConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.skydoves.landscapist.ImageOptions
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.CardVariant
import net.barrage.ragu.ui.components.CustomCard
import net.barrage.ragu.ui.components.NetworkImage
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
    CustomCard(
        cardVariant = CardVariant.Secondary(isSelected = state.selectedAgentId == state.agent.id),
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
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CustomCard(
                    cardVariant = CardVariant.Secondary(),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(28.dp)
                        .zIndex(1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.agent.avatarId != null) {
                            NetworkImage(
                                imageModel = { "https://${BuildConfig.BASE_URL}/avatars/${state.agent.avatarId}" },
                                imageOptions = ImageOptions(
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center
                                ),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chat_agent),
                                contentDescription = "Agent",
                                modifier = Modifier.size(20.dp).align(Alignment.Center),
                            )
                        }
                    }
                }

                Text(
                    text = state.agent.name,
                    style = MaterialTheme.typography.titleSmall.fixCenterTextOnAllPlatforms(),
                )
            }
            if (!state.agent.description.isNullOrEmpty()) {
                AnimatedVisibility(state.selectedAgentId == state.agent.id) {
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

package net.barrage.ragu.ui.screens.history.components.currentuser

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.reveal.RevealKeys
import net.barrage.ragu.ui.screens.history.HistoryScreenStates
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.error_loading_data

@Composable
fun CurrentUserCard(
    onUserClick: () -> Unit,
    onUnauthorized: () -> Unit,
    viewState: HistoryScreenStates<ProfileViewState>,
    revealState: RevealState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier.padding(16.dp)
            .animateContentSize()
            .revealable(
                key = RevealKeys.Account,
                shape = RevealShape.RoundRect(12.dp),
                state = revealState,
                onClick = {
                    scope.launch {
                        revealState.hide()
                        delay(1000)
                        revealState.reveal(RevealKeys.MenuClose)
                    }
                },
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            when (viewState) {
                is HistoryScreenStates.Error -> {
                    Text(
                        text = stringResource(Res.string.error_loading_data),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                        modifier = Modifier.padding(8.dp),
                    )
                }

                is HistoryScreenStates.Idle -> {}

                is HistoryScreenStates.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).padding(vertical = 8.dp)
                            .then(Modifier.size(28.dp))
                    )

                }

                is HistoryScreenStates.Success<ProfileViewState> -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                        Modifier.clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceDim)
                            .clickable(onClick = onUserClick)
                            .padding(8.dp),
                    ) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "user profile")
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewState.data.header.name,
                                style =
                                MaterialTheme.typography.labelLarge
                                    .fixCenterTextOnAllPlatforms(),
                            )
                            Text(
                                text = viewState.data.email,
                                style =
                                MaterialTheme.typography.bodySmall
                                    .fixCenterTextOnAllPlatforms(),
                                overflow = Ellipsis,
                            )
                        }
                    }
                }

                is HistoryScreenStates.Unauthorized -> {
                    LaunchedEffect(Unit) {
                        onUnauthorized()
                    }
                }
            }
        }
    }
}

package net.barrage.chatwhitelabel.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.profile.components.ProfileCardHeader
import net.barrage.chatwhitelabel.ui.screens.profile.components.ProfileContent
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun ProfileCard(
    viewState: HistoryScreenStates<ProfileViewState>,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { /* No-op, as we don't want to dismiss this alert */ },
        title = {},
        text = {
            when (viewState) {
                HistoryScreenStates.Error -> {
                    Text(
                        text = "Error loading data.",
                        style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp),
                    )
                }

                HistoryScreenStates.Idle -> {}

                HistoryScreenStates.Loading -> {
                    CircularProgressIndicator(modifier = Modifier)
                }

                is HistoryScreenStates.Success -> {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "close",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.clip(CircleShape).clickable { onCloseClick() },
                            )
                        }
                        ProfileSpacer()
                        ProfileCardHeader(
                            modifier = Modifier.padding(vertical = 16.dp),
                            viewState = viewState.data.header,
                        )
                        ProfileSpacer()
                        ProfileContent(viewState = viewState.data.content)
                        ProfileSpacer()
                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier.padding(top = 16.dp).align(Alignment.End),
                        ) {
                            Text(
                                text = "Logout",
                                style =
                                    MaterialTheme.typography.titleMedium
                                        .fixCenterTextOnAllPlatforms(),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { /* No buttons needed */ },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    )
}

@Composable
fun ProfileSpacer(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Spacer(Modifier.background(MaterialTheme.colorScheme.outline).height(1.dp).fillMaxWidth())
    }
}

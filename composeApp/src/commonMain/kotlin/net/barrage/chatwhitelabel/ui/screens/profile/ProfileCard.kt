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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.profile.components.ProfileCardHeader
import net.barrage.chatwhitelabel.ui.screens.profile.components.ProfileContent
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileViewState

@Composable
fun ProfileCard(
    viewState: HistoryScreenStates<ProfileViewState>,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
) {
    Box(modifier = modifier) {
        when (viewState) {
            HistoryScreenStates.Error -> {
                Text(
                    text = "Error loading data.",
                    color = Red,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(8.dp),
                )
            }

            HistoryScreenStates.Idle -> {}

            HistoryScreenStates.Loading -> {
                Text(
                    text = "Loading...",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(8.dp),
                ) // TODO loader
            }

            is HistoryScreenStates.Success -> {
                Card(modifier = Modifier.clip(RoundedCornerShape(12.dp))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Spacer(modifier = Modifier)
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "close",
                                modifier = Modifier.clip(CircleShape).clickable { onCloseClick() },
                            )
                        }
                        ProfileSpacer()
                        ProfileCardHeader(viewState = viewState.data.header)
                        ProfileSpacer()
                        ProfileContent(viewState = viewState.data.content)
                        ProfileSpacer()
                        Button(onClick = onLogoutClick, modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSpacer(modifier: Modifier = Modifier) {
    Box(modifier.padding(top = 16.dp)) {
        Spacer(
            Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer)
                .height(1.dp)
                .fillMaxWidth()
        )
    }
}

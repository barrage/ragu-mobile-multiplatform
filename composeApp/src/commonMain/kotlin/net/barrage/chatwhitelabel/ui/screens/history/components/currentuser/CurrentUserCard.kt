package net.barrage.chatwhitelabel.ui.screens.history.components.currentuser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileViewState

@Composable
fun CurrentUserCard(
    viewState: HistoryScreenStates<ProfileViewState>,
    modifier: Modifier = Modifier,
    onUserClick: () -> Unit,
) {
    Card(modifier.padding(16.dp)) {
        Box(modifier = Modifier) {
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
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = viewState.data.content["Email"]?.value ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                overflow = Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

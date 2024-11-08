package net.barrage.chatwhitelabel.ui.screens.history.components.currentuser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.domain.model.CurrentUser
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates

@Composable
fun CurrentUserCard(
    viewState: HistoryScreenStates<CurrentUser>,
    modifier: Modifier = Modifier,
    onUserClick: () -> Unit,
) {
    Card(modifier.padding(8.dp).clip(RoundedCornerShape(12.dp)).clickable { onUserClick() }) {
        Box(modifier = Modifier.padding(8.dp)) {
            when (viewState) {
                HistoryScreenStates.Error -> {
                    Text(text = "Error loading data.", color = Red, fontSize = 24.sp)
                }

                HistoryScreenStates.Idle -> {}
                HistoryScreenStates.Loading -> {
                    Text(text = "Loading...", fontSize = 24.sp) // TODO loader
                }

                is HistoryScreenStates.Success<CurrentUser> -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "user profile")
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = viewState.data.fullName, fontSize = 20.sp)
                            Text(text = viewState.data.email, fontSize = 12.sp, overflow = Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

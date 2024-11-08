package net.barrage.chatwhitelabel.ui.screens.profile.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileHeaderViewState

@Composable
fun ProfileHeader(viewState: ProfileHeaderViewState, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            viewState.profileImage,
            "user profile image",
            modifier =
                Modifier.size(50.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clip(CircleShape),
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = viewState.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            StatusIndicator(viewState.active)
        }
    }
}

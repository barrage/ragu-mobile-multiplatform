package net.barrage.ragu.ui.screens.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileHeaderViewState
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms

@Composable
fun ProfileCardHeader(viewState: ProfileHeaderViewState, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Card(shape = CircleShape) {
            Box(modifier = Modifier.padding(12.dp)) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = viewState.name,
                style = MaterialTheme.typography.headlineMedium.fixCenterTextOnAllPlatforms(),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            StatusIndicator(viewState.active)
        }
    }
}

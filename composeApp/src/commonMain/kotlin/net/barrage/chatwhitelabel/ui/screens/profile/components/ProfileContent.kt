package net.barrage.chatwhitelabel.ui.screens.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableMap
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileContentItem
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileContent(
    viewState: ImmutableMap<String, ProfileContentItem>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        viewState.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp),
            ) {
                Icon(
                    painter = painterResource(it.value.iconId),
                    contentDescription = it.value.iconDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = it.key + ":",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = it.value.value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

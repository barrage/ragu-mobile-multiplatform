package net.barrage.chatwhitelabel.ui.screens.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableMap
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileContentItem
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileContent(
    viewState: ImmutableMap<StringResource, ProfileContentItem>,
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
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(it.key) + ":",
                    style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms(),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = it.value.value,
                    style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

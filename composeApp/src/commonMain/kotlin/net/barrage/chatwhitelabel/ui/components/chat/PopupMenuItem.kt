package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun PopupMenuItem(item: PopupMenuItemState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable(onClick = item.onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(item.icon, contentDescription = null)
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodySmall.fixCenterTextOnAllPlatforms(),
        )
    }
}

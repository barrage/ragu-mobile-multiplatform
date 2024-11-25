package net.barrage.chatwhitelabel.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(onMenuClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        IconButton(onClick = onMenuClick, modifier = Modifier.height(36.dp)) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.height(36.dp),
            )
        }
    }
}

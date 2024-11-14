package net.barrage.chatwhitelabel.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_brain
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppIconCard(modifier: Modifier = Modifier) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_brain),
            contentDescription = null,
            modifier = Modifier.size(80.dp).padding(12.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

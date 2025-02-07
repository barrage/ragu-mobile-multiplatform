package net.barrage.ragu.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.reveal.RevealKeys


@Composable
fun TopBar(
    onMenuClick: () -> Unit,
    revealState: RevealState,
    scope: CoroutineScope,
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        CustomIconButton(
            onClick = onMenuClick,
            modifier = Modifier.padding(horizontal = 8.dp).height(36.dp).revealable(
                key = RevealKeys.Menu,
                shape = RevealShape.Circle,
                state = revealState,
                onClick = {
                    scope.launch {
                        revealState.hide()
                        delay(1000)
                        drawerState.open()
                        revealState.reveal(RevealKeys.MenuTheme)
                    }
                },
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = null,
                modifier = Modifier.height(36.dp),
            )
        }
    }
}





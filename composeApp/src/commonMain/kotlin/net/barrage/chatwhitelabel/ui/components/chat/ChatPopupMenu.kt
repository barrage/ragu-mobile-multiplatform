package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlin.math.roundToInt

@Composable
fun ChatPopupMenu(state: ChatPopupMenuState, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val popupOffset =
        remember(state.iconPositionInRoot, state.iconPositionInParent) {
            with(density) {
                IntOffset(
                    x = state.iconPositionInRoot.x.roundToInt() - 150,
                    y = (state.iconPositionInParent.y + 48.dp.toPx()).roundToInt(),
                )
            }
        }

    Popup(onDismissRequest = state.onDismiss, offset = popupOffset) {
        AnimatedVisibility(
            visible = state.visible,
            enter =
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 300),
                ),
            exit =
                slideOutVertically(
                    targetOffsetY = { -2 * it },
                    animationSpec = tween(durationMillis = 300),
                ),
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = modifier.widthIn(min = 0.dp, max = 300.dp),
            ) {
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    state.menuItems.forEach { item ->
                        PopupMenuItem(
                            item,
                            modifier = Modifier.fillMaxWidth().align(Alignment.Start),
                        )
                    }
                }
            }
        }
    }
}

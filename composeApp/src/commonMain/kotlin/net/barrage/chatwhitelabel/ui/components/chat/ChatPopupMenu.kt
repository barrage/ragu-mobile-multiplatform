package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider

@Composable
fun ChatPopupMenu(state: ChatPopupMenuState, modifier: Modifier = Modifier) {
    Popup(
        onDismissRequest = state.onDismiss,
        popupPositionProvider =
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ): IntOffset {
                    val x = anchorBounds.center.x - (popupContentSize.width / 2)
                    val y = anchorBounds.bottom
                    return IntOffset(x, y)
                }
            },
    ) {
        AnimatedVisibility(
            visible = state.visible,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = modifier.widthIn(min = 0.dp, max = 200.dp).padding(12.dp),
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

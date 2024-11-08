package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MessageList(
    messages: ImmutableList<String>,
    lazyListState: LazyListState,
    chatInteractionSource: MutableInteractionSource,
    chatInputFocused: Boolean,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier.fillMaxSize().clickable(chatInteractionSource, null) {
                if (chatInputFocused) focusManager.clearFocus()
            },
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        state = lazyListState,
    ) {
        itemsIndexed(messages) { index, item ->
            MessageItem(message = item, isUserMessage = index % 2 == 0)
        }
    }
}

package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem

@Composable
fun MessageList(
    messages: ImmutableList<ChatMessageItem>,
    lazyListState: LazyListState,
    onCopy: (ChatMessageItem) -> Unit,
    onPositiveEvaluation: (ChatMessageItem) -> Unit,
    onNegativeEvaluation: (ChatMessageItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        state = lazyListState,
    ) {
        items(messages) { item ->
            MessageItem(
                chatMessage = item,
                onCopy = onCopy,
                onPositiveEvaluation = onPositiveEvaluation,
                onNegativeEvaluation = onNegativeEvaluation,
                modifier = Modifier,
            )
        }
        item { Spacer(modifier = Modifier.height(1.dp)) }
    }
}

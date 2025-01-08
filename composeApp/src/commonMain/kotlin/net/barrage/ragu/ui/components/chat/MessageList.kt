package net.barrage.ragu.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import net.barrage.ragu.domain.model.ChatMessageItem

@Composable
fun MessageList(
    onScrollToTop: () -> Unit,
    onCopy: (ChatMessageItem) -> Unit,
    onPositiveEvaluation: (ChatMessageItem) -> Unit,
    onNegativeEvaluation: (ChatMessageItem) -> Unit,
    messages: ImmutableList<ChatMessageItem>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        state = lazyListState,
        reverseLayout = true,
    ) {
        items(messages.reversed()) { item ->
            MessageItem(
                chatMessage = item,
                onCopy = onCopy,
                onPositiveEvaluation = onPositiveEvaluation,
                onNegativeEvaluation = onNegativeEvaluation,
                modifier = Modifier,
            )
        }
        item {
            LaunchedEffect(Unit) {
                onScrollToTop()
            }
        }
    }
}

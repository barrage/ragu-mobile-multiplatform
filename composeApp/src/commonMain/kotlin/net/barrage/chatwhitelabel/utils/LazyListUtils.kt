package net.barrage.chatwhitelabel.utils

import androidx.compose.foundation.lazy.LazyListState

/**
 * Checks if the LazyListState has reached the end of the list.
 *
 * @return True if the last visible item is the last item in the list, false otherwise.
 */
fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
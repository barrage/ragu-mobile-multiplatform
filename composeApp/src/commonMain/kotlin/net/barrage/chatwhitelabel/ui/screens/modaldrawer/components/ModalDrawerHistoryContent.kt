package net.barrage.chatwhitelabel.ui.screens.modaldrawer.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.history.ModalDrawerHistoryElement

@Composable
fun ModalDrawerHistoryContent(
    viewState: ImmutableList<ModalDrawerHistoryViewState>,
    modifier: Modifier = Modifier,
    onElementClick: (String) -> Unit,
) {
    LazyColumn(modifier) {
        items(viewState) { ModalDrawerHistoryElement(viewState = it, onClick = onElementClick) }
    }
}

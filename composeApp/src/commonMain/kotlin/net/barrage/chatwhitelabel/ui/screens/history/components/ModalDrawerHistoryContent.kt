package net.barrage.chatwhitelabel.ui.screens.history.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.domain.model.History
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.history.components.history.ModalDrawerHistoryElement

@Composable
fun ModalDrawerHistoryContent(
    viewState: HistoryScreenStates<History>,
    modifier: Modifier = Modifier,
    onElementClick: (String) -> Unit,
) {
    Box(modifier = modifier) {
        when (viewState) {
            HistoryScreenStates.Error -> {
                Text(text = "Error loading data.", color = Red, fontSize = 24.sp)
            }

            HistoryScreenStates.Idle -> {}

            HistoryScreenStates.Loading -> {
                Text(text = "Loading...", fontSize = 24.sp) // TODO loader
            }

            is HistoryScreenStates.Success<History> -> {
                if (viewState.data.elements.isEmpty()) {
                    Text(text = "You have no previous chats.", fontSize = 24.sp)
                } else {
                    LazyColumn {
                        items(viewState.data.elements) {
                            ModalDrawerHistoryElement(viewState = it, onClick = onElementClick)
                        }
                    }
                }
            }
        }
    }
}

package net.barrage.chatwhitelabel.ui.screens.history.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.domain.model.HistoryElement
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.history.HistoryViewState
import net.barrage.chatwhitelabel.ui.screens.history.components.history.ModalDrawerHistoryElement

@Composable
fun ModalDrawerHistoryContent(
    viewState: HistoryScreenStates<HistoryViewState>,
    modifier: Modifier = Modifier,
    onElementClick: (HistoryElement) -> Unit,
) {
    Box(modifier = modifier) {
        when (viewState) {
            HistoryScreenStates.Error -> {
                Text(
                    text = "Error loading data.",
                    color = Red,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }

            HistoryScreenStates.Idle -> {}

            HistoryScreenStates.Loading -> {
                Text(
                    text = "Loading...",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp),
                ) // TODO loader
            }

            is HistoryScreenStates.Success<HistoryViewState> -> {
                if (viewState.data.elements.isEmpty()) {
                    Text(
                        text = "You have no previous chats.",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp),
                    )
                } else {
                    LazyColumn(modifier = Modifier) {
                        viewState.data.elements.forEach { (timePeriod, elements) ->
                            if (timePeriod != null && !elements.isEmpty()) {
                                item {
                                    Column(
                                        modifier =
                                            Modifier.padding(horizontal = 8.dp).padding(top = 8.dp)
                                    ) {
                                        Text(
                                            text = timePeriod,
                                            style =
                                                MaterialTheme.typography.labelLarge.copy(
                                                    fontSize = 16.sp
                                                ),
                                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                                        )
                                    }
                                }

                                items(elements) { element ->
                                    ModalDrawerHistoryElement(
                                        viewState = element,
                                        onClick = onElementClick,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

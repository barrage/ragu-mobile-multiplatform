package net.barrage.chatwhitelabel.ui.screens.history.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.domain.model.HistoryElement
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.history.HistoryViewState
import net.barrage.chatwhitelabel.ui.screens.history.components.history.ModalDrawerHistoryElement
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

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
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }

            HistoryScreenStates.Idle -> {}

            HistoryScreenStates.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            is HistoryScreenStates.Success<HistoryViewState> -> {
                if (viewState.data.elements.isEmpty()) {
                    Text(
                        text = "You have no previous chats.",
                        style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
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
                                                MaterialTheme.typography.labelMedium
                                                    .fixCenterTextOnAllPlatforms(),
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

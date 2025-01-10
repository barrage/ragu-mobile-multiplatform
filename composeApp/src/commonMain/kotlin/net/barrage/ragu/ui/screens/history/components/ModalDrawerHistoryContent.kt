package net.barrage.ragu.ui.screens.history.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.ui.screens.history.HistoryScreenStates
import net.barrage.ragu.ui.screens.history.HistoryViewState
import net.barrage.ragu.ui.screens.history.components.history.ModalDrawerHistoryElement
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.error_loading_data
import ragumultiplatform.composeapp.generated.resources.no_previous_chats

@Composable
fun ModalDrawerHistoryContent(
    viewState: HistoryScreenStates<HistoryViewState>,
    modifier: Modifier = Modifier,
    onElementClick: (ChatHistoryItem) -> Unit,
    onScrollToBottom: () -> Unit,
    onUnauthorized: () -> Unit,
) {
    Box(modifier = modifier) {
        when (viewState) {
            is HistoryScreenStates.Error -> {
                Text(
                    text = stringResource(Res.string.error_loading_data),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }

            is HistoryScreenStates.Idle -> {}

            is HistoryScreenStates.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            is HistoryScreenStates.Success<HistoryViewState> -> {
                if (viewState.data.elements.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.no_previous_chats),
                        style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                        modifier = Modifier.padding(16.dp),
                    )
                } else {
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                    ) {
                        viewState.data.elements.forEach { (timePeriod, elements) ->
                            if (timePeriod != null && elements.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .padding(top = 8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(timePeriod),
                                            style = MaterialTheme.typography.labelMedium
                                                .fixCenterTextOnAllPlatforms()
                                                .copy(color = MaterialTheme.colorScheme.outline),
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
                                item {
                                    LaunchedEffect(Unit) {
                                        onScrollToBottom()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is HistoryScreenStates.Unauthorized -> {
                onUnauthorized()
            }
        }
    }
}

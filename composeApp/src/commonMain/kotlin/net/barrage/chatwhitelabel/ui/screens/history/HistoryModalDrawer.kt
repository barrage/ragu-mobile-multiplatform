package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerContentTopBar
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerHistoryContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ModalDrawer(
    theme: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel(),
    onSelectThemeClick: (Color) -> Unit = {},
    onDarkLightModeClick: () -> Unit = {},
    onElementClick: (String) -> Unit = {},
) {
    ModalDrawerSheet(modifier = modifier.fillMaxWidth().padding(end = 75.dp)) {
        Column {
            ModalDrawerContentTopBar(
                viewState = viewModel.historyViewState,
                theme = theme,
                isDarkMode = isDarkMode,
                modifier = Modifier.padding(vertical = 8.dp),
                onSelectThemeClick = onSelectThemeClick,
                onDarkLightModeClick = onDarkLightModeClick,
            )
            Divider(
                Modifier.padding(vertical = 8.dp, horizontal = 8.dp).background(Gray).height(1.dp)
            )
            when (val historyState = viewModel.historyViewState.history) {
                HistoryScreenState.Error -> {
                    Text(text = "Error loading data.", color = Red, fontSize = 24.sp)
                }

                HistoryScreenState.Idle -> {}
                HistoryScreenState.Loading -> {
                    Text(text = "Loading...", fontSize = 24.sp) // TODO loader
                }

                is HistoryScreenState.Success -> {
                    if (historyState.data.elements.isEmpty()) {
                        Text(text = "You have no previous chats.", fontSize = 24.sp)
                    } else {
                        ModalDrawerHistoryContent(
                            viewState = historyState.data.elements,
                            onElementClick = onElementClick,
                        )
                    }
                }
            }
        }
    }
}

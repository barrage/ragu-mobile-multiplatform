package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerContentTopBar
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerHistoryContent
import net.barrage.chatwhitelabel.ui.screens.history.components.currentuser.CurrentUserCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ModalDrawer(
    theme: Color,
    isDarkMode: Boolean,
    onSelectThemeClick: (Color) -> Unit,
    onDarkLightModeClick: () -> Unit,
    onUserClick: () -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
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
            ModalDrawerHistoryContent(
                modifier = Modifier.weight(1f),
                viewState = viewModel.historyViewState.history,
                onElementClick = {
                    viewModel.getHistoryChatById(id = it.id, title = it.title)
                    scope.launch { drawerState.close() }
                },
            )
            Divider(
                Modifier.padding(vertical = 8.dp, horizontal = 8.dp).background(Gray).height(1.dp)
            )
            CurrentUserCard(
                modifier = Modifier.fillMaxWidth(),
                viewState = viewModel.historyViewState.currentUser,
                onUserClick = onUserClick,
            )
        }
    }
}

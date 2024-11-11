package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.PaletteStyle
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerContentTopBar
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerHistoryContent
import net.barrage.chatwhitelabel.ui.screens.history.components.currentuser.CurrentUserCard

@Composable
fun ModalDrawer(
    currentTheme: Color,
    currentVariant: PaletteStyle,
    isDarkMode: Boolean,
    viewModel: ChatViewModel,
    onSelectThemeClick: (Color) -> Unit,
    onSelectVariantClick: (PaletteStyle) -> Unit,
    onDarkLightModeClick: () -> Unit,
    onUserClick: () -> Unit,
    changeDrawerVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    ModalDrawerSheet(modifier = modifier.fillMaxWidth().padding(end = 75.dp)) {
        Column {
            ModalDrawerContentTopBar(
                viewState = viewModel.historyViewState,
                currentTheme = currentTheme,
                currentVariant = currentVariant,
                isDarkMode = isDarkMode,
                modifier = Modifier.padding(vertical = 16.dp),
                onSelectThemeClick = onSelectThemeClick,
                onSelectVariantClick = onSelectVariantClick,
                onDarkLightModeClick = onDarkLightModeClick,
            )
            Divider(Modifier.background(Gray).height(1.dp))
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .clickable {
                            viewModel.newChat()
                            changeDrawerVisibility()
                        }
                        .padding(16.dp)
            ) {
                Text("New Chat", fontSize = 24.sp, textAlign = TextAlign.Start)
            }
            Divider(Modifier.background(Gray).height(1.dp))
            ModalDrawerHistoryContent(
                modifier = Modifier.weight(1f),
                viewState = viewModel.historyViewState.history,
                onElementClick = {
                    viewModel.getHistoryChatById(id = it.id, title = it.title)
                    scope.launch { drawerState.close() }
                },
            )
            Divider(Modifier.background(Gray).height(1.dp))
            CurrentUserCard(
                modifier = Modifier.fillMaxWidth(),
                viewState = viewModel.currentUserViewState,
                onUserClick = onUserClick,
            )
        }
    }
}

package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_brain
import com.materialkolor.PaletteStyle
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerContentTopBar
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerHistoryContent
import net.barrage.chatwhitelabel.ui.screens.history.components.currentuser.CurrentUserCard
import org.jetbrains.compose.resources.painterResource

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
            HistoryDivider()
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .clickable {
                            viewModel.newChat()
                            changeDrawerVisibility()
                        }
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painter = painterResource(Res.drawable.ic_brain), null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Chat", fontSize = 24.sp, textAlign = TextAlign.Start)
            }
            HistoryDivider()
            ModalDrawerHistoryContent(
                modifier = Modifier.weight(1f),
                viewState = viewModel.historyViewState.history,
                onElementClick = {
                    viewModel.getHistoryChatById(id = it.id, title = it.title)
                    changeDrawerVisibility()
                },
            )
            HistoryDivider()
            CurrentUserCard(
                modifier = Modifier.fillMaxWidth(),
                viewState = viewModel.currentUserViewState,
                onUserClick = onUserClick,
            )
        }
    }
}

@Composable
fun HistoryDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier.height(1.dp))
}

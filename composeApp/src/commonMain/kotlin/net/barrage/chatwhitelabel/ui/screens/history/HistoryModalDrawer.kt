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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_brain
import chatwhitelabel.composeapp.generated.resources.new_chat
import com.materialkolor.PaletteStyle
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.ui.components.reveal.RevealKeys
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerContentTopBar
import net.barrage.chatwhitelabel.ui.screens.history.components.ModalDrawerHistoryContent
import net.barrage.chatwhitelabel.ui.screens.history.components.currentuser.CurrentUserCard
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
    revealState: RevealState,
    scope: CoroutineScope,
    inputEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val historyViewState by viewModel.historyViewState.collectAsState()
    ModalDrawerSheet(modifier = modifier) {
        Column {
            ModalDrawerContentTopBar(
                viewState = historyViewState,
                currentTheme = currentTheme,
                currentVariant = currentVariant,
                isDarkMode = isDarkMode,
                onChangeDrawerVisibility = changeDrawerVisibility,
                modifier = Modifier.padding(start = 10.dp, bottom = 16.dp, end = 20.dp),
                onSelectThemeClick = onSelectThemeClick,
                onSelectVariantClick = onSelectVariantClick,
                onDarkLightModeClick = onDarkLightModeClick,
                revealState = revealState,
                inputEnabled = inputEnabled,
                scope = scope,
            )
            Column(
                modifier = Modifier.revealable(
                    key = RevealKeys.MenuNewChat,
                    shape = RevealShape.RoundRect(12.dp),
                    state = revealState,
                    onClick = {
                        scope.launch {
                            revealState.hide()
                            delay(1000)
                            revealState.reveal(RevealKeys.MenuHistory)
                        }
                    },
                )
            ) {
                HistoryDivider()
                Row(
                    modifier =
                    Modifier.fillMaxWidth()
                        .clickable {
                            if (inputEnabled) {
                                viewModel.newChat()
                                changeDrawerVisibility()
                            }
                        }
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painter = painterResource(Res.drawable.ic_brain), null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(Res.string.new_chat),
                        style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms(),
                    )
                }
                HistoryDivider()
            }
            ModalDrawerHistoryContent(
                modifier = Modifier.weight(1f).fillMaxWidth().revealable(
                    key = RevealKeys.MenuHistory,
                    shape = RevealShape.RoundRect(12.dp),
                    state = revealState,
                    onClick = {
                        scope.launch {
                            revealState.hide()
                            delay(1000)
                            revealState.reveal(RevealKeys.Account)
                        }
                    },
                ),
                viewState = historyViewState.history,
                onElementClick = {
                    if (inputEnabled) {
                        viewModel.getChatById(id = it.id, title = it.title)
                        changeDrawerVisibility()
                    }
                },
                onScrollToBottom = { viewModel.loadMoreHistory() },
            )
            HistoryDivider()
            val currentUserViewState by viewModel.currentUserViewState.collectAsState()
            CurrentUserCard(
                modifier = Modifier.fillMaxWidth(),
                viewState = currentUserViewState,
                onUserClick = if (inputEnabled) {
                    onUserClick
                } else {
                    {}
                },
                revealState = revealState,
                scope = scope,
            )
        }
    }
}

@Composable
fun HistoryDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier.height(1.dp))
}

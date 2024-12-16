package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_check
import chatwhitelabel.composeapp.generated.resources.ic_three_dots
import chatwhitelabel.composeapp.generated.resources.popup_menu_delete_chat
import chatwhitelabel.composeapp.generated.resources.popup_menu_edit_title
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.ui.components.TypewriterText
import net.barrage.chatwhitelabel.ui.components.TypewriterTextState
import net.barrage.chatwhitelabel.ui.components.reveal.RevealKeys
import net.barrage.chatwhitelabel.utils.AppContext.coreComponent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatTitle(
    state: ChatTitleState,
    revealState: RevealState,
    scope: CoroutineScope,
    maxWidth: Dp,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        if (coreComponent.appPreferences.shouldShowChatTitleTutorial()) {
            delay(1000)
            revealState.reveal(RevealKeys.ChatTitle)
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 20.dp),
    ) {
        TypewriterText(
            state =
            TypewriterTextState(
                text = state.title,
                isEditing = state.isEditingTitle,
                onTextChange = state.onTitleChange,
                textStyle = MaterialTheme.typography.titleMedium,
                textColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.widthIn(min = 0.dp, max = maxWidth - 24.dp),
        )
        Column {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(
                    onClick =
                    if (state.isEditingTitle) state.onTitleChangeConfirmation
                    else state.onThreeDotsClick,
                    modifier =
                    Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp).size(24.dp)
                        .revealable(
                            key = RevealKeys.ChatTitle,
                            state = revealState,
                            shape = RevealShape.Circle,
                            onClick = {
                                scope.launch {
                                    revealState.hide()
                                    coreComponent.appPreferences.saveShouldShowChatTitleTutorial(
                                        false
                                    )
                                }
                            },
                        ),
                    enabled = state.title.length in 3..255,
                ) {
                    Icon(
                        painter =
                        painterResource(
                            if (state.isEditingTitle) Res.drawable.ic_check
                            else Res.drawable.ic_three_dots
                        ),
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }
            ChatPopupMenu(
                state =
                ChatPopupMenuState(
                    visible = state.isMenuVisible,
                    onDismiss = state.onDismiss,
                    menuItems =
                    listOf(
                        PopupMenuItemState(
                            Icons.Filled.Edit,
                            stringResource(Res.string.popup_menu_edit_title),
                            state.onEditTitleClick,
                        ),
                        PopupMenuItemState(
                            Icons.Filled.Delete,
                            stringResource(Res.string.popup_menu_delete_chat),
                            state.onDeleteChatClick,
                        ),
                    ),
                )
            )
        }
        if (state.isEditingTitle) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(
                    onClick = state.onTitleChangeDismiss,
                    modifier =
                    Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp).size(24.dp),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.padding(2.dp),
                    )
                }
            }
        }
    }
}

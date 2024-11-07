import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import net.barrage.chatwhitelabel.ui.components.chat.ChatInput
import net.barrage.chatwhitelabel.ui.components.chat.ChatInputState
import net.barrage.chatwhitelabel.ui.components.chat.ChatTitle
import net.barrage.chatwhitelabel.ui.components.chat.ChatTitleState
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.chat.ReceiveMessageCallback
import net.barrage.chatwhitelabel.ui.theme.LocalCustomColorsPalette
import net.barrage.chatwhitelabel.ui.theme.customTypography

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    isKeyboardOpen: Boolean,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val chatInteractionSource = remember { MutableInteractionSource() }
    val chatInputFocused by chatInteractionSource.collectIsFocusedAsState()
    var menuVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isKeyboardOpen, viewModel.messages) {
        if (viewModel.messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(viewModel.messages.lastIndex)
            lazyListState.animateScrollBy(lazyListState.layoutInfo.viewportEndOffset.toFloat())
        }
    }

    LaunchedEffect(Unit) { initializeWebSocketClient(viewModel, scope) }

    Column(modifier = modifier.fillMaxSize().imePadding()) {
        ChatTitle(
            state =
                ChatTitleState(
                    title = viewModel.chatTitle,
                    isMenuVisible = menuVisible,
                    isEditingTitle = viewModel.isEditingTitle,
                    onThreeDotsClick = { menuVisible = true },
                    onEditTitleClick = {
                        viewModel.setEditingTitle(true)
                        menuVisible = false
                    },
                    onDeleteChatClick = {
                        // Delete chat
                        menuVisible = false
                    },
                    onDismiss = { menuVisible = false },
                    onTitleChange = { viewModel.setChatTitle(it) },
                    onTitleChangeConfirmation = { viewModel.updateTitle() },
                    isChatOpen = viewModel.isChatOpen,
                ),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        MessageList(
            messages = viewModel.messages.toImmutableList(),
            lazyListState = lazyListState,
            chatInteractionSource = chatInteractionSource,
            chatInputFocused = chatInputFocused,
            focusManager = focusManager,
            modifier = Modifier.weight(1f),
        )
        ChatInput(
            state =
                ChatInputState(
                    inputText = viewModel.inputText,
                    onInputTextChange = { viewModel.updateInputText(it) },
                    onSendMessage = { viewModel.sendMessage() },
                    onStopReceivingMessage = { viewModel.webSocketChatClient?.stopMessageStream() },
                    isSendEnabled = viewModel.isSendEnabled,
                    isReceivingMessage = viewModel.isReceivingMessage,
                    focusManager = focusManager,
                    chatInteractionSource = chatInteractionSource,
                )
        )
    }
}

@Composable
private fun MessageList(
    messages: ImmutableList<String>,
    lazyListState: LazyListState,
    chatInteractionSource: MutableInteractionSource,
    chatInputFocused: Boolean,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier.fillMaxSize().clickable(chatInteractionSource, null) {
                if (chatInputFocused) focusManager.clearFocus()
            },
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        state = lazyListState,
    ) {
        itemsIndexed(messages) { index, item ->
            MessageItem(message = item, isUserMessage = index % 2 == 0)
        }
    }
}

@Composable
private fun MessageItem(message: String, isUserMessage: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier =
                Modifier.align(if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart)
                    .widthIn(min = 0.dp, max = 300.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = message,
                style = customTypography().textBase,
                color = LocalCustomColorsPalette.current.textBase,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

private fun initializeWebSocketClient(viewModel: ChatViewModel, scope: CoroutineScope) {
    if (viewModel.webSocketChatClient == null) {
        viewModel.initializeWebSocketClient(
            object : ReceiveMessageCallback {
                var addNewMessage = true

                override fun receiveMessage(message: String) {
                    if (addNewMessage) {
                        viewModel.addMessage(message)
                    } else {
                        viewModel.updateLastMessage(message)
                    }
                    if (addNewMessage) {
                        addNewMessage = false
                    }
                    viewModel.setReceivingMessage(true)
                }

                override fun enableSending() {
                    viewModel.setSendEnabled(true)
                }

                override fun disableSending() {
                    viewModel.setSendEnabled(false)
                }

                override fun stopReceivingMessage() {
                    addNewMessage = true
                    enableSending()
                    viewModel.setReceivingMessage(false)
                }

                override fun setTtsLanguage(language: String) {
                    // Set TTS language
                }

                override fun setChatTitle(title: String) {
                    viewModel.setChatTitle(title)
                }

                override fun onError(errorMessage: String) {
                    // Handle error
                }

                override fun setChatOpen(isChatOpen: Boolean) {
                    viewModel.setChatOpen(isChatOpen)
                }
            },
            scope = scope,
        )
    }
}

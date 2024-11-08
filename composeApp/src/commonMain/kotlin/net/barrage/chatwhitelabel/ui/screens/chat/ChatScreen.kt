import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import net.barrage.chatwhitelabel.ui.components.chat.AgentContent
import net.barrage.chatwhitelabel.ui.components.chat.ChatInput
import net.barrage.chatwhitelabel.ui.components.chat.ChatInputState
import net.barrage.chatwhitelabel.ui.components.chat.ChatTitle
import net.barrage.chatwhitelabel.ui.components.chat.ChatTitleState
import net.barrage.chatwhitelabel.ui.components.chat.ErrorContent
import net.barrage.chatwhitelabel.ui.components.chat.MessageList
import net.barrage.chatwhitelabel.ui.screens.chat.ChatScreenState
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.chat.ReceiveMessageCallback
import net.barrage.chatwhitelabel.ui.screens.profile.ProfileCard

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    isKeyboardOpen: Boolean,
    profileVisible: Boolean,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    changeProfileVisibility: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val chatInteractionSource = remember { MutableInteractionSource() }
    val chatInputFocused by chatInteractionSource.collectIsFocusedAsState()
    var menuVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val chatScreenState = viewModel.chatScreenState

    LaunchedEffect(isKeyboardOpen, chatScreenState) {
        if (chatScreenState is ChatScreenState.Success && chatScreenState.messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatScreenState.messages.lastIndex)
            lazyListState.animateScrollBy(lazyListState.layoutInfo.viewportEndOffset.toFloat())
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
        initializeWebSocketClient(viewModel, scope)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .clickable(chatInteractionSource, null) {
                    if (chatInputFocused) focusManager.clearFocus()
                }
                .imePadding()
        ) {
            when (chatScreenState) {
                is ChatScreenState.Success -> {
                    if (
                        chatScreenState.messages.isNotEmpty() ||
                        viewModel.webSocketChatClient?.currentChatId?.value != null
                    ) {
                        ChatTitle(
                            state =
                            ChatTitleState(
                                title = chatScreenState.chatTitle,
                                isMenuVisible = menuVisible,
                                isEditingTitle = chatScreenState.isEditingTitle,
                                onThreeDotsClick = { menuVisible = true },
                                onEditTitleClick = {
                                    viewModel.setEditingTitle(true)
                                    menuVisible = false
                                },
                                onDeleteChatClick = {
                                    showDeleteConfirmation = true
                                    menuVisible = false
                                },
                                onDismiss = { menuVisible = false },
                                onTitleChange = { viewModel.setChatTitle(it) },
                                onTitleChangeConfirmation = { viewModel.updateTitle() },
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = { viewModel.newChat() }) { Text("New Chat") }
                        }
                    }

                    if (
                        chatScreenState.agents.isNotEmpty() &&
                        chatScreenState.messages.isEmpty() &&
                        viewModel.webSocketChatClient?.currentChatId?.value.isNullOrEmpty()
                    ) {
                        AgentContent(
                            agents = chatScreenState.agents.toImmutableList(),
                            selectedAgent = viewModel.selectedAgent.value,
                            onAgentClick = { selectedAgent -> viewModel.setAgent(selectedAgent) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        MessageList(
                            messages = chatScreenState.messages.toImmutableList(),
                            lazyListState = lazyListState,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    ChatInput(
                        state =
                        ChatInputState(
                            inputText = chatScreenState.inputText,
                            onInputTextChange = { viewModel.updateInputText(it) },
                            onSendMessage = { viewModel.sendMessage() },
                            onStopReceivingMessage = {
                                viewModel.webSocketChatClient?.stopMessageStream()
                            },
                            isSendEnabled = chatScreenState.isSendEnabled,
                            isReceivingMessage = chatScreenState.isReceivingMessage,
                            focusManager = focusManager,
                            chatInteractionSource = chatInteractionSource,
                        )
                    )
                }

                is ChatScreenState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ChatScreenState.Error ->
                    ErrorContent(
                        errorMessage = chatScreenState.message,
                        onRetry = { viewModel.loadAllData() },
                    )

                is ChatScreenState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        if (profileVisible) {
            Box(
                modifier =
                Modifier.fillMaxSize().background(Color.DarkGray.copy(alpha = 0.7f)).clickable(
                    MutableInteractionSource(),
                    null,
                ) {},
                contentAlignment = Alignment.Center,
            ) {
                ProfileCard(
                    viewState = viewModel.currentUserViewState,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    onCloseClick = changeProfileVisibility,
                    onLogoutClick = { viewModel.logout() },
                )
            }
        }
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Chat") },
            text = { Text("Are you sure you want to delete this chat?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteChat()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) { Text("No") }
            },
        )
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

                override fun closeChat() {
                    viewModel.onChatClosed()
                }
            },
            scope = scope,
        )
    }
}

@file:Suppress("CyclomaticComplexMethod")

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import net.barrage.chatwhitelabel.data.remote.dto.history.SenderType
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
import net.barrage.chatwhitelabel.utils.isDebug

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    isKeyboardOpen: Boolean,
    profileVisible: Boolean,
    networkAvailable: Boolean,
    scope: CoroutineScope,
    onLogoutSuccess: () -> Unit,
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
    val density = LocalDensity.current
    var width by remember { mutableStateOf(0.dp) }
    val clipboardManager = LocalClipboardManager.current

    if (chatScreenState is ChatScreenState.Success) {
        LaunchedEffect(isKeyboardOpen, chatScreenState.messages) {
            if (chatScreenState.messages.isNotEmpty()) {
                if (isKeyboardOpen)
                    lazyListState.animateScrollBy(
                        lazyListState.layoutInfo.viewportEndOffset.toFloat()
                    )
                else lazyListState.animateScrollToItem(chatScreenState.messages.size - 1)
            }
        }
    }
    LaunchedEffect(networkAvailable) {
        when {
            !networkAvailable -> viewModel.webSocketChatClient?.disconnect()
            else -> viewModel.webSocketChatClient?.reconnect()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
        initializeWebSocketClient(viewModel, scope)
    }

    Box(
        modifier =
            modifier.fillMaxSize().onGloballyPositioned {
                with(density) { width = it.size.width.toDp() - 80.dp }
            }
    ) {
        Column(
            modifier =
                Modifier.fillMaxSize()
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
                                    onTitleChangeDismiss = {
                                        viewModel.cancelTitleEdit()
                                        menuVisible = false
                                    },
                                ),
                            maxWidth = width,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }

                    if (
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
                            onCopy = {
                                clipboardManager.setText(
                                    buildAnnotatedString { append(it.content) }
                                )
                            },
                            onPositiveEvaluation = { viewModel.evaluateMessage(it, true) },
                            onNegativeEvaluation = { viewModel.evaluateMessage(it, false) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (chatScreenState.isAgentActive) {
                        ChatInput(
                            state =
                                ChatInputState(
                                    inputText = chatScreenState.inputText,
                                    onInputTextChange = { viewModel.updateInputText(it) },
                                    onSendMessage = { viewModel.sendMessage() },
                                    onStopReceivingMessage = {
                                        viewModel.webSocketChatClient?.stopMessageStream()
                                    },
                                    isEnabled =
                                        chatScreenState.isSendEnabled &&
                                            chatScreenState.agents.isNotEmpty(),
                                    isReceivingMessage = chatScreenState.isReceivingMessage,
                                    focusManager = focusManager,
                                    chatInteractionSource = chatInteractionSource,
                                )
                        )
                    } else {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(horizontal = 20.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                            border = CardDefaults.outlinedCardBorder(enabled = true),
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "This agent is no longer active. " +
                                        "Please select an active agent to begin a new conversation.",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ProfileCard(
                    viewState = viewModel.currentUserViewState,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    onCloseClick = changeProfileVisibility,
                    onLogoutClick = {
                        changeProfileVisibility()
                        viewModel.logout(onLogoutSuccess)
                    },
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
                        viewModel.addMessage(message, senderType = SenderType.ASSISTANT)
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
                    viewModel.setReceivingMessage(false)
                }

                override fun setTtsLanguage(language: String) {
                    // Set TTS language
                }

                override fun setChatTitle(title: String, chatId: String) {
                    viewModel.setChatTitle(title, chatId)
                }

                override fun onError(error: String) {
                    val errorMessage = if (isDebug) error else "An error occurred"
                    viewModel.addMessage(errorMessage, senderType = SenderType.ERROR)
                    viewModel.setReceivingMessage(false)
                }

                override fun closeChat() {
                    viewModel.onChatClosed()
                }
            },
            scope = scope,
        )
    }
}

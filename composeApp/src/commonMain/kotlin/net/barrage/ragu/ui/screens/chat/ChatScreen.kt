package net.barrage.ragu.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.data.remote.dto.history.SenderType
import net.barrage.ragu.ui.components.chat.AgentContent
import net.barrage.ragu.ui.components.chat.ChatInput
import net.barrage.ragu.ui.components.chat.ChatInputState
import net.barrage.ragu.ui.components.chat.ChatTitle
import net.barrage.ragu.ui.components.chat.ChatTitleState
import net.barrage.ragu.ui.components.chat.ErrorContent
import net.barrage.ragu.ui.components.chat.MessageList
import net.barrage.ragu.ui.components.reveal.RevealKeys
import net.barrage.ragu.ui.components.reveal.RevealOverlayContent
import net.barrage.ragu.ui.screens.profile.ProfileContent
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.agent_inactive_text
import ragumultiplatform.composeapp.generated.resources.delete_chat_description
import ragumultiplatform.composeapp.generated.resources.delete_chat_title
import ragumultiplatform.composeapp.generated.resources.no
import ragumultiplatform.composeapp.generated.resources.sign_out_description
import ragumultiplatform.composeapp.generated.resources.sign_out_title
import ragumultiplatform.composeapp.generated.resources.yes

@Composable
fun ChatScreen(
    onLogoutSuccess: () -> Unit,
    changeProfileVisibility: () -> Unit,
    changeInputEnabled: (Boolean) -> Unit,
    viewModel: ChatViewModel,
    scope: CoroutineScope,
    revealCanvasState: RevealCanvasState,
    revealState: RevealState,
    shouldShowOnboardingTutorial: Boolean,
    isKeyboardOpen: Boolean,
    profileVisible: Boolean,
    networkAvailable: Boolean,
    inputEnabled: Boolean,
    checkAuth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val chatInteractionSource = remember { MutableInteractionSource() }
    val chatInputFocused by chatInteractionSource.collectIsFocusedAsState()
    var menuVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val chatScreenState by viewModel.chatScreenState.collectAsState()
    val profileViewState by viewModel.currentUserViewState.collectAsState()
    val density = LocalDensity.current
    var width by remember { mutableStateOf(0.dp) }
    val clipboardManager = LocalClipboardManager.current

    OnEventListener {
        if (it == Lifecycle.Event.ON_RESUME) {
            checkAuth()
        }
    }

    if (
        chatScreenState is ChatScreenState.Success &&
        (chatScreenState as ChatScreenState.Success).messages.isNotEmpty()
    ) {
        LaunchedEffect((chatScreenState as ChatScreenState.Success).messages.last()) {
            lazyListState.animateScrollToItem(0)
        }
    }
    LaunchedEffect(isKeyboardOpen) {
        if (
            isKeyboardOpen &&
            chatScreenState is ChatScreenState.Success &&
            (chatScreenState as ChatScreenState.Success).messages.isNotEmpty()
        ) {
            lazyListState.animateScrollToItem(0)
        }
    }
    LaunchedEffect(networkAvailable) {
        when {
            !networkAvailable -> viewModel.webSocketManager.disconnect()
            else -> viewModel.webSocketManager.reconnect()
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
            when (val state = chatScreenState) {
                is ChatScreenState.Success -> {
                    if (
                        state.messages.isNotEmpty() ||
                        viewModel.webSocketManager.getChatId() != null
                    ) {
                        ChatTitle(
                            state =
                            ChatTitleState(
                                title =
                                state.chatTitle ?: stringResource(state.chatTitleRes),
                                isMenuVisible = menuVisible,
                                isEditingTitle = state.isEditingTitle,
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
                                revealCanvasState = revealCanvasState,
                                revealState = revealState,
                            ),
                            revealState = revealState,
                            scope = scope,
                            maxWidth = width,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }

                    if (
                        state.messages.isEmpty() &&
                        viewModel.webSocketManager.getChatId().isNullOrEmpty()
                    ) {
                        AgentContent(
                            agents = state.agents.toImmutableList(),
                            selectedAgent = viewModel.selectedAgent.value,
                            onAgentClick = { selectedAgent -> viewModel.setAgent(selectedAgent) },
                            revealState = revealState,
                            scope = scope,
                            changeInputEnabled = changeInputEnabled,
                            shouldShowOnboardingTutorial = shouldShowOnboardingTutorial,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        AnimatedVisibility(state.isLoadingMessages) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                            LaunchedEffect(Unit) {
                                lazyListState.animateScrollBy(1000f)
                            }
                        }
                        MessageList(
                            messages = state.messages.toImmutableList(),
                            lazyListState = lazyListState,
                            onCopy = {
                                clipboardManager.setText(
                                    buildAnnotatedString { append(it.content) }
                                )
                            },
                            onPositiveEvaluation = { viewModel.evaluateMessage(it, true) },
                            onNegativeEvaluation = { viewModel.evaluateMessage(it, false) },
                            onScrollToTop = { viewModel.loadMoreChatMessages() },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (state.isAgentActive) {
                        Reveal(
                            onOverlayClick = { key ->
                                scope.launch {
                                    revealState.hide()
                                    if (key == RevealKeys.ChatInput) {
                                        delay(1000)
                                        revealState.reveal(RevealKeys.Menu)
                                    }
                                }
                            },
                            modifier = modifier,
                            revealCanvasState = revealCanvasState,
                            revealState = revealState,
                            overlayContent = { key -> RevealOverlayContent(key) },
                        ) {
                            ChatInput(
                                state =
                                ChatInputState(
                                    inputText = state.inputText,
                                    onInputTextChange = { viewModel.updateInputText(it) },
                                    onSendMessage = { viewModel.sendMessage() },
                                    onStopReceivingMessage = {
                                        viewModel.webSocketManager.stopMessageStream()
                                    },
                                    isEnabled = state.isSendEnabled && state.agents.isNotEmpty() && inputEnabled,
                                    isReceivingMessage = state.isReceivingMessage,
                                    focusManager = focusManager,
                                    chatInteractionSource = chatInteractionSource,
                                ),
                                revealState = revealState,
                                scope = scope
                            )
                        }
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
                                    stringResource(Res.string.agent_inactive_text),
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
                        errorMessage = stringResource(state.message),
                        onRetry = { viewModel.loadAllData() },
                    )

                is ChatScreenState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ChatScreenState.Unauthorized -> {
                    LaunchedEffect(Unit) {
                        onLogoutSuccess()
                    }
                }
            }
        }

        if (profileVisible) {
            Dialog(
                onDismissRequest = changeProfileVisibility,
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                ProfileContent(
                    viewState = profileViewState,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    onCloseClick = changeProfileVisibility,
                    onLogoutClick = {
                        changeProfileVisibility()
                        showLogoutConfirmation = true
                    },
                    onUnauthorized = onLogoutSuccess,
                )
            }
        }
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(Res.string.delete_chat_title)) },
            text = { Text(stringResource(Res.string.delete_chat_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteChat()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(stringResource(Res.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(Res.string.no))
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )
    }
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text(stringResource(Res.string.sign_out_title)) },
            text = { Text(stringResource(Res.string.sign_out_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout(onLogoutSuccess)
                        showLogoutConfirmation = false
                    }
                ) {
                    Text(stringResource(Res.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text(stringResource(Res.string.no))
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )
    }
}

private fun initializeWebSocketClient(viewModel: ChatViewModel, scope: CoroutineScope) {
    if (viewModel.webSocketManager.webSocketChatClient == null) {
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

                override fun onError(error: String, retry: Boolean) {
                    if (retry) {
                        viewModel.webSocketManager.retryLastMessage()
                    } else {
                        viewModel.addMessage(error, senderType = SenderType.ERROR)
                        viewModel.setReceivingMessage(false)
                    }
                }

                override fun closeChat() {
                    viewModel.chatStateManager.clearChat()
                }
            },
            scope = scope,
        )
    }
}

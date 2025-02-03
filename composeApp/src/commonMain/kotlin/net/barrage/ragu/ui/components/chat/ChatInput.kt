package net.barrage.ragu.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.reveal.RevealKeys
import net.barrage.ragu.ui.theme.customTypography

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatInput(
    state: ChatInputState,
    revealState: RevealState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 8.dp),
    ) {
        TextField(
            value = state.inputText,
            onValueChange = state.onInputTextChange,
            textStyle = customTypography().textBase,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
            KeyboardActions(
                onDone = {
                    state.onSendMessage()
                    state.focusManager.clearFocus()
                }
            ),
            modifier = Modifier.weight(1f).revealable(
                key = RevealKeys.ChatInput,
                shape = RevealShape.RoundRect(12.dp),
                state = revealState,
                onClick = {
                    scope.launch {
                        revealState.hide()
                        delay(1000)
                        revealState.reveal(RevealKeys.Menu)
                    }
                },
            ),
            interactionSource = state.chatInteractionSource,
            shape = RoundedCornerShape(12.dp),
            colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = if (state.inputText.isEmpty()) MaterialTheme.colorScheme.primaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor =
                if (state.inputText.isEmpty()) MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = 0.4f
                ) else MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor =
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                focusedPlaceholderColor = if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedPlaceholderColor =
                if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onSecondaryContainer,
                disabledPlaceholderColor =
                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
                focusedSuffixColor = if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedSuffixColor =
                if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onSecondaryContainer,
                disabledSuffixColor =
                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
                focusedPrefixColor = if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedPrefixColor = if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onSecondaryContainer,
                disabledPrefixColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = if (state.inputText.isEmpty()) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onSecondaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
            ),
            prefix = {
                /*  CompositionLocalProvider(
                      LocalMinimumInteractiveComponentEnforcement provides false
                  ) {
                      IconButton(
                          onClick = {
                              state.onCameraClick()
                              state.focusManager.clearFocus()
                          },
                          enabled = state.isEnabled && state.isReceivingMessage.not(),
                          modifier = Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                              .padding(end = 4.dp),
                      ) {
                          Icon(
                              painter = painterResource(Res.drawable.ic_camera_add),
                              contentDescription = null,
                          )
                      }
                  }*/
            },
            suffix = {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false
                ) {
                    IconButton(
                        onClick = {
                            if (state.isReceivingMessage) {
                                state.onStopReceivingMessage()
                            } else {
                                state.onSendMessage()
                            }
                            state.focusManager.clearFocus()
                        },
                        modifier = Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp),
                        enabled = state.isEnabled,
                    ) {
                        Icon(
                            if (state.isReceivingMessage) Icons.Filled.Close
                            else Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                        )
                    }
                }
            },
            enabled = state.isEnabled,
            placeholder = { Text(text = "Send a message") },
        )
    }
}

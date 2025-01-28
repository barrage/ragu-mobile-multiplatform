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
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.ic_camera_add

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
                unfocusedContainerColor =
                if (state.inputText.isEmpty()) TextFieldDefaults.colors().focusedContainerColor.copy(
                    alpha = 0.4f
                ) else TextFieldDefaults.colors().focusedContainerColor,
                disabledContainerColor =
                TextFieldDefaults.colors().focusedContainerColor.copy(alpha = 0.2f),
                unfocusedPlaceholderColor =
                if (state.inputText.isEmpty()) TextFieldDefaults.colors().unfocusedPlaceholderColor.copy(
                    alpha = 0.6f
                ) else TextFieldDefaults.colors().unfocusedPlaceholderColor,
                disabledPlaceholderColor =
                TextFieldDefaults.colors().disabledPlaceholderColor.copy(alpha = 0.2f),
                unfocusedSuffixColor =
                if (state.inputText.isEmpty()) TextFieldDefaults.colors().unfocusedSuffixColor.copy(
                    alpha = 0.6f
                ) else TextFieldDefaults.colors().unfocusedSuffixColor,
                disabledSuffixColor =
                TextFieldDefaults.colors().disabledSuffixColor.copy(alpha = 0.2f),
                unfocusedPrefixColor = if (state.inputText.isEmpty()) TextFieldDefaults.colors().unfocusedPrefixColor.copy(
                    alpha = 0.6f
                ) else TextFieldDefaults.colors().unfocusedPrefixColor,
                disabledPrefixColor = TextFieldDefaults.colors().disabledPrefixColor.copy(alpha = 0.2f),
                unfocusedTextColor = if (state.inputText.isEmpty()) TextFieldDefaults.colors().unfocusedTextColor.copy(
                    alpha = 0.6f
                ) else TextFieldDefaults.colors().unfocusedTextColor,
                disabledTextColor = TextFieldDefaults.colors().disabledTextColor.copy(alpha = 0.2f),
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

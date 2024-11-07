package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.ui.theme.GlobalsPrimary
import net.barrage.chatwhitelabel.ui.theme.customTypography

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatInput(state: ChatInputState, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) {
        val customTextSelectionColors =
            TextSelectionColors(
                handleColor = GlobalsPrimary,
                backgroundColor = GlobalsPrimary.copy(alpha = 0.4f),
            )

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
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
                modifier = Modifier.weight(1f),
                interactionSource = state.chatInteractionSource,
                shape = RoundedCornerShape(12.dp),
                colors =
                    TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
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
                            enabled = state.isSendEnabled,
                        ) {
                            Icon(
                                if (state.isReceivingMessage) Icons.Filled.Close
                                else Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                            )
                        }
                    }
                },
            )
        }
    }
}

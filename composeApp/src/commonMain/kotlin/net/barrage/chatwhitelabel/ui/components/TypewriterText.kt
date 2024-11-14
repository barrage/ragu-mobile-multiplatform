package net.barrage.chatwhitelabel.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun TypewriterText(state: TypewriterTextState, modifier: Modifier = Modifier) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = "")) }
    val focusRequester = remember { FocusRequester() }
    var previousText by remember { mutableStateOf("") }

    LaunchedEffect(state.text) {
        if (state.text != previousText && !state.isEditing) {
            textFieldValue = TextFieldValue("")
            state.text.forEachIndexed { charIndex, _ ->
                val partialText = state.text.substring(startIndex = 0, endIndex = charIndex + 1)
                textFieldValue = TextFieldValue(text = partialText)
                delay(80)
            }
            previousText = state.text
        }
    }

    LaunchedEffect(state.isEditing) {
        if (state.isEditing) {
            textFieldValue =
                TextFieldValue(text = state.text, selection = TextRange(state.text.length))
            focusRequester.requestFocus()
        }
    }
    Row(modifier = modifier) {
        if (state.isEditing) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    state.onTextChange(it.text)
                },
                textStyle =
                    state.textStyle.copy(color = state.textColor).fixCenterTextOnAllPlatforms(),
                modifier = Modifier.focusRequester(focusRequester),
                enabled = true,
                singleLine = true,
            )
        } else {
            Text(
                text = textFieldValue.text,
                style = state.textStyle.copy(color = state.textColor).fixCenterTextOnAllPlatforms(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

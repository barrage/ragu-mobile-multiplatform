package net.barrage.chatwhitelabel.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
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
import kotlinx.coroutines.delay

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

    BasicTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            state.onTextChange(it.text)
        },
        textStyle = state.style,
        modifier = modifier.width(IntrinsicSize.Min).focusRequester(focusRequester),
        enabled = state.isEditing,
    )
}

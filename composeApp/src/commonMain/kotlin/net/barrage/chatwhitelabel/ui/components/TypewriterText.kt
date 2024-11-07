package net.barrage.chatwhitelabel.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    var textToDisplay by remember { mutableStateOf("") }

    LaunchedEffect(key1 = text) {
        text.forEachIndexed { charIndex, _ ->
            textToDisplay = text.substring(startIndex = 0, endIndex = charIndex + 1)
            delay(160)
        }
    }

    Text(text = textToDisplay, style = style, modifier = modifier)
}

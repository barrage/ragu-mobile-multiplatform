package net.barrage.chatwhitelabel.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class TypewriterTextState(
    val text: String,
    val isEditing: Boolean,
    val textStyle: TextStyle,
    val textColor: Color,
    val onTextChange: (String) -> Unit,
)

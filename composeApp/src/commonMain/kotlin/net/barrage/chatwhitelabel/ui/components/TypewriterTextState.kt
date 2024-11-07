package net.barrage.chatwhitelabel.ui.components

import androidx.compose.ui.text.TextStyle

data class TypewriterTextState(
    val text: String,
    val isEditing: Boolean,
    val style: TextStyle,
    val onTextChange: (String) -> Unit,
)

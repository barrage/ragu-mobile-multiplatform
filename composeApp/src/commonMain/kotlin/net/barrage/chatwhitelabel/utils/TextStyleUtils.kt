package net.barrage.chatwhitelabel.utils

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

/**
 * Extension function for TextStyle to ensure consistent text centering across all platforms.
 *
 * This function addresses potential inconsistencies in text alignment and trimming
 * that may occur across different platforms (Android, iOS, Desktop) when using Jetpack Compose.
 *
 * @receiver The TextStyle to be modified.
 * @return A new TextStyle with centered alignment and both-ends trimming applied to the line height.
 */
fun TextStyle.fixCenterTextOnAllPlatforms() =
    this.copy(
        lineHeightStyle =
        LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
        )
    )
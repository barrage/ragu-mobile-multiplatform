package net.barrage.chatwhitelabel.utils

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

fun TextStyle.fixCenterTextOnAllPlatforms() =
    this.copy(
        lineHeightStyle =
            LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both,
            )
    )

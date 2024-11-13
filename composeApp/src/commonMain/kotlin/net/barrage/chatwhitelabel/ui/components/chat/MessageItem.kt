package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.ui.theme.LocalCustomColorsPalette
import net.barrage.chatwhitelabel.ui.theme.customTypography
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun MessageItem(message: String, isUserMessage: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier =
                Modifier.align(if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart)
                    .widthIn(min = 0.dp, max = 300.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = message,
                style = customTypography().textBase.fixCenterTextOnAllPlatforms(),
                color = LocalCustomColorsPalette.current.textBase,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

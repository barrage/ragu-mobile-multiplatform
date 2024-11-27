package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_brain
import chatwhitelabel.composeapp.generated.resources.ic_copy
import chatwhitelabel.composeapp.generated.resources.ic_thumb_down
import chatwhitelabel.composeapp.generated.resources.ic_thumb_up
import net.barrage.chatwhitelabel.data.remote.dto.history.SenderType
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem
import net.barrage.chatwhitelabel.ui.theme.LocalCustomColorsPalette
import net.barrage.chatwhitelabel.ui.theme.customTypography
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageItem(
    chatMessage: ChatMessageItem,
    onCopy: (ChatMessageItem) -> Unit,
    onPositiveEvaluation: (ChatMessageItem) -> Unit,
    onNegativeEvaluation: (ChatMessageItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier.align(
                        when (chatMessage.senderType) {
                            SenderType.USER -> Alignment.CenterEnd
                            SenderType.ASSISTANT,
                            SenderType.ERROR -> Alignment.CenterStart
                        }
                    )
                    .widthIn(min = 0.dp, max = 300.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                when (chatMessage.senderType) {
                    SenderType.ASSISTANT,
                    SenderType.ERROR -> SenderIcon(SenderType.ASSISTANT)
                    SenderType.USER -> Unit
                }

                Card(shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = chatMessage.content,
                        style = customTypography().textBase.fixCenterTextOnAllPlatforms(),
                        color = LocalCustomColorsPalette.current.textBase,
                        modifier = Modifier.padding(12.dp),
                    )
                }

                when (chatMessage.senderType) {
                    SenderType.USER -> SenderIcon(SenderType.USER)
                    SenderType.ASSISTANT,
                    SenderType.ERROR -> Unit
                }
            }
            if (chatMessage.senderType == SenderType.ASSISTANT) {
                Row(
                    modifier = Modifier.padding(start = 30.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentEnforcement provides false
                    ) {
                        IconButton(
                            onClick = { onCopy(chatMessage) },
                            modifier =
                                Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                                    .size(24.dp),
                        ) {
                            Icon(
                                painterResource(Res.drawable.ic_copy),
                                contentDescription = "Copy message",
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        IconButton(
                            onClick = { onPositiveEvaluation(chatMessage) },
                            modifier =
                                Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                                    .size(24.dp),
                        ) {
                            Icon(
                                painterResource(Res.drawable.ic_thumb_up),
                                contentDescription = "Positive evaluation",
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        IconButton(
                            onClick = { onNegativeEvaluation(chatMessage) },
                            modifier =
                                Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                                    .size(24.dp),
                        ) {
                            Icon(
                                painterResource(Res.drawable.ic_thumb_down),
                                contentDescription = "Negative evaluation",
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SenderIcon(senderType: SenderType, modifier: Modifier = Modifier) {
    Card(shape = CircleShape, modifier = modifier) {
        Box(modifier = Modifier.padding(4.dp)) {
            when (senderType) {
                SenderType.ASSISTANT,
                SenderType.ERROR ->
                    Icon(
                        painterResource(Res.drawable.ic_brain),
                        contentDescription = "Assistant profile",
                        modifier = Modifier.size(18.dp),
                    )

                SenderType.USER ->
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "User profile",
                        modifier = Modifier.size(18.dp),
                    )
            }
        }
    }
}

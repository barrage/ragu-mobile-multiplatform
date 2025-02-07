package net.barrage.ragu.ui.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.highlightedCodeBlock
import com.mikepenz.markdown.compose.elements.highlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import net.barrage.ragu.data.remote.dto.history.SenderType
import net.barrage.ragu.domain.model.ChatMessageItem
import net.barrage.ragu.utils.getScreenWidth
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.assistant_icon_content_description
import ragumultiplatform.composeapp.generated.resources.copy_button_content_description
import ragumultiplatform.composeapp.generated.resources.ic_copy
import ragumultiplatform.composeapp.generated.resources.ic_ragu
import ragumultiplatform.composeapp.generated.resources.ic_thumb
import ragumultiplatform.composeapp.generated.resources.ic_thumb_filled
import ragumultiplatform.composeapp.generated.resources.negative_evaluation_button_content_description
import ragumultiplatform.composeapp.generated.resources.positive_evaluation_button_content_description

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageItem(
    chatMessage: ChatMessageItem,
    userAvatarBitmap: ImageBitmap?,
    agentAvatarBitmap: ImageBitmap?,
    onCopy: (ChatMessageItem) -> Unit,
    onPositiveEvaluation: (ChatMessageItem) -> Unit,
    onNegativeEvaluation: (ChatMessageItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenWidth = getScreenWidth()
    val maxWidth = (screenWidth * 0.7f - 40.dp).coerceAtMost(400.dp)

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
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                when (chatMessage.senderType) {
                    SenderType.ASSISTANT,
                    SenderType.ERROR -> SenderIcon(
                        senderType = SenderType.ASSISTANT,
                        userAvatarBitmap = userAvatarBitmap,
                        agentAvatarBitmap = agentAvatarBitmap
                    )

                    SenderType.USER -> Unit
                }
                Card(shape = RoundedCornerShape(12.dp)) {
                    Markdown(
                        chatMessage.content,
                        modifier = Modifier.padding(12.dp).widthIn(max = maxWidth),
                        components =
                        markdownComponents(
                            codeBlock = highlightedCodeBlock,
                            codeFence = highlightedCodeFence,
                            paragraph = customParagraphComponent,
                        ),
                    )
                }

                when (chatMessage.senderType) {
                    SenderType.USER -> SenderIcon(
                        senderType = SenderType.USER,
                        userAvatarBitmap = userAvatarBitmap,
                        agentAvatarBitmap = agentAvatarBitmap
                    )

                    SenderType.ASSISTANT,
                    SenderType.ERROR -> Unit
                }
            }
            if (chatMessage.senderType == SenderType.ASSISTANT && !chatMessage.id.isNullOrEmpty()) {
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
                                contentDescription =
                                stringResource(Res.string.copy_button_content_description),
                                tint = MaterialTheme.colorScheme.onSurface,
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
                                if (chatMessage.evaluation == true) painterResource(Res.drawable.ic_thumb_filled) else painterResource(
                                    Res.drawable.ic_thumb
                                ),
                                contentDescription =
                                stringResource(
                                    Res.string.positive_evaluation_button_content_description
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
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
                                if (chatMessage.evaluation == false) painterResource(Res.drawable.ic_thumb_filled) else painterResource(
                                    Res.drawable.ic_thumb
                                ),
                                contentDescription =
                                stringResource(
                                    Res.string.negative_evaluation_button_content_description
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp).rotate(180F),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SenderIcon(
    senderType: SenderType,
    userAvatarBitmap: ImageBitmap?,
    agentAvatarBitmap: ImageBitmap?,
    modifier: Modifier = Modifier
) {
    Card(shape = CircleShape, modifier = modifier.size(24.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (senderType) {
                SenderType.ASSISTANT,
                SenderType.ERROR -> {
                    if (agentAvatarBitmap != null) {
                        Image(
                            bitmap = agentAvatarBitmap,
                            contentDescription = "Agent Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            painterResource(Res.drawable.ic_ragu),
                            contentDescription =
                            stringResource(Res.string.assistant_icon_content_description),
                            modifier = Modifier.size(18.dp).align(Alignment.Center),
                        )
                    }
                }

                SenderType.USER ->
                    if (userAvatarBitmap != null) {
                        Image(
                            bitmap = userAvatarBitmap,
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription =
                            stringResource(Res.string.assistant_icon_content_description),
                            modifier = Modifier.size(18.dp).align(Alignment.Center),
                        )
                    }
            }
        }
    }
}

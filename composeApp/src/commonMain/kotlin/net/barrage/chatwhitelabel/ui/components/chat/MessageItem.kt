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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.assistant_icon_content_description
import chatwhitelabel.composeapp.generated.resources.copy_button_content_description
import chatwhitelabel.composeapp.generated.resources.ic_brain
import chatwhitelabel.composeapp.generated.resources.ic_copy
import chatwhitelabel.composeapp.generated.resources.ic_thumb_down
import chatwhitelabel.composeapp.generated.resources.ic_thumb_up
import chatwhitelabel.composeapp.generated.resources.negative_evaluation_button_content_description
import chatwhitelabel.composeapp.generated.resources.positive_evaluation_button_content_description
import chatwhitelabel.composeapp.generated.resources.user_icon_content_description
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.highlightedCodeBlock
import com.mikepenz.markdown.compose.elements.highlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import net.barrage.chatwhitelabel.data.remote.dto.history.SenderType
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem
import net.barrage.chatwhitelabel.ui.theme.customTypography
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms
import net.barrage.chatwhitelabel.utils.getScreenWidth
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageItem(
    chatMessage: ChatMessageItem,
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
                    SenderType.ERROR -> SenderIcon(SenderType.ASSISTANT)

                    SenderType.USER -> Unit
                }
                Card(shape = RoundedCornerShape(12.dp)) {
                    Markdown(
                        chatMessage.content,
                        modifier = Modifier.padding(12.dp).widthIn(max = maxWidth),
                        typography =
                        markdownTypography(
                            text = customTypography().textBase.fixCenterTextOnAllPlatforms(),
                            h1 = markdownTypography().h1.fixCenterTextOnAllPlatforms(),
                            h2 = markdownTypography().h2.fixCenterTextOnAllPlatforms(),
                            h3 = markdownTypography().h3.fixCenterTextOnAllPlatforms(),
                            h4 = markdownTypography().h4.fixCenterTextOnAllPlatforms(),
                            h5 = markdownTypography().h5.fixCenterTextOnAllPlatforms(),
                            h6 = markdownTypography().h6.fixCenterTextOnAllPlatforms(),
                            code = markdownTypography().code.fixCenterTextOnAllPlatforms(),
                            inlineCode =
                            markdownTypography().inlineCode.fixCenterTextOnAllPlatforms(),
                            quote = markdownTypography().quote.fixCenterTextOnAllPlatforms(),
                            paragraph =
                            markdownTypography().paragraph.fixCenterTextOnAllPlatforms(),
                            ordered =
                            markdownTypography().ordered.fixCenterTextOnAllPlatforms(),
                            bullet = markdownTypography().bullet.fixCenterTextOnAllPlatforms(),
                            list = markdownTypography().list.fixCenterTextOnAllPlatforms(),
                            link = markdownTypography().link.fixCenterTextOnAllPlatforms(),
                        ),
                        components =
                        markdownComponents(
                            codeBlock = highlightedCodeBlock,
                            codeFence = highlightedCodeFence,
                            paragraph = customParagraphComponent,
                        ),
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
                                contentDescription =
                                stringResource(Res.string.copy_button_content_description),
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
                                contentDescription =
                                stringResource(
                                    Res.string.positive_evaluation_button_content_description
                                ),
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
                                contentDescription =
                                stringResource(
                                    Res.string.negative_evaluation_button_content_description
                                ),
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
                        contentDescription =
                        stringResource(Res.string.assistant_icon_content_description),
                        modifier = Modifier.size(18.dp),
                    )

                SenderType.USER ->
                    Icon(
                        Icons.Filled.Person,
                        contentDescription =
                        stringResource(Res.string.user_icon_content_description),
                        modifier = Modifier.size(18.dp),
                    )
            }
        }
    }
}

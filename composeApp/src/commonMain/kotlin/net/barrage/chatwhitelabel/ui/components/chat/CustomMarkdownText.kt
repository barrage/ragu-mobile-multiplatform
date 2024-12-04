package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.utils.buildMarkdownAnnotatedString
import kotlin.math.ceil
import kotlin.math.floor

val customParagraphComponent: MarkdownComponent = {
    val styledText = buildAnnotatedString {
        pushStyle(LocalMarkdownTypography.current.paragraph.toSpanStyle())
        buildMarkdownAnnotatedString(it.content, it.node)
        pop()
    }

    TightWrapText(styledText)
}

@Composable
private fun TightWrapText(text: AnnotatedString) {
    var textLayoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    Text(
        text = text,
        modifier =
            Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val newTextLayoutResult = textLayoutResult!!

                if (newTextLayoutResult.lineCount == 0) {
                    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                } else {
                    val minX =
                        (0 until newTextLayoutResult.lineCount).minOf(
                            newTextLayoutResult::getLineLeft
                        )
                    val maxX =
                        (0 until newTextLayoutResult.lineCount).maxOf(
                            newTextLayoutResult::getLineRight
                        )

                    layout(ceil(maxX - minX).toInt(), placeable.height) {
                        placeable.place(-floor(minX).toInt(), 0)
                    }
                }
            },
        onTextLayout = { textLayoutResult = it },
    )
}

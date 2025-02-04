package net.barrage.ragu.ui.components.reveal

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import com.svenjacobs.reveal.shapes.balloon.Arrow
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.reveal_account
import ragumultiplatform.composeapp.generated.resources.reveal_agent_item
import ragumultiplatform.composeapp.generated.resources.reveal_chat_input
import ragumultiplatform.composeapp.generated.resources.reveal_chat_title
import ragumultiplatform.composeapp.generated.resources.reveal_menu
import ragumultiplatform.composeapp.generated.resources.reveal_menu_close
import ragumultiplatform.composeapp.generated.resources.reveal_menu_color
import ragumultiplatform.composeapp.generated.resources.reveal_menu_history
import ragumultiplatform.composeapp.generated.resources.reveal_menu_new_chat
import ragumultiplatform.composeapp.generated.resources.reveal_menu_theme
import ragumultiplatform.composeapp.generated.resources.reveal_title_menu

@Composable
fun RevealOverlayScope.RevealOverlayContent(key: Key) {
    when (key as RevealKeys) {
        RevealKeys.Menu -> OverlayText(
            modifier = Modifier.align(
                horizontalArrangement = RevealOverlayArrangement.End,
            ),
            text = stringResource(Res.string.reveal_menu),
            arrow = Arrow.start(),
        )

        RevealKeys.TitleMenu -> OverlayText(
            modifier = Modifier.align(
                verticalArrangement = RevealOverlayArrangement.Bottom,
            ),
            text = stringResource(Res.string.reveal_title_menu),
            arrow = Arrow.top(),
        )

        RevealKeys.Account -> OverlayText(
            modifier = Modifier.widthIn(0.dp, 250.dp).align(
                verticalArrangement = RevealOverlayArrangement.Top,
            ),
            text = stringResource(Res.string.reveal_account),
            arrow = Arrow.bottom(),
        )

        RevealKeys.AgentItem -> OverlayText(
            modifier = Modifier.align(
                verticalArrangement = RevealOverlayArrangement.Bottom,
            ),
            text = stringResource(Res.string.reveal_agent_item),
            arrow = Arrow.top(),
        )

        RevealKeys.ChatInput -> OverlayText(
            modifier = Modifier.align(
                verticalArrangement = RevealOverlayArrangement.Top,
            ),
            text = stringResource(Res.string.reveal_chat_input),
            arrow = Arrow.bottom(),
        )

        RevealKeys.MenuClose -> OverlayText(
            modifier = Modifier.align(
                horizontalArrangement = RevealOverlayArrangement.End,
            ),
            text = stringResource(Res.string.reveal_menu_close),
            arrow = Arrow.start(),
        )

        RevealKeys.MenuColor -> OverlayText(
            modifier = Modifier.align(
                horizontalArrangement = RevealOverlayArrangement.Start,
            ),
            text = stringResource(Res.string.reveal_menu_color),
            arrow = Arrow.end(),
        )

        RevealKeys.MenuHistory -> OverlayText(
            modifier = Modifier.widthIn(0.dp, 250.dp).align(
                verticalArrangement = RevealOverlayArrangement.Top,
            ),
            text = stringResource(Res.string.reveal_menu_history),
            arrow = Arrow.bottom(),
        )

        RevealKeys.MenuTheme -> OverlayText(
            modifier = Modifier.widthIn(0.dp, 250.dp).align(
                verticalArrangement = RevealOverlayArrangement.Bottom,
            ),
            text = stringResource(Res.string.reveal_menu_theme),
            arrow = Arrow.top(),
        )

        RevealKeys.MenuNewChat -> OverlayText(
            modifier = Modifier.widthIn(0.dp, 250.dp).align(
                verticalArrangement = RevealOverlayArrangement.Bottom
            ),
            text = stringResource(Res.string.reveal_menu_new_chat),
            arrow = Arrow.top(),
        )

        RevealKeys.ChatTitle -> OverlayText(
            modifier = Modifier.align(
                horizontalArrangement = RevealOverlayArrangement.Start,
            ),
            text = stringResource(Res.string.reveal_chat_title),
            arrow = Arrow.end(),
        )
    }
}
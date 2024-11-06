package net.barrage.chatwhitelabel.ui.screens.modaldrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.ModalDrawerContentTopBar
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.ModalDrawerHistoryContent

@Composable
fun ModalDrawer(
    viewState: ModalDrawerContentViewState,
    theme: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onSelectThemeClick: (Color) -> Unit = {},
    onDarkLightModeClick: () -> Unit = {},
    onElementClick: (String) -> Unit = {},
) {
    ModalDrawerSheet(modifier = modifier.fillMaxWidth().padding(end = 75.dp)) {
        Column {
            ModalDrawerContentTopBar(
                viewState = viewState,
                theme = theme,
                isDarkMode = isDarkMode,
                modifier = Modifier.padding(vertical = 8.dp),
                onSelectThemeClick = onSelectThemeClick,
                onDarkLightModeClick = onDarkLightModeClick,
            )
            Divider(
                Modifier.padding(vertical = 8.dp, horizontal = 8.dp).background(Gray).height(1.dp)
            )
            ModalDrawerHistoryContent(
                viewState = viewState.history,
                onElementClick = onElementClick,
            )
        }
    }
}

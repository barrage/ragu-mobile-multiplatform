package net.barrage.ragu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle

@Composable
fun CustomTheme(
    seedColor: Color,
    style: PaletteStyle,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    useAmoled: Boolean = false,
    content: @Composable () -> Unit,
) {
    DynamicMaterialTheme(
        seedColor = seedColor,
        useDarkTheme = useDarkTheme,
        withAmoled = useAmoled,
        animate = true,
        content = content,
        style = style,
    )
}

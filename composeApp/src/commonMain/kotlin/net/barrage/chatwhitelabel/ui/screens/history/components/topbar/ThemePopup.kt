package net.barrage.chatwhitelabel.ui.screens.history.components.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.materialkolor.PaletteStyle
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemePopup(
    themeRows: Int,
    showPopup: Boolean,
    supportedThemeColumns: Int,
    supportedThemes: ImmutableList<Color>,
    selectedTheme: Color,
    supportedVariants: ImmutableList<PaletteStyle>,
    selectedVariant: PaletteStyle,
    onDismissRequest: () -> Unit,
    onSelectVariantClick: (PaletteStyle) -> Unit,
    onSelectThemeClick: (Color) -> Unit,
) {
    var areVariantsVisible by remember { mutableStateOf(false) }
    val rotation = animateFloatAsState(if (areVariantsVisible) 180f else 0f, label = "rotation")
    Popup(
        popupPositionProvider =
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ): IntOffset {
                    val x = anchorBounds.center.x - (popupContentSize.width / 2)
                    val y = anchorBounds.bottom
                    return IntOffset(x, y)
                }
            },
        onDismissRequest = onDismissRequest,
    ) {
        AnimatedVisibility(
            visible = showPopup,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Card(modifier = Modifier.wrapContentSize().padding(12.dp)) {
                Column(modifier = Modifier.padding(12.dp).widthIn(max = 160.dp)) {
                    for (row in 0 until themeRows) {
                        ThemeRow(
                            row = row,
                            supportedThemeColumns = supportedThemeColumns,
                            supportedThemes = supportedThemes,
                            selectedTheme = selectedTheme,
                            onSelectThemeClick = onSelectThemeClick,
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Variants",
                            modifier = Modifier,
                            style =
                                MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                        )
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier =
                                Modifier.size(24.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = MutableInteractionSource(),
                                        onClick = { areVariantsVisible = !areVariantsVisible },
                                    )
                                    .rotate(rotation.value),
                        )
                    }
                    AnimatedVisibility(areVariantsVisible) {
                        Column {
                            FlowRow {
                                for (variant in supportedVariants) {
                                    VariantCard(
                                        modifier = Modifier.padding(4.dp),
                                        isSelected = variant == selectedVariant,
                                        variant = variant,
                                        onClick = onSelectVariantClick,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

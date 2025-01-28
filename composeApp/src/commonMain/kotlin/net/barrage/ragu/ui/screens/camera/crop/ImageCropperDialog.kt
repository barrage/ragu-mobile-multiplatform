package net.barrage.ragu.ui.screens.camera.crop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.CropperStyle
import com.attafitamim.krop.core.crop.DefaultCropperStyle
import com.attafitamim.krop.core.crop.LocalCropperStyle
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.restore

val CropperDialogProperties = (DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnBackPress = false,
    dismissOnClickOutside = false
))

@Composable
fun ImageCropperDialog(
    state: CropState,
    style: CropperStyle = DefaultCropperStyle,
    dialogProperties: DialogProperties = CropperDialogProperties,
    dialogPadding: PaddingValues = PaddingValues(16.dp),
    dialogShape: Shape = RoundedCornerShape(28.dp),
    topBar: @Composable (CropState) -> Unit = { CropBottomBar(it) },
    cropControls: @Composable BoxScope.(CropState) -> Unit = { DefaultControls(it) },
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        state.setInitialState(style) // Could be buggy, since it is run in a separate thread
    }

    CompositionLocalProvider(LocalCropperStyle provides style) {
        Dialog(
            onDismissRequest = { state.done(accept = false) },
            properties = dialogProperties,
        ) {
            Card(
                modifier = modifier.padding(dialogPadding).fillMaxHeight(.75f),
                shape = dialogShape,
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        CropperPreview(state = state, modifier = Modifier.fillMaxSize())
                        cropControls(state)
                    }
                    topBar(state)
                }
            }
        }
    }
}

@Composable
fun BoxScope.DefaultControls(state: CropState) {
    CropperControls(
        state = state,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(12.dp),
    )
}

@Composable
fun CropBottomBar(state: CropState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(onClick = { state.done(accept = false) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }
        IconButton(onClick = { state.reset() }) {
            Icon(painterResource(Res.drawable.restore), null)
        }
        IconButton(onClick = { state.done(accept = true) }, enabled = !state.accepted) {
            Icon(Icons.Default.Done, null)
        }
    }
}

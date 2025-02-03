package net.barrage.ragu.ui.screens.camera.crop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.CropperStyle
import com.attafitamim.krop.core.crop.LocalCropperStyle
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.restore

@Composable
fun ImageCropperScreen(
    state: CropState,
    style: CropperStyle,
    cropControls: @Composable BoxScope.(CropState) -> Unit = { DefaultControls(it) },
    modifier: Modifier = Modifier
) {
    val initialStateKey = remember(state.src) { Any() }

    LaunchedEffect(initialStateKey) {
        state.setInitialState(style)
    }

    CompositionLocalProvider(LocalCropperStyle provides style) {
        Card(
            modifier = modifier.fillMaxHeight(),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds()
                ) {
                    CropperPreview(state = state, modifier = Modifier.fillMaxSize())
                    cropControls(state)
                    CropBottomBar(state, modifier = Modifier.align(Alignment.BottomCenter))
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
            .statusBarsPadding()
            .padding(12.dp),
    )
}

@Composable
fun CropBottomBar(state: CropState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Card(onClick = { state.done(accept = false) }, shape = CircleShape) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                modifier = Modifier.size(72.dp).padding(12.dp)
            )
        }
        Card(onClick = { state.done(accept = true) }, shape = CircleShape) {
            Icon(
                Icons.Default.Done,
                null,
                modifier = Modifier.size(72.dp).padding(12.dp)
            )
        }
        Card(onClick = { state.reset() }, shape = CircleShape) {
            Icon(
                painterResource(Res.drawable.restore),
                null,
                modifier = Modifier.size(72.dp).padding(12.dp)
            )
        }
    }
}

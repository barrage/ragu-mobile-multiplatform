package net.barrage.ragu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ragumultiplatform.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/loader.json").decodeToString())
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter =
            rememberLottiePainter(
                composition = composition,
                iterations = Compottie.IterateForever,
            ),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
        )
    }
}

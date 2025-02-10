package net.barrage.ragu.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@Composable
fun NetworkImage(imageModel: () -> Any, imageOptions: ImageOptions, modifier: Modifier = Modifier) {
    CoilImage(
        imageModel = imageModel,
        imageOptions = imageOptions,
        loading = {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        },
        failure = {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Error icon",
                modifier = Modifier.align(Alignment.Center)
            )
        },
        modifier = modifier
    )
}
package net.barrage.ragu.ui.screens.camera.crop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CircleCropShape
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import kotlinx.coroutines.launch
import net.barrage.ragu.encodeToByteArray

@Composable
fun CropScreen(
    onImagePicked: (ByteArray) -> Unit,
    inputImage: ImageBitmap?,
    modifier: Modifier = Modifier
) {
    val imageCropper = rememberImageCropper()

    LaunchedEffect(inputImage) {
        launch {
            if (inputImage == null) return@launch
            val result =
                imageCropper.crop(bmp = inputImage) // Suspends until user accepts or cancels cropping
            when (result) {
                CropResult.Cancelled -> {}
                is CropError -> {}
                is CropResult.Success -> {
                    result.bitmap.encodeToByteArray(quality = 100)?.let { onImagePicked(it) }
                }
            }
        }
    }

    val cropState = imageCropper.cropState
    if (cropState != null) ImageCropperDialog(
        state = cropState, style = cropperStyle(
            shapes = listOf(CircleCropShape),
            aspects = listOf(AspectRatio(1, 1))
        ),
        modifier = modifier
    )
}
package net.barrage.ragu.ui.screens.camera.crop

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CircleCropShape
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import kotlinx.coroutines.launch
import net.barrage.ragu.encodeToByteArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropModal(
    onImagePicked: (ByteArray) -> Unit,
    onSheetDismiss: () -> Unit,
    sheetState: SheetState,
    inputImage: ImageBitmap?,
    modifier: Modifier = Modifier
) {
    val imageCropper = rememberImageCropper()
    val cropperStyle = remember {
        cropperStyle(
            shapes = listOf(CircleCropShape),
            aspects = listOf(AspectRatio(1, 1))
        )
    }

    LaunchedEffect(inputImage) {
        launch {
            if (inputImage == null) return@launch
            when (val result = imageCropper.crop(bmp = inputImage)) {
                CropResult.Cancelled -> {
                    onSheetDismiss()
                }

                is CropError -> {}
                is CropResult.Success -> {
                    result.bitmap.encodeToByteArray(quality = 100)?.let { onImagePicked(it) }
                }
            }
        }
    }

    if (inputImage != null) {
        ModalBottomSheet(
            onDismissRequest = onSheetDismiss,
            sheetState = sheetState,
            dragHandle = { },
            shape = RectangleShape,
            modifier = modifier.wrapContentHeight()
        ) {
            val cropState = imageCropper.cropState
            if (cropState != null && sheetState.isVisible) {
                ImageCropperScreen(
                    state = cropState,
                    style = cropperStyle,
                    modifier = modifier
                )
            }
        }
    }
}
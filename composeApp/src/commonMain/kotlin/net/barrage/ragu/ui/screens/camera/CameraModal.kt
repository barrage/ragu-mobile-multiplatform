package net.barrage.ragu.ui.screens.camera

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraModal(
    onSheetDismiss: () -> Unit,
    onClose: () -> Unit,
    onImagePicked: (ByteArray, CameraSource?) -> Unit,
    sheetState: SheetState,
    cameraSource: CameraSource?,
    confirmEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onSheetDismiss,
        sheetState = sheetState,
        dragHandle = { },
        shape = RectangleShape,
        modifier = modifier.wrapContentHeight()
    ) {
        CameraScreen(
            onImagePicked = { byteArray ->
                onImagePicked(byteArray, cameraSource)
            },
            onClose = onClose,
            confirmEnabled = confirmEnabled,
        )
    }
}
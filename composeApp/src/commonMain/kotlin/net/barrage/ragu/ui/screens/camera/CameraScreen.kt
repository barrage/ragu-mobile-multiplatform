package net.barrage.ragu.ui.screens.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.peekaboo.camera.CameraMode
import net.barrage.ragu.ui.peekaboo.camera.PeekabooCamera
import net.barrage.ragu.ui.peekaboo.camera.rememberPeekabooCameraState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.ic_gallery
import ragumultiplatform.composeapp.generated.resources.ic_rotate

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CameraScreen(
    onImagePicked: (ByteArray) -> Unit,
    onClose: () -> Unit,
    confirmEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val profileImageByteArray = remember { mutableStateOf<ByteArray?>(null) }
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                onImagePicked(it)
            }
        },
        resizeOptions = ResizeOptions(resizeThresholdBytes = 2 * 500 * 1024L)
    )
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val state = rememberPeekabooCameraState(
            onCapture = {
                it?.let {
                    if (confirmEnabled) {
                        profileImageByteArray.value = it
                    } else {
                        profileImageByteArray.value = null
                        onImagePicked(it)
                    }
                }
            }, initialCameraMode = CameraMode.Front
        )
        if (profileImageByteArray.value != null) {
            Image(
                bitmap = profileImageByteArray.value!!.decodeToImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { })
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Card(
                    onClick = { scope.launch { profileImageByteArray.value = null } },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp).padding(12.dp)
                    )
                }
                Card(
                    onClick = { onImagePicked(profileImageByteArray.value!!) },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp).padding(12.dp)
                    )
                }
            }
        } else {
            PeekabooCamera(
                state = state,
                modifier = Modifier.fillMaxSize(),
                permissionDeniedContent = {
                    scope.launch {
                    }
                },
            )
            Box(
                modifier = Modifier.fillMaxSize()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { })
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { })
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                Card(
                    onClick = { scope.launch { singleImagePicker.launch() } },
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_gallery),
                        contentDescription = null,
                        modifier = Modifier.size(58.dp).padding(12.dp)
                    )
                }
                Card(onClick = { state.capture() }, shape = CircleShape) {
                    Box(modifier = Modifier.size(72.dp).padding(12.dp))
                }
                Card(onClick = {
                    state.toggleCamera()
                }, shape = CircleShape) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_rotate),
                        contentDescription = null,
                        modifier = Modifier.size(58.dp).padding(12.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { })
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp).statusBarsPadding(),
            horizontalArrangement = Arrangement.Start
        ) {
            Card(onClick = onClose, shape = CircleShape) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp).padding(8.dp)
                )
            }
        }
    }
}
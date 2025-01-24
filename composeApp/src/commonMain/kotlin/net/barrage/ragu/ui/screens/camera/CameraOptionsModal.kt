package net.barrage.ragu.ui.screens.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.ic_camera
import ragumultiplatform.composeapp.generated.resources.ic_gallery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraOptionsModal(
    onImagePicked: (ByteArray, CameraSource?) -> Unit,
    onSheetDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    onCameraClick: () -> Unit,
    sheetState: SheetState,
    cameraSource: CameraSource?,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val singleImagePicker = rememberImagePickerLauncher(selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                onImagePicked(it, cameraSource)
            }
        })
    ModalBottomSheet(
        onDismissRequest = onSheetDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onCameraClick() }
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_camera),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).padding(8.dp),
                    )
                }
                Text(
                    text = "Capture an image",
                    style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms(),
                    modifier = Modifier.padding(12.dp).weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { scope.launch { singleImagePicker.launch() } }
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_gallery),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).padding(8.dp),
                    )
                }
                Text(
                    text = "Choose an image from gallery",
                    style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms(),
                    modifier = Modifier.padding(12.dp).weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onDeleteClick() }
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).padding(8.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms()
                        .copy(color = MaterialTheme.colorScheme.onErrorContainer),
                    modifier = Modifier.padding(12.dp).weight(1f)
                )
            }
        }
    }
}
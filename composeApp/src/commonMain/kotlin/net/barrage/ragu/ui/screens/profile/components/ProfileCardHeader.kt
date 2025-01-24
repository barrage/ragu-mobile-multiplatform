package net.barrage.ragu.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import net.barrage.ragu.ui.screens.camera.CameraSource
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileHeaderViewState
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.delete_chat_description
import ragumultiplatform.composeapp.generated.resources.delete_chat_title
import ragumultiplatform.composeapp.generated.resources.no
import ragumultiplatform.composeapp.generated.resources.yes

@Composable
fun ProfileCardHeader(
    viewState: ProfileHeaderViewState,
    onEditProfileImageClick: (CameraSource) -> Unit,
    onDeleteProfileImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.wrapContentSize()) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(74.dp)
                    .zIndex(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (viewState.profileImage != null) {
                        Image(
                            bitmap = viewState.profileImage,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(50.dp).align(Alignment.Center),
                        )
                    }
                }
            }
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .offset(x = 26.dp, y = 26.dp)
                    .zIndex(2f),
                onClick = { onEditProfileImageClick(CameraSource.PROFILE) },
                border = CardDefaults.outlinedCardBorder(enabled = true)
            ) {
                Box(modifier = Modifier.padding(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Profile Image",
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .offset(x = (-26).dp, y = 26.dp)
                    .zIndex(2f),
                onClick = { onDeleteProfileImageClick() },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Box(modifier = Modifier.padding(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Edit Profile Image",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = viewState.name,
                style = MaterialTheme.typography.headlineMedium.fixCenterTextOnAllPlatforms(),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            StatusIndicator(viewState.active)
        }
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(Res.string.delete_chat_title)) },
            text = { Text(stringResource(Res.string.delete_chat_description)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteProfileImageClick()
                    showDeleteConfirmation = false
                }) {
                    Text(stringResource(Res.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(Res.string.no))
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )
    }
}

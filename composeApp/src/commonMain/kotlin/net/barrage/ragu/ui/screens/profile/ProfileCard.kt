package net.barrage.ragu.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.barrage.ragu.ui.components.CardVariant
import net.barrage.ragu.ui.components.CustomButton
import net.barrage.ragu.ui.components.CustomCard
import net.barrage.ragu.ui.screens.camera.CameraSource
import net.barrage.ragu.ui.screens.history.HistoryScreenStates
import net.barrage.ragu.ui.screens.profile.components.ProfileCardHeader
import net.barrage.ragu.ui.screens.profile.components.ProfileContent
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.error_occurred
import ragumultiplatform.composeapp.generated.resources.sign_out

@Composable
fun ProfileContent(
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onUnauthorized: () -> Unit,
    onEditProfileImageClick: (CameraSource) -> Unit,
    viewState: HistoryScreenStates<ProfileViewState>,
    modifier: Modifier = Modifier,
) {
    CustomCard(
        cardVariant = CardVariant.Secondary(),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            when (viewState) {
                is HistoryScreenStates.Error -> {
                    Text(
                        text = stringResource(Res.string.error_occurred),
                        style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp),
                    )
                }

                is HistoryScreenStates.Idle -> {}

                is HistoryScreenStates.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is HistoryScreenStates.Success -> {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "close",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.clip(CircleShape).clickable { onCloseClick() },
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        ProfileCardHeader(
                            modifier = Modifier.padding(vertical = 16.dp),
                            onEditProfileImageClick = onEditProfileImageClick,
                            viewState = viewState.data.header,
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        ProfileContent(viewState = viewState.data.content)
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        CustomButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.padding(top = 16.dp).align(Alignment.End),
                        ) {
                            Text(
                                text = stringResource(Res.string.sign_out),
                                style =
                                MaterialTheme.typography.titleMedium
                                    .fixCenterTextOnAllPlatforms(),
                            )
                        }
                    }
                }

                is HistoryScreenStates.Unauthorized -> {
                    LaunchedEffect(Unit) {
                        onUnauthorized()
                    }
                }
            }
        }
    }
}

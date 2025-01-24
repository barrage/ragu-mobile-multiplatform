package net.barrage.ragu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.rememberRevealCanvasState
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.compose.BindEffect
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.compose.ext.DeepLinkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.main.MainContent
import net.barrage.ragu.ui.main.Overlays
import net.barrage.ragu.ui.main.navigateToLogin
import net.barrage.ragu.ui.main.rememberAppState
import net.barrage.ragu.ui.screens.camera.CameraModal
import net.barrage.ragu.ui.screens.camera.CameraOptionsModal
import net.barrage.ragu.ui.screens.camera.CameraSource
import net.barrage.ragu.ui.theme.RaguTheme
import net.barrage.ragu.utils.SnackbarHelper
import net.barrage.ragu.utils.coreComponent
import net.barrage.ragu.utils.debugLogError
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.camera_permission_denied
import ragumultiplatform.composeapp.generated.resources.message_evaluated

/**
 * The main composable function for the application.
 * It sets up the app's theme, handles deep links, and manages the main content and overlays.
 *
 * @param modifier Modifier to be applied to the app's root composable.
 * @param onThemeChange Callback function to be invoked when the theme changes. It receives a Boolean
 *                      indicating whether dark mode is enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    onThemeChange: ((Boolean) -> Unit)? = null,
    onInputEnabled: ((Boolean) -> Unit)? = null
) {
    val appState = rememberAppState()
    var deepLink by remember { mutableStateOf<DeepLink?>(null) }
    var isDarkTheme by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf(White) }
    var selectedVariant by remember { mutableStateOf(PaletteStyle.TonalSpot) }
    var isThemeLoaded by remember { mutableStateOf(false) }
    var profileVisible by remember { mutableStateOf(false) }
    var shouldShowOnboardingTutorial by remember { mutableStateOf(false) }
    val revealCanvasState = rememberRevealCanvasState()
    val inputEnabled = remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessages = mapOf(
        Res.string.message_evaluated to stringResource(Res.string.message_evaluated),
        Res.string.camera_permission_denied to stringResource(Res.string.camera_permission_denied)
    )
    val cameraModalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val cameraOptionsModalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var cameraModalBottomSheetVisible by
    remember { mutableStateOf(cameraModalBottomSheetState.isVisible) }

    var cameraOptionsModalBottomSheetVisible by
    remember { mutableStateOf(cameraOptionsModalBottomSheetState.isVisible) }

    var cameraSource by remember { mutableStateOf<CameraSource?>(null) }

    var navigateToSettings by remember { mutableStateOf(false) }

    DeepLinkListener { deepLink = it }
    LaunchedEffect(Unit) {
        isDarkTheme = coreComponent.appPreferences.getDarkModeEnabled()
        selectedTheme = coreComponent.appPreferences.getThemeColor()
        selectedVariant = coreComponent.appPreferences.getThemeVariant()
        shouldShowOnboardingTutorial =
            coreComponent.appPreferences.getShouldShowOnboardingTutorial()
        isThemeLoaded = true
        SnackbarHelper.initialize(
            snackbarHostState,
            appState.coroutineScope,
            getString = { snackbarMessages[it] ?: "" }
        )
    }
    LaunchedEffect(isDarkTheme) { onThemeChange?.invoke(isDarkTheme) }
    BindEffect(appState.permissionController)
    AnimatedVisibility(isThemeLoaded, enter = fadeIn() + expandVertically()) {
        RaguTheme(
            seedColor = selectedTheme,
            useDarkTheme = isDarkTheme,
            style = selectedVariant,
        ) {
            RevealCanvas(
                modifier = Modifier.fillMaxSize(),
                revealCanvasState = revealCanvasState,
            ) {
                Surface(modifier = modifier) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MainContent(
                            appState = appState,
                            deepLink = deepLink,
                            currentTheme = selectedTheme,
                            currentVariant = selectedVariant,
                            isDarkMode = isDarkTheme,
                            profileVisible = profileVisible,
                            onSelectThemeClick = {
                                selectedTheme = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.saveThemeColor(it)
                                }
                            },
                            onSelectVariantClick = {
                                selectedVariant = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.saveThemeVariant(it)
                                }
                            },
                            onDarkLightModeClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.saveDarkModeEnabled(isDarkTheme)
                                }
                                isDarkTheme = !isDarkTheme
                            },
                            onLogoutSuccess = {
                                selectedTheme = White
                                selectedVariant = PaletteStyle.TonalSpot
                                isDarkTheme = false
                                deepLink = null
                                appState.chatViewModel.clearViewModel()
                                appState.loginViewModel.clearViewModel()
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.clear()
                                    coreComponent.appPreferences.saveThemeColor(selectedTheme)
                                    coreComponent.appPreferences.saveThemeVariant(selectedVariant)
                                    coreComponent.appPreferences.saveDarkModeEnabled(isDarkTheme)
                                }
                                appState.navController.navigateToLogin()
                            },
                            onProfileVisibilityChange = { profileVisible = !profileVisible },
                            revealCanvasState = revealCanvasState,
                            shouldShowOnboardingTutorial = shouldShowOnboardingTutorial,
                            inputEnabled = inputEnabled.value,
                            changeInputEnabled = {
                                inputEnabled.value = it
                                onInputEnabled?.invoke(it)
                            },
                            openCameraModalBottomSheet = { source ->
                                appState.coroutineScope.launch {
                                    cameraSource = source
                                    cameraOptionsModalBottomSheetVisible = true
                                    cameraOptionsModalBottomSheetState.show()
                                }
                            },
                        )
                        Overlays(appState)
                        if (cameraModalBottomSheetVisible) {
                            CameraModal(
                                sheetState = cameraModalBottomSheetState,
                                onSheetDismiss = {
                                    cameraModalBottomSheetVisible = false
                                },
                                onImagePicked = { imageByteArray, source ->
                                    appState.coroutineScope.launch {
                                        when (source) {
                                            CameraSource.PROFILE -> {
                                                appState.chatViewModel.updateAvatar(
                                                    imageByteArray
                                                )
                                                cameraModalBottomSheetState.hide()
                                                cameraModalBottomSheetVisible = false
                                            }

                                            else -> {
                                                // TODO
                                            }
                                        }
                                    }
                                },
                                onClose = {
                                    appState.coroutineScope.launch {
                                        cameraModalBottomSheetState.hide()
                                        cameraModalBottomSheetVisible = false
                                    }
                                },
                                cameraSource = cameraSource,
                            )
                        }
                        if (cameraOptionsModalBottomSheetVisible) {
                            CameraOptionsModal(
                                sheetState = cameraOptionsModalBottomSheetState,
                                onSheetDismiss = {
                                    cameraOptionsModalBottomSheetVisible = false
                                },
                                onImagePicked = { imageByteArray, source ->
                                    appState.coroutineScope.launch {
                                        when (source) {
                                            CameraSource.PROFILE -> {
                                                appState.chatViewModel.updateAvatar(
                                                    imageByteArray
                                                )
                                                cameraOptionsModalBottomSheetState.hide()
                                                cameraOptionsModalBottomSheetVisible = false
                                            }

                                            else -> {
                                                // TODO
                                            }
                                        }
                                    }
                                },
                                cameraSource = cameraSource,
                                onDeleteClick = {
                                    appState.coroutineScope.launch {
                                        cameraOptionsModalBottomSheetState.hide()
                                        cameraOptionsModalBottomSheetVisible = false
                                        appState.chatViewModel.setDeleteAvatarVisible(true)
                                    }
                                },
                                onCameraClick = {
                                    appState.coroutineScope.launch {
                                        if (appState.permissionController.isPermissionGranted(
                                                Permission.CAMERA
                                            )
                                        ) {
                                            cameraOptionsModalBottomSheetState.hide()
                                            cameraOptionsModalBottomSheetVisible = false
                                            cameraModalBottomSheetVisible = true
                                            cameraModalBottomSheetState.show()
                                        } else {
                                            try {
                                                appState.permissionController.providePermission(
                                                    Permission.CAMERA
                                                )
                                                cameraOptionsModalBottomSheetState.hide()
                                                cameraOptionsModalBottomSheetVisible = false
                                                cameraModalBottomSheetVisible = true
                                                cameraModalBottomSheetState.show()
                                            } catch (e: DeniedException) {
                                                debugLogError("Camera permission denied", e)
                                                SnackbarHelper.getInstance()
                                                    .showSnackbar(messageRes = Res.string.camera_permission_denied)
                                                if (!navigateToSettings) {
                                                    navigateToSettings = true
                                                } else {
                                                    appState.permissionController.openAppSettings()
                                                }
                                            } catch (e: DeniedAlwaysException) {
                                                debugLogError("Camera permission denied always", e)
                                                SnackbarHelper.getInstance()
                                                    .showSnackbar(messageRes = Res.string.camera_permission_denied)
                                                if (!navigateToSettings) {
                                                    navigateToSettings = true
                                                } else {
                                                    appState.permissionController.openAppSettings()
                                                }
                                            } catch (e: RequestCanceledException) {
                                                debugLogError(
                                                    "Camera permission request cancelled", e
                                                )
                                                SnackbarHelper.getInstance()
                                                    .showSnackbar(messageRes = Res.string.camera_permission_denied)

                                            }
                                        }
                                    }
                                },
                            )
                        }
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        ) {
                            LaunchedEffect(Unit) {
                                if (profileVisible) profileVisible = false
                            }
                            Snackbar(
                                snackbarData = it,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}
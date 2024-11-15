package net.barrage.chatwhitelabel.ui.main

import ChatScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.theolm.rinku.DeepLink
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.usecase.user.CurrentUserUseCase
import net.barrage.chatwhitelabel.navigation.Chat
import net.barrage.chatwhitelabel.navigation.Empty
import net.barrage.chatwhitelabel.navigation.FellowNavigation
import net.barrage.chatwhitelabel.navigation.Login
import net.barrage.chatwhitelabel.ui.components.keyboardAsState
import net.barrage.chatwhitelabel.ui.screens.login.LoginScreen
import net.barrage.chatwhitelabel.utils.Constants
import net.barrage.chatwhitelabel.utils.getAndroidVersion
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    appState: AppState,
    deepLink: DeepLink?,
    profileVisible: Boolean,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    changeProfileVisibility: () -> Unit,
) {
    val currentUserUseCase: CurrentUserUseCase = koinInject()
    var startDestination by remember { mutableStateOf<String?>(null) }
    val loginViewModel = appState.loginViewModel

    LaunchedEffect(appState.networkAvailable.value) {
        if (!appState.networkAvailable.value || startDestination != null) return@LaunchedEffect
        appState.coroutineScope.launch {
            startDestination =
                if (currentUserUseCase() is Response.Success) {
                    Chat.route
                } else {
                    FellowNavigation.startDestination
                }
        }
    }

    if (startDestination != null) {
        NavHost(
            appState.navController,
            startDestination = startDestination ?: Empty.route,
            modifier = modifier,
        ) {
            composable(Chat.route) {
                ChatScreen(
                    viewModel = appState.chatViewModel,
                    isKeyboardOpen = keyboardAsState().value,
                    scope = appState.coroutineScope,
                    profileVisible = profileVisible,
                    modifier =
                        Modifier.then(
                            if (getAndroidVersion() != -1)
                                Modifier.consumeWindowInsets(PaddingValues())
                            else Modifier
                        ),
                    changeProfileVisibility = changeProfileVisibility,
                    onLogoutSuccess = {
                        appState.loginViewModel.clearViewModel()
                        appState.navController.navigateSingleTopTo(Login.route)
                        onLogoutSuccess()
                    },
                )
            }
            composable(Login.route) {
                val uriHandler = LocalUriHandler.current
                LoginScreen(
                    onGoogleLogin = { codeVerifier ->
                        appState.coroutineScope.launch {
                            codeVerifier.let {
                                val googleUrl = Constants.Auth.getGoogleAuthUrl(it)
                                uriHandler.openUri(googleUrl)
                            }
                        }
                    },
                    deepLink = deepLink,
                    navigateToChat = { appState.navController.navigateSingleTopTo(Chat.route) },
                    viewModel = loginViewModel,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            composable(Empty.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // EmptyScreen()
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().route ?: return@navigate) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

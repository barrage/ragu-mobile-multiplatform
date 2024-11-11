package net.barrage.chatwhitelabel.ui.main

import ChatScreen
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
import net.barrage.chatwhitelabel.navigation.Login
import net.barrage.chatwhitelabel.ui.components.keyboardAsState
import net.barrage.chatwhitelabel.ui.screens.login.LoginScreen
import net.barrage.chatwhitelabel.utils.Constants
import net.barrage.chatwhitelabel.utils.getAndroidVersion
import org.koin.compose.koinInject

@Composable
fun AppNavHost(appState: AppState, deepLink: DeepLink?, modifier: Modifier = Modifier) {
    val currentUserUseCase: CurrentUserUseCase = koinInject()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        appState.coroutineScope.launch {
            startDestination =
                if (currentUserUseCase() is Response.Success) {
                    appState.chatViewModel.loadAllData()
                    Chat.route
                } else {
                    Login.route
                }
        }
    }

    if (startDestination != null) {
        NavHost(
            appState.navController,
            startDestination = startDestination!!,
            modifier = modifier,
        ) {
            composable(Chat.route) {
                ChatScreen(
                    viewModel = appState.chatViewModel,
                    isKeyboardOpen = keyboardAsState().value,
                    scope = appState.coroutineScope,
                    modifier =
                        Modifier.then(
                            if (getAndroidVersion() != -1)
                                Modifier.consumeWindowInsets(PaddingValues())
                            else Modifier
                        ),
                )
            }
            composable(Login.route) {
                val uriHandler = LocalUriHandler.current
                LoginScreen(
                    onGoogleLogin = { uriHandler.openUri(Constants.Auth.getGoogleAuthUrl()) },
                    deepLink = deepLink,
                    navigateToChat = {
                        appState.chatViewModel.loadAllData()
                        appState.navController.navigateSingleTopTo(Chat.route)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
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

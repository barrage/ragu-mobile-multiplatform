package net.barrage.chatwhitelabel.ui.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tmapps.konnection.Konnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.navigation.Chat
import net.barrage.chatwhitelabel.navigation.FellowNavigation
import net.barrage.chatwhitelabel.navigation.NavDestination
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import org.koin.compose.viewmodel.koinViewModel

data class AppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val chatViewModel: ChatViewModel,
    val networkAvailable: State<Boolean>,
    val currentScreen: NavDestination,
    val drawerState: DrawerState,
)

@Composable
fun rememberAppState(): AppState {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val chatViewModel = koinViewModel<ChatViewModel>()
    val networkAvailable = remember { mutableStateOf(false) }
    val konnection = Konnection.instance

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        FellowNavigation.screens.find { it.route == currentDestination?.route } ?: Chat
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            konnection.observeHasConnection().collect { networkAvailable.value = it }
        }
    }

    DisposableEffect(coroutineScope) {
        onDispose { chatViewModel.webSocketChatClient?.disconnect() }
    }

    return remember(navController, coroutineScope, chatViewModel, networkAvailable, currentScreen) {
        AppState(
            navController,
            coroutineScope,
            chatViewModel,
            networkAvailable,
            currentScreen,
            drawerState,
        )
    }
}

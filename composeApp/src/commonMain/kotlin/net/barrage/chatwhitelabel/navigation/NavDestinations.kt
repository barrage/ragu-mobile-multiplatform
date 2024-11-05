package net.barrage.chatwhitelabel.navigation

import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_assistant
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource

interface NavDestination {
    val icon: DrawableResource
    val route: String
    val name: String
}

object Chat : NavDestination {
    override val icon = Res.drawable.ic_assistant
    override val route = "chat"
    override val name = "Chat"
}

object Login : NavDestination {
    override val icon = Res.drawable.ic_assistant
    override val route = "login"
    override val name = "Login"
}

object FellowNavigation {
    val screens = persistentListOf(Chat)
    val startDestination = Login.route
}

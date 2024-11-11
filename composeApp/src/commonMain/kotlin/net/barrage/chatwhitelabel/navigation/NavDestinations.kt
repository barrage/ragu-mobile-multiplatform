package net.barrage.chatwhitelabel.navigation

import kotlinx.collections.immutable.persistentListOf

interface NavDestination {
    val route: String
    val name: String
}

object Chat : NavDestination {
    override val route = "chat"
    override val name = "Chat"
}

object Login : NavDestination {
    override val route = "login"
    override val name = "Login"
}

object Empty : NavDestination {
    override val route = "empty"
    override val name = "Empty"
}

object FellowNavigation {
    val screens = persistentListOf(Chat)
    val startDestination = Login.route
}

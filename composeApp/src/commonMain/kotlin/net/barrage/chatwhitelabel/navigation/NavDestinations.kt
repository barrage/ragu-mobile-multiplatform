package net.barrage.chatwhitelabel.navigation

import kotlinx.collections.immutable.persistentListOf

/**
 * Represents a navigation destination in the app.
 */
interface NavDestination {
    /** The route identifier for this destination */
    val route: String

    /** The display name for this destination */
    val name: String
}

/**
 * Represents the Chat screen destination.
 */
object Chat : NavDestination {
    override val route = "chat"
    override val name = "Chat"
}

/**
 * Represents the Login screen destination.
 */
object Login : NavDestination {
    override val route = "login"
    override val name = "Login"
}

/**
 * Represents an Empty screen destination.
 */
object Empty : NavDestination {
    override val route = "empty"
    override val name = "Empty"
}

/**
 * Contains navigation-related information for the app.
 */
object FellowNavigation {
    /** List of all available screen destinations */
    val screens = persistentListOf(Chat, Login, Empty)

    /** The initial destination route when the app starts */
    val startDestination = Login.route
}
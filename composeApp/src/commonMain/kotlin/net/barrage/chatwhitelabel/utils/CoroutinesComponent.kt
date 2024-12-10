package net.barrage.chatwhitelabel.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Interface defining the core coroutine-related components for the application.
 * This interface provides access to a main dispatcher and an application-wide CoroutineScope.
 */
interface CoroutinesComponent {
    /**
     * The main immediate dispatcher for UI-related coroutines.
     * This dispatcher runs tasks immediately if already on the main thread.
     */
    val mainImmediateDispatcher: CoroutineDispatcher

    /**
     * An application-wide CoroutineScope for launching coroutines that are not bound to any specific lifecycle.
     * This scope uses a SupervisorJob to prevent failures of one coroutine from cancelling others.
     */
    val applicationScope: CoroutineScope
}

/**
 * Internal implementation of the CoroutinesComponent interface.
 * This class is responsible for creating and managing the coroutine-related components.
 */
internal class CoroutinesComponentImpl private constructor() : CoroutinesComponent {

    companion object {
        /**
         * Factory method to create a new instance of CoroutinesComponent.
         *
         * @return A new instance of CoroutinesComponent.
         */
        fun create(): CoroutinesComponent = CoroutinesComponentImpl()
    }

    /**
     * Implementation of the main immediate dispatcher.
     * Uses Dispatchers.Main.immediate for immediate execution on the main thread when possible.
     */
    override val mainImmediateDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate

    /**
     * Implementation of the application-wide CoroutineScope.
     * Uses a SupervisorJob combined with the mainImmediateDispatcher for fault tolerance.
     */
    override val applicationScope: CoroutineScope
        get() = CoroutineScope(SupervisorJob() + mainImmediateDispatcher)
}
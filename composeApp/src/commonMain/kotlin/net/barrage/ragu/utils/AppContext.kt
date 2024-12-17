package net.barrage.ragu.utils

/**
 * AppContext is a singleton object that manages the application's core component.
 * It provides a centralized point for initializing and accessing the core component
 * throughout the application.
 */
object AppContext {
    /**
     * The private backing field for the core component.
     * It's nullable to allow for lazy initialization.
     */
    private var _coreComponent: CoreComponent? = null

    /**
     * Provides access to the core component.
     * Throws an error if accessed before initialization.
     *
     * @throws IllegalStateException if accessed before [init] is called.
     * @return The initialized [CoreComponent].
     */
    val coreComponent: CoreComponent
        get() = _coreComponent ?: error("Make sure to call AppContext.init()")

    /**
     * Initializes the core component.
     * This method should be called early in the application's lifecycle,
     * typically during app startup.
     */
    fun init() {
        _coreComponent = CoreComponentImpl()
    }
}

/**
 * A global accessor for the core component.
 * This property allows for easy access to the core component from anywhere in the application.
 *
 * @return The initialized [CoreComponent] from [AppContext].
 */
val coreComponent: CoreComponent
    get() = AppContext.coreComponent
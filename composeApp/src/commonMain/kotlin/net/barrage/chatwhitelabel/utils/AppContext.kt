package net.barrage.chatwhitelabel.utils

object AppContext {
    private var _coreComponent: CoreComponent? = null
    val coreComponent: CoreComponent
        get() = _coreComponent ?: error("Make sure to call AppContext.init()")

    fun init() {
        _coreComponent = CoreComponentImpl()
    }
}

val coreComponent: CoreComponent
    get() = AppContext.coreComponent

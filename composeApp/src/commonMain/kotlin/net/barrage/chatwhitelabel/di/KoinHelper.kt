package net.barrage.chatwhitelabel.di

import org.koin.core.context.startKoin

object KoinHelper {

    fun initKoin() = startKoin { modules(allModules()) }
}

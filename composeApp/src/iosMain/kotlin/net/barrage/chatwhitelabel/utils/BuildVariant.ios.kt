package net.barrage.chatwhitelabel.utils

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class) actual val isDebug: Boolean = Platform.isDebugBinary

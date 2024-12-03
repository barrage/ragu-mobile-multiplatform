package net.barrage.chatwhitelabel.ui.screens.login

import org.jetbrains.compose.resources.StringResource

sealed class LoginScreenState {
    data object Idle : LoginScreenState()

    data object Loading : LoginScreenState()

    data object Success : LoginScreenState()

    data class Error(val message: String? = null, val messageRes: StringResource) :
        LoginScreenState()
}

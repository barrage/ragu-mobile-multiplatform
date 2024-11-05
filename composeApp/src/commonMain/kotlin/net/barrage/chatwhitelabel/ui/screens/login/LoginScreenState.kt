package net.barrage.chatwhitelabel.ui.screens.login

sealed class LoginScreenState {
    data object Idle : LoginScreenState()

    data object Loading : LoginScreenState()

    data object Success : LoginScreenState()

    data class Error(val message: String) : LoginScreenState()
}

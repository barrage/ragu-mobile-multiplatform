package net.barrage.chatwhitelabel.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.usecase.auth.LoginUseCase
import net.barrage.chatwhitelabel.domain.usecase.user.CurrentUserUseCase
import net.barrage.chatwhitelabel.utils.TokenStorage

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    var loginState by mutableStateOf<LoginScreenState>(LoginScreenState.Idle)
        private set

    fun login(code: String) {
        viewModelScope.launch {
            loginState = LoginScreenState.Loading
            loginState =
                when (val result = loginUseCase(code)) {
                    is Response.Success -> {
                        tokenStorage.saveCookie(result.data.value)
                        getCurrentUser()
                        LoginScreenState.Loading
                    }

                    is Response.Failure -> {
                        LoginScreenState.Error(result.e?.message ?: "Unknown error occurred")
                    }

                    else -> {
                        LoginScreenState.Error("Unexpected error occurred")
                    }
                }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            loginState =
                when (val result = currentUserUseCase()) {
                    is Response.Success -> {
                        LoginScreenState.Success
                    }

                    is Response.Failure -> {
                        LoginScreenState.Error(result.e?.message ?: "Unknown error occurred")
                    }

                    else -> {
                        LoginScreenState.Error("Unexpected error occurred")
                    }
                }
        }
    }
}

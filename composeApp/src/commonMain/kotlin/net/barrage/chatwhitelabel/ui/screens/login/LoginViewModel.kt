package net.barrage.chatwhitelabel.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.usecase.auth.LoginUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LogoutUseCase
import net.barrage.chatwhitelabel.utils.TokenStorage

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
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
                        Napier.d(result.toString())
                        tokenStorage.saveCookie(result.data.value)
                        LoginScreenState.Success
                    }

                    is Response.Failure -> {
                        Napier.d(result.toString())
                        LoginScreenState.Error(result.e?.message ?: "Unknown error occurred")
                    }

                    else -> {
                        Napier.d(result.toString())
                        LoginScreenState.Error("Unexpected error occurred")
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch { logoutUseCase() }
    }
}

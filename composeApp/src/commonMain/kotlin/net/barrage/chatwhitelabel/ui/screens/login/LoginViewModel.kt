package net.barrage.chatwhitelabel.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.usecase.auth.LoginUseCase
import net.barrage.chatwhitelabel.domain.usecase.user.CurrentUserUseCase
import net.barrage.chatwhitelabel.utils.PKCEUtil
import net.barrage.chatwhitelabel.utils.TokenStorage
import net.barrage.chatwhitelabel.utils.debugLogError

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginScreenState>(LoginScreenState.Idle)
    val loginState: StateFlow<LoginScreenState> = _loginState.asStateFlow()

    suspend fun generateCodeVerifier(): String {
        val codeVerifier = PKCEUtil.generateCodeVerifier()
        tokenStorage.saveCodeVerifier(codeVerifier)
        return codeVerifier
    }

    suspend fun login(code: String) {
        _loginState.value = LoginScreenState.Loading
        val codeVerifier = tokenStorage.getCodeVerifier()

        if (codeVerifier == null) {
            _loginState.value = LoginScreenState.Error("Code verifier is null")
            debugLogError("Login failed: Code verifier is null")
            return
        }

        _loginState.value =
            when (val result = loginUseCase(code, codeVerifier)) {
                is Response.Success -> {
                    clearCodeVerifier()
                    tokenStorage.saveCookie(result.data.value)
                    getCurrentUser()
                    LoginScreenState.Loading
                }

                is Response.Failure -> {
                    debugLogError("Login failed", result.e)
                    clearCodeVerifier()
                    LoginScreenState.Error(result.e?.message ?: "Unknown error occurred")
                }

                else -> {
                    debugLogError("Unexpected login result")
                    clearCodeVerifier()
                    LoginScreenState.Error("Unexpected error occurred")
                }
            }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _loginState.value =
                when (val result = currentUserUseCase()) {
                    is Response.Success -> LoginScreenState.Success
                    is Response.Failure -> {
                        debugLogError("Failed to get current user", result.e)
                        LoginScreenState.Error(result.e?.message ?: "Unknown error occurred")
                    }

                    else -> {
                        debugLogError("Unexpected result when getting current user")
                        LoginScreenState.Error("Unexpected error occurred")
                    }
                }
        }
    }

    fun clearViewModel() {
        _loginState.value = LoginScreenState.Idle
        clearCodeVerifier()
    }

    private fun clearCodeVerifier() {
        viewModelScope.launch { tokenStorage.clearCodeVerifier() }
    }

    override fun onCleared() {
        super.onCleared()
        clearCodeVerifier()
    }
}

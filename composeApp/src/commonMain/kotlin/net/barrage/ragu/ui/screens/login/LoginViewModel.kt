package net.barrage.ragu.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.usecase.auth.LoginUseCase
import net.barrage.ragu.domain.usecase.user.CurrentUserUseCase
import net.barrage.ragu.utils.DeepLinkParser
import net.barrage.ragu.utils.PKCEUtil
import net.barrage.ragu.utils.TokenStorage
import net.barrage.ragu.utils.debugLogError
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.code_verifier_null
import ragumultiplatform.composeapp.generated.resources.unexpected_error

/**
 * ViewModel responsible for managing the login process and state.
 *
 * @property loginUseCase Use case for handling the login process
 * @property currentUserUseCase Use case for fetching the current user's information
 * @property tokenStorage Storage mechanism for managing authentication tokens
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginScreenState>(LoginScreenState.Loading)

    init {
        viewModelScope.launch {
            val deepLink = tokenStorage.getDeepLink()
            if (deepLink != null) {
                login(deepLink)
            }
        }
    }

    /**
     * Represents the current state of the login process.
     */
    val loginState: StateFlow<LoginScreenState> = _loginState.asStateFlow()

    /**
     * Generates a code verifier for PKCE (Proof Key for Code Exchange) authentication.
     *
     * @return The generated code verifier string
     */
    suspend fun generateCodeVerifier(): String {
        val codeVerifier = PKCEUtil.generateCodeVerifier()
        tokenStorage.saveCodeVerifier(codeVerifier)
        return codeVerifier
    }

    suspend fun saveDeepLink(deepLink: String) {
        tokenStorage.saveDeepLink(deepLink)
    }

    fun tryLogin() {
        viewModelScope.launch {
            val deepLink = tokenStorage.getDeepLink()
            if (deepLink != null) {
                _loginState.value = LoginScreenState.Loading
                login(deepLink)
            } else {
                _loginState.value = LoginScreenState.Idle
            }
        }
    }

    suspend fun saveProvider(provider: String) {
        tokenStorage.saveProvider(provider)
    }

    private suspend fun getProvider(): String? {
        return tokenStorage.getProvider()
    }

    /**
     * Initiates the login process with the provided authorization code.
     *
     * @param deepLink The authorization code received from the authentication server
     */

    fun login(deepLink: String) {
        viewModelScope.launch {
            val code = DeepLinkParser.extractCodeFromDeepLink(deepLink)

            _loginState.value = LoginScreenState.Loading

            val codeVerifier = try {
                tokenStorage.getCodeVerifier()
            } catch (e: Exception) {
                debugLogError("Failed to get code verifier", e)
                null
            }
            val currentProvider = getProvider()

            if (codeVerifier == null) {
                _loginState.value =
                    LoginScreenState.Error(messageRes = Res.string.code_verifier_null)
                debugLogError("Login failed: Code verifier is null")
                return@launch
            }
            if (currentProvider == null) {
                _loginState.value = LoginScreenState.Error(messageRes = Res.string.unexpected_error)
                debugLogError("Login failed: Provider is null")
                return@launch
            }
            val loginResult =
                loginUseCase(
                    code = code ?: "", codeVerifier = codeVerifier,
                    grantType = "authorization_code",
                    provider = currentProvider,
                    source = "android",
                )

            _loginState.value = when (loginResult) {
                is Response.Success -> {
                    clearCodeVerifier()
                    tokenStorage.saveCookie(loginResult.data.value)
                    getCurrentUser()
                    LoginScreenState.Loading
                }

                is Response.Failure -> {
                    debugLogError("Login failed", loginResult.e)
                    clearCodeVerifier()
                    LoginScreenState.Error(
                        message = loginResult.e?.message,
                        messageRes = Res.string.unexpected_error,
                    )
                }

                is Response.Loading -> LoginScreenState.Loading
                else -> {
                    debugLogError("Unexpected login result")
                    clearCodeVerifier()
                    LoginScreenState.Error(messageRes = Res.string.unexpected_error)
                }
            }
            tokenStorage.clearDeepLink()
            tokenStorage.clearProvider()
            tokenStorage.clearCodeVerifier()
        }
    }

    /**
     * Fetches the current user's information.
     */
    private fun getCurrentUser() {
        viewModelScope.launch {
            currentUserUseCase(withAvatar = false).collectLatest { response ->
                _loginState.value =
                    when (response) {
                        is Response.Success -> LoginScreenState.Success
                        is Response.Failure -> {
                            debugLogError("Failed to get current user", response.e)
                            LoginScreenState.Error(
                                message = response.e?.message,
                                messageRes = Res.string.unexpected_error,
                            )
                        }

                        else -> {
                            debugLogError("Unexpected result when getting current user")
                            LoginScreenState.Error(messageRes = Res.string.unexpected_error)
                        }

                    }
            }
        }
    }

    /**
     * Clears the ViewModel state, resetting it to the initial state.
     */
    fun clearViewModel() {
        _loginState.value = LoginScreenState.Idle
        clearCodeVerifier()
    }

    /**
     * Clears the stored code verifier.
     */
    private fun clearCodeVerifier() {
        viewModelScope.launch { tokenStorage.clearCodeVerifier() }
    }

    /**
     * Called when the ViewModel is about to be destroyed.
     * Ensures that the code verifier is cleared for security reasons.
     */
    override fun onCleared() {
        super.onCleared()
        clearCodeVerifier()
    }
}
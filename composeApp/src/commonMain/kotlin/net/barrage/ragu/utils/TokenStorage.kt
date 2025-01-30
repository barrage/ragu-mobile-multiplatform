package net.barrage.ragu.utils

import net.barrage.ragu.utils.preferences.AppPreferences

/**
 * Interface defining operations for storing and retrieving authentication tokens.
 */
interface TokenStorage {
    /**
     * Saves the authentication cookie.
     * @param cookie The cookie string to be saved.
     */
    suspend fun saveCookie(cookie: String)

    /**
     * Retrieves the saved authentication cookie.
     * @return The saved cookie string, or null if not present.
     */
    suspend fun getCookie(): String?

    /**
     * Clears the saved authentication cookie.
     */
    suspend fun clearCookie()

    /**
     * Saves the PKCE code verifier.
     * @param codeVerifier The code verifier string to be saved.
     */
    suspend fun saveCodeVerifier(codeVerifier: String)

    /**
     * Retrieves the saved PKCE code verifier.
     * @return The saved code verifier string, or null if not present.
     */
    suspend fun getCodeVerifier(): String?

    /**
     * Clears the saved PKCE code verifier.
     */
    suspend fun clearCodeVerifier()

    suspend fun saveDeepLink(deepLink: String)
    suspend fun getDeepLink(): String?
    suspend fun clearDeepLink()

    suspend fun saveProvider(provider: String)
    suspend fun getProvider(): String?
    suspend fun clearProvider()
}

/**
 * In-memory implementation of TokenStorage.
 * Useful for temporary storage or testing purposes.
 */
class InMemoryTokenStorage : TokenStorage {
    private var cookie: String? = null
    private var codeVerifier: String? = null
    private var deepLink: String? = null
    private var provider: String? = null

    override suspend fun saveCookie(cookie: String) {
        this.cookie = cookie
    }

    override suspend fun getCookie(): String? = cookie

    override suspend fun clearCookie() {
        cookie = null
    }

    override suspend fun saveCodeVerifier(codeVerifier: String) {
        this.codeVerifier = codeVerifier
    }

    override suspend fun getCodeVerifier(): String? = codeVerifier

    override suspend fun clearCodeVerifier() {
        codeVerifier = null
    }

    override suspend fun saveDeepLink(deepLink: String) {
        this.deepLink = deepLink
    }

    override suspend fun getDeepLink(): String? = deepLink
    override suspend fun clearDeepLink() {
        deepLink = null
    }

    override suspend fun saveProvider(provider: String) {
        this.provider = provider
    }

    override suspend fun getProvider(): String? = provider
    override suspend fun clearProvider() {
        provider = null
    }
}

/**
 * DataStore-based implementation of TokenStorage.
 * Provides persistent storage of tokens using AppPreferences.
 * @param appPreferences The AppPreferences instance used for persistent storage.
 */
class DataStoreTokenStorage(private val appPreferences: AppPreferences) : TokenStorage {

    override suspend fun saveCookie(cookie: String) {
        appPreferences.saveCookie(cookie)
    }

    override suspend fun getCookie(): String? {
        return appPreferences.getCookie()
    }

    override suspend fun clearCookie() {
        appPreferences.clearCookie()
    }

    override suspend fun saveCodeVerifier(codeVerifier: String) {
        appPreferences.saveCodeVerifier(codeVerifier)
    }

    override suspend fun getCodeVerifier(): String? {
        return appPreferences.getCodeVerifier()
    }

    override suspend fun clearCodeVerifier() {
        appPreferences.clearCodeVerifier()
    }

    override suspend fun saveDeepLink(deepLink: String) {
        appPreferences.saveDeepLink(deepLink)
    }

    override suspend fun getDeepLink(): String? {
        return appPreferences.getDeepLink()
    }

    override suspend fun clearDeepLink() {
        appPreferences.clearDeepLink()
    }

    override suspend fun saveProvider(provider: String) {
        appPreferences.saveProvider(provider)
    }

    override suspend fun getProvider(): String? {
        return appPreferences.getProvider()
    }

    override suspend fun clearProvider() {
        appPreferences.clearProvider()
    }

}
package net.barrage.chatwhitelabel.utils

import net.barrage.chatwhitelabel.utils.preferences.AppPreferences

interface TokenStorage {
    suspend fun saveCookie(cookie: String)

    suspend fun getCookie(): String?

    suspend fun clearCookie()

    suspend fun saveCodeVerifier(codeVerifier: String)

    suspend fun getCodeVerifier(): String?

    suspend fun clearCodeVerifier()
}

class InMemoryTokenStorage : TokenStorage {
    private var cookie: String? = null
    private var codeVerifier: String? = null

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
}

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
}

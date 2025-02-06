package net.barrage.ragu.utils

import RaguMultiplatform.composeApp.BuildConfig

/**
 * Object containing constant values used throughout the application.
 */
object Constants {
    /**
     * The base URL for the API.
     */
    const val BASE_URL = BuildConfig.BASE_URL

    /**
     * Object containing authentication-related constants and utilities.
     */
    object Auth {
        private const val GOOGLE_AUTH_URL = BuildConfig.GOOGLE_AUTH_URL
        private const val AAI_AUTH_URL = BuildConfig.AAI_AUTH_URL
        private const val GOOGLE_CLIENT_ID = BuildConfig.GOOGLE_CLIENT_ID
        private const val AAI_CLIENT_ID = BuildConfig.AAI_CLIENT_ID
        const val REDIRECT_HOST = BuildConfig.REDIRECT_HOST
        const val REDIRECT_PATH = BuildConfig.REDIRECT_PATH
        const val REDIRECT_URI = "https://$REDIRECT_HOST$REDIRECT_PATH"

        private const val RESPONSE_TYPE = "code"
        private const val GOOGLE_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email%20openid"
        private const val AAI_SCOPE = "openid%20email"

        /**
         * Generates the full Google OAuth URL with all necessary parameters.
         */
        suspend fun getGoogleAuthUrl(codeVerifier: String): String {
            return buildString {
                append(GOOGLE_AUTH_URL)
                append("?client_id=").append(GOOGLE_CLIENT_ID)
                append("&redirect_uri=").append(REDIRECT_URI)
                append("&response_type=").append(RESPONSE_TYPE)
                append("&scope=").append(GOOGLE_SCOPE)
                append("&code_challenge=").append(PKCEUtil.generateCodeChallenge(codeVerifier))
                append("&code_challenge_method=").append("S256")
            }
        }

        /**
         * Generates the full AAI OAuth URL with all necessary parameters.
         */
        suspend fun getAaiAuthUrl(codeVerifier: String): String {
            return buildString {
                append(AAI_AUTH_URL)
                append("?client_id=").append(AAI_CLIENT_ID)
                append("&redirect_uri=").append(REDIRECT_URI)
                append("&response_type=").append(RESPONSE_TYPE)
                append("&scope=").append(AAI_SCOPE)
                append("&code_challenge=").append(PKCEUtil.generateCodeChallenge(codeVerifier))
                append("&code_challenge_method=").append("S256")
                append("&access_type=offline")
                append("&include_granted_scopes=true")
            }
        }
    }
}
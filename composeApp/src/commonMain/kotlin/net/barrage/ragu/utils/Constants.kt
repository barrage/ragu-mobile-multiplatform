package net.barrage.ragu.utils

/**
 * Object containing constant values used throughout the application.
 */
object Constants {
    /**
     * The base URL for the API.
     */
    const val BASE_URL = "llmao-kotlin-api-development.barrage.dev"

    /**
     * Object containing authentication-related constants and utilities.
     */
    object Auth {
        /**
         * The URL for Google's OAuth 2.0 authorization endpoint.
         */
        private const val GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"

        /**
         * The URL for AAI's OAuth 2.0 authorization endpoint.
         */
        private const val AAI_AUTH_URL =
            "https://fed-lab.aaiedu.hr/sso/module.php/oidc/authorize.php"

        /**
         * The client ID for the application, used in Google OAuth.
         */
        private const val GOOGLE_CLIENT_ID =
            "983914104581-ndgb7tsdc9eio8rfu1ohsrdprihk7mqi.apps.googleusercontent.com"

        /**
         * The client ID for the application, used in AAI OAuth.
         */
        private const val AAI_CLIENT_ID = "2cc90ca6-d400-4414-95a8-efb3cbb9f37c"

        /**
         * The redirect URI for the OAuth flow.
         */
        const val REDIRECT_PATH = "/oauthredirect"
        const val REDIRECT_URI = "https://llmao-kotlin-api-development.barrage.dev$REDIRECT_PATH"

        /**
         * The response type for the OAuth flow. Set to "code" for authorization code flow.
         */
        private const val RESPONSE_TYPE = "code"

        /**
         * The scopes requested for the Google OAuth flow.
         */
        private const val GOOGLE_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email%20openid"

        /**
         * The scopes requested for the AAI OAuth flow.
         */
        private const val AAI_SCOPE = "openid%20email"

        /**
         * Generates the full Google OAuth URL with all necessary parameters.
         *
         * @param codeVerifier The PKCE code verifier to be used in the OAuth flow.
         * @return A complete Google OAuth URL as a String.
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
         *
         * @param codeVerifier The PKCE code verifier to be used in the OAuth flow.
         * @return A complete AAI OAuth URL as a String.
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
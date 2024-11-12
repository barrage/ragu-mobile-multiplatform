package net.barrage.chatwhitelabel.utils

object Constants {
    const val BASE_URL = "llmao-kotlin-api-staging.m2.barrage.beer"

    object Auth {
        private const val GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val CLIENT_ID =
            "983914104581-ndgb7tsdc9eio8rfu1ohsrdprihk7mqi.apps.googleusercontent.com"
        const val REDIRECT_URI = "https://llmao-kotlin-api-staging.m2.barrage.beer/oauthredirect"
        private const val RESPONSE_TYPE = "code"
        private const val SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email%20openid"

        fun getGoogleAuthUrl(): String {
            return buildString {
                append(GOOGLE_AUTH_URL)
                append("?client_id=").append(CLIENT_ID)
                append("&redirect_uri=").append(REDIRECT_URI)
                append("&response_type=").append(RESPONSE_TYPE)
                append("&scope=").append(SCOPE)
            }
        }
    }

    object AiModels {
        const val GPT_4 = "GPT-4"
        const val GPT_35_TURBO = "GPT-3.5-TURBO"
    }
}

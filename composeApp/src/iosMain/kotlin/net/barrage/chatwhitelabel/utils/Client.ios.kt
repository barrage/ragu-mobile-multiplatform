package net.barrage.chatwhitelabel.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * A platform-specific HttpClient instance configured for WebSocket connections.
 * This is the iOS implementation using the Darwin engine.
 *
 * Features:
 * - Uses Darwin engine
 * - Configures WebSockets with a 5-second ping interval
 */
actual val wsClient: HttpClient
    get() = HttpClient(Darwin) { install(WebSockets) { pingInterval = 5.seconds } }

/**
 * A platform-specific HttpClient instance configured for RESTful API calls.
 * This is the iOS implementation using the Darwin engine.
 *
 * Features:
 * - Uses Darwin engine
 * - Configures timeouts (10 seconds for both socket and request)
 * - Sets default Content-Type header to application/json
 * - Sets default base URL
 * - Installs ContentNegotiation with JSON configuration
 * - Installs logging in debug mode
 */
actual val restClient: HttpClient
    get() =
        HttpClient(Darwin) {
            install(HttpTimeout) {
                socketTimeoutMillis = 10_000
                requestTimeoutMillis = 10_000
            }
            defaultRequest {
                header("Content-Type", "application/json")
                url("https://${Constants.BASE_URL}/")
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    }
                )
            }
            if (isDebug) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.SIMPLE
                }
            }
        }
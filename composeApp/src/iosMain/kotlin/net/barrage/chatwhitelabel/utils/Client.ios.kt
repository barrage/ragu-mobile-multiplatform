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
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json

actual val wsClient: HttpClient
    get() = HttpClient(Darwin) { install(WebSockets) { pingInterval = 5.seconds } }
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

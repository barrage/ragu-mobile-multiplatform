package net.barrage.ragu.utils

import io.ktor.client.HttpClient

/**
 * A platform-specific HttpClient instance configured for WebSocket connections.
 * This client is expected to be implemented for each supported platform.
 *
 * The implementation should configure the client with appropriate settings
 * for handling WebSocket connections, such as timeouts and connection pooling.
 */
expect val wsClient: HttpClient

/**
 * A platform-specific HttpClient instance configured for RESTful API calls.
 * This client is expected to be implemented for each supported platform.
 *
 * The implementation should configure the client with appropriate settings
 * for handling REST API calls, such as content negotiation, logging, and
 * default headers.
 */
expect val restClient: HttpClient
package net.barrage.ragu.domain

/**
 * A sealed class representing the different states of an API call response.
 *
 * @param T The type of data expected in the API response.
 */
sealed class Response<out T> {
    /**
     * Represents an ongoing API call.
     * This state can be used to show loading indicators in the UI.
     */
    data object Loading : Response<Nothing>()

    /**
     * Represents a successful API call with the response data.
     *
     * @property data The data returned by the API.
     */
    data class Success<out T>(val data: T) : Response<T>()

    /**
     * Represents a failed API call with an optional exception.
     *
     * @property e The exception that occurred during the API call, if any.
     */
    data class Failure(val e: Exception?) : Response<Nothing>()
    data object Unauthorized : Response<Nothing>()
}
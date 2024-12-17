package net.barrage.ragu.utils

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import net.barrage.ragu.domain.Response

/**
 * Executes an API call safely, handling various HTTP response scenarios and exceptions.
 *
 * This function wraps an API call and returns a [Response] object, which can be either
 * a Success or a Failure, depending on the outcome of the API call.
 *
 * @param T The expected type of the successful response body.
 * @param apiCall A suspend lambda function that makes the actual API call and returns an [HttpResponse].
 * @return [Response<T>] A sealed class representing either a successful or failed API call.
 *
 * @throws Nothing This function catches all exceptions and wraps them in [Response.Failure].
 *
 * Usage:
 * ```
 * val result: Response<UserData> = safeApiCall { api.getUserData() }
 * when (result) {
 *     is Response.Success -> handleSuccess(result.data)
 *     is Response.Failure -> handleError(result.e)
 * }
 * ```
 */
suspend inline fun <reified T> safeApiCall(apiCall: () -> HttpResponse): Response<T> {
    return try {
        val response = apiCall()
        if (response.status.value in 200..299) {
            // Successful response (2xx status code)
            Response.Success(response.body() as T)
        } else {
            // Non-2xx status code
            Response.Failure(
                Exception("HTTP error ${response.status.value} ${response.status.description}")
            )
        }
    } catch (e: RedirectResponseException) {
        // 3xx - responses
        Response.Failure(e)
    } catch (e: ClientRequestException) {
        // 4xx - responses
        Response.Failure(e)
    } catch (e: ServerResponseException) {
        // 5xx - responses
        Response.Failure(e)
    } catch (e: Exception) {
        // Unknown errors
        Response.Failure(e)
    }
}
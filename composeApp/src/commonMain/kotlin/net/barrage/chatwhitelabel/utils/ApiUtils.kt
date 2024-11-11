@file:Suppress("TooGenericExceptionCaught")

package net.barrage.chatwhitelabel.utils

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import net.barrage.chatwhitelabel.domain.Response

suspend inline fun <reified T> safeApiCall(apiCall: () -> HttpResponse): Response<T> {
    return try {
        val response = apiCall()
        if (response.status.value in 200..299) {
            Response.Success(response.body() as T)
        } else {
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
        // Unknown error
        Response.Failure(e)
    }
}

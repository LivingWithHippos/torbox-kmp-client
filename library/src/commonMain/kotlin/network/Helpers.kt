package com.github.livingwithhippos.torbox.network

import com.github.livingwithhippos.torbox.model.ApiSuccessResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException

internal suspend fun HttpResponse.safeBodyText(): String? = runCatching { bodyAsText() }.getOrNull()

internal fun HttpStatusCode.isSuccessOrRedirect(): Boolean = value in 200..399

internal fun toFailure(
    throwable: Throwable,
    body: String?,
): ApiResult.Failure =
    when (throwable) {
        is ResponseException -> {
            ApiResult.Failure.Http(
                throwable.response.status.value,
                body,
            )
        }

        is SerializationException -> {
            ApiResult.Failure.Serialization(throwable, body)
        }

        else -> {
            ApiResult.Failure.Network(throwable)
        }
    }

internal suspend inline fun <reified T : ApiSuccessResponse> executeRequest(noinline request: suspend () -> HttpResponse): ApiResult<T> =
    runCatching { request() }.fold(
        onSuccess = { response ->
            if (response.status.isSuccessOrRedirect()) {
                runCatching { response.body<T>() }.fold(
                    onSuccess = { parsed ->
                        if (parsed.success) {
                            ApiResult.Success(parsed)
                        } else {
                            ApiResult.Failure.NoSuccess(parsed)
                        }
                    },
                    onFailure = { toFailure(it, response.safeBodyText()) },
                )
            } else {
                ApiResult.Failure.Http(response.status.value, response.safeBodyText())
            }
        },
        onFailure = { toFailure(it, null) },
    )

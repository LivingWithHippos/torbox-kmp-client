package com.github.livingwithhippos.torbox.impl

import com.github.livingwithhippos.torbox.api.NotificationsApi
import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.NotificationDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.executeRequest
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class NotificationsApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : NotificationsApi {
    override suspend fun rssFeed(token: String): ApiResult<String> =
        withContext(dispatcher) {
            runCatching {
                val response =
                    client.get("notifications/rss") {
                        parameter("token", token)
                    }
                if (response.status.isSuccess()) {
                    ApiResult.Success(response.body<String>())
                } else {
                    ApiResult.Failure.Http(response.status.value, response.bodyAsText())
                }
            }.getOrElse {
                ApiResult.Failure.Network(it)
            }
        }

    override suspend fun jsonFeed(): ApiResult<ApiResponseListDto<NotificationDto>> =
        executeRequest(dispatcher) {
            client.get("notifications/mynotifications")
        }

    override suspend fun clear(notificationId: Int?): ApiResult<ApiResponseDto<String>> =
        executeRequest(dispatcher) {
            if (notificationId != null) {
                client.post("notifications/clear/$notificationId")
            } else {
                client.post("notifications/clear")
            }
        }

    override suspend fun sendTest(): ApiResult<ApiResponseDto<String>> =
        executeRequest(dispatcher) {
            client.post("notifications/test")
        }
}

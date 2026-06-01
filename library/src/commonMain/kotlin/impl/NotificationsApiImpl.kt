package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.NotificationsApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.NotificationDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import com.github.livingwithhippos.torbox.kmp.client.network.noAuth
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class NotificationsApiImpl(
    private val client: HttpClient,
) : NotificationsApi {
    override suspend fun rssFeed(token: String): ApiResult<ApiResponseDto<String>> {
        if (token.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("token must not be blank"),
            )
        }

        return executeRequest {
            client.get("notifications/rss") {
                // RSS notifications use token query param auth, not bearer auth header.
                noAuth()
                parameter("token", token)
            }
        }
    }

    override suspend fun jsonFeed(): ApiResult<ApiResponseListDto<NotificationDto>> =
        executeRequest {
            client.get("notifications/mynotifications")
        }

    override suspend fun clear(notificationId: Int?): ApiResult<ApiResponseDto<String>> =
        executeRequest {
            if (notificationId != null) {
                client.post("notifications/clear/$notificationId")
            } else {
                client.post("notifications/clear")
            }
        }

    override suspend fun sendTest(): ApiResult<ApiResponseDto<String>> =
        executeRequest {
            client.post("notifications/test")
        }
}

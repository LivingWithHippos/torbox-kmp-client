package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.QueuedApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ControlQueuedDownloadsRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.QueuedDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class QueuedApiImpl(
    private val client: HttpClient,
) : QueuedApi {
    override suspend fun downloads(
        bypassCache: Boolean?,
        id: Int?,
        offset: Int?,
        limit: Int?,
        type: String,
    ): ApiResult<ApiResponseListDto<QueuedDownloadDto>> {
        val normalizedType = type.trim().lowercase()
        if (normalizedType !in listOf("torrent", "usenet", "webdl")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("type must be one of: torrent, usenet, webdl"),
            )
        }
        if (id != null && id <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("id must be a positive integer"),
            )
        }
        if (offset != null && offset < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("offset must be >= 0"),
            )
        }
        if (limit != null && limit <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("limit must be a positive integer"),
            )
        }

        return executeRequest {
            client.get("queued/getqueued") {
                if (bypassCache == true) {
                    parameter("bypass_cache", "")
                }
                id?.let { parameter("id", it) }
                offset?.let { parameter("offset", it) }
                limit?.let { parameter("limit", it) }
                parameter("type", normalizedType)
            }
        }
    }

    override suspend fun controlQueued(
        queuedId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        val normalizedOperation = operation.trim().lowercase()
        if (normalizedOperation !in listOf("delete", "start")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("operation must be one of: delete, start"),
            )
        }
        if (queuedId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either queuedId or all = true must be provided"),
            )
        }
        if (queuedId != null && queuedId <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("queuedId must be a positive integer"),
            )
        }
        if (normalizedOperation == "start" && all == true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("start cannot be used with the all parameter"),
            )
        }

        return executeRequest {
            client.post("queued/controlqueued") {
                contentType(ContentType.Application.Json)
                setBody(
                    ControlQueuedDownloadsRequestDto(
                        queuedId = queuedId,
                        operation = normalizedOperation,
                        all = all,
                    ),
                )
            }
        }
    }
}

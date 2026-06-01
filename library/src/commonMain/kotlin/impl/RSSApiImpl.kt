package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.RSSApi
import com.github.livingwithhippos.torbox.kmp.client.model.AddRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ControlRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedItemDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RSSApiImpl(
    private val client: HttpClient,
) : RSSApi {
    override suspend fun add(
        url: String,
        name: String,
        doRegex: String?,
        dontRegex: String?,
        dontOlderThan: Int?,
        scanInterval: Int?,
        passCheck: Boolean?,
        rssType: String?,
        torrentSeeding: Int?,
    ): ApiResult<ApiResponseDto<String>> {
        if (url.isBlank()) {
            return ApiResult.Failure.BadRequest(IllegalArgumentException("url must not be blank"))
        }

        return executeRequest {
            client.post("rss/addrss") {
                contentType(ContentType.Application.Json)
                setBody(
                    AddRssRequestDto(
                        url = url,
                        name = name,
                        doRegex = doRegex,
                        dontRegex = dontRegex,
                        dontOlderThan = dontOlderThan,
                        scanInterval = scanInterval,
                        passCheck = passCheck,
                        rssType = rssType,
                        torrentSeeding = torrentSeeding,
                    ),
                )
            }
        }
    }

    override suspend fun control(
        operation: String,
        rssFeedId: Int,
    ): ApiResult<ApiResponseDto<String>> {
        val normalized = operation.trim().lowercase()
        if (listOf("update", "delete", "resume", "pause").contains(normalized).not()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be one of: \"update\", \"delete\", \"resume\", \"pause\""),
            )
        }

        return executeRequest {
            client.post("rss/controlrss") {
                contentType(ContentType.Application.Json)
                setBody(
                    ControlRssRequestDto(
                        operation = normalized,
                        rssFeedId = rssFeedId,
                    ),
                )
            }
        }
    }

    override suspend fun edit(
        rssFeedId: Int,
        name: String?,
        doRegex: String?,
        dontRegex: String?,
        dontOlderThan: Int?,
        scanInterval: Int?,
        passCheck: Boolean?,
        rssType: String?,
        torrentSeeding: Int?,
    ): ApiResult<ApiResponseDto<String>> =
        executeRequest {
            client.post("rss/modifyrss") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditRssRequestDto(
                        rssFeedId = rssFeedId,
                        name = name,
                        doRegex = doRegex,
                        dontRegex = dontRegex,
                        dontOlderThan = dontOlderThan,
                        scanInterval = scanInterval,
                        passCheck = passCheck,
                        rssType = rssType,
                        torrentSeeding = torrentSeeding,
                    ),
                )
            }
        }

    override suspend fun userFeeds(id: Int?): ApiResult<ApiResponseListDto<RssFeedDto>> {
        if (id != null && id <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("id must be a positive integer"),
            )
        }

        return executeRequest {
            client.get("rss/getfeeds") {
                id?.let { parameter("id", it) }
            }
        }
    }

    override suspend fun feeds(id: Int): ApiResult<ApiResponseListDto<RssFeedItemDto>> {
        if (id <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("id must be a positive integer"),
            )
        }

        return executeRequest {
            client.get("rss/getfeeditems") {
                parameter("id", id)
            }
        }
    }
}

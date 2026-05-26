package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.TorboxApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiSuccessResponse
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException

class TorboxApiImpl(
    private val client: HttpClient,
) : TorboxApi {
    private suspend fun HttpResponse.safeBodyText(): String? = runCatching { bodyAsText() }.getOrNull()

    private fun HttpStatusCode.isSuccessOrRedirect(): Boolean = value in 200..399

    private fun toFailure(
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

    private suspend inline fun <reified T : ApiSuccessResponse> executeRequest(noinline request: suspend () -> HttpResponse): ApiResult<T> =
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

    override suspend fun getTorrents(
        bypassCache: String?,
        id: Int?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<TorrentListResponseDto> =
        executeRequest {
            client.get("torrents/mylist") {
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                if (id != null) {
                    parameter("id", id)
                }
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }

    override suspend fun asyncCreateTorrent(
        fileBytes: ByteArray?,
        fileName: String?,
        magnet: String?,
        seed: Int?,
        allowZip: Boolean?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<GenericResponseDto> {
        if (fileBytes == null && magnet.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either fileBytes or magnet must be provided"),
            )
        } else if (fileBytes != null && !magnet.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Only one between fileBytes or magnet must be provided"),
            )
        }

        return executeRequest {
            client.post("torrents/asynccreatetorrent") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            if (fileBytes != null) {
                                append(
                                    key = "file",
                                    value = fileBytes,
                                    headers =
                                        Headers.build {
                                            append(HttpHeaders.ContentType, "application/x-bittorrent")
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${fileName ?: "file.torrent"}\"",
                                            )
                                        },
                                )
                            }
                            magnet?.takeIf { it.isNotBlank() }?.let { append("magnet", it) }
                            seed?.let { append("seed", seed.toString()) }
                            allowZip?.let { append("allow_zip", allowZip.toString()) }
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        },
                    ),
                )
            }
        }
    }

    override suspend fun createTorrent(
        fileBytes: ByteArray?,
        fileName: String?,
        magnet: String?,
        seed: Int?,
        allowZip: Boolean?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<CreateTorrentResponseDto> {
        if (fileBytes == null && magnet.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either fileBytes or magnet must be provided"),
            )
        } else if (fileBytes != null && !magnet.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Only one between fileBytes or magnet must be provided"),
            )
        }

        return executeRequest {
            client.post("torrents/createtorrent") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            if (fileBytes != null) {
                                append(
                                    key = "file",
                                    value = fileBytes,
                                    headers =
                                        Headers.build {
                                            append(HttpHeaders.ContentType, "application/x-bittorrent")
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${fileName ?: "file.torrent"}\"",
                                            )
                                        },
                                )
                            }
                            magnet?.takeIf { it.isNotBlank() }?.let { append("magnet", it) }
                            seed?.let { append("seed", seed.toString()) }
                            allowZip?.let { append("allow_zip", allowZip.toString()) }
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        },
                    ),
                )
            }
        }
    }

    override suspend fun controlTorrent(
        torrentId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        //
        if (listOf("reannounce", "delete", "resume", "stop_seeding").contains(operation.lowercase()).not()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be one of: reannounce, delete, resume or stop_seeding"),
            )
        } else if (torrentId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a torrent id or all = true must be set"),
            )
        }

        return executeRequest {
            client.post("torrents/controltorrent") {
                contentType(ContentType.Application.Json)
                setBody(
                    TorrentControlRequestDto(
                        torrentId = torrentId,
                        operation = operation.lowercase(),
                        all = all,
                    ),
                )
            }
        }
    }

    override suspend fun requestTorrentDownloadLink(
        token: String,
        torrentId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<DownloadResponseDto> =
        executeRequest {
            client.get("torrents/requestdl") {
                parameter("token", token)
                parameter("torrent_id", torrentId)
                parameter("file_id", fileId)
                parameter("zip_link", zipLink)
                parameter("user_ip", userIp)
                parameter("redirect", redirect)
                parameter("append_name", appendName)
            }
        }

    override suspend fun getTorrentAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<TorrentAvailabilityListResponseDto> =
        executeRequest {
            client.get("torrents/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun getTorrentAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<TorrentAvailabilityObjectResponseDto> =
        executeRequest {
            client.get("torrents/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun getTorrentAvailabilityBatchObject(hashes: List<String>): ApiResult<TorrentAvailabilityObjectResponseDto> =
        executeRequest {
            client.post("torrents/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    TorrentAvailabilityRequestDto(
                        format = "object",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun getTorrentAvailabilityBatchList(hashes: List<String>): ApiResult<TorrentAvailabilityListResponseDto> =
        executeRequest {
            client.post("torrents/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    TorrentAvailabilityRequestDto(
                        format = "list",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun getWebLinks(
        bypassCache: String?,
        id: Int?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<WebDownloadListResponseDto> =
        executeRequest {
            client.get("webdl/mylist") {
                // todo: check if this changes the response format
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                if (id != null) {
                    parameter("id", id)
                }
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }

    override suspend fun createWebLink(
        link: String,
        password: String?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<CreateWebDownloadResponseDto> =
        executeRequest {
            client.post("webdl/createwebdownload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("link", link)
                            password?.takeIf { it.isNotBlank() }?.let { append("password", it) }
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        },
                    ),
                )
            }
        }

    override suspend fun downloadWebLink(
        token: String,
        webId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<DownloadResponseDto> =
        executeRequest {
            client.get("webdl/requestdl") {
                parameter("token", token)
                parameter("web_id", webId)
                parameter("file_id", fileId)
                parameter("zip_link", zipLink)
                parameter("user_ip", userIp)
                parameter("redirect", redirect)
                parameter("append_name", appendName)
            }
        }
}

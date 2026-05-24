package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.TorboxApi
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.*

class TorboxApiImpl(
    private val client: HttpClient,
) : TorboxApi {
    override suspend fun getTorrents(
        bypassCache: String?,
        id: Int?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<TorrentListResponseDto> =
        runCatching {
            client
                .get("torrents/mylist") {
                    if (bypassCache != null) {
                        parameter("bypass_cache", "")
                    }
                    if (id != null) {
                        parameter("id", id)
                    }
                    parameter("offset", offset)
                    parameter("limit", limit)
                }.body<TorrentListResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )

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

        return runCatching {
            client
                .post("torrents/asynccreatetorrent") {
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
                }.body<GenericResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )
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

        return runCatching {
            client
                .post("torrents/createtorrent") {
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
                }.body<CreateTorrentResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )
    }

    override suspend fun controlTorrent(
        torrentId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        //
        if (listOf("reannounce", "delete", "resume", "stop_seeding").contains(operation.lowercase()).not()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be: reannounce, delete, resume or stop_seeding"),
            )
        } else if (torrentId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a torrent id or all = true must be set"),
            )
        }

        return runCatching {
            client
                .post("torrents/controltorrent") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        TorrentControlRequestDto(
                            torrentId = torrentId,
                            operation = operation.lowercase(),
                            all = all,
                        ),
                    )
                }.body<GenericResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )
    }

    override suspend fun downloadTorrent(
        token: String,
        torrentId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<DownloadResponseDto> =
        runCatching {
            client
                .get("torrents/requestdl") {
                    parameter("token", token)
                    parameter("torrent_id", torrentId)
                    parameter("file_id", fileId)
                    parameter("zip_link", zipLink)
                    parameter("user_ip", userIp)
                    parameter("redirect", redirect)
                    parameter("append_name", appendName)
                }.body<DownloadResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )

    override suspend fun getTorrentAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<TorrentAvailabilityListResponseDto> =
        runCatching {
            client
                .get("torrents/checkcached") {
                    hashes.forEach {
                        parameter("hash", it)
                    }
                    parameter("format", "list")
                    parameter("list_files", listFiles)
                }.body<TorrentAvailabilityListResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )

    override suspend fun getTorrentAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<TorrentAvailabilityObjectResponseDto> =
        runCatching {
            client
                .get("torrents/checkcached") {
                    hashes.forEach {
                        parameter("hash", it)
                    }
                    parameter("format", "object")
                    parameter("list_files", listFiles)
                }.body<TorrentAvailabilityObjectResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )

    override suspend fun getWebLinks(
        bypassCache: String?,
        id: Int?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<WebDownloadListResponseDto> =
        runCatching {
            client
                .get("webdl/mylist") {
                    // todo: check if this changes the response format
                    if (bypassCache != null) {
                        parameter("bypass_cache", "")
                    }
                    if (id != null) {
                        parameter("id", id)
                    }
                    parameter("offset", offset)
                    parameter("limit", limit)
                }.body<WebDownloadListResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )

    override suspend fun createWebLink(
        link: String,
        password: String?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<CreateWebDownloadResponseDto> =
        runCatching {
            client
                .post("webdl/createwebdownload") {
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
                }.body<CreateWebDownloadResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )

    override suspend fun downloadWebLink(
        token: String,
        webId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<DownloadResponseDto> =
        runCatching {
            client
                .get("webdl/requestdl") {
                    parameter("token", token)
                    parameter("web_id", webId)
                    parameter("file_id", fileId)
                    parameter("zip_link", zipLink)
                    parameter("user_ip", userIp)
                    parameter("redirect", redirect)
                    parameter("append_name", appendName)
                }.body<DownloadResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) },
        )
}

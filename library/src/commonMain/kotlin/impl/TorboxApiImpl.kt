package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.TorboxApi
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData

class TorboxApiImpl(
    private val client: HttpClient,
) : TorboxApi {

    override suspend fun getTorrents(
        bypassCache: String?,
        offset: Int?,
        limit: Int?
    ): ApiResult<TorrentListResponseDto> = runCatching {
        client.get("torrents/mylist") {
            if (bypassCache != null)
                parameter("bypass_cache", "")
            parameter("offset", offset)
            parameter("limit", limit)
        }.body<TorrentListResponseDto>()
    }.fold(
        onSuccess = { ApiResult.Success(it) },
        onFailure = { ApiResult.Failure.Network(it) }
    )

    override suspend fun createTorrent(
        file: String?,
        magnet: String?,
        seed: Int,
        allowZip: Boolean,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?
    ): ApiResult<CreateTorrentResponseDto> {
        if (file.isNullOrBlank() && magnet.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either file or magnet must be provided")
            )
        }

        return runCatching {
            client.post("torrents/createtorrent") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            // todo: support passing file blob, this is a placeholder
                            file?.takeIf { it.isNotBlank() }?.let { append("file", it) }
                            magnet?.takeIf { it.isNotBlank() }?.let { append("magnet", it) }
                            append("seed", seed.toString())
                            append("allow_zip", allowZip.toString())
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        }
                    )
                )
            }.body<CreateTorrentResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) }
        )
    }

    override suspend fun downloadTorrent(
        token: String,
        torrentId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?
    ): ApiResult<DownloadTorrentResponseDto> = runCatching {
        client.get("torrents/requestdl") {
            parameter("token", token)
            parameter("torrent_id", torrentId)
            parameter("file_id", fileId)
            parameter("zip_link", zipLink)
            parameter("user_ip", userIp)
            parameter("redirect", redirect)
            parameter("append_name", appendName)
        }.body<DownloadTorrentResponseDto>()
    }.fold(
        onSuccess = { ApiResult.Success(it) },
        onFailure = { ApiResult.Failure.Network(it) }
    )

    override suspend fun getTorrentAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean?
    ): ApiResult<TorrentAvailabilityListResponseDto> = runCatching {
        client.get("torrents/checkcached") {
            hashes.forEach {
                parameter("hash", it)
            }
            parameter("format", "list")
            parameter("list_files", listFiles)
        }.body<TorrentAvailabilityListResponseDto>()
    }.fold(
        onSuccess = { ApiResult.Success(it) },
        onFailure = { ApiResult.Failure.Network(it) }
    )

    override suspend fun getTorrentAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?
    ): ApiResult<TorrentAvailabilityObjectResponseDto> = runCatching {
        client.get("torrents/checkcached") {
            hashes.forEach {
                parameter("hash", it)
            }
            parameter("format", "object")
            parameter("list_files", listFiles)
        }.body<TorrentAvailabilityObjectResponseDto>()
    }.fold(
        onSuccess = { ApiResult.Success(it) },
        onFailure = { ApiResult.Failure.Network(it) }
    )

    override suspend fun downloadWebLink(
        link: String,
        password: String?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?
    ): ApiResult<CreateWebDownloadResponseDto> = runCatching {
        client.post("webdl/createwebdownload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("link", link)
                        password?.takeIf { it.isNotBlank() }?.let { append("password", it) }
                        name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                        asQueued?.let { append("as_queued", it.toString()) }
                        addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                    }
                )
            )
        }.body<CreateWebDownloadResponseDto>()
    }.fold(
    onSuccess = { ApiResult.Success(it) },
    onFailure = { ApiResult.Failure.Network(it) }
    )


}
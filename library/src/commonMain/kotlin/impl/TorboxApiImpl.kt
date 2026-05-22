package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.TorboxApi
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
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
        addOnlyIfCached: Boolean
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
                            append("add_only_if_cached", addOnlyIfCached.toString())
                        }
                    )
                )
            }.body<CreateTorrentResponseDto>()
        }.fold(
            onSuccess = { ApiResult.Success(it) },
            onFailure = { ApiResult.Failure.Network(it) }
        )
    }
}
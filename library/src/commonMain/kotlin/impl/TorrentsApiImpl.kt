package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.TorrentsApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditTorrentRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.MagnetToFileRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import com.github.livingwithhippos.torbox.kmp.client.network.isSuccessOrRedirect
import com.github.livingwithhippos.torbox.kmp.client.network.noAuth
import com.github.livingwithhippos.torbox.kmp.client.network.safeBodyText
import com.github.livingwithhippos.torbox.kmp.client.network.toFailure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class TorrentsApiImpl(
    private val client: HttpClient,
) : TorrentsApi {
    override suspend fun listTorrents(
        bypassCache: String?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<ApiResponseListDto<TorrentDto>> =
        executeRequest {
            client.get("torrents/mylist") {
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }

    override suspend fun getTorrent(
        bypassCache: String?,
        id: Int,
    ): ApiResult<ApiResponseDto<TorrentDto>> =
        executeRequest {
            client.get("torrents/mylist") {
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                parameter("id", id)
            }
        }

    override suspend fun asyncCreate(
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

    override suspend fun create(
        fileBytes: ByteArray?,
        fileName: String?,
        magnet: String?,
        seed: Int?,
        allowZip: Boolean?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<ApiResponseDto<CreateTorrentDto>> {
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
        val normalizedOperation = operation.trim().lowercase()
        if (normalizedOperation !in listOf("reannounce", "delete", "resume", "stop_seeding")) {
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
                        operation = normalizedOperation,
                        all = all,
                    ),
                )
            }
        }
    }

    override suspend fun editTorrent(
        torrentId: Int,
        name: String?,
        tags: List<String>?,
        alternativeHashes: List<String>?,
    ): ApiResult<GenericResponseDto> {
        if (name == null && tags == null && alternativeHashes == null) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("At least one between name, tags, or alternativeHashes must be provided"),
            )
        }

        return executeRequest {
            client.put("torrents/edittorrent") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditTorrentRequestDto(
                        torrentId = torrentId,
                        name = name,
                        tags = tags,
                        alternativeHashes = alternativeHashes,
                    ),
                )
            }
        }
    }

    override suspend fun torrentDownloadLink(
        token: String,
        torrentId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<GenericResponseDto> =
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

    override suspend fun listTorrentAvailability(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.get("torrents/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listTorrentAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.get("torrents/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listTorrentAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.post("torrents/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "object",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun listTorrentAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.post("torrents/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "list",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun export(
        torrentId: Int,
        type: String,
    ): ApiResult<ExportTorrentResultDto> {
        if (type != "magnet" && type != "file") {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("type must be \"magnet\" or \"file\""),
            )
        }

        return runCatching {
            client.get("torrents/exportdata") {
                parameter("torrent_id", torrentId)
                parameter("type", type)
            }
        }.fold(
            onSuccess = { response ->
                if (!response.status.isSuccessOrRedirect()) {
                    return@fold ApiResult.Failure.Http(response.status.value, response.safeBodyText())
                }
                if (type == "magnet") {
                    runCatching { response.body<GenericResponseDto>() }.fold(
                        onSuccess = { parsed ->
                            if (parsed.success && parsed.data != null) {
                                ApiResult.Success(ExportTorrentResultDto.Magnet(parsed.data))
                            } else {
                                ApiResult.Failure.NoSuccess(parsed)
                            }
                        },
                        onFailure = { toFailure(it, response.safeBodyText()) },
                    )
                } else {
                    runCatching { ExportTorrentResultDto.File(response.body<ByteArray>()) }.fold(
                        onSuccess = { ApiResult.Success(it) },
                        // don't attempt to stringify binary payloads as error body
                        onFailure = { toFailure(it, null) },
                    )
                }
            },
            onFailure = { toFailure(it, null) },
        )
    }

    override suspend fun magnetToFile(magnet: String): ApiResult<ByteArray> {
        if (magnet.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("magnet must not be blank"),
            )
        }

        return runCatching {
            client.post("torrents/magnettofile") {
                noAuth()
                contentType(ContentType.Application.Json)
                setBody(MagnetToFileRequestDto(magnet = magnet))
            }
        }.fold(
            onSuccess = { response ->
                if (!response.status.isSuccessOrRedirect()) {
                    return@fold ApiResult.Failure.Http(response.status.value, response.safeBodyText())
                }

                runCatching { response.body<ByteArray>() }.fold(
                    onSuccess = { ApiResult.Success(it) },
                    // don't attempt to stringify binary payloads as error body
                    onFailure = { toFailure(it, null) },
                )
            },
            onFailure = { toFailure(it, null) },
        )
    }
}

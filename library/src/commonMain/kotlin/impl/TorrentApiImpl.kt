package com.github.livingwithhippos.torbox.impl

import com.github.livingwithhippos.torbox.api.TorrentApi
import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.ApiResponseMapDto
import com.github.livingwithhippos.torbox.model.CacheDto
import com.github.livingwithhippos.torbox.model.CacheRequestDto
import com.github.livingwithhippos.torbox.model.CreateTorrentDto
import com.github.livingwithhippos.torbox.model.EditTorrentRequestDto
import com.github.livingwithhippos.torbox.model.ExportResultDto
import com.github.livingwithhippos.torbox.model.MagnetToFileRequestDto
import com.github.livingwithhippos.torbox.model.TorrentControlRequestDto
import com.github.livingwithhippos.torbox.model.TorrentDto
import com.github.livingwithhippos.torbox.model.TorrentInfoDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.executeRequest
import com.github.livingwithhippos.torbox.network.isSuccessOrRedirect
import com.github.livingwithhippos.torbox.network.noAuth
import com.github.livingwithhippos.torbox.network.safeBodyText
import com.github.livingwithhippos.torbox.network.toFailure
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

class TorrentApiImpl(
    private val client: HttpClient,
) : TorrentApi {
    override suspend fun list(
        bypassCache: Boolean?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<ApiResponseListDto<TorrentDto>> =
        executeRequest {
            client.get("torrents/mylist") {
                bypassCache?.let { parameter("bypass_cache", it) }
                offset?.let { parameter("offset", it) }
                limit?.let { parameter("limit", it) }
            }
        }

    override suspend fun get(
        bypassCache: Boolean?,
        id: Int,
    ): ApiResult<ApiResponseDto<TorrentDto>> =
        executeRequest {
            client.get("torrents/mylist") {
                bypassCache?.let { parameter("bypass_cache", it) }
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
    ): ApiResult<ApiResponseDto<String>> {
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

    override suspend fun control(
        torrentId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<ApiResponseDto<String>> {
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

    override suspend fun edit(
        torrentId: Int,
        name: String?,
        tags: List<String>?,
        alternativeHashes: List<String>?,
    ): ApiResult<ApiResponseDto<String>> {
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

    override suspend fun downloadLink(
        token: String,
        torrentId: Int,
        fileId: Int?,
        zipLink: Boolean?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<ApiResponseDto<String>> =
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

    override suspend fun listAvailability(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseListDto<CacheDto>> =
        executeRequest {
            client.get("torrents/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseMapDto<CacheDto>> =
        executeRequest {
            client.get("torrents/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listAvailabilityBatchObject(hashes: List<String>): ApiResult<ApiResponseMapDto<CacheDto>> =
        executeRequest {
            client.post("torrents/checkcached") {
                parameter("format", "object")
                contentType(ContentType.Application.Json)
                setBody(
                    CacheRequestDto(
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun listAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheDto>> =
        executeRequest {
            client.post("torrents/checkcached") {
                parameter("format", "list")
                contentType(ContentType.Application.Json)
                setBody(
                    CacheRequestDto(
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun export(
        torrentId: Int,
        type: String,
    ): ApiResult<ExportResultDto> {
        val normalizedType = type.trim().lowercase()
        if (normalizedType !in listOf("magnet", "file")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("type must be \"magnet\" or \"file\""),
            )
        }

        return runCatching {
            client.get("torrents/exportdata") {
                parameter("torrent_id", torrentId)
                parameter("type", normalizedType)
            }
        }.fold(
            onSuccess = { response ->
                if (!response.status.isSuccessOrRedirect()) {
                    return@fold ApiResult.Failure.Http(response.status.value, response.safeBodyText())
                }
                if (normalizedType == "magnet") {
                    runCatching { response.body<ApiResponseDto<String>>() }.fold(
                        onSuccess = { parsed ->
                            if (parsed.success && parsed.data != null) {
                                ApiResult.Success(ExportResultDto.Magnet(parsed.data))
                            } else {
                                ApiResult.Failure.NoSuccess(parsed)
                            }
                        },
                        onFailure = { toFailure(it, response.safeBodyText()) },
                    )
                } else {
                    runCatching { ExportResultDto.Torrent(response.body<ByteArray>()) }.fold(
                        onSuccess = { ApiResult.Success(it) },
                        // don't attempt to stringify binary payloads as error body
                        onFailure = { toFailure(it, null) },
                    )
                }
            },
            onFailure = { toFailure(it, null) },
        )
    }

    override suspend fun info(
        hash: String,
        timeout: Int?,
        cacheLookup: Boolean?,
    ): ApiResult<ApiResponseDto<TorrentInfoDto>> =
        executeRequest {
            client.get("torrents/torrentinfo") {
                parameter("hash", hash)
                parameter("timeout", timeout)
                parameter("use_cache_lookup", cacheLookup)
            }
        }

    override suspend fun fullInfo(
        magnet: String?,
        file: ByteArray?,
        fileName: String?,
        hash: String?,
        timeout: Int?,
        cacheLookup: Boolean?,
        peersOnly: Boolean?,
    ): ApiResult<ApiResponseDto<TorrentInfoDto>> {
        if (file == null && magnet.isNullOrBlank() && hash.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a torrent file, hash or magnet must be provided"),
            )
        } else if ((file != null && hash != null) ||
            (file != null && !magnet.isNullOrBlank()) ||
            (hash != null && !magnet.isNullOrBlank())
        ) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Only one between file, hash or magnet must be provided"),
            )
        }

        // fixme: this does not seem to be working when using a file

        return executeRequest {
            client.post("torrents/torrentinfo") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            if (file != null) {
                                append(
                                    key = "file",
                                    value = file,
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
                            hash?.takeIf { it.isNotBlank() }?.let { append("hash", it) }
                            timeout?.let { append("timeout", it) }
                            cacheLookup?.let { append("use_cache_lookup", it) }
                            peersOnly?.let { append("peers_only", it) }
                        },
                    ),
                )
            }
        }
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

package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.GeneralApi
import com.github.livingwithhippos.torbox.kmp.client.api.IntegrationsApi
import com.github.livingwithhippos.torbox.kmp.client.api.NotificationsApi
import com.github.livingwithhippos.torbox.kmp.client.api.QueuedApi
import com.github.livingwithhippos.torbox.kmp.client.api.RSSApi
import com.github.livingwithhippos.torbox.kmp.client.api.StreamApi
import com.github.livingwithhippos.torbox.kmp.client.api.TorrentsApi
import com.github.livingwithhippos.torbox.kmp.client.api.UsenetApi
import com.github.livingwithhippos.torbox.kmp.client.api.UserApi
import com.github.livingwithhippos.torbox.kmp.client.api.WebApi
import com.github.livingwithhippos.torbox.kmp.client.model.AddRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.AddSearchEngineRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiSuccessResponse
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ChangelogEntryDto
import com.github.livingwithhippos.torbox.kmp.client.model.ControlQueuedDownloadsRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ControlRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateUsenetDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreatedStreamDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeAuthorizationStartDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenDataDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditTorrentRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditUsenetRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditWebRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.HosterDto
import com.github.livingwithhippos.torbox.kmp.client.model.JobResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.JobsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.MagnetToFileRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ModifySearchEngineDto
import com.github.livingwithhippos.torbox.kmp.client.model.ModifySearchEngineRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.NotificationDto
import com.github.livingwithhippos.torbox.kmp.client.model.QueuedDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.model.ReferralDataDto
import com.github.livingwithhippos.torbox.kmp.client.model.RefreshApiTokenRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedItemDto
import com.github.livingwithhippos.torbox.kmp.client.model.SearchEngineControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.SearchEngineDto
import com.github.livingwithhippos.torbox.kmp.client.model.SpeedtestFileDto
import com.github.livingwithhippos.torbox.kmp.client.model.StatsDto
import com.github.livingwithhippos.torbox.kmp.client.model.StreamDto
import com.github.livingwithhippos.torbox.kmp.client.model.SubscriptionDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TransactionDto
import com.github.livingwithhippos.torbox.kmp.client.model.UsenetControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.UsenetDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserSettingsRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserStatsDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.noAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
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
) : TorrentsApi,
    WebApi,
    UsenetApi,
    GeneralApi,
    NotificationsApi,
    QueuedApi,
    UserApi,
    RSSApi,
    IntegrationsApi,
    StreamApi {
    private val uuidRegex =
        Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")

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

    override suspend fun get(
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

    override suspend fun getTorrentAvailabilityList(
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

    override suspend fun getTorrentAvailabilityObject(
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

    override suspend fun getTorrentAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto> =
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

    override suspend fun getTorrentAvailabilityBatchList(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
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

    override suspend fun list(
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

    override suspend fun create(
        link: String,
        password: String?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<ApiResponseDto<CreateWebDownloadDto>> =
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

    override suspend fun requestDownloadLink(
        token: String,
        webId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<GenericResponseDto> =
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

    override suspend fun getWebAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.get("webdl/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun getWebAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.get("webdl/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun getWebAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.post("webdl/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "object",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun getWebAvailabilityBatchList(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.post("webdl/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "list",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun hosters(auth: Boolean): ApiResult<ApiResponseListDto<HosterDto>> =
        executeRequest {
            client.get("webdl/hosters") {
                if (!auth) {
                    noAuth()
                }
            }
        }

    override suspend fun editWeb(
        webId: Int,
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
            client.put("webdl/editwebdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditWebRequestDto(
                        webId = webId,
                        name = name,
                        tags = tags,
                        alternativeHashes = alternativeHashes,
                    ),
                )
            }
        }
    }

    override suspend fun controlWebLink(
        webId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        // just rename this to delete?
        if (listOf("delete").contains(operation.lowercase()).not()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be one of: delete"),
            )
        } else if (webId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a web id or all = true must be set"),
            )
        }

        return executeRequest {
            client.post("webdl/controlwebdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    WebControlRequestDto(
                        webId = webId,
                        operation = operation.lowercase(),
                        all = all,
                    ),
                )
            }
        }
    }

    override suspend fun get(
        bypassCache: String?,
        id: Int?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<UsenetDownloadListResponseDto> =
        executeRequest {
            client.get("usenet/mylist") {
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

    override suspend fun create(
        fileBytes: ByteArray?,
        fileName: String?,
        link: String?,
        name: String?,
        password: String?,
        postProcessing: Int?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<ApiResponseDto<CreateUsenetDto>> {
        if (fileBytes != null && !link.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Only one between fileBytes or link must be provided"),
            )
        } else if (fileBytes == null && link.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either one between fileBytes or link must be provided"),
            )
        }

        return executeRequest {
            client.post("usenet/createusenetdownload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            if (fileBytes != null) {
                                append(
                                    key = "file",
                                    value = fileBytes,
                                    headers =
                                        Headers.build {
                                            append(HttpHeaders.ContentType, "application/x-nzb")
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${fileName ?: "file.nzb"}\"",
                                            )
                                        },
                                )
                            }
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        },
                    ),
                )
            }
        }
    }

    override suspend fun controlUsenet(
        usenetId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        if (listOf("delete").contains(operation.lowercase()).not()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be one of: delete"),
            )
        } else if (usenetId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a usenet id or all = true must be set"),
            )
        }

        return executeRequest {
            client.post("usenet/controlusenetdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    UsenetControlRequestDto(
                        usenetId = usenetId,
                        operation = operation.lowercase(),
                        all = all,
                    ),
                )
            }
        }
    }

    override suspend fun usenetDownloadLink(
        token: String,
        usenetId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<GenericResponseDto> =
        executeRequest {
            client.get("usenet/controlusenetdownload") {
                parameter("token", token)
                parameter("usenet_id", usenetId)
                parameter("file_id", fileId)
                parameter("zip_link", zipLink)
                parameter("user_ip", userIp)
                parameter("redirect", redirect)
                parameter("append_name", appendName)
            }
        }

    override suspend fun getUsenetAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.get("usenet/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun getUsenetAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.get("usenet/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun getUsenetAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.post("usenet/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "object",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun getUsenetAvailabilityBatchList(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.post("usenet/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "list",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun editUsenet(
        usenetId: Int,
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
            client.put("usenet/editusenetdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditUsenetRequestDto(
                        usenetId = usenetId,
                        name = name,
                        tags = tags,
                        alternativeHashes = alternativeHashes,
                    ),
                )
            }
        }
    }

    override suspend fun upStatus(): ApiResult<GenericResponseDto> =
        executeRequest {
            // what happens if the api are failing? does this return success false?
            client.get("https://api.torbox.app") {
                noAuth()
            }
        }

    override suspend fun stats(): ApiResult<ApiResponseDto<StatsDto>> =
        executeRequest {
            client.get("stats") {
                noAuth()
            }
        }

    override suspend fun changelogRSSFeed(): ApiResult<String> =
        runCatching {
            client.get("changelogs/rss") {
                noAuth()
            }
        }.fold(
            onSuccess = { response ->
                if (!response.status.isSuccessOrRedirect()) {
                    ApiResult.Failure.Http(response.status.value, response.safeBodyText())
                } else {
                    runCatching { response.bodyAsText() }.fold(
                        onSuccess = { ApiResult.Success(it) },
                        onFailure = { toFailure(it, null) },
                    )
                }
            },
            onFailure = { toFailure(it, null) },
        )

    override suspend fun changelogJson(): ApiResult<ApiResponseListDto<ChangelogEntryDto>> =
        executeRequest {
            client.get("changelogs/json") {
                noAuth()
            }
        }

    override suspend fun speedtestFiles(
        testLength: String?,
        region: String?,
        userIp: String?,
    ): ApiResult<ApiResponseListDto<SpeedtestFileDto>> {
        val normalizedTestLength = testLength?.trim()?.lowercase()
        if (normalizedTestLength != null && normalizedTestLength !in listOf("short", "long")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("testLength must be one of: short, long"),
            )
        }

        return executeRequest {
            client.get("speedtest") {
                noAuth()
                normalizedTestLength?.let { parameter("test_length", it) }
                region?.takeIf { it.isNotBlank() }?.let { parameter("region", it) }
                userIp?.takeIf { it.isNotBlank() }?.let { parameter("user_ip", it) }
            }
        }
    }

    override suspend fun rssFeed(token: String): ApiResult<String> {
        if (token.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("token must not be blank"),
            )
        }

        return runCatching {
            client.get("notifications/rss") {
                // RSS notifications use token query param auth, not bearer auth header.
                noAuth()
                parameter("token", token)
            }
        }.fold(
            onSuccess = { response ->
                if (!response.status.isSuccessOrRedirect()) {
                    ApiResult.Failure.Http(response.status.value, response.safeBodyText())
                } else {
                    runCatching { response.bodyAsText() }.fold(
                        onSuccess = { ApiResult.Success(it) },
                        onFailure = { toFailure(it, null) },
                    )
                }
            },
            onFailure = { toFailure(it, null) },
        )
    }

    override suspend fun jsonFeed(): ApiResult<ApiResponseListDto<NotificationDto>> =
        executeRequest {
            client.get("notifications/mynotifications")
        }

    override suspend fun clear(notificationId: Int?): ApiResult<GenericResponseDto> =
        executeRequest {
            if (notificationId != null) {
                client.post("notifications/clear/$notificationId")
            } else {
                client.post("notifications/clear")
            }
        }

    override suspend fun sendTest(): ApiResult<GenericResponseDto> =
        executeRequest {
            client.post("notifications/test")
        }

    override suspend fun refreshToken(sessionToken: String): ApiResult<GenericResponseDto> {
        if (sessionToken.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("sessionToken must not be blank"),
            )
        }

        // todo: test this API to check the return type

        return executeRequest {
            client.post("user/refreshtoken") {
                contentType(ContentType.Application.Json)
                setBody(
                    RefreshApiTokenRequestDto(sessionToken = sessionToken),
                )
            }
        }
    }

    override suspend fun data(settings: Boolean?): ApiResult<ApiResponseDto<UserDataDto>> =
        executeRequest {
            client.get("user/me") {
                settings?.let { parameter("settings", it) }
            }
        }

    override suspend fun addReferral(
        referral: String,
        validate: Boolean,
    ): ApiResult<GenericResponseDto> {
        if (referral.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("referral must not be blank"),
            )
        }

        if (validate && !uuidRegex.matches(referral)) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("referral must be a valid UUID"),
            )
        }

        return executeRequest {
            client.post("user/addreferral") {
                parameter("referral", referral)
            }
        }
    }

    override suspend fun confirmationCode(): ApiResult<GenericResponseDto> =
        executeRequest {
            client.get("user/getconfirmation")
        }

    override suspend fun startAuthorizationProcess(app: String?): ApiResult<ApiResponseDto<DeviceCodeAuthorizationStartDto>> =
        executeRequest {
            client.get("user/auth/device/start") {
                noAuth()
                app?.trim()?.takeIf { it.isNotEmpty() }?.let { parameter("app", it) }
            }
        }

    override suspend fun token(deviceCode: String): ApiResult<ApiResponseDto<DeviceCodeTokenDataDto>> {
        if (deviceCode.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("deviceCode must not be blank"),
            )
        }

        return executeRequest {
            client.post("user/auth/device/token") {
                noAuth()
                contentType(ContentType.Application.Json)
                setBody(DeviceCodeTokenRequestDto(deviceCode = deviceCode))
            }
        }
    }

    override suspend fun referral(): ApiResult<ApiResponseDto<ReferralDataDto>> =
        executeRequest {
            client.get("user/referraldata")
        }

    override suspend fun subscriptions(): ApiResult<ApiResponseListDto<SubscriptionDto>> =
        executeRequest {
            client.get("user/subscriptions")
        }

    override suspend fun transactions(): ApiResult<ApiResponseListDto<TransactionDto>> =
        executeRequest {
            client.get("user/transactions")
        }

    override suspend fun transactionPdf(transactionId: String): ApiResult<ByteArray> {
        if (transactionId.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("transactionId must not be blank"),
            )
        }

        return runCatching {
            client.get("user/transaction/pdf") {
                parameter("transaction_id", transactionId)
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

    override suspend fun editSettings(settings: UserSettingsRequestDto): ApiResult<GenericResponseDto> =
        executeRequest {
            client.put("user/settings/editsettings") {
                contentType(ContentType.Application.Json)
                setBody(settings)
            }
        }

    override suspend fun searchEngines(id: Int?): ApiResult<ApiResponseListDto<SearchEngineDto>> =
        executeRequest {
            client.get("user/settings/searchengines") {
                id?.let { parameter("id", it) }
            }
        }

    override suspend fun controlSearchEngines(
        id: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        val normalized = operation.trim().lowercase()
        if (normalized !in listOf("enable", "disable", "check", "delete")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("operation must be one of: enable, disable, check, delete"),
            )
        } else if (id == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either an id or all = true must be provided"),
            )
        }

        return executeRequest {
            client.post("user/settings/controlsearchengines") {
                contentType(ContentType.Application.Json)
                setBody(SearchEngineControlRequestDto(operation = normalized, id = id, all = all))
            }
        }
    }

    override suspend fun modifySearchEngine(
        id: Int,
        type: String?,
        url: String?,
        apikey: String?,
        downloadType: String?,
    ): ApiResult<ApiResponseDto<ModifySearchEngineDto>> {
        if (id <= 0) {
            return ApiResult.Failure.BadRequest(IllegalArgumentException("id must be a positive integer"))
        }

        return executeRequest {
            client.post("user/settings/modifysearchengines") {
                contentType(ContentType.Application.Json)
                setBody(
                    ModifySearchEngineRequestDto(
                        id = id,
                        type = type,
                        url = url,
                        apikey = apikey,
                        downloadType = downloadType,
                    ),
                )
            }
        }
    }

    override suspend fun addSearchEngine(
        type: String?,
        url: String,
        apikey: String?,
        downloadType: String?,
    ): ApiResult<ApiResponseListDto<SearchEngineDto>> {
        if (url.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("url must not be blank"),
            )
        }

        val normalizedDownloadType = downloadType?.trim()?.lowercase()
        if (normalizedDownloadType != null && normalizedDownloadType !in listOf("torrents", "usenet")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("downloadType must be one of: torrents, usenet"),
            )
        }

        return executeRequest {
            client.put("user/settings/addsearchengines") {
                contentType(ContentType.Application.Json)
                setBody(
                    AddSearchEngineRequestDto(
                        type = type,
                        url = url,
                        apikey = apikey,
                        downloadType = normalizedDownloadType,
                    ),
                )
            }
        }
    }

    override suspend fun stats(
        general: Boolean,
        bandwidth: Boolean,
        bandwidthGrouping: String?,
    ): ApiResult<ApiResponseDto<UserStatsDto>> {
        val normalizedGrouping = bandwidthGrouping?.trim()?.lowercase()
        if (normalizedGrouping != null && normalizedGrouping !in listOf("hour", "day", "week", "month")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("bandwidthGrouping must be one of: hour, day, week, month"),
            )
        }

        return executeRequest {
            client.get("user/stats") {
                parameter("general", general)
                parameter("bandwidth", bandwidth)
                normalizedGrouping?.let { parameter("bandwidth_grouping", it) }
            }
        }
    }

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
    ): ApiResult<GenericResponseDto> {
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

    override suspend fun controlRss(
        operation: String,
        rssFeedId: Int,
    ): ApiResult<GenericResponseDto> {
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

    override suspend fun editRss(
        rssFeedId: Int,
        name: String?,
        doRegex: String?,
        dontRegex: String?,
        dontOlderThan: Int?,
        scanInterval: Int?,
        passCheck: Boolean?,
        rssType: String?,
        torrentSeeding: Int?,
    ): ApiResult<GenericResponseDto> =
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

    override suspend fun jobs(hash: String?): ApiResult<JobsResponseDto> {
        val url =
            when {
                hash.isNullOrBlank().not() -> "integration/jobs/$hash"
                else -> "integration/jobs"
            }

        return executeRequest {
            client.get(url)
        }
    }

    override suspend fun job(id: Int): ApiResult<JobResponseDto> =
        executeRequest {
            client.get("integration/job/$id")
        }

    override suspend fun delete(id: Int): ApiResult<GenericResponseDto> =
        executeRequest {
            client.delete("integration/job/$id")
        }

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

    override suspend fun create(
        id: Int,
        fileId: Int,
        type: String,
        chosenSubtitleIndex: Int?,
        chosenAudioIndex: Int,
        chosenResolutionIndex: Int?,
    ): ApiResult<ApiResponseDto<CreatedStreamDto>> {
        val normalizedType = type.trim().lowercase()
        if (id <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("id must be a positive integer"),
            )
        }
        if (fileId < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("fileId must be >= 0"),
            )
        }
        if (normalizedType !in listOf("torrent", "usenet", "webdl")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("type must be one of: torrent, usenet, webdl"),
            )
        }
        if (chosenAudioIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenAudioIndex must be >= 0"),
            )
        }
        if (chosenSubtitleIndex != null && chosenSubtitleIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenSubtitleIndex must be >= 0"),
            )
        }
        if (chosenResolutionIndex != null && chosenResolutionIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenResolutionIndex must be >= 0"),
            )
        }

        return executeRequest {
            client.get("stream/createstream") {
                parameter("id", id)
                parameter("file_id", fileId)
                parameter("type", normalizedType)
                chosenSubtitleIndex?.let { parameter("chosen_subtitle_index", it) }
                parameter("chosen_audio_index", chosenAudioIndex)
                chosenResolutionIndex?.let { parameter("chosen_resolution_index", it) }
            }
        }
    }

    override suspend fun get(
        presignedToken: String,
        token: String,
        chosenSubtitleIndex: Int?,
        chosenAudioIndex: Int?,
        chosenResolutionIndex: Int?,
    ): ApiResult<ApiResponseDto<StreamDto>> {
        if (presignedToken.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("presignedToken must not be blank"),
            )
        }
        if (token.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("token must not be blank"),
            )
        }
        if (chosenSubtitleIndex != null && chosenSubtitleIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenSubtitleIndex must be >= 0"),
            )
        }
        if (chosenAudioIndex != null && chosenAudioIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenAudioIndex must be >= 0"),
            )
        }
        if (chosenResolutionIndex != null && chosenResolutionIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenResolutionIndex must be >= 0"),
            )
        }

        return executeRequest {
            client.get("stream/getstreamdata") {
                parameter("presigned_token", presignedToken)
                parameter("token", token)
                chosenSubtitleIndex?.let { parameter("chosen_subtitle_index", it) }
                chosenAudioIndex?.let { parameter("chosen_audio_index", it) }
                chosenResolutionIndex?.let { parameter("chosen_resolution_index", it) }
            }
        }
    }
}

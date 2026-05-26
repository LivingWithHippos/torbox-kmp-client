package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.GeneralApi
import com.github.livingwithhippos.torbox.kmp.client.api.NotificationsApi
import com.github.livingwithhippos.torbox.kmp.client.api.TorrentsApi
import com.github.livingwithhippos.torbox.kmp.client.api.UsenetApi
import com.github.livingwithhippos.torbox.kmp.client.api.UserApi
import com.github.livingwithhippos.torbox.kmp.client.api.WebApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiSuccessResponse
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ChangelogListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeAuthorizationStartResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditTorrentRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditWebRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.HostersResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.MagnetToFileRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ModifySearchEngineRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ModifySearchEngineResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.NotificationsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ReferralDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.RefreshApiTokenRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.SearchEngineControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.SpeedtestFilesResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.StatsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.SubscriptionResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TransactionResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UsenetDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserSearchEnginesResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserSettingsRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.noAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
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
    UserApi {
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

    override suspend fun requestTorrentDownloadLink(
        token: String,
        torrentId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<GenericResponseDto> =
        executeRequest {
            // todo: check if noAuth is applicable
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
    ): ApiResult<CacheAvailabilityListResponseDto> =
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

    override suspend fun getTorrentAvailabilityBatchList(hashes: List<String>): ApiResult<CacheAvailabilityListResponseDto> =
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

    override suspend fun exportTorrentData(
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
    ): ApiResult<CacheAvailabilityListResponseDto> =
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

    override suspend fun getWebAvailabilityBatchList(hashes: List<String>): ApiResult<CacheAvailabilityListResponseDto> =
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

    override suspend fun getHosters(auth: Boolean): ApiResult<HostersResponseDto> =
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

    override suspend fun getUsenetList(
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

    override suspend fun getUpStatus(): ApiResult<GenericResponseDto> =
        executeRequest {
            // what happens if the api are failing? does this return success false?
            client.get("https://api.torbox.app") {
                noAuth()
            }
        }

    override suspend fun getStats(): ApiResult<StatsResponseDto> =
        executeRequest {
            client.get("stats") {
                noAuth()
            }
        }

    override suspend fun getChangelog(): ApiResult<String> =
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

    override suspend fun getChangelogJson(): ApiResult<ChangelogListResponseDto> =
        executeRequest {
            client.get("changelogs/json") {
                noAuth()
            }
        }

    override suspend fun getSpeedtestFiles(
        testLength: String?,
        region: String?,
        userIp: String?,
    ): ApiResult<SpeedtestFilesResponseDto> {
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

    override suspend fun getNotificationsRss(token: String): ApiResult<String> {
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

    override suspend fun getNotifications(): ApiResult<NotificationsResponseDto> =
        executeRequest {
            client.get("notifications/mynotifications")
        }

    override suspend fun clearNotifications(): ApiResult<GenericResponseDto> =
        executeRequest {
            client.post("notifications/clear")
        }

    override suspend fun clearNotification(notificationId: Int): ApiResult<GenericResponseDto> =
        executeRequest {
            client.post("notifications/clear/$notificationId")
        }

    override suspend fun sendTestNotification(): ApiResult<GenericResponseDto> =
        executeRequest {
            client.post("notifications/test")
        }

    override suspend fun refreshApiToken(sessionToken: String): ApiResult<GenericResponseDto> {
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

    override suspend fun getUserData(settings: Boolean?): ApiResult<UserDataResponseDto> =
        executeRequest {
            client.get("user/me") {
                settings?.let { parameter("settings", it) }
            }
        }

    override suspend fun addReferralToAccount(
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

    override suspend fun getConfirmationCode(): ApiResult<GenericResponseDto> =
        executeRequest {
            client.get("user/getconfirmation")
        }

    override suspend fun startDeviceCodeAuthorizationProcess(app: String?): ApiResult<DeviceCodeAuthorizationStartResponseDto> =
        executeRequest {
            client.get("user/auth/device/start") {
                noAuth()
                app?.trim()?.takeIf { it.isNotEmpty() }?.let { parameter("app", it) }
            }
        }

    override suspend fun getTokenFromDeviceCodeProcess(deviceCode: String): ApiResult<DeviceCodeTokenResponseDto> {
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

    override suspend fun getReferral(): ApiResult<ReferralDataResponseDto> =
        executeRequest {
            client.get("user/referraldata")
        }

    override suspend fun getSubscriptions(): ApiResult<SubscriptionResponseDto> =
        executeRequest {
            client.get("user/subscriptions")
        }

    override suspend fun getTransactions(): ApiResult<TransactionResponseDto> =
        executeRequest {
            client.get("user/transactions")
        }

    override suspend fun getTransactionPdf(transactionId: String): ApiResult<ByteArray> {
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

    override suspend fun getSearchEngines(id: Int?): ApiResult<UserSearchEnginesResponseDto> =
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
    ): ApiResult<ModifySearchEngineResponseDto> {
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
}

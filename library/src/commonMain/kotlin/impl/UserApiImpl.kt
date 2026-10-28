package com.github.livingwithhippos.torbox.impl

import com.github.livingwithhippos.torbox.api.UserApi
import com.github.livingwithhippos.torbox.model.AddSearchEngineRequestDto
import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.DashboardFilterDto
import com.github.livingwithhippos.torbox.model.DeviceCodeAuthorizationStartDto
import com.github.livingwithhippos.torbox.model.DeviceCodeTokenDataDto
import com.github.livingwithhippos.torbox.model.DeviceCodeTokenRequestDto
import com.github.livingwithhippos.torbox.model.ModifySearchEngineDto
import com.github.livingwithhippos.torbox.model.ModifySearchEngineRequestDto
import com.github.livingwithhippos.torbox.model.ReferralDataDto
import com.github.livingwithhippos.torbox.model.RefreshApiTokenRequestDto
import com.github.livingwithhippos.torbox.model.SearchEngineControlRequestDto
import com.github.livingwithhippos.torbox.model.SearchEngineDto
import com.github.livingwithhippos.torbox.model.SubscriptionDto
import com.github.livingwithhippos.torbox.model.TransactionDto
import com.github.livingwithhippos.torbox.model.UserDataDto
import com.github.livingwithhippos.torbox.model.UserSettingsRequestDto
import com.github.livingwithhippos.torbox.model.UserStatsDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.executeRequest
import com.github.livingwithhippos.torbox.network.isSuccessOrRedirect
import com.github.livingwithhippos.torbox.network.noAuth
import com.github.livingwithhippos.torbox.network.safeBodyText
import com.github.livingwithhippos.torbox.network.toFailure
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : UserApi {
    private val uuidRegex =
        Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")

    override suspend fun refreshToken(sessionToken: String): ApiResult<ApiResponseDto<String>> {
        if (sessionToken.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("sessionToken must not be blank"),
            )
        }

        // todo: test this API to check the return type

        return executeRequest(dispatcher) {
            client.post("user/refreshtoken") {
                contentType(ContentType.Application.Json)
                setBody(
                    RefreshApiTokenRequestDto(sessionToken = sessionToken),
                )
            }
        }
    }

    override suspend fun data(settings: Boolean?): ApiResult<ApiResponseDto<UserDataDto>> =
        executeRequest(dispatcher) {
            client.get("user/me") {
                settings?.let { parameter("settings", it) }
            }
        }

    override suspend fun addReferral(
        referral: String,
        validate: Boolean,
    ): ApiResult<ApiResponseDto<String>> {
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

        return executeRequest(dispatcher) {
            client.post("user/addreferral") {
                parameter("referral", referral)
            }
        }
    }

    override suspend fun requestConfirmationCode(): ApiResult<ApiResponseDto<String>> =
        executeRequest(dispatcher) {
            client.get("user/getconfirmation")
        }

    override suspend fun startAuthorizationProcess(app: String?): ApiResult<ApiResponseDto<DeviceCodeAuthorizationStartDto>> =
        executeRequest(dispatcher) {
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

        return executeRequest(dispatcher) {
            client.post("user/auth/device/token") {
                noAuth()
                contentType(ContentType.Application.Json)
                setBody(DeviceCodeTokenRequestDto(deviceCode = deviceCode))
            }
        }
    }

    override suspend fun referral(): ApiResult<ApiResponseDto<ReferralDataDto>> =
        executeRequest(dispatcher) {
            client.get("user/referraldata")
        }

    override suspend fun subscriptions(): ApiResult<ApiResponseListDto<SubscriptionDto>> =
        executeRequest(dispatcher) {
            client.get("user/subscriptions")
        }

    override suspend fun transactions(): ApiResult<ApiResponseListDto<TransactionDto>> =
        executeRequest(dispatcher) {
            client.get("user/transactions")
        }

    override suspend fun transactionPdf(transactionId: String): ApiResult<ByteArray> {
        if (transactionId.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("transactionId must not be blank"),
            )
        }

        return withContext(dispatcher) {
            runCatching {
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
    }

    override suspend fun edit(
        emailNotifications: Boolean?,
        webNotifications: Boolean?,
        mobileNotifications: Boolean?,
        rssNotifications: Boolean?,
        downloadSpeedInTab: Boolean?,
        showTrackerInTorrents: Boolean?,
        stremioQuality: List<Int>?,
        stremioResolution: List<Int>?,
        stremioLanguage: List<Int>?,
        stremioCache: List<Int>?,
        stremioSizeLower: Long?,
        googleDriveFolderId: String?,
        onedriveSavePath: String?,
        discordId: String?,
        discordNotifications: Boolean?,
        stremioAllowAdult: Boolean?,
        stremioSeedTorrents: Int?,
        seedTorrents: Int?,
        allowZipped: Boolean?,
        stremioAllowZipped: Boolean?,
        onefichierFolderId: String?,
        gofileFolderId: String?,
        jdownloaderNotifications: Boolean?,
        webhookNotifications: Boolean?,
        webhookUrl: String?,
        telegramNotifications: Boolean?,
        telegramId: String?,
        megaEmail: String?,
        megaPassword: String?,
        stremioSort: String?,
        cdnSelection: String?,
        stremioUseCustomSearchEngines: Boolean?,
        stremioResultSort: String?,
        webdavUseLocalFiles: Boolean?,
        stremioLegacyYourMedia: Boolean?,
        stremioOnlyYourMediaStreams: Boolean?,
        stremioDisableYourMediaStreams: Boolean?,
        webdavUseFolderView: Boolean?,
        webdavFlatten: Boolean?,
        stremioLimitPerResolutionTorrent: Int?,
        stremioLimitPerResolutionUsenet: Int?,
        stremioSizeUpper: Long?,
        stremioTorrentSeedersCutoff: Int?,
        pixeldrainApiKey: String?,
        onefichierApiKey: String?,
        gofileApiKey: String?,
        patreonId: String?,
        stremioWaitForDownloadUsenet: Boolean?,
        stremioWaitForDownloadTorrent: Boolean?,
        stremioDisableFilteredNote: Boolean?,
        stremioEmojiInDescription: Boolean?,
        dashboardFilter: DashboardFilterDto?,
        webPlayerAlwaysTranscode: Boolean?,
        webPlayerAlwaysSkipIntro: Boolean?,
        webPlayerAudioPreferredLanguage: String?,
        webPlayerSubtitlePreferredLanguage: String?,
        webPlayerDisablePrestreamSelector: Boolean?,
        dashboardSort: String?,
        webPlayerDisableNextUpDialogue: Boolean?,
        webPlayerEnableScrobbling: Boolean?,
        appendFilenameToLinks: Boolean?,
    ): ApiResult<ApiResponseDto<String>> =
        executeRequest(dispatcher) {
            client.put("user/settings/editsettings") {
                contentType(ContentType.Application.Json)
                setBody(
                    UserSettingsRequestDto(
                        emailNotifications = emailNotifications,
                        webNotifications = webNotifications,
                        mobileNotifications = mobileNotifications,
                        rssNotifications = rssNotifications,
                        downloadSpeedInTab = downloadSpeedInTab,
                        showTrackerInTorrents = showTrackerInTorrents,
                        stremioQuality = stremioQuality,
                        stremioResolution = stremioResolution,
                        stremioLanguage = stremioLanguage,
                        stremioCache = stremioCache,
                        stremioSizeLower = stremioSizeLower,
                        googleDriveFolderId = googleDriveFolderId,
                        onedriveSavePath = onedriveSavePath,
                        discordId = discordId,
                        discordNotifications = discordNotifications,
                        stremioAllowAdult = stremioAllowAdult,
                        stremioSeedTorrents = stremioSeedTorrents,
                        seedTorrents = seedTorrents,
                        allowZipped = allowZipped,
                        stremioAllowZipped = stremioAllowZipped,
                        onefichierFolderId = onefichierFolderId,
                        gofileFolderId = gofileFolderId,
                        jdownloaderNotifications = jdownloaderNotifications,
                        webhookNotifications = webhookNotifications,
                        webhookUrl = webhookUrl,
                        telegramNotifications = telegramNotifications,
                        telegramId = telegramId,
                        megaEmail = megaEmail,
                        megaPassword = megaPassword,
                        stremioSort = stremioSort,
                        cdnSelection = cdnSelection,
                        stremioUseCustomSearchEngines = stremioUseCustomSearchEngines,
                        stremioResultSort = stremioResultSort,
                        webdavUseLocalFiles = webdavUseLocalFiles,
                        stremioLegacyYourMedia = stremioLegacyYourMedia,
                        stremioOnlyYourMediaStreams = stremioOnlyYourMediaStreams,
                        stremioDisableYourMediaStreams = stremioDisableYourMediaStreams,
                        webdavUseFolderView = webdavUseFolderView,
                        webdavFlatten = webdavFlatten,
                        stremioLimitPerResolutionTorrent = stremioLimitPerResolutionTorrent,
                        stremioLimitPerResolutionUsenet = stremioLimitPerResolutionUsenet,
                        stremioSizeUpper = stremioSizeUpper,
                        stremioTorrentSeedersCutoff = stremioTorrentSeedersCutoff,
                        pixeldrainApiKey = pixeldrainApiKey,
                        onefichierApiKey = onefichierApiKey,
                        gofileApiKey = gofileApiKey,
                        patreonId = patreonId,
                        stremioWaitForDownloadUsenet = stremioWaitForDownloadUsenet,
                        stremioWaitForDownloadTorrent = stremioWaitForDownloadTorrent,
                        stremioDisableFilteredNote = stremioDisableFilteredNote,
                        stremioEmojiInDescription = stremioEmojiInDescription,
                        dashboardFilter = dashboardFilter,
                        webPlayerAlwaysTranscode = webPlayerAlwaysTranscode,
                        webPlayerAlwaysSkipIntro = webPlayerAlwaysSkipIntro,
                        webPlayerAudioPreferredLanguage = webPlayerAudioPreferredLanguage,
                        webPlayerSubtitlePreferredLanguage = webPlayerSubtitlePreferredLanguage,
                        webPlayerDisablePrestreamSelector = webPlayerDisablePrestreamSelector,
                        dashboardSort = dashboardSort,
                        webPlayerDisableNextUpDialogue = webPlayerDisableNextUpDialogue,
                        webPlayerEnableScrobbling = webPlayerEnableScrobbling,
                        appendFilenameToLinks = appendFilenameToLinks,
                    ),
                )
            }
        }

    override suspend fun searchEngines(id: Int?): ApiResult<ApiResponseListDto<SearchEngineDto>> =
        executeRequest(dispatcher) {
            client.get("user/settings/searchengines") {
                id?.let { parameter("id", it) }
            }
        }

    override suspend fun controlSearchEngines(
        id: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<ApiResponseDto<String>> {
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

        return executeRequest(dispatcher) {
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

        return executeRequest(dispatcher) {
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

        return executeRequest(dispatcher) {
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

        return executeRequest(dispatcher) {
            client.get("user/stats") {
                parameter("general", general)
                parameter("bandwidth", bandwidth)
                normalizedGrouping?.let { parameter("bandwidth_grouping", it) }
            }
        }
    }
}
